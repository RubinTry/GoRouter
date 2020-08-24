package cn.gorouter;

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

import cn.gorouter.annotation.Route;
import cn.gorouter.api.launcher.GoRouter;


/**
 * @author logcat
 */
@Route(url = "app/TestFragment2")
public class Test2Fragment extends BaseFragment {
    private String TAG = this.getClass().getSimpleName();
    private TextView tv2;


    @Override
    protected int attachedLayoutRes() {
        return R.layout.fragment_test2;
    }

    @Override
    protected void processor() {
        tv2 = rootView.findViewById(R.id.tv2);
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoRouter.getInstance()
                        .build("test3Fragment")
                        .addSharedFragment(tv2 , ViewCompat.getTransitionName(tv2) , "tag" , R.id.flContainer , true)
                        .go();
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
