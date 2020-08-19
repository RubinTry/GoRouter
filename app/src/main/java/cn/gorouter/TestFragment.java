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

import cn.gorouter.annotation.Route;
import cn.gorouter.api.launcher.GoRouter;


@Route(url = "app/TestFragment")
public class TestFragment extends Fragment {

    private TextView tv1;
    private String TAG = this.getClass().getSimpleName();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_test , container , false);
        Log.d("TAG", "onCreateView: ");

        tv1 = rootView.findViewById(R.id.tv1);
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoRouter.getInstance().build("app/TestFragment2")
                        .addSharedFragment(tv1 , ViewCompat.getTransitionName(tv1) , "tag" , R.id.flContainer , true)
                        .go();
            }
        });
        return rootView;
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
