
package com.morncloud.weatherservice.service;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.morncloud.publics.bean.WeatherInfo;
import com.morncloud.weatherwidget.R;
import com.morncloud.weatherwidget.WidgetApplication;

/**
 * Copyright (C) 2013 MORNCLOUD All Rights Reserved This program contains
 * proprietary information which is a trade secret of MORNCLOUD and/or its
 * affiliates and also is protected as an unpublished work under applicable
 * Copyright laws. Recipient is to retain this program in confidence and is not
 * permitted to use or make copies thereof other than as permitted in a written
 * agreement with MORNCLOUD, unless otherwise expressly allowed by applicable
 * laws
 */

public class WeatherUtil {
    /*
     * 根据xml字符串来解析相应的内容
     * @param xml
     */
    public static ArrayList<WeatherInfo> parseWeaherInfo(String content)
            throws JSONException {
        ArrayList<WeatherInfo> weatherInfos = new ArrayList<WeatherInfo>();

        JSONObject jsonObject = new JSONObject(content);
        String updateTime = jsonObject.getString("ptime");
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        int length = jsonArray.length();
        JSONObject json;
        WeatherInfo weatherInfo;
        String temperature;
        String temperature_now;
        String climate;
        String wind;
        for (int i = 0; i < length; i++) {
            json = jsonArray.getJSONObject(i);
            weatherInfo = new WeatherInfo();
            weatherInfo.setValueByKey(WeatherInfo.DATE, json.getString("date"));
            weatherInfo.setValueByKey(WeatherInfo.CITY_NAME,
                    jsonObject.getString("location"));
            temperature = json.getString("temperature");
            weatherInfo.setValueByKey(WeatherInfo.MAXTEMPER,
                    temperature.substring(0, temperature.indexOf("/") - 1));
            temperature_now = json.getString("temperature_now");
            if (temperature_now.length() > 0) {
                weatherInfo.setValueByKey(WeatherInfo.NOWTEMPER,
                        temperature_now.substring(0,
                                temperature_now.length() - 1));
            }
            weatherInfo.setValueByKey(WeatherInfo.MINTEMPER, temperature
                    .substring(temperature.indexOf("/") + 1,
                            temperature.length() - 1));
            weatherInfo.setValueByKey(WeatherInfo.PM2D5,
                    json.getString("PM2.5"));
            wind = json.getString("wind");
            wind = wind.substring(0, wind.indexOf("/"));
            wind = wind.replace(
                    WidgetApplication.getInstance().getResources()
                            .getString(R.string.no_continue_wind), "");
            weatherInfo.setValueByKey(WeatherInfo.WIND, wind);
            climate = json.getString("climate");
            weatherInfo.setValueByKey(WeatherInfo.WEATHER_DAY,
                    climate.substring(0, climate.indexOf("/")));
            weatherInfo.setValueByKey(
                    WeatherInfo.WEATHER_NIGHT,
                    climate.substring(climate.indexOf("/") + 1,
                            climate.length()));
            weatherInfo.setValueByKey(WeatherInfo.AQI, json.getString("AQI"));
            weatherInfo.setValueByKey(WeatherInfo.LAST_UPDATE, updateTime);
            weatherInfos.add(weatherInfo);
        }

        return weatherInfos;
    }

    public static void put2Weather(WeatherInfo wi, String key, String value) {
        if ("city_id".equals(key)) {
            wi.setValueByKey(WeatherInfo.CITY_ID, value);
        } else if ("city".equals(key)) {
            wi.setValueByKey(WeatherInfo.CITY_NAME, value);
        } else if ("date".equals(key)) {
            wi.setValueByKey(WeatherInfo.DATE, value);
        } else if ("temperature1".equals(key)) {
            wi.setValueByKey(WeatherInfo.MAXTEMPER, value);
        } else if ("temperature2".equals(key)) {
            wi.setValueByKey(WeatherInfo.MINTEMPER, value);
        } else if ("direction1".equals(key)) {
            wi.setValueByKey(WeatherInfo.WIND_DIRECTION, value);
        } else if ("power1".equals(key)) {
            wi.setValueByKey(WeatherInfo.WIND_POWER, value);
        } else if ("last_update".equals(key)) {
            wi.setValueByKey(WeatherInfo.LAST_UPDATE, value);
        }
    }

}
