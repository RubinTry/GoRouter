package cn.gorouter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import cn.gorouter.annotation.Route;
import cn.gorouter.api.logger.GoLogger;

/**
 * @author logcat
 */
@Route(url = "/main/PageTwo")
public class PageTwo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_two);

        GoLogger.error("This is second page!");
    }
}