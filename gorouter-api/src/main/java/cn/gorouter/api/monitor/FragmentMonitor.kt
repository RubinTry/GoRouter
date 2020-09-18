package cn.gorouter.api.monitor

import android.app.Application
import android.os.Handler
import android.transition.Transition
import android.transition.TransitionInflater
import android.util.Log
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import cn.gorouter.api.card.FragmentSharedCard
import cn.gorouter.api.logger.GoLogger
import cn.gorouter.api.threadpool.DefaultPoolExecutor
import cn.gorouter.api.threadpool.MainExecutor
import java.lang.IllegalArgumentException
import java.util.*
import java.util.concurrent.CountDownLatch
import kotlin.collections.LinkedHashMap

/**
 * @author logcat
 * Fragment监听器
 */
class FragmentMonitor {

    private var container: Int = 0

    private var tagList: LinkedList<String> = LinkedList()
    private var application: Application? = null
    private var fragmentSharedCard: FragmentSharedCard? = null

    private var fragmentManager: FragmentManager? = null


    private val callback: FragmentManager.FragmentLifecycleCallbacks = object : FragmentManager.FragmentLifecycleCallbacks() {

        override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
            super.onFragmentDestroyed(fm, f)
            GoLogger.debug("onFragmentDestroyed: $f")
        }


        override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
            super.onFragmentResumed(fm, f)
            if (GoLogger.isOpen()) {
                GoLogger.info("onFragmentResumed: $f")
            }
        }


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

        if (fragmentManager == null) {
            fragmentManager = ActivityMonitor.instance?.lastFragmentActivity?.supportFragmentManager
            fragmentManager?.registerFragmentLifecycleCallbacks(callback, true)
        } else {
            if (fragmentManager != ActivityMonitor.instance?.lastFragmentActivity?.supportFragmentManager) {
                fragmentManager?.unregisterFragmentLifecycleCallbacks(callback)
                fragmentManager = ActivityMonitor.instance?.lastFragmentActivity?.supportFragmentManager
                fragmentManager?.registerFragmentLifecycleCallbacks(callback, true)
            }
        }

        return fragmentManager;
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
     * 检查fragment的tag是否包含在tagList中
     *
     * @param fragment
     * @return
     */
    private fun contains(tag: String): Boolean {
        val fragment = getManager()?.findFragmentByTag(tag)
        return tagList.contains(tag) && fragment != null
    }


    /**
     * 检查fragment的tag是否不包含在tagList中
     *
     * @param fragment
     * @return
     */
    private fun notContains(tag: String): Boolean {
        return !contains(tag)
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
     * 设置用于显示fragment的容器
     *
     * @param container
     */
    fun setFragmentContainer(container: Int) {
        this.container = container
    }


    /**
     * 替换fragment
     *
     * @param fragment 目标fragment
     * @param container  fragment的容器
     */
    fun show(fragment: Fragment, routeKey: String) {
        //先隐藏掉上一个显示着的fragment
        if (fragmentSharedCard != null) {
            if (fragmentSharedCard?.isUseDefaultTransition!!) {
                show(fragment, container, true, routeKey)
            } else {
                show(fragment, container, fragmentSharedCard?.enterTransition!!, fragmentSharedCard?.exitTransition!!, routeKey)
            }

        } else {
            show(fragment, container, false, routeKey)
        }

        fragmentSharedCard = null
    }


    /**
     * 隐藏掉上一个fragment（如果存在）
     *
     */
    private fun hideLast() {
        if (tagList.isNotEmpty()) {
            val topFragment = getLastFragment()
            if (topFragment != null) {
                getManager()?.beginTransaction()?.hide(topFragment)?.commit()
            }
        }
    }

    private fun getLastFragment(): Fragment? {
        if (tagList.isNotEmpty()) {
            val last = tagList.last
            val lastFragment = getManager()?.findFragmentByTag(last)
            return lastFragment
        }
        return null
    }


    /**
     * 将fragment添加进来并显示
     *
     * @param fragment 目标fragment
     * @param container fragment的容器
     * @param useDefaultTransition 是否使用默认的转场动画
     */
    private fun show(fragment: Fragment, container: Int, useDefaultTransition: Boolean, routeKey: String) {
        val manager = getManager()
        val transaction = manager?.beginTransaction()
        val lastFragment = getLastFragment()
        val tag = routeKey + System.currentTimeMillis()

        transaction?.addToBackStack(tag)
        if (fragmentSharedCard != null) {
            transaction?.addSharedElement(fragmentSharedCard?.sharedElement!!, ViewCompat.getTransitionName(fragmentSharedCard?.sharedElement!!)!!)

            if (useDefaultTransition) {
                fragment.sharedElementEnterTransition = TransitionInflater.from(ActivityMonitor.instance?.lastFragmentActivity).inflateTransition(android.R.transition.move)
                fragment.sharedElementReturnTransition = TransitionInflater.from(ActivityMonitor.instance?.lastFragmentActivity).inflateTransition(android.R.transition.move)
            }

        }

        if (lastFragment != null) {
            transaction?.detach(lastFragment)
                    ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        }

        transaction?.add(container, fragment, tag)
        tagList.add(tag)
        transaction?.commit()
    }


    /**
     * 将fragment添加进来并显示
     *
     * @param fragment 目标fragment
     * @param container fragment的容器
     * @param enterTransition 入场动画
     * @param exitTransition  出场动画
     */
    private fun show(fragment: Fragment, container: Int, enterTransition: Transition?, exitTransition: Transition?, routeKey: String) {
        val manager = getManager()
        val transaction = manager?.beginTransaction()
        val lastFragment = getLastFragment()

        val tag = routeKey + System.currentTimeMillis()

        hideLast()
        transaction?.addToBackStack(tag)
        if (fragmentSharedCard != null) {
            transaction?.addSharedElement(fragmentSharedCard?.sharedElement!!, ViewCompat.getTransitionName(fragmentSharedCard?.sharedElement!!)!!)


            fragment.sharedElementEnterTransition = enterTransition
            fragment.sharedElementReturnTransition = exitTransition

        }



        if (lastFragment != null) {
            transaction?.detach(lastFragment)
                    ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        }

        transaction?.add(container, fragment, tag)
        tagList.add(tag)
        transaction?.commit()
    }


    /**
     * 销毁顶端fragment
     *
     * @param fragment fragment对象
     */
    fun finish() {
        //current fragment
        var topElement: Fragment? = null
        var topKey: String? = null
        //last fragment
        var lastFragment: Fragment? = null
        var lastKey: String? = null
        if (tagList.isEmpty()) {
            throw IllegalArgumentException("Fragments is empty")
        } else {


            if (tagList.isNotEmpty()) {
                topKey = tagList.last()
                topElement = getManager()?.findFragmentByTag(topKey)
            }

            if (tagList.size > 1) {
                lastKey = tagList[tagList.size - 2]
                lastFragment = getManager()?.findFragmentByTag(lastKey)
            }
            val manager = getManager()
            var beginTransaction = manager?.beginTransaction()


            if (topElement != null) {
                beginTransaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                beginTransaction?.detach(topElement)
                getManager()?.popBackStackImmediate(topKey, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                if (lastFragment != null) {
                    beginTransaction?.attach(lastFragment)
                }
            } else if (lastFragment != null) {
                beginTransaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                beginTransaction?.detach(lastFragment!!)
                getManager()?.popBackStackImmediate(lastKey, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }

            beginTransaction?.commit()
        }
        clearFragmentManager()
        tagList.removeLast()
    }


    /**
     * 销毁指定fragment
     *
     * @param fragment
     */
    fun finish(fragment: Fragment) {
        //找到当前fragment的位置

        val index = tagList.indexOf(fragment.tag)
        val currentKey = tagList[index]
        val beginTransaction = getManager()?.beginTransaction()
        beginTransaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
        beginTransaction?.detach(fragment)
        getManager()?.popBackStackImmediate(currentKey, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        if (index > 0) {
            val key = tagList[index - 1]
            val lastFragment = getManager()?.findFragmentByTag(key)
            beginTransaction?.attach(lastFragment!!)
        }
        beginTransaction?.commit()
        clearFragmentManager()
        tagList.remove(currentKey)

        if (tagList.size == 0) {
            ActivityMonitor.instance?.exit()
        }else{
            clearFragmentManager()
        }


    }


    /**
     * 销毁所有fragment
     *
     */
    fun finishAllFragment() {
        val size = tagList.size
        for (index in (size - 1) downTo 0) {
            val key = tagList[index]
            getManager()?.popBackStack(key , FragmentManager.POP_BACK_STACK_INCLUSIVE)
            tagList.remove(key)
        }
        clearFragmentManager()
    }


    /**
     * 销毁FragmentManager
     *
     */
    private fun clearFragmentManager() {
        fragmentManager?.unregisterFragmentLifecycleCallbacks(callback)
        fragmentManager = null
    }


    /**
     * 是否可退出（定义为fragment仅剩一个时，可让APP退出）
     *
     * @return
     */
    fun canExit(): Boolean {
        return tagList.size == 1 && getManager()?.fragments?.size == 1
    }

}