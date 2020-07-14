package cn.gorouter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import cn.gorouter.gorouter_annotation.Route;
import cn.gorouter.gorouter_api.launcher.GoRouter;

/**
 * @author logcat
 */
@Route("main/MainActivity")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        GoRouter.getInstance()
                .build("main/PageTwo" , null)
                .go();
    }
}