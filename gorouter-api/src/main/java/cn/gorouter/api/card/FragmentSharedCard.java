package cn.gorouter.api.card;

import android.transition.Transition;

/**
 * @author logcat
 * 共享元素信息面板
 */
public class FragmentSharedCard extends SharedCard {
    private String TAG;
    private boolean useDefaultTransition;
    private Transition enterTransition;
    private Transition exitTransition;


    /**
     * 获取入场动画
     * @return
     */
    public Transition getEnterTransition() {
        return enterTransition;
    }


    /**
     * 设置入场动画
     * @param enterTransition  入场动画
     */
    public void setEnterTransition(Transition enterTransition) {
        this.enterTransition = enterTransition;
    }


    /**
     * 获取出场动画
     * @return
     */
    public Transition getExitTransition() {
        return exitTransition;
    }


    /**
     * 设置出场动画
     * @param exitTransition
     */
    public void setExitTransition(Transition exitTransition) {
        this.exitTransition = exitTransition;
    }

    /**
     * 判断是否需要使用默认动画
     * @return
     */
    public boolean isUseDefaultTransition() {
        return useDefaultTransition;
    }


    /**
     * 设置是否需要使用默认动画
     * @param useDefaultTransition
     */
    public void setUseDefaultTransition(boolean useDefaultTransition) {
        this.useDefaultTransition = useDefaultTransition;
    }


    /**
     * 获取返回栈tag
     * @return
     */
    public String getTAG() {
        return TAG;
    }

    /**
     * 设置返回栈tag
     * @param TAG 返回栈tag
     */
    public void setTAG(String TAG) {
        this.TAG = TAG;
    }



}
