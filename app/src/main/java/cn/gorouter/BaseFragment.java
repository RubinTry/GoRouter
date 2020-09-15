package cn.gorouter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author logcat
 */
public abstract class BaseFragment extends Fragment {

    protected ViewGroup rootView;
    private Unbinder unbinder;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView == null){
            rootView = (ViewGroup) inflater.inflate(attachedLayoutRes() , container , false);
            unbinder = ButterKnife.bind(this ,rootView);
        }
        return rootView;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        processor();
    }

    protected abstract int attachedLayoutRes();

    protected abstract void processor();


    @Override
    public void onDestroyView() {
        if(rootView != null){
            rootView.removeAllViews();
            rootView = null;
        }
        if(unbinder != null){
            unbinder.unbind();
        }
        super.onDestroyView();

    }



}
