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
import java.lang.IllegalArgumentException
import java.util.*

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
     * Is contains on stack.
     *
     * @param fragment
     * @return
     */
    private fun contains(tag: String): Boolean {
        val fragment = getManager()?.findFragmentByTag(tag)
        return tagList.contains(tag) && fragment != null
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
    fun show(fragment: Fragment) {
        //先隐藏掉上一个显示着的fragment
        if (fragmentSharedCard != null) {
            if (fragmentSharedCard?.isUseDefaultTransition!!) {
                show(fragment, container, true)
            } else {
                show(fragment, container, fragmentSharedCard?.enterTransition!!, fragmentSharedCard?.exitTransition!!)
            }

        } else {
            show(fragment, container, false)
        }

        fragmentSharedCard = null
    }

    private fun hideLast() {
//        if(!tagList.isNullOrEmpty()){
//            val topFragment = getLastFragment()
//            if (topFragment != null) {
//                getManager()?.beginTransaction()?.hide(topFragment)?.commit()
//            }
//        }
//        if(!tagList.isNullOrEmpty()){
//            getManager()?.popBackStackImmediate()
//            tagList.removeLast()
//        }
    }

    private fun getLastFragment(): Fragment ?{
        if(!tagList.isNullOrEmpty()){
            return getManager()?.findFragmentByTag(tagList.last)
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
    private fun show(fragment: Fragment, container: Int, useDefaultTransition: Boolean) {
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

        if(lastFragment != null){
            transaction?.detach(lastFragment)
                    ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        }

        if (notContains(fragment.javaClass.simpleName)) {
            transaction?.add(container, fragment , fragment.javaClass.simpleName)
            tagList.add(fragment.javaClass.simpleName)
        } else {
            transaction?.show(fragment)
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
    private fun show(fragment: Fragment, container: Int, enterTransition: Transition?, exitTransition: Transition?) {
        val manager = getManager()
        val transaction = manager?.beginTransaction()
        val lastFragment = getLastFragment()



        if (fragmentSharedCard != null) {
            transaction?.addSharedElement(fragmentSharedCard?.sharedElement!!, ViewCompat.getTransitionName(fragmentSharedCard?.sharedElement!!)!!)
                    ?.addToBackStack(fragmentSharedCard?.tag!!)

            fragment.sharedElementEnterTransition = enterTransition
            fragment.sharedElementReturnTransition = exitTransition

        }



        if(lastFragment != null){
            transaction?.detach(lastFragment)
                    ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        }

        if (notContains(fragment.javaClass.simpleName)) {
            transaction?.add(container, fragment, fragment.javaClass.simpleName)
            tagList.add(fragment.javaClass.simpleName)
        } else {
            transaction?.show(fragment)
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
            val topElement = getManager()?.findFragmentByTag(tagList.last)
            //last fragment
            var lastFragment : Fragment? = null
            if(tagList.size > 1){
                lastFragment = getManager()?.findFragmentByTag(tagList[tagList.size - 2])
            }
            val manager = getManager()
            var beginTransaction = manager?.beginTransaction()

            if (topElement != null && lastFragment != null && allFragment.indexOf(lastFragment!!) < allFragment.indexOf(topElement)) {
                beginTransaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                beginTransaction?.detach(topElement)
                getManager()?.popBackStackImmediate(topElement.javaClass.simpleName , FragmentManager.POP_BACK_STACK_INCLUSIVE)
                beginTransaction?.attach(lastFragment!!)
            }
            beginTransaction?.commit()
        }
        clearFragmentManager()
        tagList.removeLast()
    }

    fun finishAllFragment() {
        for (tag in tagList) {
            val fragment = getManager()?.findFragmentByTag(tag)
            val beginTransaction = getManager()?.beginTransaction()
            beginTransaction?.remove(fragment!!)?.commit()
        }
    }

    private fun clearFragmentManager() {
        fragmentManager?.unregisterFragmentLifecycleCallbacks(callback)
        fragmentManager = null
    }


    fun canExit(): Boolean {
        return tagList.size == 1
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