package cn.gorouter.gorouter_api.launcher;

import android.app.Application;
import android.os.Bundle;
import java.util.HashMap;
import java.util.Map;

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
     * 开启日志打印
     */
    public static void openLog() {
        GoLogger.openLog();
    }

    /**
     * 设置Log日志的边框符号
     * @param splitChar
     */
    public static void setLogSplitChar(String splitChar) {
        GoLogger.setSplitChar(splitChar);
    }


    /**
     * 初始化GoRouter
     * @param application
     */
    public static void init(Application application) {
        GoLogger.info("GoRouter init start.");
        init = _GoRouter.getInstance().init(application);
        GoLogger.info("GoRouter init over.");
    }


    /**
     * 构建一个路由
     * @param url
     * @return
     */
    public GoRouter build(String url){
        build(url , null , null);
        return this;
    }




    /**
     * 构建一个路由
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
     * 构建一个路由
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
     * 开始跳转
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
