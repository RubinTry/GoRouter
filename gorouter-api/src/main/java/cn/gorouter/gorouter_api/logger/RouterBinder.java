package cn.gorouter.gorouter_api.logger;

import android.util.Log;

import cn.gorouter.gorouter_api.launcher.IRouter;
import cn.gorouter.gorouter_api.launcher.Page;
import cn.gorouter.gorouter_api.launcher._GoRouter;

/**
 * @author logcat
 */
public class RouterBinder implements IRouter {
    @Override
    public void put() {
        Log.d("RouterBinder", "put: ");
        _GoRouter.getInstance().put("url" , Page.class , "className");
    }
}
