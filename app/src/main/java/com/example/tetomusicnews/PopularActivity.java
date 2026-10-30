package com.example.tetomusicnews;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class PopularActivity extends AppCompatActivity {
    private final List<Video> videos=new ArrayList<>(); private VideoAdapter adapter; private TextView empty;
    @Override protected void onCreate(Bundle b) { super.onCreate(b); setContentView(R.layout.activity_main); ((TextView)findViewById(R.id.statusText)).setText("Recent videos from the current month"); ((TextView)findViewById(R.id.emptyText)).setText("Loading this month’s videos…"); empty=findViewById(R.id.emptyText); RecyclerView list=findViewById(R.id.videoList); list.setLayoutManager(new LinearLayoutManager(this)); adapter=new VideoAdapter(videos); list.setAdapter(adapter); findViewById(R.id.popularButton).setVisibility(View.GONE); findViewById(R.id.refreshButton).setOnClickListener(v -> load()); load(); }
    void load() { new FeedClient().load(new FeedClient.Callback() { public void success(List<Video> weekly, List<Video> result, String weekStart, String updatedAt) { videos.clear(); videos.addAll(result); adapter.notifyDataSetChanged(); empty.setVisibility(result.isEmpty()?View.VISIBLE:View.GONE); } public void error(String m) { empty.setText(m); empty.setVisibility(View.VISIBLE); } }); }
}
