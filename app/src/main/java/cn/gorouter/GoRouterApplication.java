package cn.gorouter;

import android.app.Application;

import cn.gorouter.api.launcher.GoRouter;


/**
 * @author logcat
 */
public class GoRouterApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        GoRouter.openLog();
        GoRouter.setLogSplitChar("*");
        GoRouter.init(this);
    }
}
