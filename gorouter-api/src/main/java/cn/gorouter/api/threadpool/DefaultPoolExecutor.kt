package cn.gorouter.api.threadpool

import cn.gorouter.api.logger.GoLogger
import cn.gorouter.api.utils.Consts
import java.util.concurrent.*

/**
 * @author logcat
 * @date 2020/08/19 17:07
 */
class DefaultPoolExecutor private constructor(corePoolSize: Int, maximumPoolSize: Int, keepAliveTime: Long, unit: TimeUnit, workQueue: BlockingQueue<Runnable>, threadFactory: ThreadFactory) : ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, RejectedExecutionHandler { r, executor -> GoLogger.error(Consts.TAG + "Task rejected, too many task!") }) {


    /**
     * 线程执行结束，顺便看一下有没有乱七八糟的异常
     *
     * @param r the runnable that has completed
     * @param t the exception that caused termination, or null if
     */
    override fun afterExecute(r: Runnable, t: Throwable) {
        var t: Throwable? = t
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

    companion object {
        //    Thread args
        private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
        private val INIT_THREAD_COUNT = CPU_COUNT + 1
        private val MAX_THREAD_COUNT = INIT_THREAD_COUNT
        private const val SURPLUS_THREAD_LIFE = 30L

        @Volatile
        var instance: DefaultPoolExecutor? = null
            get() {
                if (null == field) {
                    synchronized(DefaultPoolExecutor::class.java) {
                        if (null == field) {
                            field = DefaultPoolExecutor(
                                    INIT_THREAD_COUNT,
                                    MAX_THREAD_COUNT,
                                    SURPLUS_THREAD_LIFE,
                                    TimeUnit.SECONDS,
                                    ArrayBlockingQueue(64),
                                    DefaultThreadFactory())
                        }
                    }
                }
                return field
            }
            private set
    }


    /**
     * 打印线程堆栈信息
     *
     * @param stackTrace
     * @return
     */
    fun formatStackTrace(stackTrace: Array<StackTraceElement>): String? {
        val sb = StringBuilder()
        for (element in stackTrace) {
            sb.append("    at ").append(element.toString())
            sb.append("\n")
        }
        return sb.toString()
    }
}