package cn.gorouter.gorouterexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import cn.gorouter.annotation.Route;
import cn.gorouter.api.launcher.GoRouter;

/**
 * @author logcat
 */
@Route(url = "/main/MainActivity")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        GoRouter.getInstance().build("/another/AnotherActivity").go();
    }
}