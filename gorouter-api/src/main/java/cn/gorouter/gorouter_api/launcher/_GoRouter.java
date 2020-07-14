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
import java.util.List;
import java.util.Map;

import cn.gorouter.gorouter_api.logger.GoLogger;
import cn.gorouter.gorouter_api.utils.ActivityUtils;
import dalvik.system.DexFile;

import static cn.gorouter.gorouter_api.launcher._GoRouter.TypeKind.ACTIVITY;
import static cn.gorouter.gorouter_api.launcher._GoRouter.TypeKind.FRAGMENT;

/**
 * @author logcat
 * @date 2020/07/14
 */
public class _GoRouter {
    private static volatile _GoRouter instance;
    private Application application;
    private String currentUrl;
    private Bundle currentData;
    private Map<String, Class> nodeTargetContainer;
    private Map<String, Class> typeContainer;
    private Integer requestCode;

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
     * 初始化获取所有路由
     * @param application
     * @return
     */
    private boolean initAllRoute(Application application) {
        this.application = application;
        List<String> classNames = getClasses(application.getApplicationContext(), "cn.gorouter.route");
        for (String className : classNames) {

            try {
                Class<? extends Activity> aClass = (Class<? extends Activity>) Class.forName(className);
                //判断这个类是不是IRouter这个接口的子类
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
     * 扫描指定包名下的所有类名
     *
     * @param context
     * @param packageName
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
     * 构建一个路由
     * @param url  路由地址
     * @param data  需要传递的数据
     * @param requestCode  实现startActivityForResult所需的参数
     */
    public void build(String url, Bundle data, Integer requestCode) {
        this.currentUrl = url;
        this.currentData = data;
        this.requestCode = requestCode;
    }


    /**
     * 开始跳转
     */
    public void go() throws NullPointerException {
        if (currentUrl == null) {
            throw new IllegalArgumentException("Please set currentUrl");
        }

        if (nodeTargetContainer == null || typeContainer == null) {
            throw new NullPointerException("container is empty!!!");
        }

        //进行各个节点的获取以及类型的判断
        Class nodeTarget = nodeTargetContainer.get(currentUrl);
        Class targetType = typeContainer.get(currentUrl);

        if(nodeTarget != null && targetType != null){
            if (Activity.class.isAssignableFrom(targetType)) {
                //如果节点类型为activity，则使用activity的方式作出跳转
                go(ACTIVITY, nodeTarget);
            } else if (Fragment.class.isAssignableFrom(targetType)) {
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
     * 开始跳转
     * @param type
     * @param nodeTarget
     */
    private void go(TypeKind type, Class nodeTarget) {
        switch (type) {
            case ACTIVITY:
                if (requestCode != null) {
                    Activity currentActivity = ActivityUtils.getCurrentActivity();
                    if (currentActivity != null) {
                        Intent intent = new Intent(currentActivity, nodeTarget);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        currentActivity.startActivityForResult(intent, requestCode.intValue());
                    }
                    currentActivity = null;
                } else {
                    Intent intent = new Intent(application.getApplicationContext(), nodeTarget);
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

    public void setContainer(Map<String, Class> nodeTargetContainer, Map<String, Class> typeContainer) {
        this.nodeTargetContainer = nodeTargetContainer;
        this.typeContainer = typeContainer;
    }


    /**
     * 页面类型
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
