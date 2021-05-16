package cn.gorouter.api.kotlin.core

import android.content.Context
import android.os.Bundle
import cn.gorouter.api.kotlin.pub.IProvider
import java.lang.ref.WeakReference
import kotlin.jvm.Throws

class Panel(path: String) {
    private var context: WeakReference<Context>? = null
    private var path: String? = null
    private var requestCode = 0

    /**
     * The extras data that we need to deliver.
     */
    private val extraMap = mutableMapOf<String , Any>()

    init {
        this.path = path
    }

    fun getPath(): String? {
        return path
    }

    fun setContext(context: Context) {
        this.context = WeakReference(context)
    }

    fun getContext(): Context? {
        return context?.get()
    }

    fun getExtraMap() : Map<String , Any> {
        return extraMap
    }

    fun setRequestCode(requestCode: Int){
        this.requestCode = requestCode
    }

    fun getRequestCode(): Int{
        return requestCode
    }

    fun go(): Any? {
        val instance = Core.getInstance().go(this)
        return instance
    }



    fun <T> go(serviceType: Class<T>) : T{
        val instance = Core.getInstance().go(serviceType , this)
        checkNotNull(instance)
        return instance
    }


    fun go(context: Context , requestCode: Int){
        Core.getInstance().go(context , this  , requestCode)
    }


    fun with(key: String , value: Bundle) : Panel{
        extraMap[key] = value
        return this
    }

    fun withString(key: String , value: String) : Panel{
        extraMap[key] = value
        return this
    }

    fun withInt(key: String , value: Int): Panel {
        extraMap[key] = value
        return this
    }

    fun withFloat(key: String , value: Float): Panel{
        extraMap[key] = value
        return this
    }

    fun withDouble(key: String , value: Double): Panel {
        extraMap[key] = value
        return this
    }

    fun withFloatArray(key: String , value : FloatArray): Panel {
        extraMap[key] = value
        return this
    }

    fun clearExtras() {
        extraMap.clear()
    }
}