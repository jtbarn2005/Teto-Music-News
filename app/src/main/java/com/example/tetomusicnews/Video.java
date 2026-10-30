package com.example.tetomusicnews;

public class Video {
    public final String id, title, channel, published, description, thumbnail;
    public Video(String id, String title, String channel, String published, String description, String thumbnail) {
        this.id = id; this.title = title; this.channel = channel; this.published = published; this.description = description; this.thumbnail = thumbnail;
    }
}
