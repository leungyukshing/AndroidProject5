package com.example.httpapi;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GithubActivity extends AppCompatActivity {
    private EditText githubInputBox;
    private Button githubSearchBtn;
    RecyclerView recyclerView;
    List<Repository> list;
    MyRepositoryAdapter myRepositoryAdapter;
    private RecyclerView githubRecyclerView;
    private String user_name;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.github);

        // 初始化变量
        githubInputBox = (EditText)findViewById(R.id.githubInputBox);
        githubSearchBtn = (Button)findViewById(R.id.githubSearchBtn);
        githubRecyclerView = (RecyclerView)findViewById(R.id.githubRecyclerView);
        list = new ArrayList<>();
        myRepositoryAdapter = new MyRepositoryAdapter(list);

        // 渲染RecyclerView
        githubRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        githubRecyclerView.setAdapter(myRepositoryAdapter);

        myRepositoryAdapter.setOnItemClickListener(new MyRepositoryAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int i) {
                Intent intent = new Intent(GithubActivity.this, IssueActivity.class);
                intent.putExtra("repo_name", list.get(i).getName());
                intent.putExtra("user_name", user_name);
                intent.putExtra("has_issues", list.get(i).getHas_issues());
                System.out.println("Trans: " + list.get(i).getName());
                startActivity(intent);
            }
        });

        // 点击事件
        githubSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user_name = githubInputBox.getText().toString();
                githubInputBox.setText("");

                OkHttpClient build = new OkHttpClient.Builder()
                        .connectTimeout(2, TimeUnit.SECONDS)
                        .readTimeout(2, TimeUnit.SECONDS)
                        .writeTimeout(2, TimeUnit.SECONDS)
                        .build();

                // API Retrofit
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://api.github.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .client(build)
                        .build();

                GithubService service = retrofit.create(GithubService.class);
                Observable<List<Repository>> repoObservable = service.getRepo(user_name);

                repoObservable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<List<Repository>>() {
                            @Override
                            public void onCompleted() { }

                            @Override
                            public void onError(Throwable e) { }

                            @Override
                            public void onNext(List<Repository> repositories) {
                                System.out.println("Receive Repository");
                                list.clear();
                                System.out.println("Repo Size: " + repositories.size());
                                if (repositories.size() == 0) {
                                    Toast.makeText(GithubActivity.this, "该用户无仓库", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    for (int i = 0; i < repositories.size(); i++) {
                                        System.out.println(repositories.get(i).getName());
                                        System.out.println(repositories.get(i).getHas_issues());
                                        // 不显示fork的项目
                                        if (repositories.get(i).getHas_issues() == true) {
                                            list.add(repositories.get(i));
                                        }
                                    }
                                }
                                myRepositoryAdapter.notifyDataSetChanged();
                            }
                        });
            }
        });
    }
}
