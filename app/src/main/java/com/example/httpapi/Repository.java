package com.example.httpapi;

public class Repository {
    private int id;
    private String name;
    private String description;
    private Boolean has_issues;
    private int open_issues;

    public int getId() { return id; }

    public String getName() { return name; }

    public String getDescription() {
        return description;
    }

    public Boolean getHas_issues() {
        return has_issues;
    }

    public int getOpen_issues() {
        return open_issues;
    }
}
