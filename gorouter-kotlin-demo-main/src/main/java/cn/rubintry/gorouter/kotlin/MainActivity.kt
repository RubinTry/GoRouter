package cn.rubintry.gorouter.kotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import cn.gorouter.api.kotlin.core.GoRouter
import cn.rubintry.annotation.kotlin.Route
import cn.rubintry.gorouter_kotlin_demo_lib_common.service.ICommonService


@Route(path = "/Demo/MainActivity")
class MainActivity : AppCompatActivity() {
    private val TAG = this.javaClass.simpleName



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val instance = GoRouter.getInstance().build("/Demo/MFragment").go()
        instance?.let {
            print("Fragment instance ${it.javaClass.name}")
        }


//        val instance = GoRouter.getInstance().build("/Service/TargetService").go(ICommonService::class.java)
//        instance.common(this)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 10){
            Log.d(TAG, "onActivityResult: ")
        }
    }

    fun goToSecond(view: View) {
        GoRouter.getInstance()
                .build("/test/SecondActivity")
                .withString("key" , "value1")
                .go()
    }
}