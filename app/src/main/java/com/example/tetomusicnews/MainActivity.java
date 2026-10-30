package com.example.tetomusicnews;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView status, empty; private final List<Video> videos = new ArrayList<>(); private VideoAdapter adapter;
    @Override protected void onCreate(Bundle b) { super.onCreate(b); setContentView(R.layout.activity_main); status=findViewById(R.id.statusText); empty=findViewById(R.id.emptyText); RecyclerView list=findViewById(R.id.videoList); list.setLayoutManager(new LinearLayoutManager(this)); adapter=new VideoAdapter(videos); list.setAdapter(adapter); findViewById(R.id.refreshButton).setOnClickListener(v -> load(false)); findViewById(R.id.weeklyButton).setEnabled(false); findViewById(R.id.popularButton).setOnClickListener(v -> startActivity(new android.content.Intent(this, PopularActivity.class))); load(false); }
    void load(boolean ignored) { status.setText("Loading today’s feed…"); empty.setVisibility(View.GONE); new FeedClient().load(new FeedClient.Callback() { public void success(List<Video> result, List<Video> popular, String weekStart, String updatedAt) { videos.clear(); videos.addAll(result); adapter.notifyDataSetChanged(); status.setText("This week • Sunday start • Updated " + updatedAt); empty.setText(result.isEmpty() ? "No tracked Teto videos have been added this week yet." : ""); empty.setVisibility(result.isEmpty() ? View.VISIBLE : View.GONE); } public void error(String m) { status.setText("Weekly Teto videos"); empty.setText(m + "\n\nThe feed is refreshed once per day at midnight in America/Denver."); empty.setVisibility(View.VISIBLE); } }); }
}
