
package com.morncloud.publics.util;

import com.morncloud.weatherwidget.R;
import com.morncloud.weatherwidget.WidgetApplication;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Copyright (C) 2013 MORNCLOUD All Rights Reserved This program contains
 * proprietary information which is a trade secret of MORNCLOUD and/or its
 * affiliates and also is protected as an unpublished work under applicable
 * Copyright laws. Recipient is to retain this program in confidence and is not
 * permitted to use or make copies thereof other than as permitted in a written
 * agreement with MORNCLOUD, unless otherwise expressly allowed by applicable
 * laws
 */

public class DateUtil {
    public static String yyyy_MM_dd = "yyyy-MM-dd";
    public static String HH_MM = "HH:mm";
    public static String MM_dd = "MM/dd";

    public static String getDateAsYYYY_MM_DD(long mills) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(yyyy_MM_dd);
        return dateFormat.format(new Date(mills));
    }

    public static String formatDate(long mills, String target_format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(target_format);
        return dateFormat.format(new Date(mills));
    }

    public static String getNowWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.SUNDAY:
                return WidgetApplication.getInstance().
                        getResources().getString(R.string.sunday);
            case Calendar.MONDAY:
                return WidgetApplication.getInstance().
                        getResources().getString(R.string.monday);
            case Calendar.TUESDAY:
                return WidgetApplication.getInstance().
                        getResources().getString(R.string.tuesday);
            case Calendar.WEDNESDAY:
                return WidgetApplication.getInstance().
                        getResources().getString(R.string.wednesday);
            case Calendar.THURSDAY:
                return WidgetApplication.getInstance().
                        getResources().getString(R.string.thursday);
            case Calendar.FRIDAY:
                return WidgetApplication.getInstance().
                        getResources().getString(R.string.friday);
            case Calendar.SATURDAY:
                return WidgetApplication.getInstance().
                        getResources().getString(R.string.saturday);
            default:
                return WidgetApplication.getInstance().
                        getResources().getString(R.string.saturday);
        }
    }

    public static String getNowTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(HH_MM);
        return dateFormat.format(new Date(System.currentTimeMillis()));
    }

    public static long getTimeToNextDay() {
        Calendar calendar = Calendar.getInstance();
        int hour_of_day = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        return (23 - hour_of_day) * 60 * 60 * 1000 + (59 - minute) * 60 * 1000 + (59 - second)
                * 1000;
    }

    public static long getTimeToNight() {
        Calendar calendar = Calendar.getInstance();
        int hour_of_day = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        LogUtil.log("test1", "hour_of_day=" + hour_of_day);
        LogUtil.log("test1", "minute=" + minute);
        LogUtil.log("test1", "second=" + second);

        if (hour_of_day < 6) {
            return (5 - hour_of_day) * 60 * 60 * 1000 + (59 - minute) * 60 * 1000 + (59 - second)
                    * 1000;
        } else if (hour_of_day < 18) {
            return (17 - hour_of_day) * 60 * 60 * 1000 + (59 - minute) * 60 * 1000 + (59 - second)
                    * 1000;
        } else {
            return (23 - hour_of_day + 6) * 60 * 60 * 1000 + (59 - minute) * 60 * 1000
                    + (59 - second)
                    * 1000;
        }
    }

    public static String getLunarDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getDefault());
        Lunar lunar = new Lunar();
        lunar.getLunar(cal);
        return Lunar.getChinaDayString(lunar.day);
    }

    public static String getWeeksByDate(long mills) {
        String date_yesterday = getDateAsYYYY_MM_DD(System.currentTimeMillis()
                - 1 * 24 * 60 * 60 * 1000);
        String date_today = getDateAsYYYY_MM_DD(System.currentTimeMillis());
        String date_tormorow = getDateAsYYYY_MM_DD(System.currentTimeMillis()
                + 1 * 24 * 60 * 60 * 1000);
        String date = new SimpleDateFormat(yyyy_MM_dd).format(new Date(mills));

        if (date.equals(date_yesterday)) {
            return WidgetApplication.getInstance().
                    getResources().getString(R.string.yesterday);
        } else if (date.equals(date_today)) {
            return WidgetApplication.getInstance().
                    getResources().getString(R.string.today);
        } else if (date.equals(date_tormorow)) {
            return WidgetApplication.getInstance().
                    getResources().getString(R.string.tomorrow);
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(mills);

            switch (calendar.get(Calendar.DAY_OF_WEEK)) {
                case Calendar.SUNDAY:
                    return WidgetApplication.getInstance().
                            getResources().getString(R.string.sunday);
                case Calendar.MONDAY:
                    return WidgetApplication.getInstance().
                            getResources().getString(R.string.monday);
                case Calendar.TUESDAY:
                    return WidgetApplication.getInstance().
                            getResources().getString(R.string.tuesday);
                case Calendar.WEDNESDAY:
                    return WidgetApplication.getInstance().
                            getResources().getString(R.string.wednesday);
                case Calendar.THURSDAY:
                    return WidgetApplication.getInstance().
                            getResources().getString(R.string.thursday);
                case Calendar.FRIDAY:
                    return WidgetApplication.getInstance().
                            getResources().getString(R.string.friday);
                case Calendar.SATURDAY:
                    return WidgetApplication.getInstance().
                            getResources().getString(R.string.saturday);
                default:
                    return WidgetApplication.getInstance().
                            getResources().getString(R.string.saturday);
            }
        }
    }

    public static boolean isNight() {
        Calendar calendar = Calendar.getInstance();
        int hour_of_day = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour_of_day >= 6 && hour_of_day < 18) {
            return false;
        } else {
            return true;
        }
    }
}
