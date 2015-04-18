
package com.morncloud.weatherservice.service;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class WeatherProvider extends ContentProvider {

    private static final int URI_TABLE_WEATHER = 1;// 查询天气信息
    private static final int URI_TABLE_CITYS = 2;// 查询城市信息

    private static final String AUTHORITY = "com.morncloud.weatherservice";
    private static final UriMatcher URI_MATCHER = new UriMatcher(
            UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(AUTHORITY, "weather", URI_TABLE_WEATHER);
        URI_MATCHER.addURI(AUTHORITY, "citys", URI_TABLE_CITYS);
    }

    private SQLiteOpenHelper mOpenHelper = null;
    private SQLiteDatabase mWritableDatabase = null;
    private SQLiteDatabase mReadableDatabase = null;

    @Override
    public boolean onCreate() {
        mOpenHelper = WeatherDatabaseHelper.getInstance(getContext());
        return (mOpenHelper != null);
    }

    public SQLiteDatabase getWritableDatabase() {
        if (mWritableDatabase == null) {
            synchronized (mOpenHelper) {
                if (mWritableDatabase == null) {
                    mWritableDatabase = mOpenHelper.getWritableDatabase();
                }
            }
        }
        return mWritableDatabase;
    }

    public SQLiteDatabase getReadableDatabase() {
        if (mReadableDatabase == null) {
            synchronized (mOpenHelper) {
                if (mReadableDatabase == null) {
                    mReadableDatabase = mOpenHelper.getReadableDatabase();
                }
            }
        }
        return mReadableDatabase;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();
        Uri myUri = null;
        long rowId = 0;
        switch (URI_MATCHER.match(uri)) {
            case URI_TABLE_WEATHER: {// 插入天气天气信息
                rowId = db.insert(WeatherDatabaseHelper.TABLE_WEATHER,
                        WeatherDatabaseHelper.CITY_ID, values);
                break;
            }
            case URI_TABLE_CITYS: {
                rowId = db.insert(WeatherDatabaseHelper.TABLE_CITYS,
                        WeatherDatabaseHelper.CITY_ID, values);
                break;
            }
            default:
                break;
        }
        if (rowId > 0) {
            myUri = ContentUris.withAppendedId(uri, rowId);
        }
        return myUri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;

        switch (URI_MATCHER.match(uri)) {
            case URI_TABLE_WEATHER: {// 插入天气天气信息
                cursor = db.query(WeatherDatabaseHelper.TABLE_WEATHER, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case URI_TABLE_CITYS: {
                cursor = db.query(WeatherDatabaseHelper.TABLE_CITYS, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            }
            default:
                break;
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        SQLiteDatabase db = getWritableDatabase();
        int affectedRows = 0;

        switch (URI_MATCHER.match(uri)) {
            case URI_TABLE_WEATHER: {// 插入天气天气信息
                affectedRows = db.update(WeatherDatabaseHelper.TABLE_WEATHER,
                        values, selection, selectionArgs);
                break;
            }
            case URI_TABLE_CITYS: {
                affectedRows = db.update(WeatherDatabaseHelper.TABLE_CITYS, values,
                        selection, selectionArgs);
                break;
            }
            default:
                break;
        }
        return affectedRows;
    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase db = getWritableDatabase();
        int affectedRows = 0;
        switch (URI_MATCHER.match(uri)) {
            case URI_TABLE_WEATHER: {// 插入天气天气信息
                affectedRows = db.delete(WeatherDatabaseHelper.TABLE_WEATHER,
                        selection, selectionArgs);
                break;
            }
            case URI_TABLE_CITYS: {
                affectedRows = db.delete(WeatherDatabaseHelper.TABLE_CITYS,
                        selection, selectionArgs);
                break;
            }
            default:
                break;
        }
        return affectedRows;
    }

}
