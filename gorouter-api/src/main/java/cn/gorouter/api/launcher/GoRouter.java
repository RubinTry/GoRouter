package cn.gorouter.api.launcher;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.transition.Transition;
import android.view.View;

import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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


    /**
     * 获取GoRouter单例
     * @return
     */
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
     * 开启日志打印
     */
    public synchronized static void openLog() {
        GoLogger.openLog();
    }

    /**
     * Set logger's split char
     * 设置日志打印边框
     * @param splitChar
     */
    public static void setLogSplitChar(String splitChar) {
        GoLogger.setSplitChar(splitChar);
    }


    /**
     * Initialize GoRouter
     * 初始化GoRouter
     * @param application
     */
    public static void init(Application application) {
        applicationContext = application.getApplicationContext();
        GoLogger.info("GoRouter initialize start.");
        init = _GoRouter.init(application);
        GoLogger.info("GoRouter initialize over.");
    }


    /**
     * Build a route
     * 通过路由键创建一个路由
     * @param url
     * @return
     */
    public GoRouter build(String url) {
        build(url, null);
        return this;
    }


    /**
     * Build a route
     * 通过路由键创建一个路由
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
        } catch (Exception e) {
            GoLogger.error("Exception happen: " + e);
        }
        return this;
    }


    /**
     * 添加共享元素信息
     * @param element  需要添加共享元素效果的视图
     * @param name  视图名称
     * @param backStackTAG  返回栈tag
     * @param containerId fragment的容器的id
     * @return
     */
    public GoRouter addSharedFragment(View element , String name , String backStackTAG , int containerId){
        addSharedFragment(element , name , backStackTAG , containerId , false);
        return this;
    }


    /**
     * 添加共享元素信息
     * @param element 需要添加共享元素效果的视图
     * @param name 视图名称
     * @param backStackTAG 返回栈tag
     * @param containerId fragment的容器的id
     * @param useDefaultTransition 是否使用默认的转场动画
     * @return
     */
    public GoRouter addSharedFragment(View element , String name , String backStackTAG , int containerId , boolean useDefaultTransition){
        _GoRouter.getInstance().addSharedFragment(element , name , backStackTAG , containerId , useDefaultTransition);
        return this;
    }

    /**
     * 为共享元素添加自定义转场动画
     * @param enterTransition
     * @param exitTransition
     * @return
     */
    public GoRouter addTransition(Transition enterTransition , Transition exitTransition) {
        _GoRouter.getInstance().addTransition(enterTransition , exitTransition);
        return this;
    }


    /**
     * 设置fragment的容器
     * @param fragmentContainer
     * @return
     */
    public GoRouter setFragmentContainer(int fragmentContainer){
        _GoRouter.getInstance().setFragmentContainer(fragmentContainer);
        return this;
    }

    /**
     * Go to target page.
     * 通过先前设置好的路由键访问具体页面
     */
    public void go() {
        try {
            if (!init) {
                throw new IllegalArgumentException("You haven't initialized yet！");
            }
            _GoRouter.getInstance().go(applicationContext, null , null);
        } catch (Exception e) {
            GoLogger.error("Exception happen: " + e);
        }
    }


    /**
     * 通过先前设置好的路由键访问具体页面
     * @param context  当前页面上下文
     * @param requestCode  页面返回时回调的请求码
     */
    public void go(Context context, Integer requestCode) {
        try {
            if (!init) {
                throw new IllegalArgumentException("You haven't initialized yet！");
            }
            _GoRouter.getInstance().go(context, requestCode , null);
        } catch (Exception e) {
            GoLogger.error("Exception happen: " + e);
        }
    }


    /**
     * 通过先前设置好的路由键访问具体页面
     * @param options 这是一个配置项，用来决定如何启动activity
     */
    public void go(@Nullable Bundle options) {
        go(applicationContext , options , null);
    }


    /**
     * 通过先前设置好的路由键访问具体页面
     * @param context  当前页面上下文
     * @param options   这是一个配置项，用来决定如何启动activity
     * @param requestCode  页面返回时回调的请求码
     */
    public void go(Context context , @Nullable Bundle options , Integer requestCode){
        try {
            if (!init) {
                throw new IllegalArgumentException("You haven't initialized yet！");
            }
            _GoRouter.getInstance().go(context, requestCode , options);
        } catch (Exception e) {
            GoLogger.error("Exception happen: " + e);
        }
    }



}
