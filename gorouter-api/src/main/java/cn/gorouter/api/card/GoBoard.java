package cn.gorouter.api.card;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import cn.gorouter.api.exception.DataEmptyException;

/**
 * @author logcat
 */
public class GoBoard {
    private int flag;
    private String routeKey;
    private int fragmentContainerId;
    private Bundle data;

    public Bundle getData() {
        return data;
    }

    public void setData(Bundle data) {
        this.data = data;
    }

    public int getFragmentContainerId() {
        return fragmentContainerId;
    }

    public void setFragmentContainerId(int fragmentContainerId) {
        this.fragmentContainerId = fragmentContainerId;
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

    public void putInt(String key , int intValue){
        try {
            checkDataNotNull();
        } catch (DataEmptyException e) {
            e.printStackTrace();
            data = new Bundle();
        }

        data.putInt(key , intValue);
    }

    public void putFloat(String key , float floatValue){
        try {
            checkDataNotNull();
        } catch (DataEmptyException e) {
            e.printStackTrace();
            data = new Bundle();
        }
        data.putFloat(key , floatValue);
    }


    public void putLong(String key , long longValue){
        try {
            checkDataNotNull();
        } catch (DataEmptyException e) {
            e.printStackTrace();
            data = new Bundle();
        }

        data.putLong(key , longValue);
    }


    public void putShort(String key , short shortValue){
        try {
            checkDataNotNull();
        } catch (DataEmptyException e) {
            e.printStackTrace();
            data = new Bundle();
        }
        data.putShort(key , shortValue);
    }

    public void putDouble(String key , double doubleValue){
        try {
            checkDataNotNull();
        } catch (DataEmptyException e) {
            e.printStackTrace();
            data = new Bundle();
        }
        data.putDouble(key , doubleValue);
    }

    public void putString(String key , String value){
        try {
            checkDataNotNull();
        } catch (DataEmptyException e) {
            e.printStackTrace();
            data = new Bundle();
        }
        data.putString(key , value);
    }


    public void putCharSequence(String key , CharSequence charSequenceValue){
        try {
            checkDataNotNull();
        } catch (DataEmptyException e) {
            e.printStackTrace();
            data = new Bundle();
        }

        data.putCharSequence(key , charSequenceValue);
    }


    public void release(){
        this.data = null;
        this.routeKey = null;
        this.flag = Intent.FLAG_ACTIVITY_NEW_TASK;
        this.fragmentContainerId = View.NO_ID;
    }

    private void checkDataNotNull() throws DataEmptyException {
        if(this.data == null){
            throw new DataEmptyException("Data is empty!!!");
        }
    }
}
