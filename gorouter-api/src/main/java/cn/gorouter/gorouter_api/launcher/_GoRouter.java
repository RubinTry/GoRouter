package cn.gorouter.gorouter_api.launcher;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.gorouter.gorouter_api.logger.GoLogger;
import cn.gorouter.gorouter_api.utils.ActivityUtils;
import dalvik.system.DexFile;

import static cn.gorouter.gorouter_api.launcher._GoRouter.TypeKind.ACTIVITY;
import static cn.gorouter.gorouter_api.launcher._GoRouter.TypeKind.FRAGMENT;

/**
 * @author logcat <a href="13857769302@163.com">Contact me.</a>
 * @version 1.0.0
 * @date 2020/07/11 16:25
 */
public class _GoRouter {
    private static volatile _GoRouter instance;
    private Application application;
    private String currentUrl;
    private Bundle currentData;
    private Map<String, Class> nodeTargetContainer;
    private Map<String, Class> typeContainer;
    private Integer requestCode;


    private _GoRouter(){
        nodeTargetContainer = new HashMap<>();
        typeContainer = new HashMap<>();
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

    public boolean init(Application application) {
        return initAllRoute(application);
    }


    /**
     * Initialize all route and put them into container.
     * @param application
     * @return
     */
    private boolean initAllRoute(Application application) {
        this.application = application;
        List<String> classNames = getClasses(application.getApplicationContext(), "cn.gorouter.route");
        for (String className : classNames) {

            try {
                Class<? extends Activity> aClass = (Class<? extends Activity>) Class.forName(className);
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
    private List<String> getClasses(Context context, String packageName) {
        List<String> classList = new ArrayList<>();
        String path = null;

        try {
            path = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0).sourceDir;
            DexFile dexFile = new DexFile(path);
            Enumeration entries = dexFile.entries();
            while (entries.hasMoreElements()) {
                String name = (String) entries.nextElement();
                if (name.contains(packageName)) {
                    classList.add(name);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classList;
    }




    /**
     * Build a route
     * @param url  route url address
     * @param data  The data that needs to be passed to the target page
     * @param requestCode  Parameters required to implement {@link Activity#startActivityForResult(Intent, int)}
     */
    public void build(String url, Bundle data, Integer requestCode) {
        this.currentUrl = url;
        this.currentData = data;
        this.requestCode = requestCode;
    }


    /**
     * Go to target page.
     */
    public void go() throws NullPointerException {
        if (currentUrl == null) {
            throw new IllegalArgumentException("Please set currentUrl");
        }

        if (nodeTargetContainer == null || typeContainer == null) {
            throw new NullPointerException("container is empty!!!");
        }

        //Get all the node and type classes.
        Class nodeTarget = nodeTargetContainer.get(currentUrl);
        Class targetType = typeContainer.get(currentUrl);

        if(nodeTarget != null && targetType != null){
            if (Activity.class.isAssignableFrom(targetType)) {
                //If the node type is Activity,the jump is made in the form of Activity.
                go(ACTIVITY, nodeTarget);
            } else if (Fragment.class.isAssignableFrom(targetType)) {
                //If the node type is Fragment,the jump is made in the form of Fragment.
                go(FRAGMENT, nodeTarget);
            }
        }else{
            throw new NullPointerException("route \"" + currentUrl + "\" is not found!!!");
        }


        this.currentUrl = null;
        this.currentData = null;
        this.requestCode = null;
    }


    /**
     * Go to target page.
     * @param type
     * @param nodeTarget
     */
    private void go(TypeKind type, Class nodeTarget) throws NullPointerException{
        switch (type) {
            case ACTIVITY:
                if (requestCode != null) {
                    Activity currentActivity = ActivityUtils.getCurrentActivity();
                    if (currentActivity != null) {
                        Intent intent = new Intent(currentActivity, nodeTarget);
                        if(currentData != null){
                            intent.putExtras(currentData);
                        }
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        currentActivity.startActivityForResult(intent, requestCode.intValue());
                        currentActivity = null;
                    }else{
                        throw new NullPointerException("No activity launching!!!");
                    }

                } else {
                    Intent intent = new Intent(application.getApplicationContext(), nodeTarget);
                    if(currentData != null){
                        intent.putExtras(currentData);
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    application.getApplicationContext().startActivity(intent);
                }


                break;
            case FRAGMENT:

                break;
            default:
                break;
        }


    }

    /**
     * Put all the node and type names into container.
     * @param url
     * @param target
     * @param typeName
     */
    public void put(String url, Class target , String typeName) {
        if (url != null && target != null) {
            nodeTargetContainer.put(url, target);
            GoLogger.debug("target added!");
            try {
                Class targetType = Class.forName(typeName);
                GoLogger.info(targetType.getName());
                typeContainer.put(url , targetType);

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
}
