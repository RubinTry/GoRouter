package cn.gorouter.another.module;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import cn.gorouter.annotation.Route;

/**
 * @author logcat
 */
@Route(url = "/another/AnotherActivity")
public class AnotherActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another);
    }
}
