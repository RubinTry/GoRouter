package cn.rubintry.gorouter_kotlin_demo_lib_common.service

import android.content.Context
import cn.gorouter.api.kotlin.pub.IProvider

interface ICommonService : IProvider {
    fun common(context: Context)

    fun send(dataStr : String)
}