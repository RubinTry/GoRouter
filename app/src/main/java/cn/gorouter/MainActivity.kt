package cn.gorouter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import cn.gorouter.annotation.Route
import cn.gorouter.api.launcher.GoRouter
import cn.gorouter.api.logger.GoLogger

/**
 * @author logcat
 */
@Route(url = "/main/MainActivity")
class MainActivity : AppCompatActivity(){
    private lateinit var tvLogin : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvLogin = findViewById(R.id.tvLogin)

        val fragment = GoRouter.getInstance()
                .build("app/TestFragment2")
                .fragment

        Log.d("TAG", "onCreate: ${fragment}")
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001) {
            GoLogger.debug("从登录页返回")
        }
    }

    fun toLogin(view: View?) {

        //visit LoginActivity
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            GoRouter.getInstance()
                    .build("/main/Login")
                    .go(this , ActivityOptionsCompat.makeSceneTransitionAnimation(this , tvLogin , tvLogin.transitionName).toBundle())
        }
    }




}