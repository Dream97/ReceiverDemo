package com.zhiyuan.receiverdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Button button;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.bt);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,MyService.class);
                startService(intent);
                textView.setText("请等待3秒");
            }
        });
        textView = findViewById(R.id.text);
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.zhiyuan.receiver.SHOW");
        registerReceiver(new MyBroadcastReceiver(),filter);
    }

    public  class MyBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            textView.setText("后台服务执行成功");
            Intent intent2 = new Intent();

            intent2.setAction("android.intent.action.VIEW");

            intent2.addCategory("android.intent.category.DEFAULT");

            intent2.setData(Uri.parse("https://blog.csdn.net/qq_34261214"));

            startActivity(intent2);

        }
    }
}
