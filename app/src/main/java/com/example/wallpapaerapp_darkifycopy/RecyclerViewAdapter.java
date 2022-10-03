package com.example.wallpapaerapp_darkifycopy;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.viewHolder> {
    public static final String WALLPAPER_URL = "Wallpaper Url";
    Context context;
    List<Wallpaper> wallpaperList;

    public RecyclerViewAdapter(Context context, List<Wallpaper> list) {
        this.context = context;
        this.wallpaperList = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item, parent, false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        ImageView iv_wallpaper = holder.getIv_wallpaper();
        iv_wallpaper.setScaleX(0);
        iv_wallpaper.setScaleY(0);
        iv_wallpaper.setAlpha(0f);

        Glide.with(context)
                .load(wallpaperList.get(position).getUrl())
                .into(iv_wallpaper);

        iv_wallpaper.animate()
                .setDuration(100)
                .alpha(1f)
                .scaleY(1f)
                .scaleX(1f);
        iv_wallpaper.setOnClickListener( v-> {
            Intent i = new Intent(context, SetUpWallpaperActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra(WALLPAPER_URL,wallpaperList.get(position).getUrl());
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return wallpaperList.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        ImageView iv_wallpaper;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            iv_wallpaper = itemView.findViewById(R.id.iv_wallpaper);
        }

        public ImageView getIv_wallpaper() {
            return iv_wallpaper;
        }
    }
}
