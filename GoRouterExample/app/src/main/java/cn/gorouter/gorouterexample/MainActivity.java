package cn.gorouter.gorouterexample;

import androidx.annotation.Nullable;

import android.content.Intent;
import android.os.Bundle;

import cn.gorouter.annotation.Route;
import cn.gorouter.api.launcher.GoRouter;
import cn.gorouter.api.logger.GoLogger;
import cn.gorouter.common.library.BaseActivity;

/**
 * @author logcat
 */
@Route(url = "/main/MainActivity")
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        GoRouter.getInstance()
                .build("/another/AnotherActivity")
                .go();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1001:
                GoLogger.info("Back from another Activity!!!");
                break;
            default:
                break;
        }
    }
}