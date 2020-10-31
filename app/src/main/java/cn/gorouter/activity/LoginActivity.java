package cn.gorouter.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;

import cn.gorouter.R;
import cn.gorouter.annotation.Route;
import cn.gorouter.api.launcher.GoRouter;

/**
 * @author logcat
 */
@Route(url = "/main/Login")
public class LoginActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }
}