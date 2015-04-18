
package com.morncloud.publics.bean;

import java.io.Serializable;
import java.util.HashMap;

public class WeatherInfo implements Serializable {
    private static final long serialVersionUID = 8855018121741368901L;

    public static final String CITY_NAME = "city_name";
    public static final String CITY_ID = "city_id";
    public static final String DATE = "date";
    public static final String MAXTEMPER = "MaxTemper";
    public static final String MINTEMPER = "MinTemper";
    public static final String NOWTEMPER = "nowtemper";
    public static final String WEATHER_DAY = "weather_day";
    public static final String WEATHER_NIGHT = "weather_night";
    public static final String PM2D5 = "pm2d5";
    public static final String WIND_DIRECTION = "wind_direction";
    public static final String WIND_POWER = "wind_power";
    public static final String WIND = "wind";
    public static final String AQI = "AQI";
    public static final String LAST_UPDATE = "last_update";
    public static final String ISLOCAL = "isLocal";

    HashMap<String, String> map = new HashMap<String, String>();

    public void setValueByKey(String key, String value) {
        map.put(key, value);
    }

    public String getValueByKey(String key) {
        String value = map.get(key);
        return value == null ? "" : value;
    }
}
