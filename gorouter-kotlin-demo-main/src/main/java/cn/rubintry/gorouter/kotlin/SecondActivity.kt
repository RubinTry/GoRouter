package cn.rubintry.gorouter.kotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import cn.rubintry.annotation.kotlin.Route


@Route(path = "/test/SecondActivity")
class SecondActivity : AppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        Log.d(TAG, "This is the second page: ")
        val value = intent.getStringExtra("key")
        Log.d(TAG, "extra value is : ${value}")
    }
}