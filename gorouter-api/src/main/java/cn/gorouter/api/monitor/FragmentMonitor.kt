package cn.gorouter.api.monitor

import android.app.Application
import android.os.Handler
import android.transition.Transition
import android.transition.TransitionInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import cn.gorouter.api.card.FragmentSharedCard
import cn.gorouter.api.logger.GoLogger
import cn.gorouter.api.threadpool.MainExecutor
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.collections.LinkedHashMap

/**
 * @author logcat
 * Fragment监听器
 */
class FragmentMonitor {

    private var container: Int = 0

    private var fragmentMap: LinkedHashMap<String , Fragment> = LinkedHashMap()
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
     * Is contains on stack.
     *
     * @param fragment
     * @return
     */
    private fun contains(tag: String): Boolean {
        val fragment = getManager()?.findFragmentByTag(tag)
        return fragmentMap.contains(tag) && fragment != null
    }


    /**
     * Is not contains on stack
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
     * Set the container view of fragment
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
    fun show(fragment: Fragment , routeKey : String) {
        //先隐藏掉上一个显示着的fragment
        if (fragmentSharedCard != null) {
            if (fragmentSharedCard?.isUseDefaultTransition!!) {
                show(fragment, container, true , routeKey)
            } else {
                show(fragment, container, fragmentSharedCard?.enterTransition!!, fragmentSharedCard?.exitTransition!! , routeKey)
            }

        } else {
            show(fragment, container, false , routeKey)
        }

        fragmentSharedCard = null
    }

    private fun hideLast() {
        if(fragmentMap.isNotEmpty()){
            val topFragment = getLastFragment()
            if (topFragment != null) {
                getManager()?.beginTransaction()?.hide(topFragment)?.commit()
            }
        }
    }

    private fun getLastFragment(): Fragment? {
        if (fragmentMap.isNotEmpty()) {
            val key = fragmentMap.keys.last()
            return fragmentMap[key]
        }
        return null
    }


    /**
     * 替换fragment
     *
     * @param fragment 目标fragment
     * @param container fragment的容器
     * @param useDefaultTransition 是否使用默认的转场动画
     */
    private fun show(fragment: Fragment, container: Int, useDefaultTransition: Boolean , routeKey: String) {
        val manager = getManager()
        val transaction = manager?.beginTransaction()
        val lastFragment = getLastFragment()

        if (fragmentSharedCard != null) {
            transaction?.addSharedElement(fragmentSharedCard?.sharedElement!!, ViewCompat.getTransitionName(fragmentSharedCard?.sharedElement!!)!!)
                    ?.addToBackStack(fragmentSharedCard?.tag!!)


            if (useDefaultTransition) {
                fragment.sharedElementEnterTransition = TransitionInflater.from(ActivityMonitor.instance?.lastFragmentActivity).inflateTransition(android.R.transition.move)
                fragment.sharedElementReturnTransition = TransitionInflater.from(ActivityMonitor.instance?.lastFragmentActivity).inflateTransition(android.R.transition.move)
            }

        }

        if (lastFragment != null) {
            transaction?.detach(lastFragment)
                    ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        }

        if (notContains(fragment.javaClass.simpleName)) {
            transaction?.add(container, fragment, routeKey)
            fragmentMap[routeKey] = fragment
        } else {
            transaction?.attach(fragment)
        }
        transaction?.commit()
    }


    /**
     * 替换fragment
     *
     * @param fragment 目标fragment
     * @param container fragment的容器
     * @param enterTransition 入场动画
     * @param exitTransition  出场动画
     */
    private fun show(fragment: Fragment, container: Int, enterTransition: Transition?, exitTransition: Transition? , routeKey: String) {
        val manager = getManager()
        val transaction = manager?.beginTransaction()
        val lastFragment = getLastFragment()

        hideLast()

        if (fragmentSharedCard != null) {
            transaction?.addSharedElement(fragmentSharedCard?.sharedElement!!, ViewCompat.getTransitionName(fragmentSharedCard?.sharedElement!!)!!)
                    ?.addToBackStack(fragmentSharedCard?.tag!!)

            fragment.sharedElementEnterTransition = enterTransition
            fragment.sharedElementReturnTransition = exitTransition

        }



        if (lastFragment != null) {
            transaction?.detach(lastFragment)
                    ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        }

        if (notContains(fragment.javaClass.simpleName)) {
            transaction?.add(container, fragment, routeKey)
            fragmentMap[routeKey] = fragment
        } else {
            transaction?.attach(fragment)
        }
        transaction?.commit()
    }


    /**
     * 根据fragment类名销毁fragment
     *
     * @param fragment fragment对象
     */
    fun finish() {

        var allFragment = getAllFragment()
        if (allFragment.isEmpty()) {
            throw IllegalArgumentException("Fragments is empty")
        } else {

            //current fragment
            var topElement : Fragment ?= null
            var topKey : String ?= null
            if(fragmentMap.isNotEmpty()){
                topElement = fragmentMap.values.last()
                topKey = fragmentMap.keys.last()
            }
            //last fragment
            var lastFragment: Fragment? = null
            var lastKey : String ?= null
            if (fragmentMap.size > 1) {
                lastKey = fragmentMap.keys.elementAt(fragmentMap.size - 2)
                lastFragment = fragmentMap[lastKey]
            }
            val manager = getManager()
            var beginTransaction = manager?.beginTransaction()


            if (topElement != null) {
                beginTransaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                beginTransaction?.detach(topElement)
                getManager()?.popBackStackImmediate(topKey , FragmentManager.POP_BACK_STACK_INCLUSIVE)
                if(lastFragment != null){
                    beginTransaction?.attach(lastFragment)
                }
            }else if(lastFragment != null){
                beginTransaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                beginTransaction?.detach(lastFragment!!)
                getManager()?.popBackStackImmediate(lastKey , FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }

            beginTransaction?.commit()
        }
        clearFragmentManager()
        val last = fragmentMap.keys.last()
        fragmentMap.remove(last)
    }



    fun finish(fragment: Fragment){
        //找到当前fragment的位置
        val index = fragmentMap.values.indexOf(fragment)
        val currentKey = fragmentMap.keys.elementAt(index)
        if(index > 0){
            val beginTransaction = getManager()?.beginTransaction()
            val lastFragment = fragmentMap.values.elementAt(index - 1)
            beginTransaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
            beginTransaction?.detach(fragment)
            getManager()?.popBackStackImmediate(currentKey , FragmentManager.POP_BACK_STACK_INCLUSIVE)
            beginTransaction?.attach(lastFragment)
            beginTransaction?.commit()
        }
        clearFragmentManager()
        fragmentMap.remove(currentKey)
    }



    fun finishAllFragment() {
        val size = fragmentMap.size
        for (index in  (size - 1) downTo 0){
            val key = fragmentMap.keys.elementAt(index)
            val fragment = fragmentMap[key]
            val beginTransaction = getManager()?.beginTransaction()
            beginTransaction?.remove(fragment!!)?.commit()
        }

    }

    private fun clearFragmentManager() {
        fragmentManager?.unregisterFragmentLifecycleCallbacks(callback)
        fragmentManager = null
    }


    fun canExit(): Boolean {
        return fragmentMap.size == 1
    }


    /**
     * 获取栈内所有fragment
     *
     * @return fragment的map容器
     */
    fun getAllFragment(): LinkedHashMap<String , Fragment> {
        return fragmentMap
    }


}