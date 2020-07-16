package cn.gorouter.api.launcher;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import cn.gorouter.api.logger.GoLogger;


/**
 * @author logcat <a href="13857769302@163.com">Contact me.</a>
 * @version 1.0.0
 * @date 2020/07/11 16:25
 */
public class GoRouter {
    private static volatile GoRouter instance;
    private static boolean init;
    private static Context applicationContext;


    private GoRouter() {

    }

    public static GoRouter getInstance() {
        if (instance == null) {
            synchronized (GoRouter.class) {
                if (instance == null) {
                    instance = new GoRouter();
                }
            }
        }
        return instance;
    }


    /**
     * open log printer.
     */
    public synchronized static void openLog() {
        GoLogger.openLog();
    }

    /**
     * Set logger's split char
     *
     * @param splitChar
     */
    public synchronized static void setLogSplitChar(String splitChar) {
        GoLogger.setSplitChar(splitChar);
    }


    /**
     * Initialize GoRouter
     *
     * @param context
     */
    public static void init(Context context) {
        applicationContext = context.getApplicationContext();
        GoLogger.info("GoRouter initialize start.");
        init = _GoRouter.init(context.getApplicationContext());
        GoLogger.info("GoRouter initialize over.");
    }


    /**
     * Build a route
     *
     * @param url
     * @return
     */
    public GoRouter build(String url) {
        build(url, null);
        return this;
    }


    /**
     * Build a route
     *
     * @param url
     * @param data
     * @return
     */
    public GoRouter build(String url, Bundle data) {
        try {
            if (!init) {
                throw new IllegalArgumentException("You haven't initialized yet！");
            }
            _GoRouter.getInstance().build(url, data);
        }catch (Exception e){
            GoLogger.error("Exception happen: " + e);
        }
        return this;
    }

    /**
     * Go to target page.
     */
    public void go() {
        try {
            if (!init) {
                throw new IllegalArgumentException("You haven't initialized yet！");
            }
            _GoRouter.getInstance().go(applicationContext , null);
        } catch (Exception e) {
            GoLogger.error("Exception happen: " + e);
        }
    }


    public void go(Context context, Integer requestCode) {
        try {
            if (!init) {
                throw new IllegalArgumentException("You haven't initialized yet！");
            }
            _GoRouter.getInstance().go(context, requestCode);
        } catch (Exception e) {
            GoLogger.error("Exception happen: " + e);
        }
    }

}
