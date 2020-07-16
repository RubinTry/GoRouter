package cn.gorouter.api.utils;

import android.app.Activity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author logcat
 * @date 2020/07/14
 */
public class ActivityUtils {
    private static String TAG = "ActivityUtils";


    /**
     * 获取当前显示的activity
     * @return
     */
    public static Activity getCurrentActivity(){
       Class activityThreadClazz = null;
        try {
            activityThreadClazz = Class.forName("android.app.ActivityThread");
            //获取activityThread类对象
            Object activityThread = activityThreadClazz.getMethod("currentActivityThread").invoke(null);
            Field activitiesField = activityThreadClazz.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);
            Map activities = (Map) activitiesField.get(activityThread);
            for (Object activityRecord : activities.values()){
                Class activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    Activity activity = (Activity) activityField.get(activityRecord);
                    return activity;
                }

            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }
}
