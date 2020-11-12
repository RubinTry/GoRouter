package cn.gorouter.activity

import android.widget.TextView
import cn.gorouter.R
import cn.gorouter.annotation.Route


@Route(url = "main/ParamsActivity")
class ParamsActivity : BaseActivity() {
    private lateinit var tvParams: TextView
    override fun bindLayout(): Int {
        return R.layout.activity_params
    }

    override fun initViews() {
        tvParams = findViewById(R.id.tvParams)
        val param = intent.getIntExtra("RouterParam" , 0)
        tvParams.text = "${resources.getString(R.string.find_param)}:$param"

    }
}