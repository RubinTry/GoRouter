package cn.gorouter.gorouter_api.logger;

import android.util.Log;

import cn.gorouter.gorouter_api.launcher.GoRouter;
import cn.gorouter.gorouter_api.launcher.IRouter;
import cn.gorouter.gorouter_api.launcher.Page;

/**
 * @author logcat
 */
public class RouterBinder implements IRouter {
    @Override
    public void put() {
        Log.d("RouterBinder", "put: ");
        GoRouter.getInstance().put("url" , Page.class , "className");
    }
}
