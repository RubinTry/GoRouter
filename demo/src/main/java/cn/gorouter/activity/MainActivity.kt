package cn.gorouter.activity

import android.view.View
import android.view.WindowManager
import android.widget.Button
import cn.gorouter.R
import cn.gorouter.api.launcher.GoRouter


class MainActivity : BaseActivity(), View.OnClickListener {

    private lateinit var btnSimpleJump : Button
    private lateinit var btnJumpWithParams : Button

    override fun bindLayout(): Int {
        return R.layout.activity_main
    }

    override fun initViews() {
        btnSimpleJump = findViewById(R.id.btnSimpleJump)
        btnSimpleJump.setOnClickListener(this)
        btnJumpWithParams = findViewById(R.id.btnJumpWithParams)
        btnJumpWithParams.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.btnSimpleJump -> {
                GoRouter.getInstance().build("main/SimpleActivity").go()
            }

            R.id.btnJumpWithParams -> {
                GoRouter.getInstance().build("main/ParamsActivity")
                        .withInt("RouterParam" , 1)
                        .go()
            }
        }
    }


}