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

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class IssueActivity extends AppCompatActivity {
    private List<Issue> list;
    private String user_name;
    private String repo_name;
    private boolean has_issues;
    private RecyclerView issueRecyclerView;
    MyIssueAdapter myIssueAdapter;

    private EditText titleInput;
    private EditText bodyInput;
    private Button addIssueBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.repo_detail);

        // 初始话变量
        titleInput = (EditText)findViewById(R.id.titleInput);
        bodyInput = (EditText)findViewById(R.id.bodyInput);
        addIssueBtn = (Button)findViewById(R.id.addIssueBtn);
        list = new ArrayList<>();
        issueRecyclerView = (RecyclerView)findViewById(R.id.issueRecyclerView);
        myIssueAdapter = new MyIssueAdapter(list);

        // 渲染RecyclerView
        issueRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        issueRecyclerView.setAdapter(myIssueAdapter);

        // 接受参数
        Intent intent = getIntent();
        user_name = intent.getStringExtra("user_name");
        repo_name = intent.getStringExtra("repo_name");
        has_issues = intent.getBooleanExtra("has_issues", false);
        System.out.println("Get: " + repo_name + ", " + has_issues);

        // API
        if (has_issues) {
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
            Observable<List<Issue>> issueObservable = service.getIssue(user_name, repo_name);

            issueObservable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<Issue>>() {
                        @Override
                        public void onCompleted() { }

                        @Override
                        public void onError(Throwable e) { }

                        @Override
                        public void onNext(List<Issue> issues) {
                            System.out.println("Receive Issue");
                            for (int i = 0; i < issues.size(); i++) {
                                System.out.println(issues.get(i).getTitle());
                                list.add(issues.get(i));
                            }
                            myIssueAdapter.notifyDataSetChanged();
                        }
                    });
        }

        addIssueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = titleInput.getText().toString();
                String body = bodyInput.getText().toString();
                // 清空输入框
                titleInput.setText("");
                bodyInput.setText("");

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

                // 获取时间
                StringBuilder sb = new StringBuilder();
                sb.append("yyyy-MM-dd HH:mm:ss ");
                SimpleDateFormat sdf = new SimpleDateFormat((sb.toString()));
                String date = sdf.format(new Date());
                StringBuilder strBuilder = new StringBuilder(date);
                strBuilder.setCharAt(10, 'T');
                strBuilder.setCharAt(19, 'Z');
                String create_at = strBuilder.toString();

                // POST请求
                GithubService service = retrofit.create(GithubService.class);
                // 生成请求的Body
                JSONObject root = new JSONObject();
                try {
                    root.put("title", title);
                    root.put("body", body);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),root.toString());
                Observable<Issue> postIssueObservable = service.postIssue(user_name, repo_name, requestBody);

                postIssueObservable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Issue>() {
                            @Override
                            public void onCompleted() { }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onNext(Issue issue) {
                                System.out.println(issue.getTitle());
                                list.add(issue);
                                myIssueAdapter.notifyDataSetChanged();
                            }
                        });
            }
        });
    }
}
