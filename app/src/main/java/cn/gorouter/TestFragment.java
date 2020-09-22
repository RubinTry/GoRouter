package cn.gorouter;

import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import butterknife.BindView;
import cn.gorouter.annotation.Route;
import cn.gorouter.api.launcher.GoRouter;
import cn.gorouter.api.monitor.FragmentMonitor;


@Route(url = "app/TestFragment")
public class TestFragment extends BaseFragment {

    @BindView(R.id.tv1)
    TextView tv1;
    private String TAG = this.getClass().getSimpleName();


    @Override
    protected int attachLayoutRes() {
        return R.layout.fragment_test;
    }

    @Override
    protected void processor() {
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                int resId = getArguments().getInt("containerId");


                GoRouter.getInstance().build("app/TestFragment2")
                        .withContainer(R.id.flContainer)
                        .addSharedFragment(tv1,  "tag",  true)
                        .go();

            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        tv1 = null;
        Log.d(TAG, "onDestroyView: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }




}
