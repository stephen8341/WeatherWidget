
package com.morncloud.publics;

/**
 * Copyright (C) 2013 MORNCLOUD All Rights Reserved This program contains
 * proprietary information which is a trade secret of MORNCLOUD and/or its
 * affiliates and also is protected as an unpublished work under applicable
 * Copyright laws. Recipient is to retain this program in confidence and is not
 * permitted to use or make copies thereof other than as permitted in a written
 * agreement with MORNCLOUD, unless otherwise expressly allowed by applicable
 * laws
 */

public class WeatherConstant {
    public static final String WEATHER_SER_URL = "http://cloud.dakele.com/api/weather/forecast/";
    public static final long UPDATE_FREQUENCY_TIME = 2 * 60 * 60 * 1000;
    public static final String BAIDU_LOCATION_KEY = "9c7cf73b28c7963fa5095faa36f12961";

    public static final String ACTION_START_WEATHER_SERVICE = "morncloud.intent.action.start.weather.service";
    public static final String ACTION_MORNCLOUD_WIDGET_CLICK_WEATHER = "action.morncloud.widget.click.weather";
    public static final String ACTION_MORNCLOUD_WIDGET_CLICK_LOCATION = "action.morncloud.widget.click.location";
    public static final String ACTION_MORNCLOUD_WIDGET_CLICK_FRESH_WEATHER = "action.morncloud.widget.fresh.weather";
    public static final String ACTION_WEATHER_UPDATE = "com.morncloud.weather.update";
    public static final String ACTION_WEATHER_UPDATE_SUCCESS = "com.morncloud.weather.update.success";
    public static final String ACTION_WEATHER_UPDATE_FAILURE = "com.morncloud.weather.update.failure";
    public static final String ACTION_WEATHER_UPDATE_NULL = "com.morncloud.weather.update.null";

    public static final String ACTION_MORNCLOUD_SCREEN_ON = "action.morncloud.screen.on";
    public static final String ACTION_MORNCLOUD_SCREEN_OFF = "action.morncloud.screen.off";

    public static final String ACTION_MORNCLOUD_LOCATION_SUCCESS = "action.morncloud.weather.location.success";
    public static final String ACTION_MORNCLOUD_LOCATION_UPDATE = "action.morncloud.weather.location.update";// 定位的广播
    public static final String ACTION_MORNCLOUD_LOCATION_FAILURE = "action.morncloud.weather.location.failure";// 定位失败的广播

    public static final String ACTION_WEATHER_TIME_UPDATE = "com.morncloud.weather.time.update";
    public static final String ACTION_WEATHER_NIGHT_UPDATE = "com.morncloud.weather.night.update";
    public static final String ACTION_WEATHER_INIT_UPDATE = "com.morncloud.weather.init.update";

    public static final String DATA_ACTIVE_FRESH_LOCATION = "data_active_fresh_location";

    public static final String APP_DATA_PATH = "/data/data/com.morncloud.weatherwidget/";
    public static final String APP_DATABASE_CHINA_PATH = APP_DATA_PATH
            + "databases/china_city.db";

    public static final String SHAREDPREFERENCES_NAME = "weather_config";
    public static final String IS_OPENED = "is_opened";
    public static final String START_ACTIVITY_FROM_WIDGET = "from_widget";
}
