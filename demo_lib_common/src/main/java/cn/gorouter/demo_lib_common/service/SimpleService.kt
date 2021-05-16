package cn.gorouter.demo_lib_common.service

import android.content.Context
import cn.gorouter.api.kotlin.pub.IProvider


interface SimpleService : IProvider {
    fun simple(context: Context)
}