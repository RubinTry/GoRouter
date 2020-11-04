package cn.gorouter;

import android.app.Application;

import androidx.multidex.MultiDexApplication;

import cn.gorouter.api.launcher.GoRouter;


/**
 * @author logcat
 */
public class GoRouterApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        GoRouter.openLog();
        GoRouter.setLogSplitChar("*");
        GoRouter.init(this);
    }
}
