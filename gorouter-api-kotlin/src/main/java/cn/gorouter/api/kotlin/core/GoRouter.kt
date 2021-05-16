package cn.gorouter.api.kotlin.core

import android.content.Context
import cn.gorouter.api.kotlin.GoRouterException
import cn.gorouter.api.kotlin.isApplicationContext

final class GoRouter {


    companion object {
        @Volatile
        private var INSTANCE: GoRouter? = null

        @JvmStatic
        fun getInstance(): GoRouter {
            if (INSTANCE == null) {
                synchronized(GoRouter::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = GoRouter()
                    }
                }
            }
            return INSTANCE!!
        }

        @JvmStatic
        fun openLog() {
            Logger.open()
        }

        @JvmStatic
        fun init(context: Context) {
            Logger.info("===> GoRouter init start!!!")
            if (!context.isApplicationContext()) {
                throw GoRouterException("You must init it in Application.")
            }

            Core.getInstance().init(context)
            Logger.info("===> GoRouter init over!!!")
        }

        @JvmStatic
        fun setDebugable(debugable: Boolean) {
            Core.getInstance().debugAble = debugable
        }

        @JvmStatic
        fun isDebugable(): Boolean {
            return Core.getInstance().debugAble
        }


    }


    fun build(path: String): Panel {
        if (WareHouse.panelMap.containsKey(path)) {
            return WareHouse.panelMap[path]!!
        }
        val panel = Panel(path)
        WareHouse.panelMap[path] = panel
        panel.clearExtras()
        return panel
    }

    fun inject(thiz: Any) {
        Core.getInstance().inject(thiz)
    }
}