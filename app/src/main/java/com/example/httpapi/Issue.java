package com.example.httpapi;

public class Issue {
    private String title;
    private String state;
    private String created_at;
    private String body;

    public Issue(String title, String state, String created_at, String body) {
        this.title = title;
        this.state = state;
        this.created_at = created_at;
        this.body = body;
    }
    public String getTitle() { return title; }

    public String getState() { return state; }

    public String getCreated_at() { return created_at; }

    public String getBody() { return body; }
}
