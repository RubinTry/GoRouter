package cn.gorouter.gorouter_api.launcher;

import android.app.Application;
import android.os.Bundle;

import cn.gorouter.gorouter_api.logger.GoLogger;


/**
 * @author logcat <a href="13857769302@163.com">Contact me.</a>
 * @version 1.0.0
 * @date 2020/07/11 16:25
 */
public class GoRouter {
    private static volatile GoRouter instance;
    private static boolean init;


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
     * @param splitChar
     */
    public synchronized static void setLogSplitChar(String splitChar) {
        GoLogger.setSplitChar(splitChar);
    }


    /**
     * Initialize GoRouter
     * @param application
     */
    public static void init(Application application) {
        GoLogger.info("GoRouter initialize start.");
        init = _GoRouter.getInstance().init(application);
        GoLogger.info("GoRouter initialize over.");
    }


    /**
     * Build a route
     * @param url
     * @return
     */
    public GoRouter build(String url){
        build(url , null , null);
        return this;
    }




    /**
     * Build a route
     * @param url
     * @param data
     * @return
     */
    public GoRouter build(String url, Bundle data) {
        if(!init){
            throw new IllegalArgumentException("You haven't initialized yet！");
        }
        build(url , data , null);
        return this;
    }


    /**
     * Build a route
     * @param url
     * @param data
     * @param requestCode
     * @return
     */
    public GoRouter build(String url , Bundle data , Integer requestCode){
        if(!init){
            throw new IllegalArgumentException("You haven't initialized yet！");
        }
        _GoRouter.getInstance().build(url , data , requestCode);
        return this;
    }

    /**
     * Go to target page.
     */
    public void go(){
        if(!init){
            throw new IllegalArgumentException("You haven't initialized yet！");
        }
        try {
            _GoRouter.getInstance().go();
        }catch (Exception e){
            GoLogger.error("Exception happen: " + e);
        }
    }

}
