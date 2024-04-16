package com.example.androidproject.Model;

import java.util.List;

public class ApiResponse {

    String status;

    List<Article> results;

    public ApiResponse() {
    }

    public ApiResponse(String status, List<Article> results) {
        this.status = status;
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Article> getResults() {
        return results;
    }

    public void setResults(List<Article> results) {
        this.results = results;
    }
}
