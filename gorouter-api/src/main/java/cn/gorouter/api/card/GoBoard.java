package cn.gorouter.api.card;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * @author logcat
 */
public class GoBoard {
    private int flag;
    private String routeKey;
    private Bundle data;
    private Bundle arguments;

    public Bundle getArguments() {
        return arguments;
    }

    public void setArguments(Bundle arguments) {
        this.arguments = arguments;
    }

    public Bundle getData() {
        return data;
    }

    public void setData(Bundle data) {
        this.data = data;
    }


    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getRouteKey() {
        return routeKey;
    }

    public void setRouteKey(String routeKey) {
        this.routeKey = routeKey;
    }


    /**
     * 向bundle插入一个整型数据
     *
     * @param key
     * @param intValue
     */
    public void putInt(String key, int intValue) {
        checkDataNotNull();
        data.putInt(key, intValue);
    }


    /**
     * 向bundle插入一个单精度浮点型数据
     *
     * @param key
     * @param floatValue
     */
    public void putFloat(String key, float floatValue) {
        checkDataNotNull();
        data.putFloat(key, floatValue);
    }


    /**
     * 向bundle插入一个长整型数据
     *
     * @param key
     * @param longValue
     */
    public void putLong(String key, long longValue) {
        checkDataNotNull();
        data.putLong(key, longValue);
    }


    /**
     * 向bundle插入一个短整型数据
     *
     * @param key
     * @param shortValue
     */
    public void putShort(String key, short shortValue) {

        checkDataNotNull();
        data.putShort(key, shortValue);
    }


    /**
     * 向bundle插入一个双经度浮点型数据
     *
     * @param key
     * @param doubleValue
     */
    public void putDouble(String key, double doubleValue) {

        checkDataNotNull();
        data.putDouble(key, doubleValue);
    }


    /**
     * 向bundle插入一个字符串数据
     *
     * @param key
     * @param value
     */
    public void putString(String key, String value) {
        checkDataNotNull();
        data.putString(key, value);
    }

    public void putBoolean(String key , Boolean booleanValue){
        checkDataNotNull();
        data.putBoolean(key , booleanValue);
    }


    /**
     * 向bundle插入一个字符序列数据
     *
     * @param key
     * @param charSequenceValue
     */
    public void putCharSequence(String key, CharSequence charSequenceValue) {
        checkDataNotNull();
        data.putCharSequence(key, charSequenceValue);
    }


    /**
     * 释放掉相关数据内容
     */
    public void release() {
        if(this.data != null){
            this.data.clear();
        }
        this.data = null;
        if(this.arguments != null){
            this.arguments.clear();
        }
        this.arguments = null;
        this.routeKey = null;
        this.flag = Intent.FLAG_ACTIVITY_NEW_TASK;
    }


    /**
     * 确保data不为空
     */
    private void checkDataNotNull() {
        if (this.data == null) {
            this.data = new Bundle();
        }
    }
}
