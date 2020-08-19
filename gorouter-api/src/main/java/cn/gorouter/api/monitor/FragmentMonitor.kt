package cn.gorouter.api.monitor

import android.R
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionInflater
import android.util.Log
import android.view.FrameStats
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import cn.gorouter.api.card.FragmentSharedCard
import cn.gorouter.api.logger.GoLogger
import java.util.*

/**
 * @author logcat
 * Fragment监听器
 */
class FragmentMonitor {

    private var callCount: Int = 0
    private val TAG: String = this.javaClass.simpleName
    private var application: Application? = null
    private var container: Int? = null
    private var pageList: LinkedList<Fragment> = LinkedList()
    private var fragmentSharedCard: FragmentSharedCard? = null
        get() {
            return field
        }


    var fragmentMonitorCallback: FragmentMonitorCallback? = null
        get() {
            return field
        }
        set(value) {
            field = value
        }


    /**
     * 初始化FragmentMonitor
     *
     * @param application
     */
    fun initialize(application: Application) {
        this.application = application

    }


    /**
     * 获取FragmentManager，同时注册fragment全局监听器（仅注册一次）
     *
     * @return
     */
    private fun getManager(): FragmentManager? {
        val manager = ActivityMonitor.instance?.lastFragmentActivity?.supportFragmentManager
        if (callCount == 0) {
            manager?.registerFragmentLifecycleCallbacks(object : FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentPreAttached(fm: FragmentManager, f: Fragment, context: Context) {
                    super.onFragmentPreAttached(fm, f, context)
                    Log.d(TAG, "onFragmentPreAttached: ")
                }

                override fun onFragmentAttached(fm: FragmentManager, f: Fragment, context: Context) {
                    super.onFragmentAttached(fm, f, context)
                    Log.d(TAG, "onFragmentAttached: ")
                }

                override fun onFragmentPreCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
                    super.onFragmentPreCreated(fm, f, savedInstanceState)
                    Log.d(TAG, "onFragmentPreCreated: ")
                }


                override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
                    super.onFragmentCreated(fm, f, savedInstanceState)
                    Log.d(TAG, "onFragmentCreated: ")
                    pageList.add(f)
                }


                override fun onFragmentActivityCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
                    super.onFragmentActivityCreated(fm, f, savedInstanceState)
                    Log.d(TAG, "onFragmentActivityCreated: ")
                }


                override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
                    super.onFragmentViewCreated(fm, f, v, savedInstanceState)
                    Log.d(TAG, "onFragmentViewCreated: ")
                }

                override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
                    super.onFragmentStarted(fm, f)
                    Log.d(TAG, "onFragmentStarted: ")
                }

                override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                    super.onFragmentResumed(fm, f)
                    Log.d(TAG, "onFragmentResumed: ")
                }

                override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
                    super.onFragmentPaused(fm, f)
                    Log.d(TAG, "onFragmentPaused: ")
                }


                override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
                    super.onFragmentStopped(fm, f)
                    Log.d(TAG, "onFragmentStopped: ")
                }

                override fun onFragmentSaveInstanceState(fm: FragmentManager, f: Fragment, outState: Bundle) {
                    super.onFragmentSaveInstanceState(fm, f, outState)
                    Log.d(TAG, "onFragmentSaveInstanceState: ")
                }

                override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
                    super.onFragmentViewDestroyed(fm, f)
                    Log.d(TAG, "onFragmentViewDestroyed: ")
                }

                override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
                    super.onFragmentDestroyed(fm, f)
                    Log.d(TAG, "onFragmentDestroyed: ")
                    pageList.remove(f)
                }

                override fun onFragmentDetached(fm: FragmentManager, f: Fragment) {
                    super.onFragmentDetached(fm, f)
                    Log.d(TAG, "onFragmentDetached: ")
                }

            }, true)
            callCount++
        }

        return manager;
    }

    /**
     * 获取FragmentMonitor单例
     */
    companion object {
        @Volatile
        var instance: FragmentMonitor? = null
            get() {
                if (field == null) {
                    synchronized(FragmentMonitor::class.java) {
                        if (field == null) {
                            field = FragmentMonitor()
                        }
                    }
                }
                return field
            }
            private set
    }


    /**
     * 设置共享元素相关信息
     *
     * @param fragmentSharedCard
     * @return
     */
    fun setFragmentSharedCard(fragmentSharedCard: FragmentSharedCard?): FragmentMonitor {
        this.fragmentSharedCard = fragmentSharedCard
        return this
    }


    /**
     * 替换fragment
     *
     * @param fragment 目标fragment
     * @param container  fragment的容器
     */
    fun replace(fragment: Fragment, container: Int) {
        if (fragmentSharedCard != null) {
            if (fragmentSharedCard?.isUseDefaultTransition!!) {
                replace(fragment, container, true)
            } else {
                replace(fragment, container, fragmentSharedCard?.enterTransition!!, fragmentSharedCard?.exitTransition!!)
            }
        } else {
            replace(fragment, container, false)
        }
    }


    /**
     * 替换fragment
     *
     * @param fragment 目标fragment
     * @param container fragment的容器
     * @param useDefaultTransition 是否使用默认的转场动画
     */
    fun replace(fragment: Fragment, container: Int, useDefaultTransition: Boolean) {
        val manager = getManager()
        val transaction = manager?.beginTransaction()

        if (fragmentSharedCard != null) {
            transaction?.addSharedElement(fragmentSharedCard?.sharedElement!!, fragmentSharedCard?.name!!)
                    ?.addToBackStack(fragmentSharedCard?.name!!)

            if (useDefaultTransition) {
                fragment.sharedElementEnterTransition = TransitionInflater.from(ActivityMonitor.instance?.lastFragmentActivity).inflateTransition(android.R.transition.move)
                fragment.sharedElementReturnTransition = TransitionInflater.from(ActivityMonitor.instance?.lastFragmentActivity).inflateTransition(android.R.transition.move)
            }

        }
        transaction?.replace(container!!, fragment)?.commit()
    }


    /**
     * 替换fragment
     *
     * @param fragment 目标fragment
     * @param container fragment的容器
     * @param enterTransition 入场动画
     * @param exitTransition  出场动画
     */
    fun replace(fragment: Fragment, container: Int, enterTransition: Transition?, exitTransition: Transition?) {
        val manager = getManager()
        val transaction = manager?.beginTransaction()

        if (fragmentSharedCard != null) {
            transaction?.addSharedElement(fragmentSharedCard?.sharedElement!!, fragmentSharedCard?.name!!)
                    ?.addToBackStack(fragmentSharedCard?.name!!)

            fragment.sharedElementEnterTransition = enterTransition
            fragment.sharedElementReturnTransition = exitTransition

        }
        transaction?.replace(container!!, fragment)?.commit()
    }


    /**
     * 移除栈顶的fragment
     *
     */
    fun finishLast() {

        if (!pageList.isNullOrEmpty()) {
            pageList.removeLast()
            getManager()?.popBackStack()
        }


        if (pageList.isNullOrEmpty()) {
            if (fragmentMonitorCallback != null) {
                fragmentMonitorCallback?.noFragment()
            }
        }
    }


    /**
     * 获取当前fragment总数
     *
     * @return
     */
    fun getFragmentCount(): Int {
        return pageList.size
    }


    /**
     * activity中无fragment时，回调此接口
     *
     */
    interface FragmentMonitorCallback {
        fun noFragment();
    }
}