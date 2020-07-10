package cn.gorouter.api;

import android.app.Activity;
import android.app.Application;

import java.util.HashMap;
import java.util.Map;

public class GoRouter {
    private static volatile GoRouter instance;
    private Application application;
    private Map<String , Class<? extends Activity>> activityContainer;

    private GoRouter(){
        activityContainer = new HashMap<>();
    }

    public static GoRouter getInstance(){
        if(instance == null){
            synchronized (GoRouter.class){
                if(instance == null){
                    instance = new GoRouter();
                }
            }
        }
        return instance;
    }


    public static void init(Application application){
        GoRouter.getInstance().setApplication(application);
    }

    private void setApplication(Application application) {
        this.application = application;
    }


    public void add(String url , Class<? extends Activity> activity){
        if(url != null && activity != null){
            activityContainer.put(url , activity);
        }
    }
}
