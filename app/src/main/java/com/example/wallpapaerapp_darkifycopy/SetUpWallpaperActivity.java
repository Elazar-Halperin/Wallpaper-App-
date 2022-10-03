package com.example.wallpapaerapp_darkifycopy;

import androidx.appcompat.app.AppCompatActivity;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.URI;
import java.net.URL;

public class SetUpWallpaperActivity extends AppCompatActivity {
    Animation anim_rotateOpen;
    Animation anim_rotateClose;
    Animation anim_fromBottom;
    Animation anim_toBottom;

    FloatingActionButton fab_addToFavorite, fab_expand, fab_saveToGallery, fab_setAsWallpaper, fab_share;

    ImageView iv_wallpaper;

    WallpaperManager wallpaperManager;

    String url;

    private boolean clicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_wallpaper);

        fab_addToFavorite = findViewById(R.id.fab_addToFavorite);
        fab_expand = findViewById(R.id.fab_expand);
        fab_saveToGallery = findViewById(R.id.fab_saveToGallery);
        fab_setAsWallpaper = findViewById(R.id.fab_setAsWallpaper);
        fab_share = findViewById(R.id.fab_share);

        iv_wallpaper = findViewById(R.id.iv_wallpaper);

        anim_rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        anim_rotateClose = AnimationUtils.loadAnimation(this, R.anim.roatate_close_anim);
        anim_fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        anim_toBottom = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);

        wallpaperManager = WallpaperManager.getInstance(getApplicationContext());

        setListeners();

        url = getIntent().getStringExtra(RecyclerViewAdapter.WALLPAPER_URL);

        Glide.with(getApplicationContext())
                .load(url)
                .into(iv_wallpaper);
        clicked = false;
    }

    private void setListeners() {
        fab_expand.setOnClickListener(v -> {
            onExpandButtonClicked();
        });

        fab_share.setOnClickListener(v -> {
            showMessage("You Shred the wallpaper");
        });

        fab_setAsWallpaper.setOnClickListener(v -> {
            setPhoneWallpaper();

        });

        fab_saveToGallery.setOnClickListener(v -> {
            showMessage("Saved to gallery!");
        });
    }

    private void setPhoneWallpaper() {
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(!url.contains("http")) {
                            final String pureBase64Encoded = url.substring(url.indexOf(",") + 1);
                            byte[] decodedString = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            wallpaperManager.setBitmap(decodedByte);
                        } else {
                            URL urlObject = new URL(url);
                            Bitmap bitmap = BitmapFactory.decodeStream(urlObject.openConnection().getInputStream());
                            wallpaperManager.setBitmap(bitmap);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            thread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void onExpandButtonClicked() {
        setVisibility(clicked);
        setAnimation(clicked);
        setClickable(clicked);

        clicked = !clicked;
    }

    private void setVisibility(boolean clicked) {
        if (!clicked) {
            fab_setAsWallpaper.setVisibility(View.VISIBLE);
            fab_share.setVisibility(View.VISIBLE);
            fab_saveToGallery.setVisibility(View.VISIBLE);
        } else {
            fab_setAsWallpaper.setVisibility(View.INVISIBLE);
            fab_share.setVisibility(View.INVISIBLE);
            fab_saveToGallery.setVisibility(View.INVISIBLE);
        }
    }

    private void setAnimation(boolean clicked) {
        if (!clicked) {
            fab_share.startAnimation(anim_fromBottom);
            fab_setAsWallpaper.startAnimation(anim_fromBottom);
            fab_saveToGallery.startAnimation(anim_fromBottom);
            fab_expand.startAnimation(anim_rotateOpen);
        } else {
            fab_expand.startAnimation(anim_rotateClose);
            fab_share.startAnimation(anim_toBottom);
            fab_setAsWallpaper.startAnimation(anim_toBottom);
            fab_saveToGallery.startAnimation(anim_toBottom);
        }
    }

    private void setClickable(boolean clicked) {
        fab_saveToGallery.setClickable(!clicked);
        fab_setAsWallpaper.setClickable(!clicked);
        fab_share.setClickable(!clicked);
    }

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}