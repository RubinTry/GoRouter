package cn.gorouter.api.kotlin.core

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import cn.gorouter.api.kotlin.Const
import cn.gorouter.api.kotlin.pub.IProvider
import cn.gorouter.api.kotlin.service.AutoWireService
import cn.gorouter.api.kotlin.thread.MainExecutor
import cn.gorouter.api.kotlin.utils.ClassUtils
import cn.gorouter.api.kotlin.utils.PackageUtils
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.*

class Core {

    private var mContext: Context? = null

    var debugAble = false

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: Core? = null

        @JvmStatic
        fun getInstance(): Core {
            if (INSTANCE == null) {
                synchronized(Core::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Core()
                    }
                }
            }
            return INSTANCE!!
        }
    }


    fun put(key: String, clazz: Class<*>) {
        WareHouse.routeMap[key] = clazz
        Logger.debug("Find node ${clazz.name}")
    }




    fun init(context: Context): Boolean {
        this.mContext = context.applicationContext
        var classNames: Set<String>? = null
        try {
            if (GoRouter.isDebugable() || PackageUtils.isNewVersion(context)) {
                classNames = ClassUtils.getClass(context.applicationContext, "cn.gorouter.route")
                if (classNames.isNotEmpty()) {
                    context.getSharedPreferences(Const.GOROUTER_SP_CACHE_KEY, Context.MODE_PRIVATE).edit().putStringSet(Const.GOROUTER_SP_KEY_SET, classNames).apply()
                }
                PackageUtils.updateVersion(context)
            } else {
                Logger.info("Load router map from cache.")
                classNames = HashSet(context.getSharedPreferences(Const.GOROUTER_SP_CACHE_KEY, Context.MODE_PRIVATE).getStringSet(Const.GOROUTER_SP_KEY_SET, HashSet()))
            }
            for (aClassName in classNames) {
                val aClass = Class.forName(aClassName)
                if (IRouter::class.java.isAssignableFrom(aClass)) {
                    val iRouter: IRouter = aClass.newInstance() as IRouter
                    iRouter.put()
                }
                if(IAutoWired::class.java.isAssignableFrom(aClass)){
                    val iAutoWired : IAutoWired = aClass.newInstance() as IAutoWired
                    iAutoWired.put()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

        return true
    }



    fun inject(thiz: Any) {
        val autoWireService : AutoWireService = GoRouter.getInstance().build("/GoRouter/AutoWire/Service").go() as AutoWireService
        autoWireService.autoWire(thiz)
    }


    fun go(panel: Panel): Any? {
        return go(null, panel, null)
    }


    fun <T> go(serviceType: Class<T> , panel: Panel) : T?{
        val instance = go(mContext, panel, null) ?: return null
        return serviceType.cast(instance)
    }

    fun go(context: Context?, panel: Panel, requestCode: Int?): Any? {
        checkNotNull(panel.getPath()){ "The path is null!!!" }
        checkNotNull(mContext){ "You're not init yet!!!" }
        panel.setContext(context ?: mContext!!)
        panel.setRequestCode(requestCode ?: 0)
        return try {
            if(!WareHouse.routeMap.containsKey(panel.getPath())){
                throw IllegalArgumentException("Node ${panel.getPath()} is not found")
            }
            Logger.info("GoRouter navigation start!!!")
            val nodeClass = WareHouse.routeMap[panel.getPath()]
            checkNotNull(nodeClass){ "Class {${panel.getPath()}} is not found!!!" }
            Logger.info("GoRouter navigation over!!!")
            navigation(panel, nodeClass)

        } catch (ex: ClassNotFoundException) {
            Logger.error(ex)
            null
        }
    }


    private fun <T> navigation(panel: Panel, nodeClass: Class<T>) : Any? {
        return when {
            Activity::class.java.isAssignableFrom(nodeClass) -> {
                runOnUiThread(Runnable {
                    startActivity(panel, nodeClass, panel.getRequestCode(), null)
                })
                null
            }

            Fragment::class.java.isAssignableFrom(nodeClass) -> {
                return nodeClass.newInstance() as T
            }

            IProvider::class.java.isAssignableFrom(nodeClass) -> {
                val instance : IProvider = nodeClass.newInstance() as IProvider
                instance.init(panel.getContext()!!)
                instance
            }

            else -> {
                null
            }
        }
    }


    /**
     * 将要传递的参数写入到intent中
     *
     * @param intent
     * @param extraMap
     */
    private fun writeBundleToIntent(intent: Intent, extraMap: Map<String, Any>) {
        val iterator = extraMap.keys.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            when (val value = extraMap[key]) {
                is String -> {
                    intent.putExtra(key, value)
                }
                is Int -> {
                    intent.putExtra(key, value)
                }
                is Long -> {
                    intent.putExtra(key, value)
                }
                is CharSequence -> {
                    intent.putExtra(key, value)
                }
                is IntArray -> {
                    intent.putExtra(key, value)
                }
                is ByteArray -> {
                    intent.putExtra(key, value)
                }
                is Byte -> {
                    intent.putExtra(key, value)
                }
                is Float -> {
                    intent.putExtra(key, value)
                }
                is Double -> {
                    intent.putExtra(key, value)
                }
                is FloatArray -> {
                    intent.putExtra(key, value)
                }
                is DoubleArray -> {
                    intent.putExtra(key, value)
                }
                is Bundle -> {
                    intent.putExtra(key , value)
                }
            }
        }
    }

    private fun startActivity(panel: Panel, nodeClass: Class<*>?, requestCode: Int, options: Bundle?) {
        val context = panel.getContext() ?: mContext!!
        val intent = Intent(context, nodeClass)
        val extraMap = panel.getExtraMap()
        if (extraMap.isNotEmpty()) {
            writeBundleToIntent(intent, extraMap)
        }
        if (context == mContext) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } else {
            ActivityCompat.startActivityForResult((context as Activity), intent, requestCode, options)
        }
    }


    private fun runOnUiThread(runnable: Runnable) {
        MainExecutor.getInstance().execute(runnable)
    }




}