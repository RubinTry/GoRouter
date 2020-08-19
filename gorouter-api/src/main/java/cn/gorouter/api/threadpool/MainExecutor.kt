package cn.gorouter.api.threadpool

import android.os.Handler
import android.os.Looper
import cn.gorouter.api.logger.GoLogger
import cn.gorouter.api.utils.Consts
import java.util.concurrent.*

class MainExecutor(corePoolSize: Int, maximumPoolSize: Int, keepAliveTime: Long, unit: TimeUnit?, workQueue: BlockingQueue<Runnable>?, threadFactory: ThreadFactory?) : ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory) {
    private val handler = Handler(Looper.getMainLooper())
    override fun execute(command: Runnable?) {
        handler.post(command)
    }

    companion object {
        var CPU_COUNT: Int = Runtime.getRuntime().availableProcessors()
        var INIT_THREAD_COUNT: Int = CPU_COUNT + 1
        var MAX_THREAD_COUNT: Int = INIT_THREAD_COUNT
        var SURPLUS_THREAD_LIFE: Long = 30L

        @Volatile
        var instance: MainExecutor? = null
            get() {
                if (field == null) {
                    synchronized(MainExecutor.javaClass) {
                        if (field == null) {
                            field = MainExecutor(INIT_THREAD_COUNT, MAX_THREAD_COUNT, SURPLUS_THREAD_LIFE, TimeUnit.SECONDS, ArrayBlockingQueue<Runnable>(64), DefaultThreadFactory())
                        }
                    }
                }
                return field
            }
            private set
    }


    /*
     *  线程执行结束，顺便看一下有么有什么乱七八糟的异常
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
            GoLogger.warn(Consts.TAG + """
     Running task appeared exception! Thread [${Thread.currentThread().name}], because [${t.message}]
     ${formatStackTrace(t.stackTrace)}
     """.trimIndent())
        }
    }


    fun formatStackTrace(stackTrace: Array<StackTraceElement>): String? {
        val sb = StringBuilder()
        for (element in stackTrace) {
            sb.append("    at ").append(element.toString())
            sb.append("\n")
        }
        return sb.toString()
    }
}