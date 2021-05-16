package cn.gorouter.api.kotlin.thread

import android.os.Handler
import android.os.Looper
import cn.gorouter.api.kotlin.Const
import cn.gorouter.api.kotlin.core.Logger
import java.util.concurrent.*

class MainExecutor(corePoolSize: Int, maximumPoolSize: Int, keepAliveTime: Long, unit: TimeUnit?, workQueue: BlockingQueue<Runnable>?, threadFactory: ThreadFactory?) : ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory) {
    private val handler = Handler(Looper.getMainLooper())
    override fun execute(command: Runnable?) {
        checkNotNull(command)
        handler.post(command)
    }

    companion object {
        private var CPU_COUNT: Int = Runtime.getRuntime().availableProcessors()
        private var INIT_THREAD_COUNT: Int = CPU_COUNT + 1
        private var MAX_THREAD_COUNT: Int = INIT_THREAD_COUNT
        private var SURPLUS_THREAD_LIFE: Long = 30L

        @Volatile
        private var instance: MainExecutor? = null


        @JvmStatic
        fun getInstance(): MainExecutor{
            if(instance == null){
                synchronized(MainExecutor::class.java){
                    if(instance == null){
                        instance = MainExecutor(INIT_THREAD_COUNT, MAX_THREAD_COUNT, SURPLUS_THREAD_LIFE, TimeUnit.SECONDS, ArrayBlockingQueue<Runnable>(64), DefaultThreadFactory())
                    }
                }
            }
            return instance!!
        }
    }


    /**
     * 线程执行结束，顺便看一下有没有乱七八糟的异常
     *
     * @param r the runnable that has completed
     * @param t the exception that caused termination, or null if
     */
    override fun afterExecute(r: Runnable?, t: Throwable?) {
        var t = t
        super.afterExecute(r, t)
        if (t == null && r is Future<*>) {
            try {
                (r as Future<*>).get()
            } catch (ce: CancellationException) {
                t = ce
            } catch (ee: ExecutionException) {
                t = ee.cause
            } catch (ie: InterruptedException) {
                Thread.currentThread().interrupt() // ignore/reset
            }
        }
        if (t != null) {
            Logger.warn(Const.TAG + """
     Running task appeared exception! Thread [${Thread.currentThread().name}], because [${t.message}]
     ${formatStackTrace(t.stackTrace)}
     """.trimIndent())
        }
    }


    /**
     * 开启线程堆栈打印
     *
     * @param stackTrace
     * @return
     */
    private fun formatStackTrace(stackTrace: Array<StackTraceElement>): String? {
        val sb = StringBuilder()
        for (element in stackTrace) {
            sb.append("    at ").append(element.toString())
            sb.append("\n")
        }
        return sb.toString()
    }
}