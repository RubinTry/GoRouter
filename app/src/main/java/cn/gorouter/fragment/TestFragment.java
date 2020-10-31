package cn.gorouter.fragment;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import cn.gorouter.R;
import cn.gorouter.annotation.Route;
import cn.gorouter.api.launcher.GoRouter;


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


                GoRouter.getInstance().build("app/TestActivity")
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
