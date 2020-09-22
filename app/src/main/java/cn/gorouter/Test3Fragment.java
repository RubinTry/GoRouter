package cn.gorouter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import cn.gorouter.annotation.Route;
import cn.gorouter.api.monitor.FragmentMonitor;

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
