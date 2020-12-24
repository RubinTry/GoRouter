package cn.gorouter.activity

import android.view.View
import android.view.WindowManager
import android.widget.Button
import cn.gorouter.R
import cn.gorouter.api.launcher.GoRouter
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity(), View.OnClickListener {


    override fun bindLayout(): Int {
        return R.layout.activity_main
    }

    override fun initViews() {
        btnSimpleJump.setOnClickListener(this)
        btnJumpWithParams.setOnClickListener(this)
        btnGetDataFromAnotherModule.setOnClickListener(this)
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

            R.id.btnGetDataFromAnotherModule -> {

            }
        }
    }


}