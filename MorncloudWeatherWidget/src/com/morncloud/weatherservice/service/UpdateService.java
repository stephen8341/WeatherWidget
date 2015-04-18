
package com.morncloud.weatherservice.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.morncloud.publics.DatabaseHelper;
import com.morncloud.publics.WeatherConstant;
import com.morncloud.publics.util.DateUtil;
import com.morncloud.publics.util.LogUtil;
import com.morncloud.weatherservice.location.LocationController;
import com.morncloud.weatherwidget.R;
import com.morncloud.weatherwidget.WeatherWidget;

public class UpdateService extends Service {

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WeatherConstant.ACTION_WEATHER_UPDATE
                    .equals(action)) {// 天气更新
                String city = intent.getStringExtra("city");
                if (TextUtils.isEmpty(city)) {
                    // String localCity = DatabaseHelper
                    // .getLocalCity(UpdateService.this);
                    // if (TextUtils.isEmpty(localCity)) {
                    locationController.startLocation(false);
                    // } else {
                    WeathersManager.updateAll(UpdateService.this);
                    // }
                } else {
                    String isLocal = intent.getStringExtra("isLocal");
                    if (TextUtils.isEmpty(isLocal)) {
                        isLocal = "0";
                    }
                    WeathersManager.updateCity(UpdateService.this, city,
                            isLocal);
                }
            } else if (WeatherConstant.ACTION_MORNCLOUD_LOCATION_SUCCESS
                    .equals(action)) {// 定位成功
                String city = intent.getStringExtra("city");
                String localCity = DatabaseHelper
                        .getLocalCity(UpdateService.this);
                if (!city.equals(localCity)) {
                    WeathersManager.updateCity(UpdateService.this, city, "1");
                }
                weatherWidget.locationSuccess(context, city);
            } else if (WeatherConstant.ACTION_MORNCLOUD_LOCATION_UPDATE
                    .equals(action)) {// 定位
                locationController.startLocation(intent.getBooleanExtra(
                        WeatherConstant.DATA_ACTIVE_FRESH_LOCATION, false));
            } else if (WeatherConstant.ACTION_MORNCLOUD_LOCATION_FAILURE
                    .equals(action)) {// 定位失败
                weatherWidget.locationFailure(context, intent.getBooleanExtra(
                        WeatherConstant.DATA_ACTIVE_FRESH_LOCATION, false));
            } else if (WeatherConstant.ACTION_MORNCLOUD_WIDGET_CLICK_FRESH_WEATHER
                    .equals(action)) {
                weatherWidget.freshClick(context);
            } else if (WeatherConstant.ACTION_MORNCLOUD_WIDGET_CLICK_LOCATION
                    .equals(action)) {// 点击location
                weatherWidget.locationClick(context);
            } else if (WeatherConstant.ACTION_WEATHER_UPDATE_SUCCESS
                    .equals(action)) {// 天气更新成功
                String city = intent.getStringExtra("city");
                if (DatabaseHelper.isLocalCity(context, city)) {
                    updateWidgetViews(context);
                }
            } else if (WeatherConstant.ACTION_WEATHER_UPDATE_FAILURE
                    .equals(action)) {// 天气更新失败
                updateWidgetViews(context);
            } else if (WeatherConstant.ACTION_WEATHER_UPDATE_NULL
                    .equals(action)) {// 天气更新失败
                updateWidgetViews(context);
            } else if (WeatherConstant.ACTION_WEATHER_TIME_UPDATE
                    .equals(action)) {// 更新时钟
                updateWidgetViews(context);
            } else if (WeatherConstant.ACTION_WEATHER_NIGHT_UPDATE
                    .equals(action)) {
                updateWidgetViews(context);
            } else if (WeatherConstant.ACTION_WEATHER_INIT_UPDATE.equals(action)) {
                updateWidgetViews(context);
                cancelBroadCast(context);
                updateWeatherByFreq();
                updateTimeBroadCast(context);
            }

        }

        private void updateWidgetViews(Context context) {
            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.weather_widget);
            weatherWidget.updateViews(context, views);
            ComponentName provider = new ComponentName(context, WeatherWidget.class);
            AppWidgetManager.getInstance(context).updateAppWidget(provider, views);
        }
    };

    LocationController locationController;
    Handler handler;
    WeatherWidget weatherWidget = WeatherWidget.getInstance();

    // private WeatherWidget mAppWidgetProvider = WeatherWidget.getInstance();

    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver();
        handler = new Handler();
        locationController = LocationController.getInstance(this, handler);
        updateWeatherByFreq();
        updateTimeBroadCast(this);

        LogUtil.log("test", "Service onCreate");
        // mAppWidgetProvider.updateViews1(this);
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(
                WeatherConstant.ACTION_WEATHER_UPDATE);
        filter.addAction(WeatherConstant.ACTION_MORNCLOUD_LOCATION_SUCCESS);
        filter.addAction(WeatherConstant.ACTION_MORNCLOUD_LOCATION_FAILURE);
        filter.addAction(WeatherConstant.ACTION_MORNCLOUD_LOCATION_UPDATE);
        filter.addAction(WeatherConstant.ACTION_MORNCLOUD_WIDGET_CLICK_LOCATION);
        filter.addAction(WeatherConstant.ACTION_MORNCLOUD_WIDGET_CLICK_FRESH_WEATHER);
        filter.addAction(WeatherConstant.ACTION_WEATHER_UPDATE_SUCCESS);
        filter.addAction(WeatherConstant.ACTION_WEATHER_UPDATE_FAILURE);
        filter.addAction(WeatherConstant.ACTION_WEATHER_UPDATE_NULL);
        filter.addAction(WeatherConstant.ACTION_WEATHER_TIME_UPDATE);
        filter.addAction(WeatherConstant.ACTION_WEATHER_INIT_UPDATE);
        filter.addAction(WeatherConstant.ACTION_WEATHER_NIGHT_UPDATE);

        // filter.addAction("android.intent.action.TIME_SET");
        // filter.addAction("android.intent.action.TIMEZONE_CHANGED");
        // filter.addAction("android.intent.action.LOCALE_CHANGED");
        // filter.addAction("android.intent.action.ACTION_PRE_SHUTDOWN");

        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        cancelBroadCast(this);
    }

    /**
     * 根据设定的频率来更新天气
     */
    public void updateWeatherByFreq() {
        // 设定下次更新的意图
        Intent intent = new Intent(WeatherConstant.ACTION_WEATHER_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long updateTimes = System.currentTimeMillis() + 1 * 1000;
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC, updateTimes,
                WeatherConstant.UPDATE_FREQUENCY_TIME, pendingIntent);
    }

    private void updateTimeBroadCast(Context context) {
        Intent intent = new Intent(WeatherConstant.ACTION_WEATHER_TIME_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long updateTimes = System.currentTimeMillis() + DateUtil.getTimeToNextDay() + 5 * 1000;// 加2秒表示有延后
        AlarmManager alarm = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC, updateTimes, 24 * 60 * 60 * 1000,
                pendingIntent);

        intent = new Intent(WeatherConstant.ACTION_WEATHER_NIGHT_UPDATE);
        pendingIntent = PendingIntent.getBroadcast(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        updateTimes = System.currentTimeMillis() + DateUtil.getTimeToNight() + 5 * 1000;// 加2秒表示有延后

        LogUtil.log("test1", "updateTimes=" + updateTimes);

        alarm.setRepeating(AlarmManager.RTC, updateTimes, 12 * 60 * 60 * 1000,
                pendingIntent);
    }

    private void cancelBroadCast(Context context) {
        AlarmManager alarm = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(WeatherConstant.ACTION_WEATHER_TIME_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarm.cancel(pendingIntent);

        intent = new Intent(WeatherConstant.ACTION_WEATHER_UPDATE);
        pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarm.cancel(pendingIntent);

        intent = new Intent(WeatherConstant.ACTION_WEATHER_NIGHT_UPDATE);
        pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarm.cancel(pendingIntent);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}
