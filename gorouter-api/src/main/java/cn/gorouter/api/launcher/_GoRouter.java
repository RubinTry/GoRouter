package cn.gorouter.api.launcher;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Transition;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.gorouter.api.card.FragmentSharedCard;
import cn.gorouter.api.logger.GoLogger;
import cn.gorouter.api.monitor.FragmentMonitor;
import cn.gorouter.api.threadpool.MainExecutor;
import cn.gorouter.api.monitor.ActivityMonitor;
import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;

import static cn.gorouter.api.launcher._GoRouter.TypeKind.ACTIVITY;
import static cn.gorouter.api.launcher._GoRouter.TypeKind.FRAGMENT;
import static cn.gorouter.api.launcher._GoRouter.TypeKind.FRAGMENT_IN_APP_PACKAGE;

/**
 * @author logcat <a href="13857769302@163.com">Contact me.</a>
 * @version 1.0.0
 * @date 2020/07/11 16:25
 */
public class _GoRouter {
    private static volatile _GoRouter instance;
    private String currentUrl;
    private Bundle currentData;
    private Map<String, Class> nodeTargetContainer;
    private static Context mContext;
    private static ClassLoader mCurrentClassLoader;
    private FragmentSharedCard mFragmentSharedCard;


    static {
        mCurrentClassLoader = Thread.currentThread().getContextClassLoader();
    }

    private int container;


    private _GoRouter() {
        nodeTargetContainer = new HashMap<>();
    }

    /**
     * 获得_GoRouter单例
     *
     * @return
     */
    public static _GoRouter getInstance() {
        if (instance == null) {
            synchronized (_GoRouter.class) {
                if (instance == null) {
                    instance = new _GoRouter();
                }
            }
        }
        return instance;
    }


    /**
     * 初始化_GoRouter
     *
     * @param application
     * @return
     */
    public synchronized static boolean init(Application application) {
        mContext = application.getApplicationContext();
        ActivityMonitor.Companion.getInstance().initialize(application);
        FragmentMonitor.Companion.getInstance().initialize(application);
        return initAllRoute(mContext);
    }


    /**
     * Initialize all route and put them into container.
     * 初始化_GoRouter并且将所有节点添加进容器
     *
     * @param context
     * @return
     */
    private static synchronized boolean initAllRoute(Context context) {
        List<Class> classNames = getClasses(context.getApplicationContext(), "cn.gorouter.route");
        for (Class aClass : classNames) {

            try {
                if (IRouter.class.isAssignableFrom(aClass)) {
                    IRouter iRouter = (IRouter) aClass.newInstance();
                    iRouter.put();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }


    /**
     * Scan all class names under the specified package name.
     * 扫描某个包名下的所有类
     *
     * @param context
     * @param packageName Witch package we want scan.
     * @return
     */
    private static List<Class> getClasses(Context context, String packageName) {
        List<Class> classList = new ArrayList<>();
        String path = null;

        try {
            path = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0).sourceDir;
            DexFile dexFile = new DexFile(path);
            PathClassLoader pathClassLoader = new PathClassLoader(path, mCurrentClassLoader);
            Enumeration entries = dexFile.entries();
            while (entries.hasMoreElements()) {
                String name = (String) entries.nextElement();
                if (name.contains(packageName)) {
                    Class aClass = pathClassLoader.loadClass(name);
                    classList.add(aClass);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classList;
    }


    /**
     * Build a route
     * 通过路由键构建一个路由
     *
     * @param url  route url address
     * @param data The data that needs to be passed to the target page
     */
    public void build(String url, Bundle data) {
        this.currentUrl = url;
        this.currentData = data;
    }


    /**
     * Go to target page.
     * 通过路由键访问具体页面
     */
    public void go(Context context, Integer requestCode, @Nullable Bundle options) throws NullPointerException {
        Context currentContext = null == context ? mContext : context;
        if (currentUrl == null) {
            throw new IllegalArgumentException("Please set currentUrl");
        }

        if (nodeTargetContainer == null) {
            throw new NullPointerException("container is empty!!!");
        }

        //Get all the node and type classes.
        Class nodeTarget = nodeTargetContainer.get(currentUrl);

        if (nodeTarget != null) {
            if (Activity.class.isAssignableFrom(nodeTarget)) {
                //If the node type is Activity,the jump is made in the form of Activity.
                go(currentContext, requestCode, ACTIVITY, nodeTarget, options);
            } else if (Fragment.class.isAssignableFrom(nodeTarget)) {
                //If the node type is Fragment,the jump is made in the form of Fragment.
                go(currentContext, requestCode, FRAGMENT, nodeTarget, options);
            } else if(android.app.Fragment.class.isAssignableFrom(nodeTarget)){
                //If the node type is Fragment in package app,we should go to fragment
                go(currentContext , requestCode , FRAGMENT_IN_APP_PACKAGE , nodeTarget , options);
            }
        } else {
            throw new NullPointerException("route \"" + currentUrl + "\" is not found!!!");
        }


    }


    /**
     * 为fragment添加共享元素以便在跳转时自动携带炫酷动画
     * @param element  需要添加共享元素效果的视图
     * @param name  视图名称
     * @param backStackTAG  返回栈tag
     * @param containerId   容器的视图id
     * @param useDefaultTransition  是否启用默认动画
     */
    public void addSharedFragment(View element, String name, String backStackTAG, int containerId , boolean useDefaultTransition) {
        if (mFragmentSharedCard == null) {
            mFragmentSharedCard = new FragmentSharedCard();
        }
        mFragmentSharedCard.setSharedElement(element);
        mFragmentSharedCard.setName(name);
        mFragmentSharedCard.setTAG(backStackTAG);
        mFragmentSharedCard.setContainerId(containerId);
        mFragmentSharedCard.setUseDefaultTransition(useDefaultTransition);
    }


    /**
     * 添加共享元素动画
     * @param enterTransition  入场动画
     * @param exitTransition  出场动画
     */
    public void addTransition(Transition enterTransition, Transition exitTransition) {
        if (mFragmentSharedCard == null) {
            mFragmentSharedCard = new FragmentSharedCard();
        }
        mFragmentSharedCard.setEnterTransition(enterTransition);
        mFragmentSharedCard.setExitTransition(exitTransition);
    }




    /**
     * 设置供fragment跳转用的容器id
     * @param container
     */
    public void setFragmentContainer(int container) {
        if(container != 0){
            if (container == View.NO_ID) {
                throw new IllegalArgumentException("Can't add fragment with no id");
            }
            this.container = container;
        }
    }


    /**
     * Go to target page.
     * 通过路由键访问具体页面
     *
     * @param type       页面类型 {@link TypeKind#ACTIVITY} activity类型   {@link TypeKind#FRAGMENT} fragment 类型
     * @param nodeTarget
     */
    private void go(Context currentContext, Integer requestCode, TypeKind type, Class nodeTarget, @Nullable Bundle options) throws NullPointerException {
        switch (type) {
            case ACTIVITY:

                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(currentContext, nodeTarget, options, requestCode);
                    }
                });

                break;
            case FRAGMENT:
                try {
                    Fragment curFragment = (Fragment) nodeTarget.getConstructor().newInstance();

                    if (mFragmentSharedCard != null) {
                        FragmentMonitor.Companion.getInstance().setFragmentSharedCard(mFragmentSharedCard).replace(curFragment , container);
                    } else {
                        FragmentMonitor.Companion.getInstance().replace(curFragment , container);
                    }
                    mFragmentSharedCard = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }


    }


    /**
     * Put all the node and type names into container.
     * 将所有节点与节点类型装入容器
     *
     * @param url
     * @param target
     */
    public void put(String url, Class target) {
        if (url != null && target != null) {
            nodeTargetContainer.put(url, target);
            GoLogger.debug("target added!");
            try {
                GoLogger.info(target.getSimpleName());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




    /**
     * Page type.
     * 页面类型
     */
    enum TypeKind {
        ACTIVITY(0),
        FRAGMENT(1),
        FRAGMENT_IN_APP_PACKAGE(2);

        private final int type;

        TypeKind(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }


    /**
     * Jump into an Activity
     * 开始跳转至对应的activity
     *
     * @param currentContext 当前页面上下文（可能为应用全局的上下文）
     * @param activityClazz  目标activity的类对象
     * @param requestCode    页面返回时回调的请求码
     */
    private void startActivity(Context currentContext, Class activityClazz, @Nullable Bundle options, Integer requestCode) {

        Intent intent = new Intent(currentContext, activityClazz);
        if (currentData != null) {
            intent.putExtras(currentData);
        }


        if (requestCode != null && requestCode.intValue() >= 0) {
            if (currentContext instanceof Activity) {
                ActivityCompat.startActivityForResult((Activity) currentContext, intent, requestCode, options);
            } else {
                GoLogger.warn("Must use [go(activity, ...)] to support [startActivityForResult]");
            }
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ActivityCompat.startActivity(currentContext, intent, options);
        }

    }


    /**
     * Run on UIThread
     * 确保再UI线程上进行
     *
     * @param runnable
     */
    private void runOnMainThread(Runnable runnable) {
        MainExecutor.Companion.getInstance().execute(runnable);
    }
}
