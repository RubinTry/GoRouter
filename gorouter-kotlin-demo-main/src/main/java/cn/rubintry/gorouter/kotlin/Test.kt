package cn.rubintry.gorouter.kotlin

import cn.gorouter.api.kotlin.core.GoRouter
import cn.rubintry.annotation.kotlin.AutoWired
import cn.rubintry.gorouter_kotlin_demo_lib_common.service.ICommonService

class Test {
    fun test() {
        commonService.send("1212")
    }

    @AutoWired("/Service/TargetService")
    private lateinit var commonService : ICommonService

    init {
        GoRouter.getInstance().inject(this)
    }
}