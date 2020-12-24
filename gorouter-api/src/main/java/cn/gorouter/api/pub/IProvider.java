package cn.gorouter.api.pub;

import android.content.Context;

public interface IProvider {
    /**
     * Make some initialize for service
     * @param context  The context what your application context is.
     */
    void init(Context context);
}
