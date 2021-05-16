package cn.rubintry.gorouter.kotlin

import android.app.Application
import cn.gorouter.api.kotlin.core.GoRouter

class TestApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        GoRouter.openLog()
        GoRouter.setDebugable(true)
        GoRouter.init(this)
    }
}