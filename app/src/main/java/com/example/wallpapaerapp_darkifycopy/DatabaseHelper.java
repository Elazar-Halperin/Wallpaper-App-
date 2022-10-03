package com.example.wallpapaerapp_darkifycopy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String ID = "id";
    public static final String URL = "url";
    public static final String ID_WALLPAPER = "id_wallpaper";
    public static final String WALLPAPERS = "wallpapers";
    public static final String FAVORITES = "favorites";

    public DatabaseHelper(@Nullable Context context) {
        super(context, "WallpaperApplication.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String wallpaperQuery = "CREATE TABLE " + WALLPAPERS + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + URL + " TEXT"
                + ")";

        String favorites = "CREATE TABLE " + FAVORITES + " ("
                + ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ID_WALLPAPER + " INTEGER,"
                + "FOREIGN KEY (" + ID_WALLPAPER + ") REFERENCES " + WALLPAPERS+"(" + ID +")"
                + ")";

        db.execSQL(wallpaperQuery);
        db.execSQL(favorites);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean insertWallpaper(Wallpaper wallpaper) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(URL, wallpaper.getUrl());

        boolean inserted =  db.insert(WALLPAPERS, null, cv) > 0;

        db.close();

        return inserted;
    }
    public boolean insertToFavorites(Wallpaper wallpaper) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(ID_WALLPAPER, wallpaper.getId());

        boolean inserted =  db.insert(WALLPAPERS, null, cv) > 0;

        db.close();

        return inserted;
    }


    public boolean removeFromFavorites(Wallpaper wallpaper) {
        SQLiteDatabase db = this.getWritableDatabase();

        boolean deleted = db.delete(FAVORITES, ID_WALLPAPER + "==" + wallpaper.getId(), null) > 0;

        db.close();

        return deleted;
    }
    public boolean removeFromWallpapers(Wallpaper wallpaper) {
        SQLiteDatabase db = this.getWritableDatabase();

        boolean deleted = db.delete(WALLPAPERS, ID + "==" + wallpaper.getId(), null) > 0;

        db.close();

        return deleted;
    }

    public List<Wallpaper> getAllWallpapersShuffled() {
        SQLiteDatabase db = this.getReadableDatabase();

        List<Wallpaper> wallpapersList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + WALLPAPERS, null);

        while(cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String url = cursor.getString(1);

            wallpapersList.add(new Wallpaper(id, url));
        }
        Collections.shuffle(wallpapersList);
        db.close();
        cursor.close();

        return wallpapersList;
    }

    public boolean isInFavorites(Wallpaper wallpaper) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + FAVORITES + " WHERE " + ID_WALLPAPER + " == " + wallpaper.getId(), null);

        boolean isFavorite = cursor.moveToFirst();
        db.close();
        cursor.close();

        return isFavorite;
    }
}
