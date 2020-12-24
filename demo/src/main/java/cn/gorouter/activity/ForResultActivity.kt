package cn.gorouter.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.gorouter.R
import cn.gorouter.annotation.Route

@Route(url = "main/ForResultActivity")
class ForResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_for_result)
    }
}