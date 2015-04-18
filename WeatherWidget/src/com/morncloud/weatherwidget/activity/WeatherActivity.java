
package com.morncloud.weatherwidget.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.morncloud.publics.CommonCallback;
import com.morncloud.publics.DatabaseHelper;
import com.morncloud.publics.WeatherConstant;
import com.morncloud.publics.bean.WeatherInfo;
import com.morncloud.publics.util.ConnectUtil;
import com.morncloud.publics.util.DateUtil;
import com.morncloud.publics.util.DisplayUtil;
import com.morncloud.publics.util.ResourceUtil;
import com.morncloud.publics.util.Utils;
import com.morncloud.weatherservice.service.UpdateService;
import com.morncloud.weatherwidget.R;
import com.morncloud.weatherwidget.adapter.ViewPagerAdapter;
import com.morncloud.weatherwidget.view.WeatherViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Copyright (C) 2013 MORNCLOUD All Rights Reserved This program contains
 * proprietary information which is a trade secret of MORNCLOUD and/or its
 * affiliates and also is protected as an unpublished work under applicable
 * Copyright laws. Recipient is to retain this program in confidence and is not
 * permitted to use or make copies thereof other than as permitted in a written
 * agreement with MORNCLOUD, unless otherwise expressly allowed by applicable
 * laws
 */

public class WeatherActivity extends BaseActivity implements OnClickListener {
    ViewPager viewpager;
    RelativeLayout ll_promt;
    ViewPagerAdapter viewPagerAdapter;
    LayoutInflater layoutInflater;
    HashMap<String, View> views_map = new HashMap<String, View>();
    ArrayList<View> views_list = new ArrayList<View>();
    ArrayList<ArrayList<WeatherInfo>> allWeatherInfos;
    View loc_weather_view;// 当前城市view
    SharedPreferences sp;
    Editor editor;
    LinearLayout ll_dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layoutInflater = LayoutInflater.from(this);
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        sp = getSharedPreferences(WeatherConstant.SHAREDPREFERENCES_NAME,
                Context.MODE_PRIVATE);
        editor = sp.edit();

        if (!sp.getBoolean(WeatherConstant.IS_OPENED, false)) {
            ll_promt = (RelativeLayout) findViewById(R.id.ll_promt);
            Button bt_start = (Button) findViewById(R.id.bt_start);
            ll_promt.setVisibility(View.VISIBLE);
            bt_start.setOnClickListener(this);
        } else {
            loadData();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void loadData() {
        asyncLoad(null, new CommonCallback() {
            @Override
            public void exec() {
                allWeatherInfos = DatabaseHelper
                        .getAllDatas(WeatherActivity.this);
            }
        }, new CommonCallback() {
            @Override
            public void exec() {
                setWeatherViews(allWeatherInfos);
                viewPagerAdapter = new ViewPagerAdapter(views_list);
                viewpager.setAdapter(viewPagerAdapter);

                // 绘制底部引导小点
                initDots();
                setCurrentDot(0);

                String city = getIntent().getStringExtra("city");
                if (!TextUtils.isEmpty(city)) {// 表示添加新城市进来
                    String isLocal = getIntent().getStringExtra("isLocal");
                    if ("1".equals(isLocal) && loc_weather_view != null) {
                        String pre_city = DatabaseHelper.getLocalCity(WeatherActivity.this);
                        views_map.remove(pre_city);
                        views_map.put(city, loc_weather_view);
                        showProgressDialog();
                        View tv_del_city = loc_weather_view
                                .findViewById(R.id.tv_del_city);
                        tv_del_city.setTag(city);
                        tv_del_city.setOnClickListener(WeatherActivity.this);
                        sendWeatherBroadCast(city, isLocal);
                    } else {
                        addWeather(city, getIntent().getStringExtra("isLocal"));
                    }
                }
                registerReceiver();
                viewpager.setOnPageChangeListener(new Weather_PagerChangedListener());

                if (allWeatherInfos.size() > 0) {// 主动刷新第一页
                    initWeatherView(views_list.get(0), allWeatherInfos.get(0),
                            true);
                }

            }

        });
    }

    private void initDots() {
        ll_dots = (LinearLayout) findViewById(R.id.ll_dots);
        int len = allWeatherInfos.size();
        ImageView dot;
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.leftMargin = DisplayUtil.dip2px(WeatherActivity.this, 5);
        for (int i = 0; i <= len; i++) {
            dot = new ImageView(WeatherActivity.this);
            dot.setImageResource(R.drawable.weather_widget_dot_disable);
            ll_dots.addView(dot, params);
        }
    }

    private void setCurrentDot(int position) {
        if (position < 0 || position > ll_dots.getChildCount() - 1) {
            return;
        }

        Integer current_posit = (Integer) ll_dots.getTag();
        if (current_posit != null) {
            ImageView iv = (ImageView) ll_dots.getChildAt(current_posit);
            if (iv != null) {
                iv.setImageResource(R.drawable.weather_widget_dot_disable);
            }
        }
        ll_dots.setTag(position);

        ImageView iv = (ImageView) ll_dots.getChildAt(position);
        if (iv != null) {
            iv.setImageResource(R.drawable.weather_widget_dot_enable);
        }
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WeatherConstant.ACTION_WEATHER_UPDATE_SUCCESS);
        filter.addAction(WeatherConstant.ACTION_WEATHER_UPDATE_FAILURE);
        filter.addAction(WeatherConstant.ACTION_WEATHER_UPDATE_NULL);
        filter.addAction(WeatherConstant.ACTION_MORNCLOUD_LOCATION_SUCCESS);
        filter.addAction(WeatherConstant.ACTION_MORNCLOUD_LOCATION_FAILURE);
        registerReceiver(broadcastReceiver, filter);
    }

    private void setWeatherViews(
            ArrayList<ArrayList<WeatherInfo>> allWeatherInfos) {
        View weather_view;
        if (allWeatherInfos == null) {
            return;
        }

        // 加一个判断 如果数据过老的话， 让service去更新
        weatherUpdateJudge();

        for (ArrayList<WeatherInfo> weatherInfos : allWeatherInfos) {
            weather_view = layoutInflater.inflate(R.layout.weather_view, null);
            addData(weatherInfos.get(0).getValueByKey(WeatherInfo.CITY_NAME),
                    weather_view);
        }

        View weather_view_add = layoutInflater.inflate(
                R.layout.weather_view_add, null);

        View add_city = weather_view_add.findViewById(R.id.add_city);
        add_city.setOnClickListener(this);

        addData("add", weather_view_add);

    }

    private void weatherUpdateJudge() {
        String last_update = DatabaseHelper.getUpdateTime(this);
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        Date date;
        try {
            date = dateFormat.parse(last_update);
            long time = date.getTime();
            if (System.currentTimeMillis() - time > WeatherConstant.UPDATE_FREQUENCY_TIME) {
                startService(new Intent(this, UpdateService.class));// 防止某些情况下service死掉
                sendBroadcast(new Intent(WeatherConstant.ACTION_WEATHER_UPDATE));
            }
        } catch (Exception e) {
            startService(new Intent(this, UpdateService.class));// 防止某些情况下service死掉
            sendBroadcast(new Intent(WeatherConstant.ACTION_WEATHER_UPDATE));
        }
    }

    private void addData(String key, View weather_view) {
        views_map.put(key, weather_view);
        views_list.add(weather_view);
        if (DatabaseHelper.isLocalCity(WeatherActivity.this, key)) {
            loc_weather_view = weather_view;
        }

    }

    private void deleteData(String key, View weather_view) {
        String city;
        ArrayList<WeatherInfo> weatherInfos;
        Iterator<ArrayList<WeatherInfo>> iterator = allWeatherInfos.iterator();
        while (iterator.hasNext()) {
            weatherInfos = iterator.next();
            city = weatherInfos.get(0).getValueByKey(WeatherInfo.CITY_NAME);
            if (key.equals(city)) {
                allWeatherInfos.remove(weatherInfos);
                break;
            }
        }

        views_map.remove(key);
        views_list.remove(weather_view);

        ll_dots.removeViewAt(ll_dots.getChildCount() - 1);
        setCurrentDot(viewpager.getCurrentItem());
    }

    private WeatherInfo findTodayData(ArrayList<WeatherInfo> weatherInfos) {
        for (WeatherInfo wi : weatherInfos) {
            if (wi.getValueByKey(WeatherInfo.DATE).equals(
                    DateUtil.getDateAsYYYY_MM_DD(System.currentTimeMillis()))) {
                return wi;
            }
        }
        return null;
    }

    private void initWeatherView(View weather_view,
            ArrayList<WeatherInfo> weatherInfos, boolean singleFresh) {
        Object tag = weather_view.getTag();// tag表示view是否刷新过
        if (singleFresh && tag != null && (Boolean) tag) {
            return;
        }
        weather_view.setTag(true);

        if (weatherInfos.size() <= 0) {
            return;
        }

        WeatherViewGroup weatherViewGroup;
        TextView tv_weather_city;
        ImageView iv_weather_main;
        TextView tv_temper_main;
        TextView tv_weather_weather;
        ImageView tv_del_city;
        ImageView tv_fresh_city;
        WeatherInfo weatherInfo;
        weatherInfo = findTodayData(weatherInfos);

        tv_weather_city = (TextView) weather_view
                .findViewById(R.id.tv_weather_city);
        iv_weather_main = (ImageView) weather_view
                .findViewById(R.id.iv_weather_main);
        tv_temper_main = (TextView) weather_view
                .findViewById(R.id.tv_temper_main);

        tv_weather_weather = (TextView) weather_view
                .findViewById(R.id.tv_weather_weather);
        TextView tv_weather_wind = (TextView) weather_view
                .findViewById(R.id.tv_weather_wind);
        TextView tv_weather_pm = (TextView) weather_view
                .findViewById(R.id.tv_weather_pm);
        TextView tv_weather_aqi = (TextView) weather_view
                .findViewById(R.id.tv_weather_aqi);

        tv_del_city = (ImageView) weather_view.findViewById(R.id.tv_del_city);
        tv_fresh_city = (ImageView) weather_view.findViewById(R.id.tv_fresh_city);

        String city = weatherInfo.getValueByKey(WeatherInfo.CITY_NAME);
        String weather;
        if (DateUtil.isNight()) {
            weather = weatherInfo.getValueByKey(WeatherInfo.WEATHER_NIGHT);
            if (TextUtils.isEmpty(weather)) {
                weather = weatherInfo.getValueByKey(WeatherInfo.WEATHER_DAY);
            }
        } else {
            weather = weatherInfo.getValueByKey(WeatherInfo.WEATHER_DAY);
        }

        if (DatabaseHelper.isLocalCity(WeatherActivity.this, city)) {
            weather_view.findViewById(R.id.tv_weather_loc_icon).setVisibility(
                    View.VISIBLE);
            tv_weather_city.setOnClickListener(this);
            // loc_weather_view = weather_view;
        }

        tv_weather_city.setText(subCity(city));
        weather_view.findViewById(R.id.iv_weather_fresh).setVisibility(View.GONE);
        iv_weather_main.setVisibility(View.VISIBLE);
        iv_weather_main.setImageResource(ResourceUtil.getResourceByWeather(
                weather, true, true));
        try {
            int nowtemper = Integer.parseInt(weatherInfo.getValueByKey(WeatherInfo.NOWTEMPER));
            int maxtemper = Integer.parseInt(weatherInfo
                    .getValueByKey(WeatherInfo.MAXTEMPER));
            int mintemper = Integer.parseInt(weatherInfo
                    .getValueByKey(WeatherInfo.MINTEMPER));
            if (nowtemper < mintemper || nowtemper > maxtemper) {
                nowtemper = Utils.getNowTemper(maxtemper, mintemper);
            }
            tv_temper_main.setText(nowtemper + getResources().getString(R.string.temperature_sign));
        } catch (NumberFormatException e1) {

        }

        String aqi = weatherInfo.getValueByKey(WeatherInfo.AQI);
        int color = 0xffffff;
        if (!TextUtils.isEmpty(aqi)) {
            try {
                int aqi_int = Integer.parseInt(aqi);
                aqi = Utils.AQI_transform(aqi_int);
                color = Utils.AQI_color(aqi_int);
            } catch (NumberFormatException e) {
            }

        }
        tv_weather_weather.setText(weather);
        String wind = weatherInfo.getValueByKey(WeatherInfo.WIND);
        if (TextUtils.isEmpty(wind)) {
            weather_view.findViewById(R.id.tv_weather_wind_div).setVisibility(
                    View.GONE);
        } else {
            tv_weather_wind.setText(wind);
        }
        String pm = weatherInfo.getValueByKey(WeatherInfo.PM2D5);
        if (TextUtils.isEmpty(pm)) {
            weather_view.findViewById(R.id.tv_weather_pm_div).setVisibility(
                    View.GONE);
        } else {
            tv_weather_pm.setText(pm);
            Drawable left = getResources().getDrawable(
                    R.drawable.weather_widget_image_pm2d5);
            left.setBounds(0, DisplayUtil.dip2px(this, 1),
                    left.getMinimumWidth(),
                    left.getMinimumHeight());
            tv_weather_pm.setCompoundDrawables(left, null, null, null);
        }
        if (TextUtils.isEmpty(aqi)) {
            weather_view.findViewById(R.id.tv_weather_aqi_div).setVisibility(
                    View.GONE);
        } else {
            tv_weather_aqi.setTextColor(color);
            tv_weather_aqi.setText(aqi);
        }

        tv_del_city.setOnClickListener(this);
        tv_fresh_city.setOnClickListener(this);
        tv_del_city.setTag(city);

        weatherViewGroup = (WeatherViewGroup) weather_view
                .findViewById(R.id.weatherViewGroup);
        weatherViewGroup.freshViews(weatherInfos, singleFresh);

        boolean localCity = DatabaseHelper.isLocalCity(this, city);
        if (localCity) {
            tv_fresh_city.setVisibility(View.VISIBLE);
        } else {
            tv_del_city.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_city:// 添加城市
                Intent locIntent = new Intent(this, CitySearchActivity.class);
                startActivity(locIntent);
                break;
            case R.id.tv_weather_city:// 点击城市
                reFreshWeather();

                break;
            case R.id.tv_fresh_city:// 点击城市
                reFreshWeather();

                break;
            case R.id.tv_del_city:// 删除城市
                String city = (String) v.getTag();
                if (!TextUtils.isEmpty(city)) {
                    viewpager.setCurrentItem(viewpager.getCurrentItem() - 1);
                    View weather_view = views_map.get(city);
                    deleteData(city, weather_view);
                    viewPagerAdapter.notifyDataSetChanged();
                    DatabaseHelper.deleteCityData(this, city);
                    showToast("删除城市" + subCity(city));
                }
                break;
            case R.id.iv_weather_fresh:// 刷新天气
                city = (String) v.getTag();
                if (!TextUtils.isEmpty(city)) {
                    showProgressDialog();
                    sendWeatherBroadCast(city, "0");
                }
                break;
            case R.id.bt_start:// 启动页
                editor.putBoolean(WeatherConstant.IS_OPENED, true).commit();
                Animation animation = new AlphaAnimation(1.0f, 0.0f);
                animation.setDuration(1000);
                animation.setAnimationListener(new AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ll_promt.setVisibility(View.GONE);
                        loadData();
                    }
                });
                ll_promt.startAnimation(animation);

                break;
            default:
                break;
        }

    }

    private void reFreshWeather() {
        if (ConnectUtil.isNetworkConnected(this)) {
            ((TextView) (views_list.get(0)
                    .findViewById(R.id.tv_weather_city))).setText("正在定位...");
            Intent intent = new Intent(
                    WeatherConstant.ACTION_MORNCLOUD_LOCATION_UPDATE);
            intent.putExtra(WeatherConstant.DATA_ACTIVE_FRESH_LOCATION, true);
            sendBroadcast(intent);
        } else {
            showToast(getResources().getString(
                    R.string.no_network));
        }
    }

    private String subCity(String city) {
        return city.substring(city.indexOf("|") + 1,
                city.length());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String city = intent.getStringExtra("city");
        String isLocal = intent.getStringExtra("isLocal");
        if ("1".equals(isLocal)) {
            String pre_city = DatabaseHelper.getLocalCity(WeatherActivity.this);
            views_map.remove(pre_city);
            views_map.put(city, loc_weather_view);
            showProgressDialog();
            View tv_del_city = loc_weather_view
                    .findViewById(R.id.tv_del_city);
            tv_del_city.setTag(city);
            tv_del_city.setOnClickListener(WeatherActivity.this);
            sendWeatherBroadCast(city, "1");
            viewpager.setCurrentItem(0);
        } else {
            addWeather(city, "0");
        }
        if (intent.getBooleanExtra(WeatherConstant.START_ACTIVITY_FROM_WIDGET, false)) {
            viewpager.setCurrentItem(0, true);
        }
    }

    private void addWeather(final String city, String isLocal) {
        if (!TextUtils.isEmpty(city)) {// 说明添加新城市进来
            View weather_view = layoutInflater.inflate(R.layout.weather_view,
                    null);
            TextView tv_weather_city = (TextView) weather_view
                    .findViewById(R.id.tv_weather_city);
            showProgressDialog();
            tv_weather_city.setText(city);
            View tv_del_city = weather_view.findViewById(R.id.tv_del_city);
            tv_del_city.setTag(city);
            tv_del_city.setOnClickListener(this);

            views_map.put(city, weather_view);
            views_list.add(views_list.size() - 1, weather_view);
            viewPagerAdapter.notifyDataSetChanged();
            viewpager.setCurrentItem(views_list.size() - 2);
            if ("1".equals(isLocal) && loc_weather_view == null) {
                loc_weather_view = weather_view;
            }

            sendWeatherBroadCast(city, isLocal);

            // 处理小点
            ImageView dot;
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            params.leftMargin = DisplayUtil.dip2px(WeatherActivity.this, 5);
            dot = new ImageView(WeatherActivity.this);
            dot.setImageResource(R.drawable.weather_widget_dot_disable);
            ll_dots.addView(dot, params);
            setCurrentDot(ll_dots.getChildCount() - 2);
        }
    }

    private void sendWeatherBroadCast(final String city, String isLocal) {
        Intent i = new Intent(WeatherConstant.ACTION_WEATHER_UPDATE);
        i.putExtra("city", city);
        i.putExtra("isLocal", isLocal);
        i.putExtra(WeatherConstant.DATA_ACTIVE_FRESH_LOCATION, true);
        sendBroadcast(i);
    }

    Handler handler = new Handler();

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WeatherConstant.ACTION_WEATHER_UPDATE_SUCCESS.equals(intent
                    .getAction())) {// 天气更新成功
                String city = intent.getStringExtra("city");
                View weather_view = views_map.get(city);
                hideProgressDialog();

                if (weather_view != null) {// 刷新页面
                    ArrayList<WeatherInfo> weatherInfos = DatabaseHelper
                            .getCityDatas(context, city);

                    initWeatherView(weather_view, weatherInfos, false);
                }

            } else if (WeatherConstant.ACTION_WEATHER_UPDATE_FAILURE
                    .equals(intent.getAction())) {// 天气更新失败
                String city = intent.getStringExtra("city");
                if (!TextUtils.isEmpty(city)) {
                    failureHandle(city);
                    showToast(getResources().getString(
                            R.string.weather_load_failure));
                }
            } else if (WeatherConstant.ACTION_WEATHER_UPDATE_NULL.equals(intent
                    .getAction())) {// 天气更新失败
                String city = intent.getStringExtra("city");
                if (!TextUtils.isEmpty(city)) {
                    failureHandle(city);
                    showToast(getResources().getString(R.string.weather_load_null));
                }
            } else if (WeatherConstant.ACTION_MORNCLOUD_LOCATION_SUCCESS
                    .equals(intent.getAction())) {// 定位成功
                if (loc_weather_view == null) {
                    return;
                }
                String city = intent.getStringExtra("city");
                String pre_city = DatabaseHelper.getLocalCity(context);
                views_map.remove(pre_city);
                views_map.put(city, loc_weather_view);
                showProgressDialog();
                View tv_del_city = loc_weather_view
                        .findViewById(R.id.tv_del_city);
                tv_del_city.setTag(city);
                tv_del_city.setOnClickListener(WeatherActivity.this);
                sendWeatherBroadCast(city, "1");
            } else if (WeatherConstant.ACTION_MORNCLOUD_LOCATION_FAILURE
                    .equals(intent.getAction())) {//
                String city = DatabaseHelper.getLocalCity(context);
                if (loc_weather_view == null || TextUtils.isEmpty(city)) {
                    return;
                }
                TextView tv_weather_city = (TextView) loc_weather_view
                        .findViewById(R.id.tv_weather_city);
                tv_weather_city.setText(subCity(city));
                showToast(getResources().getString(
                        R.string.weather_location_failure));
            }
        }

        private void failureHandle(String city) {
            View weather_view = views_map.get(city);
            if (weather_view != null) {// 刷新页面
                hideProgressDialog();
                View iv_weather_fresh = weather_view
                        .findViewById(R.id.iv_weather_fresh);
                iv_weather_fresh.setVisibility(View.VISIBLE);
                iv_weather_fresh.setTag(city);
                iv_weather_fresh.setOnClickListener(WeatherActivity.this);
                weather_view.findViewById(R.id.iv_weather_main).setVisibility(
                        View.GONE);
            }
        }

    };

    class Weather_PagerChangedListener implements OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int page) {
            if (page < allWeatherInfos.size()) {
                View weather_view = views_list.get(page);
                if (weather_view != null) {
                    initWeatherView(weather_view, allWeatherInfos.get(page),
                            true);
                }
            }
            setCurrentDot(page);
        }
    }

}
