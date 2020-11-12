package cn.gorouter.activity

import cn.gorouter.R
import cn.gorouter.annotation.Route


@Route(url = "main/SimpleActivity")
class SimpleActivity : BaseActivity() {

    override fun bindLayout(): Int {
        return R.layout.activity_simple
    }

    override fun initViews() {

    }
}