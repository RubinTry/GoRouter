package cn.gorouter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import cn.gorouter.annotation.Route
import cn.gorouter.api.launcher.GoRouter
import cn.gorouter.api.logger.GoLogger
import cn.gorouter.api.monitor.ActivityMonitor
import cn.gorouter.api.monitor.FragmentMonitor

/**
 * @author logcat
 */
@Route(url = "/main/MainActivity")
class MainActivity : AppCompatActivity(){
    private val TAG = this.javaClass.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        var data = Bundle()
        data.putInt("containerId" , R.id.flContainer)
        GoRouter.getInstance().build("app/TestFragment" , data)
                .setFragmentContainer(R.id.flContainer)
                .go()

    }


    override fun onBackPressed() {
        if (FragmentMonitor.instance?.canExit()!!) {
            //强制杀死当前进程
            ActivityMonitor.instance?.exit()
        } else{
            FragmentMonitor.instance?.finish()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001) {
            GoLogger.debug("从登录页返回")
        }
    }

    fun toLogin(view: View?) {

        //visit LoginActivity
        GoRouter.getInstance()
                .build("/main/Login")
                .go(this, ActivityOptionsCompat.makeBasic().toBundle(), 0)
    }




}