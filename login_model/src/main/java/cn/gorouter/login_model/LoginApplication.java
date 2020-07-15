package cn.gorouter.login_model;

import android.app.Application;

import cn.gorouter.gorouter_api.launcher.GoRouter;

public class LoginApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        GoRouter.openLog();
        GoRouter.setLogSplitChar("*");
        GoRouter.init(this);
    }
}
