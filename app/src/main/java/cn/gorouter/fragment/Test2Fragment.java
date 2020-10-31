package cn.gorouter.fragment;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import cn.gorouter.R;
import cn.gorouter.annotation.Route;
import cn.gorouter.api.launcher.GoRouter;


/**
 * @author logcat
 */
@Route(url = "app/TestFragment2")
public class Test2Fragment extends BaseFragment {
    private String TAG = this.getClass().getSimpleName();
    @BindView(R.id.tv2)
    TextView tv2;


    @Override
    protected int attachLayoutRes() {
        return R.layout.fragment_test2;
    }

    @Override
    protected void processor() {
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }
}
