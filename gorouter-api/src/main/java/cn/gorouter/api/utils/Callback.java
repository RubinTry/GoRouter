package cn.gorouter.api.utils;

import cn.gorouter.api.launcher.GoRouter;

/**
 * @author logcat
 * @apiNote This is a callback when you called {@link GoRouter#go()}
 */
public interface Callback {
    /**
     * This method will be called after lost.
     * @param ex exception
     */
    void onFail(Throwable ex);

    /**
     * Navigation success
     */
    void onArrival();
}
