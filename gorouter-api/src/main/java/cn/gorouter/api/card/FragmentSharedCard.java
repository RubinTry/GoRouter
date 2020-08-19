package cn.gorouter.api.card;

import android.transition.Transition;

/**
 * @author logcat
 */
public class FragmentSharedCard extends SharedCard {
    private String TAG;
    private int containerId;
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


    /**
     * 获取fragment的容器的id
     * @return
     */
    public int getContainerId() {
        return containerId;
    }

    /**
     * 设置fragment的容器的id
     * @param containerId
     */
    public void setContainerId(int containerId) {
        this.containerId = containerId;
    }
}
