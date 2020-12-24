package cn.gorouter.activity

import android.content.Intent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import cn.gorouter.R
import cn.gorouter.api.launcher.GoRouter
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity(), View.OnClickListener {


    private val REQUEST_CODE: Int = 10001

    override fun bindLayout(): Int {
        return R.layout.activity_main
    }

    override fun initViews() {
        btnSimpleJump.setOnClickListener(this)
        btnJumpWithParams.setOnClickListener(this)
        btnGetDataFromAnotherModule.setOnClickListener(this)
        btnForResult.setOnClickListener(this)
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
                GoRouter.getInstance().build("main/SimpleActivity").go()
            }

            R.id.btnForResult -> {
                GoRouter.getInstance().build("main/ForResultActivity").go(this , REQUEST_CODE)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQUEST_CODE -> {
                Toast.makeText(this.applicationContext, resources.getString(R.string.back_from_page_for_result), Toast.LENGTH_SHORT).show()
            }
        }
    }

}