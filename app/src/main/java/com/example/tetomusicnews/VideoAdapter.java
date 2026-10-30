package com.example.tetomusicnews;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.Holder> {
    private final List<Video> videos;
    public VideoAdapter(List<Video> videos) { this.videos = videos; }
    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup p, int t) { return new Holder(LayoutInflater.from(p.getContext()).inflate(R.layout.item_video, p, false)); }
    @Override public void onBindViewHolder(@NonNull Holder h, int pos) {
        Video v = videos.get(pos);
        h.title.setText(v.title); h.meta.setText(v.channel + "  •  " + v.published); h.description.setText(v.description);
        if (!v.thumbnail.isEmpty()) Glide.with(h.image).load(v.thumbnail).placeholder(R.drawable.teto_placeholder).into(h.image);
        h.itemView.setOnClickListener(view -> { Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + v.id)); view.getContext().startActivity(i); });
    }
    @Override public int getItemCount() { return videos.size(); }
    static class Holder extends RecyclerView.ViewHolder {
        ImageView image; TextView title, meta, description;
        Holder(View v) { super(v); image=v.findViewById(R.id.thumbnail); title=v.findViewById(R.id.title); meta=v.findViewById(R.id.meta); description=v.findViewById(R.id.description); }
    }
}
