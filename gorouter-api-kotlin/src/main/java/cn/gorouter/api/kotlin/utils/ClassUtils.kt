package cn.gorouter.api.kotlin.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import cn.gorouter.api.kotlin.core.GoRouter
import cn.gorouter.api.kotlin.core.Logger
import cn.gorouter.api.kotlin.thread.DefaultPoolExecutor
import dalvik.system.DexFile
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.regex.Pattern

object ClassUtils {
    private const val TAG = "ClassUtils"
    private const val EXTRACTED_NAME_EXT = ".classes"
    private const val EXTRACTED_SUFFIX = ".zip"

    private val SECONDARY_FOLDER_NAME = "code_cache" + File.separator + "secondary-dexes"

    private const val PREFS_FILE = "multidex.version"
    private const val KEY_DEX_NUMBER = "dex.number"

    private const val VM_WITH_MULTIDEX_VERSION_MAJOR = 2
    private const val VM_WITH_MULTIDEX_VERSION_MINOR = 1

    @JvmStatic
    fun getClass(context: Context, packageName: String): Set<String> {
        val classList: MutableSet<String> = HashSet()
        val paths: List<String>? = getSourcePaths(context)
        checkNotNull(paths)
        //线程总数锁存器
        val pathParserCtl = CountDownLatch(paths.size)
        for (path in paths) {
            DefaultPoolExecutor.getInstance().execute(Runnable {
                var dexFile: DexFile? = null
                try {
                    dexFile = if (path.endsWith(EXTRACTED_SUFFIX)) {
                        //NOT use new DexFile(path), because it will throw "permission error in /data/dalvik-cache"
                        DexFile.loadDex(path, "$path.tmp", 0)
                    } else {
                        DexFile(path)
                    }
                    val dexEntries = dexFile!!.entries()
                    while (dexEntries.hasMoreElements()) {
                        val className = dexEntries.nextElement()
                        if (className.startsWith(packageName)) {
                            classList.add(className)
                        }
                    }
                } catch (ignore: Throwable) {
                    Logger.error("Scan map file in dex files made error.", ignore)
                } finally {
                    if (null != dexFile) {
                        try {
                            dexFile.close()
                        } catch (ignore: Throwable) {
                        }
                    }

                    //总数 -1
                    pathParserCtl.countDown()
                }
            })
        }

        //一直等待，直到线程总数降为0(一开始的线程总数为 paths.size())
        pathParserCtl.await()
        Logger.debug(TAG + "Filter " + classList.size + " classes by packageName <" + packageName + ">")
        return classList
    }


    /**
     * 获取所有的dex路径
     *
     * @param context the application context
     * @return all the dex path
     * @throws PackageManager.NameNotFoundException
     * @throws IOException
     */
    @JvmStatic
    @Throws(PackageManager.NameNotFoundException::class, IOException::class)
    fun getSourcePaths(context: Context): List<String>? {
        val applicationInfo = context.packageManager.getApplicationInfo(context.packageName, 0)
        val sourceApk = File(applicationInfo.sourceDir)
        val sourcePaths: MutableList<String> = ArrayList()
        //add the default apk path
        sourcePaths.add(applicationInfo.sourceDir)

        //the prefix of extracted file, ie: test.classes
        val extractedFilePrefix = sourceApk.name + EXTRACTED_NAME_EXT

        //如果VM已经支持了MultiDex，就不要去Secondary Folder加载 Classesx.zip了，那里已经么有了
        //通过是否存在sp中的multidex.version是不准确的，因为从低版本升级上来的用户，是包含这个sp配置的
        if (!isVMMultidexCapable()) {
            //the total dex numbers
            val totalDexNumber: Int = getMultiDexPreferences(context)?.getInt(KEY_DEX_NUMBER, 1) ?: 0
            val dexDir = File(applicationInfo.dataDir, SECONDARY_FOLDER_NAME)
            for (secondaryNumber in 2..totalDexNumber) {
                //for each dex file, ie: test.classes2.zip, test.classes3.zip...
                val fileName = extractedFilePrefix + secondaryNumber + EXTRACTED_SUFFIX
                val extractedFile = File(dexDir, fileName)
                if (extractedFile.isFile) {
                    sourcePaths.add(extractedFile.absolutePath)
                    //we ignore the verify zip part
                } else {
                    throw IOException("Missing extracted secondary dex file '" + extractedFile.path + "'")
                }
            }
        }
        // Search instant run support only debuggable
        if (GoRouter.isDebugable()) {
            sourcePaths.addAll(tryLoadInstantRunDexFile(applicationInfo))
        }
        return sourcePaths
    }


    /**
     * Identifies if the current VM has a native support for multidex, meaning there is no need for
     * additional installation by this library.
     *
     * @return true if the VM handles multidex
     */
    private fun isVMMultidexCapable(): Boolean {
        var isMultidexCapable = false
        var vmName: String? = null
        try {
            //原生Android
            vmName = "'Android'"
            val versionString = System.getProperty("java.vm.version")
            if (versionString != null) {
                val matcher = Pattern.compile("(\\d+)\\.(\\d+)(\\.\\d+)?").matcher(versionString)
                if (matcher.matches()) {
                    try {
                        val major = matcher.group(1).toInt()
                        val minor = matcher.group(2).toInt()
                        isMultidexCapable = (major > VM_WITH_MULTIDEX_VERSION_MAJOR
                                || (major == VM_WITH_MULTIDEX_VERSION_MAJOR
                                && minor >= VM_WITH_MULTIDEX_VERSION_MINOR))
                    } catch (ignore: NumberFormatException) {
                        // let isMultidexCapable be false
                    }
                }
            }
        } catch (ignore: Exception) {
        }
        Logger.error("VM with name " + vmName + if (isMultidexCapable) " has multidex support" else " does not have multidex support")
        return isMultidexCapable
    }


    /**
     * 获取 instant run dex 路径, 用来捕获分支 usingApkSplits=false.
     */
    private fun tryLoadInstantRunDexFile(applicationInfo: ApplicationInfo): List<String> {
        val instantRunSourcePaths: MutableList<String> = ArrayList()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && null != applicationInfo.splitSourceDirs) {
            // add the split apk, normally for InstantRun, and newest version.
            instantRunSourcePaths.addAll(Arrays.asList(*applicationInfo.splitSourceDirs))
            Logger.debug("Found InstantRun support")
        } else {
            try {
                // This man is reflection from Google instant run sdk, he will tell me where the dex files go.
                val pathsByInstantRun = Class.forName("com.android.tools.fd.runtime.Paths")
                val getDexFileDirectory = pathsByInstantRun.getMethod("getDexFileDirectory", String::class.java)
                val instantRunDexPath = getDexFileDirectory.invoke(null, applicationInfo.packageName) as String
                val instantRunFilePath = File(instantRunDexPath)
                if (instantRunFilePath.exists() && instantRunFilePath.isDirectory) {
                    val dexFile = instantRunFilePath.listFiles()
                    for (file in dexFile) {
                        if (null != file && file.exists() && file.isFile && file.name.endsWith(".dex")) {
                            instantRunSourcePaths.add(file.absolutePath)
                        }
                    }
                    Logger.debug("Found InstantRun support")
                }
            } catch (e: java.lang.Exception) {
                Logger.error("InstantRun support error, " + e.message)
            }
        }
        return instantRunSourcePaths
    }

    private fun getMultiDexPreferences(context: Context): SharedPreferences? {
        return context.getSharedPreferences(PREFS_FILE, if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) Context.MODE_PRIVATE else Context.MODE_PRIVATE or Context.MODE_MULTI_PROCESS)
    }
}