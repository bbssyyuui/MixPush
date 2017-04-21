package com.zdf.mixpush;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.zdf.lib_push.Push;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Push.getInstance().onAppStart(this);
    }
}
