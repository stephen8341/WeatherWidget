
package com.morncloud.publics.util;

import android.content.res.Resources;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
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

public class Utils {
    public static <T> ArrayList<T> mapToList(HashMap<String, T> map) {
        ArrayList<T> list = new ArrayList<T>();
        for (String key : map.keySet()) {
            list.add(map.get(key));
        }
        return list;
    }

    public static String formatProvince(String province) {
        if (TextUtils.isEmpty(province)) {
            return null;
        }
        Resources resources = WidgetApplication.getInstance().getResources();
        if (province.endsWith(resources.getString(R.string.loc_province))
                || province.endsWith(resources.getString(R.string.loc_city))) {
            province = province.substring(0, province.length() - 1);
        } else if (province.startsWith(resources
                .getString(R.string.loc_xinjiang))) {
            return resources.getString(R.string.loc_xinjiang);
        } else if (province
                .startsWith(resources.getString(R.string.loc_xizang))) {
            return resources.getString(R.string.loc_xizang);
        } else if (province.startsWith(resources
                .getString(R.string.loc_ningxia))) {
            return resources.getString(R.string.loc_ningxia);
        } else if (province.startsWith(resources
                .getString(R.string.loc_neimenggu))) {
            return resources.getString(R.string.loc_neimenggu);
        } else if (province.startsWith(resources
                .getString(R.string.loc_guangxi))) {
            return resources.getString(R.string.loc_guangxi);
        } else if (province.startsWith(resources
                .getString(R.string.loc_xianggang))) {
            return resources.getString(R.string.loc_xianggang);
        } else if (province.startsWith(resources.getString(R.string.loc_aomen))) {
            return resources.getString(R.string.loc_aomen);
        }
        return province;
    }

    public static String AQI_transform(int aqi) {
        Resources resources = WidgetApplication.getInstance().getResources();
        switch (aqi / 50) {
            case 0:
                return resources.getString(R.string.air_quality_excellent);
            case 1:
                return resources.getString(R.string.air_quality_good);
            case 2:
                return resources.getString(R.string.air_quality_mid_pollution);
            case 3:
                return resources.getString(R.string.air_quality_moderate_pollution);
            case 4:
            case 5:
                return resources.getString(R.string.air_quality_severe_pollution);
            default:
                return resources.getString(R.string.air_quality_x_severe_pollution);
        }
    }

    public static int AQI_color(int aqi) {
        switch (aqi / 50) {
            case 0:
                return 0xff059902;
            case 1:
                return 0xff999900;
            case 2:
                return 0xff994803;
            case 3:
                return 0xff990909;
            case 4:
            case 5:
                return 0xff992660;
            default:
                return 0xff991d3f;
        }
    }

    public static int getNowTemper(float maxtemper, float mintemper) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour > 4 && hour < 14) {
            return (int) ((maxtemper - mintemper) / 10 * (hour - 4) + mintemper);
        } else {
            int des = hour - 14;
            des = des < 0 ? (des + 24) : des;
            return (int) (maxtemper - (maxtemper - mintemper) / 14 * des);
        }
    }
}
