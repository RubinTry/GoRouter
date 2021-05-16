package cn.rubintry.gorouter_kotlin_demo_main2.service

import android.content.Context
import cn.gorouter.api.kotlin.core.Logger
import cn.rubintry.annotation.kotlin.Route


@Route(path = "/Service/TargetService")
class TargetService : cn.rubintry.gorouter_kotlin_demo_lib_common.service.ICommonService {
    override fun common(context: Context) {
        Logger.info("common方法被调用: ")
    }

    override fun send(dataStr: String) {
        Logger.info("数据: $dataStr  发送成功")
    }

    override fun init(context: Context) {
        Logger.info("服务自动初始化中: ")
    }
}