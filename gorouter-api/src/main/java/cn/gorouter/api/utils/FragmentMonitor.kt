package cn.gorouter.api.utils

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import cn.gorouter.api.utils.ActivityMonitor
import java.lang.IllegalArgumentException

/**
 * @author logcat
 * Fragment监听器
 */
class FragmentMonitor {

    private var callback: ActivityLifeCycleCallback ?= null
    private var container: View? = null
    private var pageList: ArrayList<Fragment> = ArrayList()


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
     * 将fragment压入栈中
     *
     * @param fragment  需要被添加的fragment
     * @return
     */
    private fun add(fragment: Fragment): Boolean {
        pageList.add(fragment)
        return true
    }


    /**
     * 跳转至某个fragment
     *
     * @param fragment  目标fragment
     */
    fun jump(fragment: Fragment) {
        jump(fragment, this.container!!)
    }


    /**
     * 跳转至某个fragment
     *
     * @param fragment  目标fragment
     * @param container  盛装fragment的堆栈
     */
    fun jump(fragment: Fragment, container: View) {
        if (container != null) {
            this.container = container
        }
        //进行跳转
        val manager = getManager()
                ?: throw IllegalArgumentException("No activity running!!!")
        val transaction = manager.beginTransaction()

        if (pageList.size == 0) {
            transaction.add(container.id, fragment)
            transaction.commit()
            add(fragment)
            return
        }

        //隐藏除当前fragment外的所有fragment
        for (current in pageList) {
            if (current != fragment) {
                transaction.hide(current)
            }
        }

        if (contains(fragment)) {
            transaction.show(fragment)
        } else {
            transaction.add(container.id, fragment)
            add(fragment)
        }


        transaction.commit()
    }


    /**
     * 栈中是否包含某个fragment
     *
     * @param fragment  被包含的fragment
     * @return
     */
    private fun contains(fragment: Fragment): Boolean {
        for (current in pageList) {
            if (fragment.javaClass.name == current.javaClass.name) {
                return true
            }
        }
        return false
    }


    /**
     * 栈中是否包含某个fragment
     *
     * @param aClass  需要被包含的fragment的类对象
     * @return
     */
    fun contains(aClass: Class<out Fragment?>?): Boolean {
        for (current in pageList) {
            if (current.javaClass == aClass) {
                return true
            }
        }
        return false
    }

    /**
     * 隐藏某个fragment
     *
     * @param aClass  需要被隐藏的fragment的类对象
     */
    fun hide(aClass: Class<out Fragment?>?) {
        val manager = getManager()
                ?: throw IllegalArgumentException("No activity running!!!")
        val transaction = manager.beginTransaction()
        for (current in pageList) {
            if (current.javaClass.name.equals(aClass?.name)) {
                transaction.hide(current)
                transaction.commit()
                break
            }
        }

        jumpToLast()
    }


    /**
     * 隐藏某个fragment
     *
     * @param fragment  需要被隐藏的fragment
     */
    fun hide(fragment: Fragment) {
        if(pageList.isNullOrEmpty()){
            return
        }
        val manager = getManager()
        val transaction = manager?.beginTransaction()
        for (current in pageList) {
            if (current.javaClass.name.equals(fragment.javaClass.name)) {
                transaction?.hide(current)
                break
            }
        }

        transaction?.commit()
    }


    /**
     * 销毁（移除）某个fragment
     *
     * @param fragment 需要被销毁(移除)的fragment
     */
    fun finish(fragment: Fragment) {
        if(pageList.isNullOrEmpty()){
            return
        }

        val manager = getManager()
                ?: throw IllegalArgumentException("No activity running!!!")

        val transaction = manager.beginTransaction()

        if (pageList.contains(fragment)) {
            transaction.remove(fragment).commit()
        }

        pageList.remove(fragment)

        jumpToLast()
    }


    /**
     * 销毁（移除）某个fragment
     *
     * @param aClass  需要被销毁(移除)的fragment的类对象
     */
    fun finish(aClass: Class<out Fragment?>?) {
        val manager = getManager()
        val transaction = manager?.beginTransaction()
        if(!pageList.isNullOrEmpty()){
            for (current in pageList) {
                if (current.javaClass.name == aClass?.name) {
                    transaction?.remove(current)?.commit()
                    pageList.remove(current)
                    break
                }
            }
        }else{
            if(callback != null){
                callback?.noFragmentNow()
            }
        }
    }


    /**
     * 获取fragment管理器
     *
     * @return
     */
    private fun getManager(): FragmentManager? {
        return ActivityMonitor.instance?.lastFragmentActivity?.supportFragmentManager
    }


    /**
     * 跳转至栈中的上一个fragment
     *
     */
    private fun jumpToLast() {
        if (!pageList.isNullOrEmpty()) {
            val manager = getManager()
            val transaction = manager?.beginTransaction()
            transaction?.show(pageList.last())?.commit()
        }else{
            if(callback != null){
                callback?.noFragmentNow()
            }
        }
    }


    /**
     * 栈中的fragment总数
     *
     * @return
     */
    fun getFragmentCount(): Int {
        return pageList.size
    }


    /**
     * 销毁栈顶的fragment
     *
     */
    fun finishLast() {
        if(!pageList.isNullOrEmpty()){
            val fragment = pageList.last()
            if (fragment != null) {
                finish(fragment)
            }

        }

        jumpToLast()
    }


    /**
     * 获得栈中的所有fragment
     *
     * @return
     */
    fun getAllFragments(): List<Fragment> {
        return pageList
    }


    /**
     * 移除栈中的所有fragment
     *
     */
    fun clear() {
        val manager = getManager()
        val transaction = manager?.beginTransaction()
        for (current in pageList) {
            transaction?.remove(current)
        }
        pageList.clear()
    }


    /**
     * 注册fragment回调函数，栈为空时回调callback内的方法
     *
     * @param callback
     */
    fun registerCallback(callback: ActivityLifeCycleCallback) {
        this.callback = callback
    }


    /**
     * 反注册回调函数
     *
     */
    fun unRegisterCallback(){
        if(callback != null){
            callback = null
        }
    }


    /**
     * 无fragment时回调这个接口
     */
    interface ActivityLifeCycleCallback{

        /**
         * 无fragment时回调这个方法
         *
         */
        fun noFragmentNow();
    }

}