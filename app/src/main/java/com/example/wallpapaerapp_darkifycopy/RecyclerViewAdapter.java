package com.example.wallpapaerapp_darkifycopy;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.viewHolder> implements Filterable {
    public static final String WALLPAPER_URL = "Wallpaper Url";
    Context context;
    List<Wallpaper> wallpaperListFull;
    List<Wallpaper> wallpaperList;
    DatabaseHelper database;


    public RecyclerViewAdapter(Context context, List<Wallpaper> list) {
        this.context = context;
        this.wallpaperList = list;
        wallpaperListFull = new ArrayList<>(wallpaperList);
        database = new DatabaseHelper(context);
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
                .load(wallpaperListFull.get(position).getUrl())
                .into(iv_wallpaper);

        iv_wallpaper.animate()
                .setDuration(100)
                .alpha(1f)
                .scaleY(1f)
                .scaleX(1f);
        iv_wallpaper.setOnClickListener(v -> {
            Intent i = new Intent(context, SetUpWallpaperActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra(WALLPAPER_URL, wallpaperListFull.get(position).getUrl());
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return wallpaperListFull.size();
    }

    @Override
    public Filter getFilter() {
        return wallpaperFilter;
    }

    private Filter wallpaperFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Wallpaper> favorites = new ArrayList<>(database.getAllFavorites());
            FilterResults filterResults = new FilterResults();
            filterResults.values = favorites;

            return filterResults;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            wallpaperList.clear();
            wallpaperList.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };

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
