package cn.gorouter.api.card;

import android.view.View;


/**
 * 共享元素模板
 */
class SharedCard {
    private View sharedElement;
    private String name;

    public View getSharedElement() {
        return sharedElement;
    }

    public void setSharedElement(View sharedElement) {
        this.sharedElement = sharedElement;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
