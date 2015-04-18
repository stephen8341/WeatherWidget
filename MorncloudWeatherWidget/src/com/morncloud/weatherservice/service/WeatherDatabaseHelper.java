
package com.morncloud.weatherservice.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.morncloud.publics.WeatherConstant;
import com.morncloud.publics.bean.WeatherInfo;
import com.morncloud.publics.util.FileUtil;
import com.morncloud.publics.util.LogUtil;
import com.morncloud.weatherwidget.R;

import java.io.InputStream;

public class WeatherDatabaseHelper extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "WeatherProvider.db";
    static final int DATABASE_VERSION = 2;
    private static WeatherDatabaseHelper mInstance = null;
    Context mContext = null;

    static final String TABLE_WEATHER = "weather";// 包含所有所添加城市往前推5天往后推5天的所有天气数据
    static final String TABLE_CITYS = "citys";// 所有所添加城市的表

    static final String CITY_NAME = "city_name";
    static final String CITY_ID = "city_id";
    static final String ISLOCAL = "isLocal";

    public static synchronized WeatherDatabaseHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new WeatherDatabaseHelper(context);
            mInstance.setContext(context);
        }
        return mInstance;
    }

    private void setContext(Context context) {
        mContext = context;
    }

    private WeatherDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        create_table_citys(db);
        create_table_weather(db);

        try {
            InputStream openRawResource = mContext.getResources()
                    .openRawResource(R.raw.china_city);
            FileUtil.copyFile(openRawResource,
                    WeatherConstant.APP_DATABASE_CHINA_PATH);

        } catch (Exception e) {
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < DATABASE_VERSION) {// database china_city is fixed
            try {
                InputStream openRawResource = mContext.getResources()
                        .openRawResource(R.raw.china_city);
                FileUtil.copyFile(openRawResource,
                        WeatherConstant.APP_DATABASE_CHINA_PATH);

            } catch (Exception e) {
            }
        }
    }

    private void create_table_weather(SQLiteDatabase db) {
        try {
            String sql = "CREATE TABLE " + TABLE_WEATHER + " ("
                    + WeatherInfo.CITY_NAME + " TEXT," + WeatherInfo.CITY_ID
                    + " TEXT," + WeatherInfo.DATE + " TEXT,"
                    + WeatherInfo.NOWTEMPER + " TEXT," + WeatherInfo.MAXTEMPER
                    + " TEXT," + WeatherInfo.MINTEMPER + " TEXT,"
                    + WeatherInfo.WEATHER_DAY + " TEXT,"
                    + WeatherInfo.WEATHER_NIGHT + " TEXT," + WeatherInfo.PM2D5
                    + " TEXT," + WeatherInfo.WIND_DIRECTION + " TEXT,"
                    + WeatherInfo.WIND_POWER + " TEXT," + WeatherInfo.WIND
                    + " TEXT, " + WeatherInfo.AQI + " TEXT, "
                    + WeatherInfo.LAST_UPDATE + " TEXT" + ");";
            LogUtil.log("test", "sql:" + sql);
            db.execSQL(sql);
            LogUtil.log("test", "CREATE TABLE " + TABLE_WEATHER);
        } catch (Exception e) {
            Toast.makeText(mContext, "table weather 创建失败", Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void create_table_citys(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE " + TABLE_CITYS + " (" + CITY_ID
                    + " TEXT," + CITY_NAME + " TEXT," + ISLOCAL + " TEXT"
                    + ");");
            LogUtil.log("test", "CREATE TABLE " + TABLE_CITYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
