package cn.gorouter.fragment;

import cn.gorouter.R;
import cn.gorouter.annotation.Route;

/**
 * @author logcat
 */
@Route(url = "test3Fragment")
public class Test3Fragment extends BaseFragment {

    @Override
    protected int attachLayoutRes() {
        return R.layout.fragment_test3;
    }

    @Override
    protected void processor() {

    }
}
