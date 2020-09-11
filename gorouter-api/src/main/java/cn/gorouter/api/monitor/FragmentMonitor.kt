package cn.gorouter.api.monitor

import android.app.Application
import android.transition.Transition
import android.transition.TransitionInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import cn.gorouter.api.FragmentInfo
import cn.gorouter.api.card.FragmentSharedCard
import cn.gorouter.api.logger.GoLogger
import java.lang.IllegalArgumentException
import java.util.*

/**
 * @author logcat
 * Fragment监听器
 */
class FragmentMonitor {

    private var container: Int = 0
    private var fragmentStack: Stack<FragmentInfo> = Stack();
    private var application: Application? = null
    private var fragmentSharedCard: FragmentSharedCard? = null

    private var fragmentManager: FragmentManager? = null


    private val callback : FragmentManager.FragmentLifecycleCallbacks = object : FragmentManager.FragmentLifecycleCallbacks() {

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


    private fun addToStack(fragment: Fragment, isShareElement: Boolean , fragmentSharedCard: FragmentSharedCard?) {
        val fragmentInfo = FragmentInfo(fragment, isShareElement, fragmentSharedCard)
        val search = fragmentStack.search(fragmentInfo)
        //If search is -1 , it means this fragment is not on the stack.
        if (search == -1) {
            fragmentStack.push(fragmentInfo)
        }
    }


    /**
     * Is contains on stack.
     *
     * @param fragment
     * @return
     */
    private fun contains(fragment: Fragment): Boolean {
        if (!fragmentStack.empty()) {
            for (fragmentInfo in fragmentStack) {
                if (fragmentInfo?.currentFragment != null && fragmentInfo?.currentFragment.equals(fragment)) {
                    return true;
                }
            }
        }
        return false
    }


    /**
     * Is not contains on stack
     *
     * @param fragment
     * @return
     */
    private fun notContains(fragment: Fragment): Boolean {
        return !contains(fragment)
    }


    /**
     * Get top fragment
     *
     * @return
     */
    fun getTopFragment(): Fragment? {
        if (!fragmentStack.empty()) {
            return fragmentStack.peek()?.currentFragment;
        }
        return null
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
    fun replace(fragment: Fragment) {
        if (fragmentSharedCard != null) {
            if (fragmentSharedCard?.isUseDefaultTransition!!) {
                replace(fragment, container, true)
            } else {
                replace(fragment, container, fragmentSharedCard?.enterTransition!!, fragmentSharedCard?.exitTransition!!)
            }

        } else {
            replace(fragment, container, false)
        }
        if (fragmentSharedCard != null) {
            addToStack(fragment, true , fragmentSharedCard)
        } else {
            addToStack(fragment, false , fragmentSharedCard)
        }

        fragmentSharedCard = null
    }




    /**
     * 替换fragment
     *
     * @param fragment 目标fragment
     * @param container fragment的容器
     * @param useDefaultTransition 是否使用默认的转场动画
     */
    private fun replace(fragment: Fragment, container: Int, useDefaultTransition: Boolean) {
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
        transaction?.replace(container, fragment)?.commit()
    }


    /**
     * 替换fragment
     *
     * @param fragment 目标fragment
     * @param container fragment的容器
     * @param enterTransition 入场动画
     * @param exitTransition  出场动画
     */
    private fun replace(fragment: Fragment, container: Int, enterTransition: Transition?, exitTransition: Transition?) {
        val manager = getManager()
        val transaction = manager?.beginTransaction()

        if (fragmentSharedCard != null) {
            transaction?.addSharedElement(fragmentSharedCard?.sharedElement!!, fragmentSharedCard?.name!!)
                    ?.addToBackStack(fragmentSharedCard?.name!!)

            fragment.sharedElementEnterTransition = enterTransition
            fragment.sharedElementReturnTransition = exitTransition

        }
        transaction?.replace(container, fragment)?.commit()
    }


    /**
     * 根据fragment类名销毁fragment
     *
     * @param fragment fragment对象
     */
    fun finish() {

        if (fragmentStack.isEmpty()) {
            throw IllegalArgumentException("Stack is empty")
        } else {
            val topElement = fragmentStack.peek()
            val manager = topElement.currentFragment.fragmentManager
            val beginTransaction = manager?.beginTransaction()
            if (topElement.isShareElement) {
                manager?.popBackStackImmediate()
                fragmentStack.pop()
                beginTransaction?.remove(topElement.currentFragment)
            }else{

                beginTransaction?.remove(topElement.currentFragment)
                fragmentStack.pop()
                //If stack is not empty , we should show the last fragment.
                if(!fragmentStack.isEmpty()){
                    val lastElement = fragmentStack.peek()
                    beginTransaction?.replace(container , lastElement.currentFragment)
                }
            }
            beginTransaction?.commit()
            topElement.release()
        }
        clearFragmentManager()
    }

    fun finishAllFragment() {
        if(!fragmentStack.isEmpty()){
            val size = fragmentStack.size
            for (index in 0 until size){
                finish()
            }
        }
    }

    private fun clearFragmentManager() {
        fragmentManager?.unregisterFragmentLifecycleCallbacks(callback)
        fragmentManager = null
    }


    /**
     * 获取当前fragment总数
     *
     * @return 返回fragment的个数
     */
    fun getFragmentCount(): Int {
        return fragmentStack.size
    }


    /**
     * 获取栈内所有fragment
     *
     * @return fragment的map容器
     */
    fun getAllFragment(): List<Fragment> {
        return getManager()?.fragments!!
    }




}