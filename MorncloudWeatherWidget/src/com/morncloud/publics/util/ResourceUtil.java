
package com.morncloud.publics.util;

import android.content.res.Resources;
import android.text.TextUtils;

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

public class ResourceUtil {
    /**
     * select weather icon
     * 
     * @param weather
     * @param isLarge 
     * @return
     */
    public static int getResourceByWeather(String weather, boolean isLarge) {
        if (TextUtils.isEmpty(weather)) {
            return isLarge ? R.drawable.weather_widget_icon_withoutinfo_l
                    : R.drawable.weather_widget_icon_withoutinfo_s;
        }
        Resources resources = WidgetApplication.getInstance().getResources();
        if (resources.getString(R.string.weather_qing).equals(weather)) {
            return isLarge ? R.drawable.weather_widget_icon_qing_l
                    : R.drawable.weather_widget_icon_qing_s;
        } else if (weather.contains(resources
                .getString(R.string.weather_duoyun))) {
            return isLarge ? R.drawable.weather_widget_icon_duoyun_l
                    : R.drawable.weather_widget_icon_duoyun_s;
        } else if (weather.contains(resources.getString(R.string.weather_yin))) {
            return isLarge ? R.drawable.weather_widget_icon_yin_l
                    : R.drawable.weather_widget_icon_yin_s;
        } else if (weather.contains(resources
                .getString(R.string.weather_xiaoyu))) {
            return isLarge ? R.drawable.weather_widget_icon_xiaoyu_l
                    : R.drawable.weather_widget_icon_xiaoyu_s;
        } else if (weather.contains(resources
                .getString(R.string.weather_zhongyu))) {
            return isLarge ? R.drawable.weather_widget_icon_zhongyu_l
                    : R.drawable.weather_widget_icon_zhongyu_s;
        } else if (weather.contains(resources.getString(R.string.weather_dayu))) {
            return isLarge ? R.drawable.weather_widget_icon_dayu_l
                    : R.drawable.weather_widget_icon_dayu_s;
        } else if (weather.contains(resources
                .getString(R.string.weather_zhenyu))) {
            return isLarge ? R.drawable.weather_widget_icon_zhenyu_l
                    : R.drawable.weather_widget_icon_zhenyu_s;
        } else if (weather.contains(resources
                .getString(R.string.weather_leizhenyu))) {
            return isLarge ? R.drawable.weather_widget_icon_leizhenyu_l
                    : R.drawable.weather_widget_icon_leizhenyu_s;
        } else if (weather
                .contains(resources.getString(R.string.weather_baoyu))) {
            return isLarge ? R.drawable.weather_widget_icon_baoyu_l
                    : R.drawable.weather_widget_icon_baoyu_s;
        } else if (weather.contains(resources
                .getString(R.string.weather_dabaoyu))) {
            return isLarge ? R.drawable.weather_widget_icon_dabaoyu_l
                    : R.drawable.weather_widget_icon_dabaoyu_s;
        } else if (weather.contains(resources
                .getString(R.string.weather_tedabaoyu))) {
            return isLarge ? R.drawable.weather_widget_icon_tedabaoyu_l
                    : R.drawable.weather_widget_icon_tedabaoyu_s;
        } else if (weather.contains(resources
                .getString(R.string.weather_xiaoxue))) {
            return isLarge ? R.drawable.weather_widget_icon_xiaoxue_l
                    : R.drawable.weather_widget_icon_xiaoxue_s;
        } else if (weather.contains(resources
                .getString(R.string.weather_zhongxue))) {
            return isLarge ? R.drawable.weather_widget_icon_zhongxue_l
                    : R.drawable.weather_widget_icon_zhongxue_s;
        } else if (weather.contains(resources
                .getString(R.string.weather_zhenxue))) {
            return isLarge ? R.drawable.weather_widget_icon_zhenxue_l
                    : R.drawable.weather_widget_icon_zhenxue_s;
        } else if (weather
                .contains(resources.getString(R.string.weather_daxue))) {
            return isLarge ? R.drawable.weather_widget_icon_daxue_l
                    : R.drawable.weather_widget_icon_daxue_s;
        } else if (weather.contains(resources
                .getString(R.string.weather_yujiaxue))) {
            return isLarge ? R.drawable.weather_widget_icon_yujiaxue_l
                    : R.drawable.weather_widget_icon_yujiaxue_s;
        } else if (weather.contains(resources
                .getString(R.string.weather_baoxue))) {
            return isLarge ? R.drawable.weather_widget_icon_baoxue_l
                    : R.drawable.weather_widget_icon_baoxue_s;
        } else if (weather.contains(resources.getString(R.string.weather_yu))) {
            return isLarge ? R.drawable.weather_widget_icon_zhongyu_l
                    : R.drawable.weather_widget_icon_zhongyu_s;
        } else if (weather.contains(resources.getString(R.string.weather_xue))) {
            return isLarge ? R.drawable.weather_widget_icon_zhongxue_l
                    : R.drawable.weather_widget_icon_zhongxue_s;
        } else if (weather.contains(resources
                .getString(R.string.weather_leizhenyubingbao))) {
            return isLarge ? R.drawable.weather_widget_icon_leizhenyubingbao_l
                    : R.drawable.weather_widget_icon_leizhenyubingbao_s;
        } else if (weather.contains(resources.getString(R.string.weather_wu))) {
            return isLarge ? R.drawable.weather_widget_icon_wu_l
                    : R.drawable.weather_widget_icon_wu_s;
        } else if (weather.contains(resources
                .getString(R.string.weather_shachenbao))) {
            return isLarge ? R.drawable.weather_widget_icon_shachenbao_l
                    : R.drawable.weather_widget_icon_shachenbao_s;
        } else if (weather.contains(resources
                .getString(R.string.weather_mai))) {
            return isLarge ? R.drawable.weather_widget_icon_mai_l
                    : R.drawable.weather_widget_icon_mai_s;
        }
        return isLarge ? R.drawable.weather_widget_icon_withoutinfo_l
                : R.drawable.weather_widget_icon_withoutinfo_s;
    }

    public static int getResourceByWeather(String weather, boolean isLarge, boolean nightMode) {
        if (TextUtils.isEmpty(weather)) {
            return isLarge ? R.drawable.weather_widget_icon_withoutinfo_l
                    : R.drawable.weather_widget_icon_withoutinfo_s;
        }
        Resources resources = WidgetApplication.getInstance().getResources();
        if (nightMode && DateUtil.isNight()) {
            if (resources.getString(R.string.weather_qing).equals(weather)) {
                return isLarge ? R.drawable.weather_widget_icon_yejianqing_l
                        : R.drawable.weather_widget_icon_yejianqing_s;
            } else if (weather.contains(resources
                    .getString(R.string.weather_duoyun)) ||
                    weather.contains(resources.getString(R.string.weather_yin))) {
                return isLarge ? R.drawable.weather_widget_icon_yejianyin_l
                        : R.drawable.weather_widget_icon_yejianyin_s;
            } else {
                return getResourceByWeather(weather, isLarge);
            }
        } else {
            return getResourceByWeather(weather, isLarge);
        }
    }
}
