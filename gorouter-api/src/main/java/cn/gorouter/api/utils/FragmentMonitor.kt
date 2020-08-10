package cn.gorouter.api.utils

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import cn.gorouter.api.utils.ActivityMonitor
import java.lang.IllegalArgumentException

/**
 * @author logcat
 */
class FragmentMonitor {

    private var callback: ActivityLifeCycleCallback ?= null
    private var container: View? = null
    private var pageList: ArrayList<Fragment> = ArrayList()


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


    private fun add(fragment: Fragment): Boolean {
        pageList.add(fragment)
        return true
    }


    fun jump(fragment: Fragment) {
        jump(fragment, this.container!!)
    }

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

    private fun contains(fragment: Fragment): Boolean {
        for (current in pageList) {
            if (fragment.javaClass.name == current.javaClass.name) {
                return true
            }
        }
        return false
    }


    fun contains(aClass: Class<out Fragment?>?): Boolean {
        for (current in pageList) {
            if (current.javaClass == aClass) {
                return true
            }
        }
        return false
    }

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

    private fun getManager(): FragmentManager? {
        return ActivityMonitor.instance?.lastFragmentActivity?.supportFragmentManager
    }

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


    fun getFragmentCount(): Int {
        return pageList.size
    }

    fun finishLast() {
        if(!pageList.isNullOrEmpty()){
            val fragment = pageList.last()
            if (fragment != null) {
                finish(fragment)
            }

        }

        jumpToLast()
    }

    fun getAllFragments(): List<Fragment> {
        return pageList
    }

    fun clear() {
        val manager = getManager()
        val transaction = manager?.beginTransaction()
        for (current in pageList) {
            transaction?.remove(current)
        }
        pageList.clear()
    }

    fun registerCallback(callback: ActivityLifeCycleCallback) {
        this.callback = callback
    }

    fun unRegisterCallback(){
        if(callback != null){
            callback = null
        }
    }


    /**
     * 无fragment时回调这个方法
     */
    interface ActivityLifeCycleCallback{
        fun noFragmentNow();
    }

}