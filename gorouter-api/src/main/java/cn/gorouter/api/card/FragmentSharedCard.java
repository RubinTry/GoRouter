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

    public Transition getEnterTransition() {
        return enterTransition;
    }

    public void setEnterTransition(Transition enterTransition) {
        this.enterTransition = enterTransition;
    }

    public Transition getExitTransition() {
        return exitTransition;
    }

    public void setExitTransition(Transition exitTransition) {
        this.exitTransition = exitTransition;
    }

    public boolean isUseDefaultTransition() {
        return useDefaultTransition;
    }

    public void setUseDefaultTransition(boolean useDefaultTransition) {
        this.useDefaultTransition = useDefaultTransition;
    }

    public String getTAG() {
        return TAG;
    }

    public void setTAG(String TAG) {
        this.TAG = TAG;
    }

    public int getContainerId() {
        return containerId;
    }

    public void setContainerId(int containerId) {
        this.containerId = containerId;
    }
}
