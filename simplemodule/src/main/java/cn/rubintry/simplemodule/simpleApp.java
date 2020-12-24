package cn.rubintry.simplemodule;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import cn.gorouter.annotation.Route;
import cn.gorouter.api.launcher.GoRouter;


public class simpleApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        GoRouter.setDebugable(true);
        GoRouter.init(this);
    }

}
