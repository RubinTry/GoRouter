package cn.gorouter.api.kotlin.core

import android.util.Log
import cn.gorouter.api.kotlin.Const.TAG

object Logger {

    private var open = false

    @JvmStatic
    fun open(){
        open = true
    }

    @JvmStatic
    fun debug(message: String) {
        if(open){
            Log.d(TAG, message)
        }
    }


    @JvmStatic
    fun error(message: String) {
        if(open){
            Log.e(TAG, message)
        }
    }

    @JvmStatic
    fun error(message: String, throwable: Throwable) {
        if(open){
            Log.e(TAG, message, throwable)
        }
    }

    @JvmStatic
    fun error(throwable: Throwable){
        if(open){
            Log.e(TAG, "" , throwable)
        }
    }

    @JvmStatic
    fun info(message: String) {
        if(open){
            Log.i(TAG, message)
        }
    }


    @JvmStatic
    fun warn(message: String) {
       if(open){
           Log.w(TAG, message)
       }
    }


}