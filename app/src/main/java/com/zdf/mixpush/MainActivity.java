package com.zdf.mixpush;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.zdf.lib_push.Push;
import com.zdf.lib_push.rom.RomUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Push.getInstance().onAppStart(this);

        // test
        Toast.makeText(getApplication(), "当前推送渠道：" + RomUtil.rom(), Toast.LENGTH_SHORT).show();
    }
}
