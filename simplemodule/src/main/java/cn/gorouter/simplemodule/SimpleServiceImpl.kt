package cn.gorouter.simplemodule

import android.content.Context
import android.util.Log
import android.widget.Toast
import cn.gorouter.annotation.Route
import cn.gorouter.demo_lib_common.service.SimpleService


@Route(url = "Simple/SimpleService")
class SimpleServiceImpl: SimpleService {
    override fun simple(context: Context) {
        Toast.makeText(context, context.resources.getString(R.string.you_called_method_simple_in_SimpleServiceImpl), Toast.LENGTH_SHORT).show()
        Log.d("TAG", "simple: ")
    }

    override fun init(context: Context) {
        Log.d("TAG", "SimpleServiceImpl被初始化了: ")
    }
}