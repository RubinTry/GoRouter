package cn.gorouter.api.kotlin.core

import android.content.Context
import android.util.LruCache
import cn.gorouter.api.kotlin.service.AutoWireService
import cn.rubintry.annotation.kotlin.Route


@Route(path = "/GoRouter/AutoWire/Service")
class AutoWireServiceImpl : AutoWireService {

    private var classCache: LruCache<String, ISyringe>? = null
    private var blackList: MutableList<String>? = null

    override fun autoWire(instance: Any) {
        inject(instance, null)
    }


    override fun init(context: Context) {
        classCache = LruCache<String, ISyringe>(50)
        blackList = mutableListOf()
    }


    private fun inject(instance: Any, parent: Class<*>?) {
        val clazz = parent ?: instance.javaClass

        val syringe = getSyringe(clazz)
        syringe?.inject(instance)

        val superClazz = clazz.superclass
        if(null != superClazz && !superClazz.name.startsWith("android")){
            inject(instance , superClazz)
        }
    }

    private fun getSyringe(clazz: Class<*>): ISyringe? {
        val className = clazz.name

        try {
            if (blackList?.contains(className) == false) {
                var syringe = classCache?.get(className)
                if (syringe == null) {
                    syringe = Class.forName("${clazz.name}_GoRouter_AutoWired").getConstructor().newInstance() as ISyringe?
                }
                classCache?.put(className, syringe)
                return syringe
            }
        } catch (ex: ClassNotFoundException) {
            // This instance need not autowired.
            blackList?.add(className)
        }

        return null
    }
}