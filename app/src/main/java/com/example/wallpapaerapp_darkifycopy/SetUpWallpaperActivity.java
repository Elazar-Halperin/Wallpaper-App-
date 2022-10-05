package com.example.wallpapaerapp_darkifycopy;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Locale;

public class SetUpWallpaperActivity extends AppCompatActivity {
    public static final int DELETE_REQUEST_CODE = 1;
    Animation anim_rotateOpen;
    Animation anim_rotateClose;
    Animation anim_fromBottom;
    Animation anim_toBottom;

    FloatingActionButton fab_addToFavorite, fab_expand, fab_saveToGallery, fab_setAsWallpaper, fab_share;

    ImageView iv_wallpaper;

    WallpaperManager wallpaperManager;

    String url;

    Bitmap bitmap;

    private boolean clicked;

    ContentResolver contentResolver;
    Uri imageContentUri;


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
            shareWallpaper();
        });

        fab_setAsWallpaper.setOnClickListener(v -> {
            setPhoneWallpaper();

        });

        fab_saveToGallery.setOnClickListener(v -> {
            saveWallpaperToGallery();
        });
    }

    private void shareWallpaper() {
        Uri contentUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentUri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        BitmapDrawable drawable = (BitmapDrawable) iv_wallpaper.getDrawable();
        Bitmap image = drawable.getBitmap();

        contentResolver = getApplicationContext().getContentResolver();
        ContentValues newImageDetails = new ContentValues();
        newImageDetails.put(MediaStore.Images.Media.DISPLAY_NAME, "Image." + System.currentTimeMillis() + ".png");
        imageContentUri = contentResolver.insert(contentUri, newImageDetails);

        try (ParcelFileDescriptor fileDescriptor =
                     contentResolver.openFileDescriptor(imageContentUri, "w", null)) {
            FileDescriptor fd = fileDescriptor.getFileDescriptor();
            OutputStream outputStream = new FileOutputStream(fd);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            image.compress(Bitmap.CompressFormat.JPEG, 50, bufferedOutputStream);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();


        } catch (IOException e) {
            Log.e("errorrr", "Error saving bitmap", e);
        }

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_STREAM, imageContentUri);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "some text here");
        sendIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        sendIntent.setType("image/*");
        Intent shareIntent = Intent.createChooser(sendIntent, "Share with");
        startActivityForResult(shareIntent, DELETE_REQUEST_CODE);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DELETE_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                contentResolver.delete(imageContentUri, null);
            }
        }
    }

    private void setPhoneWallpaper() {
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (!url.contains("http")) {
                            final String pureBase64Encoded = url.substring(url.indexOf(",") + DELETE_REQUEST_CODE);
                            byte[] decodedString = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
                            bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            wallpaperManager.setBitmap(bitmap);
                        } else {
                            URL urlObject = new URL(url);
                            bitmap = BitmapFactory.decodeStream(urlObject.openConnection().getInputStream());
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

    private void saveWallpaperToGallery() {
        Uri contentUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentUri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        BitmapDrawable drawable = (BitmapDrawable) iv_wallpaper.getDrawable();
        Bitmap image = drawable.getBitmap();

        ContentResolver cr = getApplicationContext().getContentResolver();
        ContentValues newImageDetails = new ContentValues();
        newImageDetails.put(MediaStore.Images.Media.DISPLAY_NAME, "Image." + System.currentTimeMillis() + ".png");
        Uri uri = cr.insert(contentUri, newImageDetails);

        try (ParcelFileDescriptor fileDescriptor =
                     cr.openFileDescriptor(uri, "w", null)) {
            FileDescriptor fd = fileDescriptor.getFileDescriptor();
            OutputStream outputStream = new FileOutputStream(fd);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            image.compress(Bitmap.CompressFormat.JPEG, 50, bufferedOutputStream);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();

            Toast.makeText(getApplicationContext(), "Saved successfully", Toast.LENGTH_SHORT).show();


        } catch (IOException e) {
            Log.e("errorrr", "Error saving bitmap", e);
            Toast.makeText(getApplicationContext(), "Failed to save the wallpaper, try again.", Toast.LENGTH_SHORT).show();
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