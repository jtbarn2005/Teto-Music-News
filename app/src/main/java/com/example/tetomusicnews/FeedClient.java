package com.example.tetomusicnews;

import android.os.Handler;
import android.os.Looper;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FeedClient {
    public interface Callback { void success(List<Video> weekly, List<Video> popular, String weekStart, String updatedAt); void error(String message); }
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public void load(Callback callback) {
        if (BuildConfig.FEED_URL.contains("YOUR_GITHUB_USERNAME")) {
            callback.error("Set TETO_FEED_URL in local.properties after publishing data/videos.json.");
            return;
        }
        executor.execute(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(BuildConfig.FEED_URL).openConnection();
                connection.setConnectTimeout(10000); connection.setReadTimeout(10000);
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder body = new StringBuilder(); String line;
                while ((line = reader.readLine()) != null) body.append(line);
                reader.close();
                JSONObject root = new JSONObject(body.toString());
                List<Video> weekly = parseVideos(root.optJSONArray("weeklyVideos"));
                List<Video> popular = parseVideos(root.optJSONArray("popularVideos"));
                String weekStart = root.optString("weekStart", "Sunday");
                String updatedAt = root.optString("updatedAt", "");
                new Handler(Looper.getMainLooper()).post(() -> callback.success(weekly, popular, weekStart, updatedAt));
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.error("The daily feed is unavailable. Check your connection or feed URL."));
            }
        });
    }

    private List<Video> parseVideos(JSONArray items) {
        List<Video> result = new ArrayList<>();
        if (items == null) return result;
        for (int i = 0; i < items.length(); i++) {
            JSONObject v = items.optJSONObject(i);
            if (v == null) continue;
            result.add(new Video(v.optString("videoId"), v.optString("title"), v.optString("channel"), v.optString("publishedAt"), v.optString("description"), v.optString("thumbnail")));
        }
        return result;
    }
}
