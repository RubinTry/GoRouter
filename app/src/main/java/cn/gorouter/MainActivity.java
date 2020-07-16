package cn.gorouter;

import androidx.annotation.Nullable;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import cn.gorouter.annotation.Route;
import cn.gorouter.api.launcher.GoRouter;
import cn.gorouter.api.logger.GoLogger;

/**
 * @author logcat
 */
@Route(url = "/main/MainActivity")
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoLogger.info("This is main page!");


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1001){
            GoLogger.debug("从登录页返回");
        }

    }

    public void toLogin(View view) {
        //visit "login_model" 's LoginActivity
        GoRouter.getInstance()
                .build("/main/PageTwo")
                .go(this , 1001);
    }
}