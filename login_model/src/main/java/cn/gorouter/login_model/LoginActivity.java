package cn.gorouter.login_model;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import cn.gorouter.gorouter_annotation.Route;
import cn.gorouter.gorouter_api.logger.GoLogger;

/**
 * @author logcat
 * @date 2020/07/15 13:55
 * @version 1.0.0
 * This activity is use for test in another module.
 */
@Route("/login/LoginActivity")
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        GoLogger.info("This is LoginActivity.");
    }
}
