package cn.gorouter.activity

import android.util.Log
import cn.gorouter.R
import cn.gorouter.annotation.Route
import cn.gorouter.api.launcher.GoRouter
import cn.rubintry.demo_lib_common.service.SimpleService


@Route(url = "main/SimpleActivity")
class SimpleActivity : BaseActivity() {

    override fun bindLayout(): Int {
        return R.layout.activity_simple
    }

    override fun initViews() {
        val go = GoRouter.getInstance().build("Simple/SimpleService").go(SimpleService::class.java)
        go?.simple(this.applicationContext)
    }
}