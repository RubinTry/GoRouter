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

/**
 * @author logcat
 */
@Route(url = "/main/MainActivity")
class MainActivity : AppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        GoLogger.info("This is main page!")
        val fragment = GoRouter.getInstance().build("app/TestFragment").fragmentInstance
        Log.d(TAG, "onCreate: " + fragment.javaClass.simpleName)
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
                .go(this , ActivityOptionsCompat.makeBasic().toBundle() , 0)
    }
}