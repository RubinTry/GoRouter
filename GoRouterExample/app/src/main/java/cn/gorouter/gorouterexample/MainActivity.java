package cn.gorouter.gorouterexample;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import cn.gorouter.annotation.Route;
import cn.gorouter.api.launcher.GoRouter;
import cn.gorouter.api.logger.GoLogger;

/**
 * @author logcat
 */
@Route(url = "/main/MainActivity")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        GoRouter.getInstance()
                .build("/another/AnotherActivity")
                .go(this , 1001);
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