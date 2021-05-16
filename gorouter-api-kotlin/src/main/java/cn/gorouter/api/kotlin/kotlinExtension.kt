package cn.gorouter.api.kotlin

import android.content.Context


fun Context.isApplicationContext() : Boolean{
    if(this == this.applicationContext){
        return true
    }
    return false
}