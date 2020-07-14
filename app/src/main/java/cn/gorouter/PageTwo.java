package cn.gorouter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import cn.gorouter.gorouter_annotation.Route;
import cn.gorouter.gorouter_api.launcher.GoRouter;
import cn.gorouter.gorouter_api.logger.GoLogger;

/**
 * @author logcat
 */
@Route("main/PageTwo")
public class PageTwo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_two);

//        GoLogger.debug("");
    }
}