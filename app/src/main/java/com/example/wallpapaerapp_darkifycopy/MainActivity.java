package com.example.wallpapaerapp_darkifycopy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Toolbar toolbar;
    DrawerLayout drawer;

    FloatingActionButton fab_backToTop, fab_getRandomizeWallpaper;

    FrameLayout fl_container;

    RecyclerViewAdapter adapter;
    RecyclerView rv_wallpapers;
    List<Wallpaper> wallpaperList;

    DatabaseHelper database;

    Animation anim_fromBottom;
    Animation anim_toBottom;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);

        drawer = findViewById(R.id.drawer_layout);

        rv_wallpapers = findViewById(R.id.rv_wallpapers);
        fab_backToTop = findViewById(R.id.fab_goToTop);
        fab_getRandomizeWallpaper = findViewById(R.id.fab_getRandomWallpaper);

        fl_container = findViewById(R.id.fl_container);

        anim_fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom_fab_anim);
        anim_toBottom = AnimationUtils.loadAnimation(this, R.anim.to_bottom_fab_anim);

        toolbar.setTitle("");

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);

        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        database = new DatabaseHelper(getApplicationContext());


        wallpaperList = database.getAllWallpapersShuffled();
        adapter = new RecyclerViewAdapter(getApplicationContext(), wallpaperList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rv_wallpapers.setAdapter(adapter);

        setListeners();

    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListeners() {
        fab_backToTop.setOnClickListener(v -> {
            rv_wallpapers.smoothScrollToPosition(0);
        });


        fab_getRandomizeWallpaper.setOnClickListener(v -> {
            Collections.shuffle(wallpaperList);
            adapter.notifyDataSetChanged();
        });

        final boolean[] isUp = {false};

        rv_wallpapers.setOnTouchListener(new View.OnTouchListener() {
            float y0 = 0;
            float y1 = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    y0 = event.getY();
                    Log.d("Yaxis", y0 +"");
                    if (Math.abs(y1 - y0) < 1000) return false;
                    if (y1 - y0 < 1 && !isUp[0]) {
                        Log.d("Y", "up");
                        isUp[0] = true;
//                        increaseFabs();
                        fl_container.startAnimation(anim_fromBottom);
                    } else if (y1 - y0 > 1 && isUp[0]) {
                        Log.d("Y", "down");
                        isUp[0] = false;
//                        decreaseFabs();
                        fl_container.startAnimation(anim_toBottom);
                    }
                    y1 = event.getY();
                }

                return false;
            }
        });

//        rv_wallpapers.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                if (dy > 0) {
//                    fl_container.startAnimation(anim_fromBottom);
//                } else if (dy < 0) {
//                    //Scrolling up
//                    fl_container.startAnimation(anim_toBottom);
//                }
//
//
//            }
//        });


    }

    private void decreaseFabs() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                decreaseAnimation(fab_backToTop);
            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                decreaseAnimation(fab_getRandomizeWallpaper);
            }
        });

    }

    private void increaseFabs() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                increaseAnimation(fab_getRandomizeWallpaper);
            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                increaseAnimation(fab_backToTop);
            }
        });


    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void decreaseAnimation(View view) {
        view.animate()
                .scaleY(0f)
                .scaleX(0f)
                .setDuration(100);
    }

    private void increaseAnimation(View view) {

        view.animate()
                .scaleY(1.2f)
                .scaleX(1.2f)
                .setDuration(100)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        fab_backToTop.animate()
                                .setStartDelay(100)
                                .scaleY(1f)
                                .scaleX(1f)
                                .setDuration(50);
                    }
                });

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);
        switch (item.getItemId()) {
            case R.id.nav_wallpapers:
                adapter.getFilter().filter(RecyclerViewAdapter.ALL);
                break;
            case R.id.nav_favorites:
                adapter.getFilter().filter(RecyclerViewAdapter.FAVORITES);
                break;
            case R.id.nav_shareApp:
                break;
            case R.id.nav_privacyPolicy:
                break;
            case R.id.nav_exit:
                finish();
                break;
        }

        return true;
    }
}