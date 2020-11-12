package cn.gorouter.activity

import android.view.View
import android.view.WindowManager
import cn.gorouter.R


class MainActivity : BaseActivity() {
    override fun bindLayout(): Int {
        return R.layout.activity_main
    }

    override fun initViews() {
//全屏显示

        //全屏显示
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        val lp = window.attributes

        //下面图1
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        };
        //下面图2
//        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        //下面图3
//        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT;
        window.attributes = lp;
    }


}