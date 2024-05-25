package com.example.androidproject.model;

public class Article {

    String title;
    String link;
    String description;
    String image_url;

    public Article() {
    }

    public Article(String title, String link, String description, String image_url) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.image_url = image_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
