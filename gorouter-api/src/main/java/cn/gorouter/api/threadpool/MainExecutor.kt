package cn.gorouter.api.threadpool

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor

class MainExecutor : Executor {
    private val handler = Handler(Looper.getMainLooper())
    override fun execute(command: Runnable?) {
        handler.post(command)
    }

    companion object {
        @Volatile
        var instance: MainExecutor? = null
            get() {
                if (field == null) {
                    synchronized(MainExecutor.javaClass) {
                        if (field == null) {
                            field = MainExecutor()
                        }
                    }
                }
                return field
            }
        private set
    }
}