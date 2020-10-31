package cn.gorouter.api.launcher;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.transition.Transition;
import android.view.View;

import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.Nullable;

import cn.gorouter.api.logger.GoLogger;


/**
 * @author logcat <a href="13857769302@163.com">Contact me.</a>
 * @version 1.0.10
 * @since 2020/07/11 16:25
 */
public class GoRouter {
    private static volatile GoRouter instance;
    private static boolean init;
    private static Context applicationContext;


    private GoRouter() {

    }


    /**
     * 获取GoRouter单例
     *
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
     *
     * @param splitChar
     */
    public static void setLogSplitChar(String splitChar) {
        GoLogger.setSplitChar(splitChar);
    }


    /**
     * Initialize GoRouter
     * 初始化GoRouter
     *
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
     * 通过路由键创建一个路由
     *
     * @param url
     * @param extra
     * @return
     */
    public GoRouter build(String url, Bundle extra) {
        try {
            if (!init) {
                throw new IllegalArgumentException("You haven't initialized yet！");
            }
            _GoRouter.getInstance().build(url, extra);
        } catch (Exception e) {
            GoLogger.error("Exception happen: " + e);
        }
        return this;
    }


    /**
     * 添加共享元素信息
     *
     * @param element      需要添加共享元素效果的视图
     * @param name         视图名称
     * @param backStackTAG 返回栈tag
     * @return
     */
    public GoRouter addSharedFragment(View element, String name, String backStackTAG) {
        addSharedFragment(element, backStackTAG, false);
        return this;
    }


    /**
     * 添加共享元素信息
     *
     * @param element              需要添加共享元素效果的视图
     * @param backStackTAG         返回栈tag
     * @param useDefaultTransition 是否使用默认的转场动画
     * @return
     */
    public GoRouter addSharedFragment(View element, String backStackTAG, boolean useDefaultTransition) {
        _GoRouter.getInstance().addSharedFragment(element, backStackTAG, useDefaultTransition);
        return this;
    }

    /**
     * 为共享元素添加自定义转场动画
     *
     * @param enterTransition
     * @param exitTransition
     * @return
     */
    public GoRouter addTransition(Transition enterTransition, Transition exitTransition) {
        _GoRouter.getInstance().addTransition(enterTransition, exitTransition);
        return this;
    }


    /**
     * 设置fragment的容器
     *
     * @param containerId
     * @return
     */
    public GoRouter withContainer(int containerId) {
        _GoRouter.getInstance().setFragmentContainerId(containerId);
        return this;
    }


    /**
     * 设置需要携带的数据
     *
     * @param extra 数据Bundle
     * @return
     */
    public GoRouter withExtra(Bundle extra) {
        _GoRouter.getInstance().withExtra(extra);
        return this;
    }


    /**
     * 携带整型数据
     *
     * @param key
     * @param intValue
     * @return
     */
    public GoRouter withInt(String key, int intValue) {
        _GoRouter.getInstance().withInt(key, intValue);
        return this;
    }


    /**
     * 携带单精度浮点型数据
     *
     * @param key
     * @param floatValue
     * @return
     */
    public GoRouter withFloat(String key, float floatValue) {
        _GoRouter.getInstance().withFloat(key, floatValue);
        return this;
    }


    /**
     * 携带双精度浮点型数据
     *
     * @param key
     * @param doubleValue
     * @return
     */
    public GoRouter withDouble(String key, double doubleValue) {
        _GoRouter.getInstance().withDouble(key, doubleValue);
        return this;
    }


    /**
     * 携带长整型数据
     *
     * @param key
     * @param longValue
     * @return
     */
    public GoRouter withLong(String key, long longValue) {
        _GoRouter.getInstance().withLong(key, longValue);
        return this;
    }


    /**
     * 携带短整型数据
     *
     * @param key
     * @param shortValue
     * @return
     */
    public GoRouter withShort(String key, short shortValue) {
        _GoRouter.getInstance().withShort(key, shortValue);
        return this;
    }


    /**
     * 携带字符串数据
     *
     * @param key
     * @param stringValue
     * @return
     */
    public GoRouter withString(String key, String stringValue) {
        _GoRouter.getInstance().withString(key, stringValue);
        return this;
    }


    /**
     * 携带字符序列数据
     *
     * @param key
     * @param charSequenceValue
     * @return
     */
    public GoRouter withCharSequence(String key, CharSequence charSequenceValue) {
        _GoRouter.getInstance().withCharSequence(key, charSequenceValue);
        return this;
    }


    public Fragment getFragment() {
        return _GoRouter.getInstance().getFragmentInstance();
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
            _GoRouter.getInstance().go(applicationContext, null, null);
        } catch (Exception e) {
            GoLogger.error("Exception happen: " + e);
        }
    }


    /**
     * 通过先前设置好的路由键访问具体页面
     *
     * @param context     当前页面上下文
     * @param requestCode 页面返回时回调的请求码
     */
    public void go(Context context, Integer requestCode) {
        try {
            if (!init) {
                throw new IllegalArgumentException("You haven't initialized yet！");
            }
            _GoRouter.getInstance().go(context, requestCode, null);
        } catch (Exception e) {
            GoLogger.error("Exception happen: " + e);
        }
    }

    /**
     * 通过先前设置好的路由键访问具体页面
     *
     * @param context 当前页面上下文
     * @param options 这是一个配置项，用来决定如何启动activity
     */
    public void go(Context context, @Nullable Bundle options) {
        go(context, options, null);
    }

    /**
     * 通过先前设置好的路由键访问具体页面
     *
     * @param context     当前页面上下文
     * @param options     这是一个配置项，用来决定如何启动activity
     * @param requestCode 页面返回时回调的请求码
     */
    public void go(Context context, @Nullable Bundle options, Integer requestCode) {
        try {
            if (!init) {
                throw new IllegalArgumentException("You haven't initialized yet！");
            }
            _GoRouter.getInstance().go(context, requestCode, options);
            if (context instanceof Activity) {
                ((Activity) context).overridePendingTransition(0, 0);
            }
        } catch (Exception e) {
            GoLogger.error("Exception happen: " + e);
        }
    }


}
