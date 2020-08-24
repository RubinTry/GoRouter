package cn.gorouter.api.monitor

import android.R
import android.app.Application
import android.transition.Transition
import android.transition.TransitionInflater
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
    private var application: Application? = null
    private var pageContainer: TreeMap<String  , Fragment> = TreeMap()
    private var fragmentSharedCard: FragmentSharedCard? = null


    var fragmentMonitorCallback: FragmentMonitorCallback? = null


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

                override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
                    super.onFragmentDestroyed(fm, f)
                    GoLogger.debug("onFragmentDestroyed: $f")
                    pageContainer.remove(f.javaClass.name)
                }


                override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                    super.onFragmentResumed(fm, f)
                    if(GoLogger.isOpen()){
                        GoLogger.info(getFragmentCount().toString() + "  name: " + f)
                    }
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
     * 将fragment添加进容器中
     *
     * @param fragment
     */
    fun add(fragment: Fragment){
        pageContainer[fragment.javaClass.name] = fragment
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
    fun finish(fragment: Fragment){
        val manager = getManager()

        if(!pageContainer.isNullOrEmpty() && pageContainer.contains(fragment.javaClass.name)){
            manager?.beginTransaction()?.remove(fragment)?.commit()
            pageContainer.remove(fragment.javaClass.name)
        }


    }






    /**
     * 移除栈顶的fragment
     *
     */
    fun finishLast() {

        getManager()?.popBackStack()
        GoLogger.info("GoRouter::Fragment count -----> " + getManager()?.backStackEntryCount)

    }


    /**
     * 获取当前fragment总数
     *
     * @return 返回fragment的个数
     */
    fun getFragmentCount(): Int {
        return pageContainer.size
    }


    /**
     * 获取栈内所有fragment
     *
     * @return fragment的map容器
     */
    fun getAllFragment() : List<Fragment>{
        return getManager()?.fragments!!
    }


    /**
     * activity中无fragment时，回调此接口
     *
     */
    interface FragmentMonitorCallback {
        fun noFragment();
    }



}