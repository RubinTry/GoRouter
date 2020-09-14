package cn.gorouter.api.threadpool

import androidx.annotation.NonNull
import cn.gorouter.api.logger.GoLogger
import cn.gorouter.api.utils.Const
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger


/**
 * 默认线程工厂
 * @author logcat
 */
class DefaultThreadFactory : ThreadFactory{
    private val poolNumber = AtomicInteger(1)

    private val threadNumber = AtomicInteger(1)
    private var group: ThreadGroup? = null
    private var namePrefix: String? = null


    constructor(){
        val s = System.getSecurityManager()
        group = if (s != null) s.threadGroup else Thread.currentThread().threadGroup
        namePrefix = "GoRouter task pool No." + poolNumber.getAndIncrement() + ", thread No."
    }


    /**
     * 创建新线程
     *
     * @param runnable
     * @return
     */
    override fun newThread(@NonNull runnable: Runnable?): Thread? {
        val threadName = namePrefix + threadNumber.getAndIncrement()
        GoLogger.info(Const.TAG +  "Thread production, name is [$threadName]")
        val thread = Thread(group, runnable, threadName, 0)
        if (thread.isDaemon) {   //设为非后台线程
            thread.isDaemon = false
        }
        if (thread.priority != Thread.NORM_PRIORITY) { //优先级为normal
            thread.priority = Thread.NORM_PRIORITY
        }

        // 捕获多线程处理中的异常
        thread.uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { thread, ex -> GoLogger.info(Const.TAG +"Running task appeared exception! Thread [" + thread.name + "], because [" + ex.message + "]") }
        return thread
    }
}