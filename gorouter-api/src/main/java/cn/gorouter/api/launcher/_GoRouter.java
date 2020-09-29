package cn.gorouter.api.launcher;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.transition.Transition;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.gorouter.api.card.FragmentSharedCard;
import cn.gorouter.api.card.GoBoard;
import cn.gorouter.api.logger.GoLogger;
import cn.gorouter.api.monitor.FragmentMonitor;
import cn.gorouter.api.threadpool.DefaultPoolExecutor;
import cn.gorouter.api.threadpool.MainExecutor;
import cn.gorouter.api.monitor.ActivityMonitor;
import cn.gorouter.api.utils.Const;
import dalvik.system.DexFile;

import static cn.gorouter.api.launcher._GoRouter.TypeKind.ACTIVITY;
import static cn.gorouter.api.launcher._GoRouter.TypeKind.FRAGMENT;
import static cn.gorouter.api.launcher._GoRouter.TypeKind.FRAGMENT_IN_APP_PACKAGE;

/**
 * @author logcat <a href="13857769302@163.com">Contact me.</a>
 * @version 1.0.10
 * @since 2020/07/11 16:25
 */
public class _GoRouter {
    private static volatile _GoRouter instance;
    private Map<String, Class> nodeTargetContainer;
    private static Context mContext;
    private FragmentSharedCard mFragmentSharedCard;
    private GoBoard goBoard;


    private static final String EXTRACTED_NAME_EXT = ".classes";
    private static final String EXTRACTED_SUFFIX = ".zip";

    private static final String SECONDARY_FOLDER_NAME = "code_cache" + File.separator + "secondary-dexes";

    private static final String PREFS_FILE = "multidex.version";
    private static final String KEY_DEX_NUMBER = "dex.number";

    private static final int VM_WITH_MULTIDEX_VERSION_MAJOR = 2;
    private static final int VM_WITH_MULTIDEX_VERSION_MINOR = 1;


    private _GoRouter() {
        nodeTargetContainer = new HashMap<>();
        goBoard = new GoBoard();
    }

    /**
     * 获得_GoRouter单例
     *
     * @return
     */
    public static _GoRouter getInstance() {
        if (instance == null) {
            synchronized (_GoRouter.class) {
                if (instance == null) {
                    instance = new _GoRouter();
                }
            }
        }
        return instance;
    }


    /**
     * 初始化_GoRouter
     *
     * @param application
     * @return
     */
    public synchronized static boolean init(Application application) {
        mContext = application.getApplicationContext();
        ActivityMonitor.Companion.getInstance().initialize(application);
        FragmentMonitor.Companion.getInstance().initialize(application);
        return initAllRoute(mContext);
    }


    /**
     * Initialize all route and put them into container.
     * 初始化_GoRouter并且将所有节点添加进容器
     *
     * @param context
     * @return
     */
    private static synchronized boolean initAllRoute(Context context) {
        try {
            List<String> classNames = getClasses(context.getApplicationContext(), "cn.gorouter.route");
            for (String aClassName : classNames) {
                Class aClass = Class.forName(aClassName);
                if (IRouter.class.isAssignableFrom(aClass)) {
                    IRouter iRouter = (IRouter) aClass.newInstance();
                    iRouter.put();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


    /**
     * Scan all class names under the specified package name.
     * 扫描某个包名下的所有类
     *
     * @param context
     * @param packageName Witch package we want scan.
     * @return All the classes from a package
     */
    private static List<String> getClasses(Context context, String packageName) throws PackageManager.NameNotFoundException, IOException, InterruptedException {
        List<String> classList = new ArrayList<>();

        List<String> paths = getSourcePaths(context);
        //线程总数锁存器
        final CountDownLatch pathParserCtl = new CountDownLatch(paths.size());
        for (String path : paths) {
            DefaultPoolExecutor.Companion.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    DexFile dexFile = null;
                    try {
                        if (path.endsWith(EXTRACTED_SUFFIX)) {
                            //NOT use new DexFile(path), because it will throw "permission error in /data/dalvik-cache"
                            dexFile = DexFile.loadDex(path, path + ".tmp", 0);
                        } else {
                            dexFile = new DexFile(path);
                        }

                        Enumeration<String> dexEntries = dexFile.entries();
                        while (dexEntries.hasMoreElements()) {
                            String className = dexEntries.nextElement();
                            if (className.startsWith(packageName)) {
                                classList.add(className);
                            }
                        }
                    } catch (Throwable ignore) {
                        GoLogger.error("Scan map file in dex files made error.", ignore);
                    } finally {
                        if (null != dexFile) {
                            try {
                                dexFile.close();
                            } catch (Throwable ignore) {
                            }
                        }

                        //总数 -1
                        pathParserCtl.countDown();
                    }
                }
            });
        }

        //一直等待，直到线程总数降为0
        pathParserCtl.await();
        GoLogger.debug(Const.TAG + "Filter " + classList.size() + " classes by packageName <" + packageName + ">");
        return classList;
    }


    /**
     * 获取所有的dex路径
     *
     * @param context the application context
     * @return all the dex path
     * @throws PackageManager.NameNotFoundException
     * @throws IOException
     */
    public static List<String> getSourcePaths(Context context) throws PackageManager.NameNotFoundException, IOException {
        ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
        File sourceApk = new File(applicationInfo.sourceDir);

        List<String> sourcePaths = new ArrayList<>();
        //add the default apk path
        sourcePaths.add(applicationInfo.sourceDir);

        //the prefix of extracted file, ie: test.classes
        String extractedFilePrefix = sourceApk.getName() + EXTRACTED_NAME_EXT;

        //如果VM已经支持了MultiDex，就不要去Secondary Folder加载 Classesx.zip了，那里已经么有了
        //通过是否存在sp中的multidex.version是不准确的，因为从低版本升级上来的用户，是包含这个sp配置的
        if (!isVMMultidexCapable()) {
            //the total dex numbers
            int totalDexNumber = getMultiDexPreferences(context).getInt(KEY_DEX_NUMBER, 1);
            File dexDir = new File(applicationInfo.dataDir, SECONDARY_FOLDER_NAME);

            for (int secondaryNumber = 2; secondaryNumber <= totalDexNumber; secondaryNumber++) {
                //for each dex file, ie: test.classes2.zip, test.classes3.zip...
                String fileName = extractedFilePrefix + secondaryNumber + EXTRACTED_SUFFIX;
                File extractedFile = new File(dexDir, fileName);
                if (extractedFile.isFile()) {
                    sourcePaths.add(extractedFile.getAbsolutePath());
                    //we ignore the verify zip part
                } else {
                    throw new IOException("Missing extracted secondary dex file '" + extractedFile.getPath() + "'");
                }
            }
        }
        // Search instant run support only debuggable
        if (GoLogger.isOpen()) {
            sourcePaths.addAll(tryLoadInstantRunDexFile(applicationInfo));
        }
        return sourcePaths;
    }


    /**
     * 获取 instant run dex 路径, 用来捕获分支 usingApkSplits=false.
     */
    private static List<String> tryLoadInstantRunDexFile(ApplicationInfo applicationInfo) {
        List<String> instantRunSourcePaths = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && null != applicationInfo.splitSourceDirs) {
            // add the split apk, normally for InstantRun, and newest version.
            instantRunSourcePaths.addAll(Arrays.asList(applicationInfo.splitSourceDirs));
            Log.d(Const.TAG, "Found InstantRun support");
        } else {
            try {
                // This man is reflection from Google instant run sdk, he will tell me where the dex files go.
                Class pathsByInstantRun = Class.forName("com.android.tools.fd.runtime.Paths");
                Method getDexFileDirectory = pathsByInstantRun.getMethod("getDexFileDirectory", String.class);
                String instantRunDexPath = (String) getDexFileDirectory.invoke(null, applicationInfo.packageName);

                File instantRunFilePath = new File(instantRunDexPath);
                if (instantRunFilePath.exists() && instantRunFilePath.isDirectory()) {
                    File[] dexFile = instantRunFilePath.listFiles();
                    for (File file : dexFile) {
                        if (null != file && file.exists() && file.isFile() && file.getName().endsWith(".dex")) {
                            instantRunSourcePaths.add(file.getAbsolutePath());
                        }
                    }
                    Log.d(Const.TAG, "Found InstantRun support");
                }

            } catch (Exception e) {
                Log.e(Const.TAG, "InstantRun support error, " + e.getMessage());
            }
        }

        return instantRunSourcePaths;
    }

    private static SharedPreferences getMultiDexPreferences(Context context) {
        return context.getSharedPreferences(PREFS_FILE, Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ? Context.MODE_PRIVATE : Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
    }

    /**
     * Identifies if the current VM has a native support for multidex, meaning there is no need for
     * additional installation by this library.
     *
     * @return true if the VM handles multidex
     */
    private static boolean isVMMultidexCapable() {
        boolean isMultidexCapable = false;
        String vmName = null;

        try {
            //原生Android
            vmName = "'Android'";
            String versionString = System.getProperty("java.vm.version");
            if (versionString != null) {
                Matcher matcher = Pattern.compile("(\\d+)\\.(\\d+)(\\.\\d+)?").matcher(versionString);
                if (matcher.matches()) {
                    try {
                        int major = Integer.parseInt(matcher.group(1));
                        int minor = Integer.parseInt(matcher.group(2));
                        isMultidexCapable = (major > VM_WITH_MULTIDEX_VERSION_MAJOR)
                                || ((major == VM_WITH_MULTIDEX_VERSION_MAJOR)
                                && (minor >= VM_WITH_MULTIDEX_VERSION_MINOR));
                    } catch (NumberFormatException ignore) {
                        // let isMultidexCapable be false
                    }
                }
            }

        } catch (Exception ignore) {

        }

        Log.i(Const.TAG, "VM with name " + vmName + (isMultidexCapable ? " has multidex support" : " does not have multidex support"));
        return isMultidexCapable;
    }


    /**
     * Build a route
     * 通过路由键构建一个路由
     *
     * @param routeKey route key address
     * @param extra    The data that needs to be passed to the target page
     */
    public void build(String routeKey, Bundle extra) {
        goBoard.setRouteKey(routeKey);
        goBoard.setData(extra);
    }


    /**
     * Go to target page.
     * 通过路由键访问具体页面
     */
    public void go(Context context, Integer requestCode, @Nullable Bundle options) {
        String currentUrl = goBoard.getRouteKey();
        try {
            Context currentContext = null == context ? mContext : context;
            if (currentUrl == null) {
                throw new IllegalArgumentException("Please set routeKey");
            }

            if (nodeTargetContainer == null) {
                throw new NullPointerException("container is empty!!!");
            }

            //Get all the node and type classes.
            Class nodeTarget = nodeTargetContainer.get(currentUrl);

            if (nodeTarget != null) {
                if (Activity.class.isAssignableFrom(nodeTarget)) {
                    //If the node type is Activity,the jump is made in the form of Activity.
                    go(currentContext, requestCode, ACTIVITY, nodeTarget, options);
                } else if (Fragment.class.isAssignableFrom(nodeTarget)) {
                    //If the node type is Fragment,the jump is made in the form of Fragment.
                    go(currentContext, requestCode, FRAGMENT, nodeTarget, options);
                } else if (android.app.Fragment.class.isAssignableFrom(nodeTarget)) {
                    //If the node type is Fragment in package app,we should go to fragment
                    go(currentContext, requestCode, FRAGMENT_IN_APP_PACKAGE, nodeTarget, options);
                }
            } else {
                throw new IllegalArgumentException("route \"" + currentUrl + "\" is not found!!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 为fragment添加共享元素以便在跳转时自动携带炫酷动画
     *
     * @param element              需要添加共享元素效果的视图
     * @param backStackTAG         返回栈tag
     * @param useDefaultTransition 是否启用默认动画
     */
    public void addSharedFragment(View element, String backStackTAG, boolean useDefaultTransition) {
        if (mFragmentSharedCard == null) {
            mFragmentSharedCard = new FragmentSharedCard();
        }
        mFragmentSharedCard.setSharedElement(element);
        mFragmentSharedCard.setTAG(backStackTAG);
        mFragmentSharedCard.setUseDefaultTransition(useDefaultTransition);
    }


    /**
     * 添加共享元素动画
     *
     * @param enterTransition 入场动画
     * @param exitTransition  出场动画
     */
    public void addTransition(Transition enterTransition, Transition exitTransition) {
        if (mFragmentSharedCard == null) {
            mFragmentSharedCard = new FragmentSharedCard();
        }
        mFragmentSharedCard.setEnterTransition(enterTransition);
        mFragmentSharedCard.setExitTransition(exitTransition);
    }


    /**
     * 设置供fragment跳转用的容器id
     *
     * @param containerId
     */
    public void setFragmentContainerId(int containerId) {
        if (containerId == View.NO_ID) {
            throw new IllegalArgumentException("Can't add fragment with no id");
        }

        if (goBoard == null) {
            goBoard = new GoBoard();
        }
        goBoard.setFragmentContainerId(containerId);
    }


    /**
     * Go to target page.
     * 通过路由键访问具体页面
     *
     * @param type       页面类型 {@link TypeKind#ACTIVITY} activity类型   {@link TypeKind#FRAGMENT} fragment 类型
     * @param nodeTarget
     */
    private void go(Context currentContext, Integer requestCode, TypeKind type, Class nodeTarget, @Nullable Bundle options) throws NullPointerException {
        switch (type) {
            case ACTIVITY:

                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(currentContext, nodeTarget, options, requestCode);
                    }
                });

                break;
            case FRAGMENT:
                Bundle currentData = goBoard.getData();
                String currentUrl = goBoard.getRouteKey();
                try {
                    Fragment curFragment = (Fragment) nodeTarget.getConstructor().newInstance();

                    if (currentData != null) {
                        curFragment.setArguments(currentData);
                    }

                    if (mFragmentSharedCard != null) {
                        FragmentMonitor.Companion.getInstance().setFragmentContainerId(goBoard.getFragmentContainerId()).setFragmentSharedCard(mFragmentSharedCard).show(curFragment, currentUrl);
                    } else {
                        FragmentMonitor.Companion.getInstance().setFragmentContainerId(goBoard.getFragmentContainerId()).show(curFragment, currentUrl);
                    }

                    mFragmentSharedCard = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                goBoard.release();
                break;
            default:
                break;
        }


    }


    /**
     * Put all the node and type names into container.
     * 将所有节点与节点类型装入容器
     *
     * @param url
     * @param target
     */
    public void put(String url, Class target) {
        if (url != null && target != null) {
            nodeTargetContainer.put(url, target);
            try {
                GoLogger.info(target.getSimpleName() + "  added!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置需要携带的数据
     *
     * @param extra 数据Bundle
     */
    public void withExtra(Bundle extra) {
        goBoard.setData(extra);
    }

    /**
     * 携带int数据
     *
     * @param key
     * @param intValue 需要携带的int值
     */
    public void withInt(String key, int intValue) {
        goBoard.putInt(key, intValue);
    }


    /**
     * 携带float数据
     *
     * @param key
     * @param floatValue
     */
    public void withFloat(String key, float floatValue) {
        goBoard.putFloat(key, floatValue);
    }


    /**
     * 携带长整型数据
     *
     * @param key
     * @param longValue
     */
    public void withLong(String key, long longValue) {
        goBoard.putLong(key, longValue);
    }


    /**
     * 携带双精度浮点数
     *
     * @param key
     * @param doubleValue
     */
    public void withDouble(String key, double doubleValue) {
        goBoard.putDouble(key, doubleValue);
    }


    /**
     * 携带字符串数据
     *
     * @param key
     * @param stringValue
     */
    public void withString(String key, String stringValue) {
        goBoard.putString(key, stringValue);
    }


    /**
     * 携带字符序列
     *
     * @param key
     * @param charSequenceValue
     */
    public void withCharSequence(String key, CharSequence charSequenceValue) {
        goBoard.putCharSequence(key, charSequenceValue);
    }


    /**
     * 携带短整型数据
     *
     * @param key
     * @param shortValue
     */
    public void withShort(String key, short shortValue) {
        goBoard.putShort(key, shortValue);
    }


    /**
     * Page type.
     * 页面类型
     */
    enum TypeKind {
        ACTIVITY(0),
        FRAGMENT(1),
        FRAGMENT_IN_APP_PACKAGE(2);

        private final int type;

        TypeKind(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }


    /**
     * Jump into an Activity
     * 开始跳转至对应的activity
     *
     * @param currentContext 当前页面上下文（可能为应用全局的上下文）
     * @param activityClazz  目标activity的类对象
     * @param requestCode    页面返回时回调的请求码
     */
    private void startActivity(Context currentContext, Class activityClazz, @Nullable Bundle options, Integer requestCode) {

        Bundle currentData = goBoard.getData();
        Intent intent = new Intent(currentContext, activityClazz);
        if (currentData != null) {
            intent.putExtras(currentData);
        }


        if (requestCode != null && requestCode >= 0) {
            if (currentContext instanceof Activity) {
                ActivityCompat.startActivityForResult((Activity) currentContext, intent, requestCode, options);
            } else {
                GoLogger.warn("Must use [go(activity, ...)] to support [startActivityForResult]");
            }
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ActivityCompat.startActivity(currentContext, intent, options);
        }
        goBoard.release();
    }


    /**
     * Run on UIThread
     * 确保再UI线程上进行
     *
     * @param runnable
     */
    private void runOnMainThread(Runnable runnable) {
        Objects.requireNonNull(MainExecutor.Companion.getInstance()).execute(runnable);
    }
}
