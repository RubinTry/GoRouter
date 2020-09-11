package cn.gorouter.api;

import androidx.fragment.app.Fragment;

import cn.gorouter.api.card.FragmentSharedCard;

/**
 * @author logcat
 * @date 2020/09/11
 * Current fragment info
 */
public class FragmentInfo {
    private Fragment currentFragment;
    private boolean isShareElement;
    private FragmentSharedCard fragmentSharedCard;

    public FragmentInfo(Fragment currentFragment, boolean isShareElement , FragmentSharedCard fragmentSharedCard) {
        this.currentFragment = currentFragment;
        this.isShareElement = isShareElement;
        this.fragmentSharedCard = fragmentSharedCard;
    }

    public Fragment getCurrentFragment() {
        return currentFragment;
    }

    public void setCurrentFragment(Fragment currentFragment) {
        this.currentFragment = currentFragment;
    }

    public boolean isShareElement() {
        return isShareElement;
    }

    public void setShareElement(boolean shareElement) {
        isShareElement = shareElement;
    }

    public FragmentSharedCard getFragmentSharedCard() {
        return fragmentSharedCard;
    }

    public void setFragmentSharedCard(FragmentSharedCard fragmentSharedCard) {
        this.fragmentSharedCard = fragmentSharedCard;
    }

    public void release(){
        currentFragment = null;
        fragmentSharedCard = null;
    }
}
