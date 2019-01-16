package com.example.httpapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button bilibiliBtn;
    private Button githubBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化变量
        bilibiliBtn = (Button)findViewById(R.id.bilibiliBtn);
        githubBtn = (Button)findViewById(R.id.githbBtn);

        // 按钮点击事件
        bilibiliBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BilibiliActivity.class);
                startActivity(intent);
            }
        });

        githubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GithubActivity.class);
                startActivity(intent);
            }
        });
    }
}
