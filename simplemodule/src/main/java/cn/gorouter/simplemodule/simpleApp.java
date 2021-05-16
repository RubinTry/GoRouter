package cn.gorouter.simplemodule;

import android.app.Application;

import cn.gorouter.api.launcher.GoRouter;


public class simpleApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        GoRouter.setDebugable(true);
        GoRouter.init(this);
    }

}
