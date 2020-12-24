package cn.rubintry.demo_lib_common.service

import android.content.Context
import cn.gorouter.annotation.Route
import cn.gorouter.api.pub.IProvider



interface SimpleService : IProvider {
    fun simple(context: Context)
}