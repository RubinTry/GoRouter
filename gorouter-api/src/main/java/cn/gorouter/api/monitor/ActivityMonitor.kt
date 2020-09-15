package cn.gorouter.api.monitor

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import android.os.Process
import androidx.fragment.app.FragmentActivity
import java.util.*
import kotlin.system.exitProcess

/**
 * @author logcat
 * Activity监听者
 */
class ActivityMonitor {
    private var activityList: LinkedList<Activity>? = null

    /**
     * 初始化ActivityMonitor并注册生命周期监听器
     *
     * @param application
     */
    fun initialize(application: Application) {
        activityList = LinkedList()
        application.registerActivityLifecycleCallbacks(
                object : ActivityLifecycleCallbacks {
                    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                        activityList!!.addFirst(activity)
                    }

                    override fun onActivityStarted(activity: Activity) {}
                    override fun onActivityResumed(activity: Activity) {}
                    override fun onActivityPaused(activity: Activity) {}
                    override fun onActivityStopped(activity: Activity) {}
                    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
                    override fun onActivityDestroyed(activity: Activity) {
                        activityList!!.remove(activity)
                    }
                }
        )
    }


    /**
     * 获得栈顶Activity
     */
    val lastFragmentActivity: FragmentActivity?
        get() {
            val size = activityList!!.size
            var activity: FragmentActivity? = null
            for (i in size - 1 downTo 0) {
                if (activityList!![i] is FragmentActivity) {
                    activity = activityList!![i] as FragmentActivity
                }
            }
            return activity
        }


    /**
     * 退出应用
     *
     */
    fun exit() {
        FragmentMonitor.instance?.finishAllFragment()
        for (activity in activityList!!) {
            activity.finish()
            activity.overridePendingTransition(0, 0)
        }
//        Process.killProcess(Process.myPid())
    }


    /**
     * 获取ActivityMonitor单例
     */
    companion object {
        @Volatile
        var instance: ActivityMonitor? = null
            get() {
                if (field == null) {
                    synchronized(ActivityMonitor::class.java) {
                        if (field == null) {
                            field = ActivityMonitor()
                        }
                    }
                }
                return field
            }
            private set
    }
}