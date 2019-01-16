package com.example.httpapi;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

public interface GithubService {
    @GET("/users/{user_name}/repos")
    Observable<List<Repository>> getRepo(@Path("user_name") String user_name);

    @GET("/repos/{user_name}/{repo_name}/issues")
    Observable<List<Issue>> getIssue(@Path("user_name") String user_name, @Path("repo_name") String repo_name);

    @Headers("Authorization: token 13ed349c00a3d51189ced83c35b5ce1cce5bae29")
    @POST("/repos/{user_name}/{repo_name}/issues")
    Observable<Issue> postIssue(@Path("user_name") String user_name, @Path("repo_name") String repo_name, @Body RequestBody requestBody);
}
