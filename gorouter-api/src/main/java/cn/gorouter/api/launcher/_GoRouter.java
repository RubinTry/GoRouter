package cn.gorouter.api.launcher;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import cn.gorouter.api.logger.GoLogger;
import cn.gorouter.api.utils.ActivityMonitor;
import cn.gorouter.api.utils.FragmentMonitor;
import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;

import static cn.gorouter.api.launcher._GoRouter.TypeKind.ACTIVITY;
import static cn.gorouter.api.launcher._GoRouter.TypeKind.FRAGMENT;

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

    private static Handler mHandler;

    static {
        mCurrentClassLoader = Thread.currentThread().getContextClassLoader();
    }


    private _GoRouter() {
        nodeTargetContainer = new HashMap<>();
    }

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

    public static boolean init(Application application) {
        mContext = application.getApplicationContext();
        mHandler = new Handler(Looper.getMainLooper());
        ActivityMonitor.Companion.getInstance().initialize(application);
        return initAllRoute(mContext);
    }


    /**
     * Initialize all route and put them into container.
     *
     * @param context
     * @return
     */
    private static synchronized boolean initAllRoute(Context context) {
        List<Class> classNames = getClasses(context.getApplicationContext(), "cn.gorouter.route");
        for (Class aClass : classNames) {

            try {
//                Class aClass = Class.forName(className);
                //Is IRouter's sub class?
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
            PathClassLoader pathClassLoader = new PathClassLoader(path , mCurrentClassLoader);
            Enumeration entries = dexFile.entries();
            while (entries.hasMoreElements()) {
                String name = (String) entries.nextElement();
                if (name.contains(packageName)) {
                    Class aClass = pathClassLoader.loadClass(name);
                    classList.add(aClass);
                }
            }
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return classList;
    }


    /**
     * Build a route
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
     */
    public void go(Context context, Integer requestCode) throws NullPointerException {
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
                go(currentContext, requestCode, ACTIVITY, nodeTarget);
            } else if (Fragment.class.isAssignableFrom(nodeTarget)) {
                //If the node type is Fragment,the jump is made in the form of Fragment.
                go(currentContext, requestCode, FRAGMENT, nodeTarget);
            }
        } else {
            throw new NullPointerException("route \"" + currentUrl + "\" is not found!!!");
        }


    }


    /**
     * Go to target page.
     *
     * @param type
     * @param nodeTarget
     */
    private void go(Context currentContext, Integer requestCode, TypeKind type, Class nodeTarget) throws NullPointerException {
        switch (type) {
            case ACTIVITY:

                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(currentContext, nodeTarget, requestCode);
                    }
                });

                break;
            case FRAGMENT:

                break;
            default:
                break;
        }


    }

    public Fragment getFragmentInstance() {
        if (currentUrl == null) {
            throw new IllegalArgumentException("Please set currentUrl");
        }

        if (nodeTargetContainer == null) {
            throw new NullPointerException("container is empty!!!");
        }


        //Get all the node and type classes.
        Class nodeTarget = nodeTargetContainer.get(currentUrl);


        if(nodeTarget != null && Fragment.class.isAssignableFrom(nodeTarget)){
            try {
                return (Fragment) nodeTarget.getConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        GoLogger.error("Don't have this fragment!!!");
        return null;

    }




    /**
     * Put all the node and type names into container.
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
     */
    enum TypeKind {
        ACTIVITY(0),
        FRAGMENT(1),
        FRAGMENT_ON_ANDROID_SUPPORT(2);

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
     *
     * @param currentContext
     * @param activityClazz
     * @param requestCode
     */
    private void startActivity(Context currentContext, Class activityClazz, Integer requestCode) {

        Intent intent = new Intent(currentContext, activityClazz);
        if (currentData != null) {
            intent.putExtras(currentData);
        }




        if (requestCode != null && requestCode.intValue() >= 0) {
            if (currentContext instanceof Activity) {
                ActivityCompat.startActivityForResult((Activity) currentContext, intent, requestCode, null);
            } else {
                GoLogger.warn("Must use [go(activity, ...)] to support [startActivityForResult]");
            }
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ActivityCompat.startActivity(currentContext, intent, null);
        }

    }


    /**
     * Run on UIThread
     *
     * @param runnable
     */
    private void runOnMainThread(Runnable runnable) {
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            mHandler.post(runnable);
        } else {
            runnable.run();
        }
    }
}
