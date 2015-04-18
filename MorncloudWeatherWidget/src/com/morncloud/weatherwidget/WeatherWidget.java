
package com.morncloud.weatherwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.morncloud.publics.DatabaseHelper;
import com.morncloud.publics.WeatherConstant;
import com.morncloud.publics.bean.WeatherInfo;
import com.morncloud.publics.util.ConnectUtil;
import com.morncloud.publics.util.DateUtil;
import com.morncloud.publics.util.LogUtil;
import com.morncloud.publics.util.ResourceUtil;
import com.morncloud.weatherservice.service.UpdateService;
import com.morncloud.weatherwidget.activity.WeatherActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WeatherWidget extends AppWidgetProvider {
    private static WeatherWidget sInstance;

    public static synchronized WeatherWidget getInstance() {
        if (sInstance == null) {
            sInstance = new WeatherWidget();
        }
        return sInstance;
    }

    @Override
    public void onEnabled(Context context) {
        context.startService(new Intent(context, UpdateService.class));
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        context.stopService(new Intent(
                WeatherConstant.ACTION_START_WEATHER_SERVICE));
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
            int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.weather_widget);
        updateViews(context, views);
        ComponentName provider = new ComponentName(context, WeatherWidget.class);
        AppWidgetManager.getInstance(context).updateAppWidget(provider,
                views);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    public void locationSuccess(final Context context, String city) {
        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.weather_widget);
        updateViews(context, views);
        views.setTextViewText(R.id.tv_location_city,
                city.substring(city.indexOf("|") + 1, city.length()));
        ComponentName provider = new ComponentName(context, WeatherWidget.class);
        AppWidgetManager.getInstance(context).updateAppWidget(provider,
                views);
    }

    public void locationFailure(final Context context, boolean active_fresh) {
        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.weather_widget);
        updateViews(context, views);
        ComponentName provider = new ComponentName(context, WeatherWidget.class);
        AppWidgetManager.getInstance(context).updateAppWidget(provider,
                views);

        if (active_fresh) {
            Intent i = new Intent();
            i.setClassName("com.morncloud.weatherwidget",
                    "com.morncloud.weatherwidget.activity.WidgetDialogActivity");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);

        } else {
            String localCity = DatabaseHelper.getLocalCity(context);
            if (TextUtils.isEmpty(localCity)) {
                Intent i = new Intent();
                i.setClassName("com.morncloud.weatherwidget",
                        "com.morncloud.weatherwidget.activity.WidgetDialogActivity");
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        }

    }

    public void locationClick(final Context context) {
        if (ConnectUtil.isNetworkConnected(context)) {
            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.weather_widget);
            updateViews(context, views);
            views.setTextViewText(R.id.tv_location_city, "正在定位...");
            Intent intent = new Intent(
                    WeatherConstant.ACTION_MORNCLOUD_LOCATION_UPDATE);
            intent.putExtra(WeatherConstant.DATA_ACTIVE_FRESH_LOCATION, true);
            context.sendBroadcast(intent);
            ComponentName provider = new ComponentName(context, WeatherWidget.class);
            AppWidgetManager.getInstance(context).updateAppWidget(provider,
                    views);
        } else {
            Toast.makeText(
                    context,
                    context.getResources().getString(
                            R.string.no_network), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void freshClick(final Context context) {
        String last_update = DatabaseHelper.getUpdateTime(context);
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        Date date;
        try {
            date = dateFormat.parse(last_update);
            long time = date.getTime();
            if (System.currentTimeMillis() - time > WeatherConstant.UPDATE_FREQUENCY_TIME) {// 天气信息过期
                clickOperation(context);
            } else {
                RemoteViews views = new RemoteViews(context.getPackageName(),
                        R.layout.weather_widget);
                updateViews(context, views);
                ComponentName provider = new ComponentName(context, WeatherWidget.class);
                AppWidgetManager.getInstance(context).updateAppWidget(provider, views);
            }
        } catch (Exception e) {
            clickOperation(context);
        }
    }

    private void clickOperation(final Context context) {
        if (ConnectUtil.isNetworkConnected(context)) {
            context.startService(new Intent(context, UpdateService.class));
            context.sendBroadcast(new Intent(
                    WeatherConstant.ACTION_WEATHER_UPDATE));
            freshAnim(context);

        } else {
            Toast.makeText(context,
                    context.getResources().getString(R.string.no_network),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void updateViews(Context context, RemoteViews views) {
        views.setViewVisibility(R.id.tv_init_pomt, View.INVISIBLE);
        views.setViewVisibility(R.id.rl_root_content, View.VISIBLE);
        updateTimeViews(views);
        updateWeatherViews(context, views);
        sendClickEvent(context, views);
    }

    private void updateWeatherViews(Context context, RemoteViews views) {
        // 先查询当前城市
        String localCity = DatabaseHelper.getLocalCity(context);
        // 再通过contentprovider获取当前城市的信息
        WeatherInfo weatherInfo = DatabaseHelper.getTodayData(context,
                localCity);// WeatherResolver.getNowData(context, "北京");
        if (weatherInfo != null) {
            String weather;
            if (DateUtil.isNight()) {
                weather = weatherInfo.getValueByKey(WeatherInfo.WEATHER_NIGHT);
                if (TextUtils.isEmpty(weather)) {
                    weather = weatherInfo.getValueByKey(WeatherInfo.WEATHER_DAY);
                }
            } else {
                weather = weatherInfo.getValueByKey(WeatherInfo.WEATHER_DAY);
            }

            String maxtemper = weatherInfo.getValueByKey(WeatherInfo.MAXTEMPER);
            String mintemper = weatherInfo.getValueByKey(WeatherInfo.MINTEMPER);
            String temper = mintemper + "\b~\b" + maxtemper
                    + context.getResources().getString(R.string.temperature_sign);
            views.setTextViewText(R.id.tv_widget_temper, temper);

            views.setImageViewResource(R.id.iv_weather,
                    ResourceUtil.getResourceByWeather(weather, true, true));

            if (!TextUtils.isEmpty(weather) && weather.length() > 5) {
                weather = weather.substring(0, 4) + "..";
            }

            views.setTextViewText(R.id.tv_widget_weather, weather);
            String city = weatherInfo.getValueByKey(WeatherInfo.CITY_NAME);
            views.setTextViewText(R.id.tv_location_city,
                    city.substring(city.indexOf("|") + 1, city.length()));
            views.setViewVisibility(R.id.rl_weather, View.VISIBLE);
            views.setViewVisibility(R.id.rl_fresh_weather, View.INVISIBLE);
        }
    }

    private void updateTimeViews(RemoteViews views) {
        String lunarDate = DateUtil.getLunarDate();
        String nowWeek = DateUtil.getNowWeek();
        String dateAsYYYY_MM_DD = DateUtil.getDateAsYYYY_MM_DD(System
                .currentTimeMillis());

        LogUtil.log("test",
                "dateAsYYYY_MM_DD=" + dateAsYYYY_MM_DD);// ///////////////////

        views.setTextViewText(R.id.tv_date, dateAsYYYY_MM_DD);
        views.setTextViewText(R.id.tv_week, nowWeek);
        views.setTextViewText(R.id.tv_lunar_date, lunarDate);
    }

    private void sendClickEvent(Context context, RemoteViews views) {
        Intent intent = new Intent(context, WeatherActivity.class);
        intent.putExtra(WeatherConstant.START_ACTIVITY_FROM_WIDGET, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.rl_weather, pendingIntent);

        intent = new Intent(
                WeatherConstant.ACTION_MORNCLOUD_WIDGET_CLICK_LOCATION);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.ll_location, pendingIntent);

        intent = new Intent(
                WeatherConstant.ACTION_MORNCLOUD_WIDGET_CLICK_FRESH_WEATHER);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.iv_fresh_weather, pendingIntent);

        // 点击时钟
        intent = new Intent();
        intent.setClassName("com.android.deskclock",
                "com.android.deskclock.DeskClock");
        pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.the_clock, pendingIntent);
    }

    public void stopFreshAnim(final Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.weather_widget);
        views.setViewVisibility(R.id.pb_fresh_weather, View.INVISIBLE);
        views.setImageViewResource(R.id.iv_fresh_weather, R.drawable.weather_widget_icon_refresh_l);
        updateViews(context, views);
        ComponentName provider = new ComponentName(context, WeatherWidget.class);
        AppWidgetManager.getInstance(context).updateAppWidget(provider,
                views);
    }

    private void freshAnim(final Context context) {// 刷新的帧动画
        final RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.weather_widget);
        views.setViewVisibility(R.id.pb_fresh_weather, View.VISIBLE);
        views.setImageViewResource(R.id.iv_fresh_weather, R.drawable.weather_widget_icon_yin_l);
        ComponentName provider = new ComponentName(context, WeatherWidget.class);
        AppWidgetManager.getInstance(context).updateAppWidget(provider,
                views);
    }
}
