package cn.gorouter.api.kotlin.pub

import android.content.Context

interface IProvider {
    /**
     * Make some initialize for service
     * @param context  The context what your application context is.
     */
    fun init(context: Context)
}