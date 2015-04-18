
package com.morncloud.publics;

import java.util.ArrayList;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.morncloud.publics.bean.WeatherInfo;
import com.morncloud.publics.util.DateUtil;
import com.morncloud.publics.util.LogUtil;

public class DatabaseHelper {
    public static final Uri uri_weather = Uri
            .parse("content://com.morncloud.weatherservice/weather");
    public static final Uri uri_citys = Uri
            .parse("content://com.morncloud.weatherservice/citys");

    /**
     * 获取当前城市
     * 
     * @param context
     * @return
     */
    public synchronized static String getLocalCity(Context context) {
        ContentResolver cr = context.getContentResolver();
        Cursor c = null;
        try {
            c = cr.query(uri_citys, new String[] {
                    "city_name"
            }, "isLocal=?",
                    new String[] {
                        "1"
                    }, null);
            if (c != null && c.moveToNext()) {
                return c.getString(0);
            }
        } catch (Exception e) {
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return null;
    }

    /**
     * 判断城市是否已经添加
     * 
     * @param context
     * @return
     */
    public synchronized static boolean isCityAdd(Context context, String city) {
        ContentResolver cr = context.getContentResolver();
        Cursor c = null;
        try {
            c = cr.query(uri_citys, new String[] {
                    "city_name"
            },
                    "city_name=?", new String[] {
                        city
                    }, null);
            if (c != null && c.moveToNext()) {
                return true;
            }
        } catch (Exception e) {
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return false;
    }

    /**
     * 判断是否是当前城市
     * 
     * @param context
     * @return
     */
    public synchronized static boolean isLocalCity(Context context, String city) {
        ContentResolver cr = context.getContentResolver();
        Cursor c = null;
        try {
            c = cr.query(uri_citys, new String[] {
                    "isLocal"
            }, "city_name=?",
                    new String[] {
                        city
                    }, null);
            if (c != null && c.moveToNext()) {
                String isLocal = c.getString(0);
                return "1".equals(isLocal) ? true : false;
            } else {
                return false;
            }
        } catch (Exception e) {
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return false;
    }

    /**
     * 获取所有的城市
     * 
     * @param context
     * @return
     */
    public synchronized static String[] getCitys(Context context) {
        String[] citys = null;
        ContentResolver cr = context.getContentResolver();
        Cursor c = null;
        try {
            c = cr.query(uri_citys, new String[] {
                    "city_name", "isLocal"
            },
                    null, null, null);
            citys = new String[c.getCount()];
            int i = 0;
            String temp;
            while (c != null && c.moveToNext()) {
                if ("1".equals(c.getString(1)) && i > 0) {
                    temp = citys[0];
                    citys[0] = c.getString(0);
                    citys[i++] = temp;
                } else {
                    citys[i++] = c.getString(0);
                }
            }
        } catch (Exception e) {
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return citys;
    }

    /**
     * 删除5天前的天气数据
     * 
     * @param context
     * @param cityId
     */
    public synchronized static void deleteOldWeathers(Context context) {
        ContentResolver cr = context.getContentResolver();
        String date_5_ago = DateUtil.getDateAsYYYY_MM_DD(System
                .currentTimeMillis() - 5 * 24 * 60 * 60 * 1000);
        cr.delete(uri_weather, WeatherInfo.DATE + " < ?",
                new String[] {
                    date_5_ago
                });

    }

    /**
     * 删除指定城市的天气数据
     * 
     * @param context
     * @param cityId
     */
    public synchronized static void deleteCityData(Context context, String city) {
        ContentResolver cr = context.getContentResolver();
        cr.delete(uri_weather, WeatherInfo.CITY_NAME + " = ?",
                new String[] {
                    city
                });
        cr.delete(uri_citys, WeatherInfo.CITY_NAME + " = ?",
                new String[] {
                    city
                });

    }

    public synchronized static void writeWeather(Context context,
            ArrayList<WeatherInfo> weaherInfos) {
        if (weaherInfos == null) {
            return;
        }

        ContentResolver cr = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        int update;
        String maxtemper;
        String mintemper;
        for (WeatherInfo wi : weaherInfos) {
            contentValues.put(WeatherInfo.CITY_ID,
                    wi.getValueByKey(WeatherInfo.CITY_ID));
            contentValues.put(WeatherInfo.CITY_NAME,
                    wi.getValueByKey(WeatherInfo.CITY_NAME));
            contentValues.put(WeatherInfo.DATE,
                    wi.getValueByKey(WeatherInfo.DATE));
            contentValues.put(WeatherInfo.LAST_UPDATE,
                    wi.getValueByKey(WeatherInfo.LAST_UPDATE));

            maxtemper = wi.getValueByKey(WeatherInfo.MAXTEMPER);
            mintemper = wi.getValueByKey(WeatherInfo.MINTEMPER);
            if (!maxtemper.equals(mintemper)) {
                contentValues.put(WeatherInfo.MAXTEMPER, maxtemper);
                contentValues.put(WeatherInfo.MINTEMPER, mintemper);
            }
            contentValues.put(WeatherInfo.NOWTEMPER,
                    wi.getValueByKey(WeatherInfo.NOWTEMPER));
            contentValues.put(WeatherInfo.AQI,
                    wi.getValueByKey(WeatherInfo.AQI));
            contentValues.put(WeatherInfo.PM2D5,
                    wi.getValueByKey(WeatherInfo.PM2D5));
            contentValues.put(WeatherInfo.WEATHER_DAY,
                    wi.getValueByKey(WeatherInfo.WEATHER_DAY));
            contentValues.put(WeatherInfo.WIND_DIRECTION,
                    wi.getValueByKey(WeatherInfo.WIND_DIRECTION));
            contentValues.put(WeatherInfo.WIND_POWER,
                    wi.getValueByKey(WeatherInfo.WIND_POWER));
            contentValues.put(WeatherInfo.WIND,
                    wi.getValueByKey(WeatherInfo.WIND));

            update = cr
                    .update(uri_weather,
                            contentValues,
                            WeatherInfo.CITY_NAME + "=? and "
                                    + WeatherInfo.DATE + "=?",
                            new String[] {
                                    wi.getValueByKey(WeatherInfo.CITY_NAME),
                                    wi.getValueByKey(WeatherInfo.DATE)
                            });

            if (update <= 0) {
                cr.insert(uri_weather, contentValues);
            }

            contentValues.clear();
        }

    }

    public synchronized static void insertCity(Context context, String city,
            String cityId, String isLocal) {
        if (city == null) {
            return;
        }

        ContentResolver cr = context.getContentResolver();
        ContentValues contentValues = new ContentValues();

        contentValues.put(WeatherInfo.CITY_ID, cityId);
        contentValues.put(WeatherInfo.CITY_NAME, city);
        String localCity = getLocalCity(context);
        if (TextUtils.isEmpty(localCity)) {
            contentValues.put(WeatherInfo.ISLOCAL, "1");
        } else {
            contentValues.put(WeatherInfo.ISLOCAL, isLocal);
        }

        cr.delete(uri_citys, "city_name=?", new String[] {
                city
        });

        if ("1".equals(isLocal)) {// 插入的是当前城市
            cr.delete(uri_citys, "isLocal=?", new String[] {
                    "1"
            });
        }

        cr.insert(uri_citys, contentValues);

    }

    public synchronized static WeatherInfo getTodayData(Context context,
            String city) {
        WeatherInfo weatherInfo = null;

        if (city == null) {
            return weatherInfo;
        }

        ContentResolver cr = context.getContentResolver();
        Cursor c = null;
        try {
            c = cr.query(
                    uri_weather,
                    null,
                    "city_name=? and date=?",
                    new String[] {
                            city,
                            DateUtil.getDateAsYYYY_MM_DD(System
                                    .currentTimeMillis())
                    }, null);
            if (c != null && c.moveToNext()) {
                weatherInfo = new WeatherInfo();
                weatherInfo.setValueByKey(WeatherInfo.CITY_ID,
                        c.getString(c.getColumnIndex(WeatherInfo.CITY_ID)));
                weatherInfo.setValueByKey(WeatherInfo.CITY_NAME,
                        c.getString(c.getColumnIndex(WeatherInfo.CITY_NAME)));
                weatherInfo.setValueByKey(WeatherInfo.DATE,
                        c.getString(c.getColumnIndex(WeatherInfo.DATE)));
                weatherInfo.setValueByKey(WeatherInfo.LAST_UPDATE,
                        c.getString(c.getColumnIndex(WeatherInfo.LAST_UPDATE)));
                weatherInfo.setValueByKey(WeatherInfo.NOWTEMPER,
                        c.getString(c.getColumnIndex(WeatherInfo.NOWTEMPER)));
                weatherInfo.setValueByKey(WeatherInfo.MAXTEMPER,
                        c.getString(c.getColumnIndex(WeatherInfo.MAXTEMPER)));
                weatherInfo.setValueByKey(WeatherInfo.MINTEMPER,
                        c.getString(c.getColumnIndex(WeatherInfo.MINTEMPER)));
                weatherInfo.setValueByKey(WeatherInfo.AQI,
                        c.getString(c.getColumnIndex(WeatherInfo.AQI)));
                weatherInfo.setValueByKey(WeatherInfo.PM2D5,
                        c.getString(c.getColumnIndex(WeatherInfo.PM2D5)));
                weatherInfo.setValueByKey(WeatherInfo.WEATHER_DAY,
                        c.getString(c.getColumnIndex(WeatherInfo.WEATHER_DAY)));
                weatherInfo
                        .setValueByKey(WeatherInfo.WEATHER_NIGHT, c.getString(c
                                .getColumnIndex(WeatherInfo.WEATHER_NIGHT)));
                weatherInfo.setValueByKey(WeatherInfo.WIND_DIRECTION,
                        c.getString(c
                                .getColumnIndex(WeatherInfo.WIND_DIRECTION)));
                weatherInfo.setValueByKey(WeatherInfo.WIND_POWER,
                        c.getString(c.getColumnIndex(WeatherInfo.WIND_POWER)));
                weatherInfo.setValueByKey(WeatherInfo.WIND,
                        c.getString(c.getColumnIndex(WeatherInfo.WIND)));

            }
        } catch (Exception e) {
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return weatherInfo;
    }

    public synchronized static String getUpdateTime(Context context) {
        ContentResolver cr = context.getContentResolver();
        Cursor c = null;
        try {
            c = cr.query(uri_weather, new String[] {
                    WeatherInfo.LAST_UPDATE
            },
                    "date=? and city_name=?", new String[] {
                            DateUtil
                                    .getDateAsYYYY_MM_DD(System.currentTimeMillis()),
                            getLocalCity(context)
                    },
                    null);
            if (c != null && c.moveToNext()) {
                return c.getString(0);

            }
        } catch (Exception e) {
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return null;
    }

    public synchronized static ArrayList<ArrayList<WeatherInfo>> getAllDatas(
            Context context) {
        String[] citys = getCitys(context);
        if (citys == null) {
            return null;
        }

        ContentResolver cr = context.getContentResolver();
        Cursor c = null;
        try {
            ArrayList<ArrayList<WeatherInfo>> allWeatherInfos = new ArrayList<ArrayList<WeatherInfo>>();
            ArrayList<WeatherInfo> weatherInfos = new ArrayList<WeatherInfo>();
            WeatherInfo weatherInfo;
            String date = DateUtil.getDateAsYYYY_MM_DD(System
                    .currentTimeMillis() - 2 * 24 * 60 * 60 * 1000);
            String date4a = DateUtil.getDateAsYYYY_MM_DD(System
                    .currentTimeMillis() + 4 * 24 * 60 * 60 * 1000);
            for (String city : citys) {
                c = cr.query(uri_weather, null, "city_name=? and "
                        + WeatherInfo.DATE + " > ? and " + WeatherInfo.DATE
                        + " <?", new String[] {
                        city, date, date4a
                }, WeatherInfo.DATE);
                weatherInfos = new ArrayList<WeatherInfo>();
                LogUtil.log("test2", city + date);
                while (c != null && c.moveToNext()) {
                    weatherInfo = new WeatherInfo();
                    weatherInfo.setValueByKey(WeatherInfo.CITY_ID,
                            c.getString(c.getColumnIndex(WeatherInfo.CITY_ID)));
                    weatherInfo
                            .setValueByKey(WeatherInfo.CITY_NAME, c.getString(c
                                    .getColumnIndex(WeatherInfo.CITY_NAME)));
                    weatherInfo.setValueByKey(WeatherInfo.DATE,
                            c.getString(c.getColumnIndex(WeatherInfo.DATE)));
                    weatherInfo.setValueByKey(WeatherInfo.LAST_UPDATE, c
                            .getString(c
                                    .getColumnIndex(WeatherInfo.LAST_UPDATE)));
                    weatherInfo
                            .setValueByKey(WeatherInfo.NOWTEMPER, c.getString(c
                                    .getColumnIndex(WeatherInfo.NOWTEMPER)));
                    weatherInfo
                            .setValueByKey(WeatherInfo.MAXTEMPER, c.getString(c
                                    .getColumnIndex(WeatherInfo.MAXTEMPER)));
                    weatherInfo
                            .setValueByKey(WeatherInfo.MINTEMPER, c.getString(c
                                    .getColumnIndex(WeatherInfo.MINTEMPER)));
                    weatherInfo.setValueByKey(WeatherInfo.AQI,
                            c.getString(c.getColumnIndex(WeatherInfo.AQI)));
                    weatherInfo.setValueByKey(WeatherInfo.PM2D5,
                            c.getString(c.getColumnIndex(WeatherInfo.PM2D5)));
                    weatherInfo.setValueByKey(WeatherInfo.WEATHER_DAY, c
                            .getString(c
                                    .getColumnIndex(WeatherInfo.WEATHER_DAY)));
                    weatherInfo
                            .setValueByKey(
                                    WeatherInfo.WEATHER_NIGHT,
                                    c.getString(c
                                            .getColumnIndex(WeatherInfo.WEATHER_NIGHT)));
                    weatherInfo
                            .setValueByKey(
                                    WeatherInfo.WIND_DIRECTION,
                                    c.getString(c
                                            .getColumnIndex(WeatherInfo.WIND_DIRECTION)));
                    weatherInfo.setValueByKey(WeatherInfo.WIND_POWER,
                            c.getString(c
                                    .getColumnIndex(WeatherInfo.WIND_POWER)));
                    weatherInfo.setValueByKey(WeatherInfo.WIND,
                            c.getString(c.getColumnIndex(WeatherInfo.WIND)));
                    weatherInfos.add(weatherInfo);
                }
                allWeatherInfos.add(weatherInfos);
            }

            return allWeatherInfos;
        } catch (Exception e) {
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return null;
    }

    public synchronized static ArrayList<WeatherInfo> getCityDatas(
            Context context, String city) {

        ContentResolver cr = context.getContentResolver();
        Cursor c = null;
        try {
            ArrayList<WeatherInfo> weatherInfos = new ArrayList<WeatherInfo>();
            WeatherInfo weatherInfo;
            String date = DateUtil.getDateAsYYYY_MM_DD(System
                    .currentTimeMillis() - 2 * 24 * 60 * 60 * 1000);
            String date4a = DateUtil.getDateAsYYYY_MM_DD(System
                    .currentTimeMillis() + 4 * 24 * 60 * 60 * 1000);
            c = cr.query(uri_weather, null,
                    "city_name=? and " + WeatherInfo.DATE + " > ? and "
                            + WeatherInfo.DATE + " <?", new String[] {
                            city,
                            date, date4a
                    }, WeatherInfo.DATE);
            weatherInfos = new ArrayList<WeatherInfo>();
            while (c != null && c.moveToNext()) {
                weatherInfo = new WeatherInfo();
                weatherInfo.setValueByKey(WeatherInfo.CITY_ID,
                        c.getString(c.getColumnIndex(WeatherInfo.CITY_ID)));
                weatherInfo.setValueByKey(WeatherInfo.CITY_NAME,
                        c.getString(c.getColumnIndex(WeatherInfo.CITY_NAME)));
                weatherInfo.setValueByKey(WeatherInfo.DATE,
                        c.getString(c.getColumnIndex(WeatherInfo.DATE)));
                weatherInfo.setValueByKey(WeatherInfo.LAST_UPDATE,
                        c.getString(c.getColumnIndex(WeatherInfo.LAST_UPDATE)));
                weatherInfo.setValueByKey(WeatherInfo.NOWTEMPER,
                        c.getString(c.getColumnIndex(WeatherInfo.NOWTEMPER)));
                weatherInfo.setValueByKey(WeatherInfo.MAXTEMPER,
                        c.getString(c.getColumnIndex(WeatherInfo.MAXTEMPER)));
                weatherInfo.setValueByKey(WeatherInfo.MINTEMPER,
                        c.getString(c.getColumnIndex(WeatherInfo.MINTEMPER)));
                weatherInfo.setValueByKey(WeatherInfo.AQI,
                        c.getString(c.getColumnIndex(WeatherInfo.AQI)));
                weatherInfo.setValueByKey(WeatherInfo.PM2D5,
                        c.getString(c.getColumnIndex(WeatherInfo.PM2D5)));
                weatherInfo.setValueByKey(WeatherInfo.WEATHER_DAY,
                        c.getString(c.getColumnIndex(WeatherInfo.WEATHER_DAY)));
                weatherInfo
                        .setValueByKey(WeatherInfo.WEATHER_NIGHT, c.getString(c
                                .getColumnIndex(WeatherInfo.WEATHER_NIGHT)));
                weatherInfo.setValueByKey(WeatherInfo.WIND_DIRECTION,
                        c.getString(c
                                .getColumnIndex(WeatherInfo.WIND_DIRECTION)));
                weatherInfo.setValueByKey(WeatherInfo.WIND_POWER,
                        c.getString(c.getColumnIndex(WeatherInfo.WIND_POWER)));
                weatherInfo.setValueByKey(WeatherInfo.WIND,
                        c.getString(c.getColumnIndex(WeatherInfo.WIND)));
                weatherInfos.add(weatherInfo);
            }

            return weatherInfos;
        } catch (Exception e) {
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return null;
    }

    public synchronized static String[] getChinaCitys() {
        String[] chinaCitys = null;
        Cursor cursor = null;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(
                    WeatherConstant.APP_DATABASE_CHINA_PATH, null,
                    SQLiteDatabase.OPEN_READONLY);

            String sql = "select CITY.[cityname],PROVINCE.[provincename] from CITY inner join PROVINCE on CITY.[provinceid]=PROVINCE.[provinceid]";
            cursor = db.rawQuery(sql, null);

            String city;
            String province;
            int i = 0;
            chinaCitys = new String[cursor.getCount()];
            while (cursor != null && cursor.moveToNext()) {
                city = cursor.getString(0);
                province = cursor.getString(1);
                chinaCitys[i++] = city + " - " + province;
            }
        } catch (Exception e) {
            Log.e("test error", e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return chinaCitys;
    }

}
