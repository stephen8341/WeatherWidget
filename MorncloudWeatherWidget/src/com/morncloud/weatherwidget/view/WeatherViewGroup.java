
package com.morncloud.weatherwidget.view;

/**
 * Copyright (C) 2013 MORNCLOUD
 * All Rights Reserved
 *
 * This program contains proprietary information which is a trade
 * secret of MORNCLOUD and/or its affiliates and also is protected as
 * an unpublished work under applicable Copyright laws. Recipient is
 * to retain this program in confidence and is not permitted to use or
 * make copies thereof other than as permitted in a written agreement
 * with MORNCLOUD, unless otherwise expressly allowed by applicable laws
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import com.morncloud.publics.bean.WeatherInfo;
import com.morncloud.publics.util.DateUtil;
import com.morncloud.publics.util.LogUtil;
import com.morncloud.publics.util.ResourceUtil;
import com.morncloud.publics.util.ScreenPixelUtil;
import com.morncloud.publics.util.TextsUtil;

/**
 * 自定义天气变化趋势的组件
 * 
 * @author wuheng
 */
public class WeatherViewGroup extends RelativeLayout {

    static final long ANIM_DURA = 350;// 动画时间
    ArrayList<WeatherInfo> weatherInfos;// 五天的天气数据
    int[][] xy_datas;// 代表每天温度显示棒的x,y1,y2
    int maxTemper;// 五天最高温度
    int minTemper;// 五天最低温度
    int averTemp;// 五天平均溫度
    int screen_width;
    int screen_height;
    static final int STICK_WIDTH = 10;// 温度显示器的宽度
    static final int TEXT_SIZE = 25;// 字体的大小
    static final int TEXT_SPACE = 10;// 字体与显示棒之间的间距
    static final int TEMPER_COLOR = 0xddffffff;// 字体的颜色
    static final int ICON_HEIGHT = 72;// 下面小图标宽度
    Activity context;
    int temper_Height;// 单位温度对应的高度
    int view_height; // 温度趋势区域块的总高度;
    ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
    Handler handler = new Handler();
    HashMap<String, View> views_map = new HashMap<String, View>();//

    public WeatherViewGroup(Context context, ArrayList<WeatherInfo> weatherInfos) {
        super(context);

        this.weatherInfos = weatherInfos;
        setMaxMinTemper(weatherInfos);
        screen_width = ScreenPixelUtil.getWidth((Activity) context);
        screen_height = ScreenPixelUtil.getHeight((Activity) context);
        this.context = (Activity) context;
    }

    public WeatherViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);

        screen_width = ScreenPixelUtil.getWidth((Activity) context);
        screen_height = ScreenPixelUtil.getHeight((Activity) context);
        this.context = (Activity) context;
    }

    public WeatherViewGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        screen_width = ScreenPixelUtil.getWidth((Activity) context);
        screen_height = ScreenPixelUtil.getHeight((Activity) context);
        this.context = (Activity) context;
    }

    /**
     * 获取五天中的最低最高温度
     * 
     * @param datas
     */
    void setMaxMinTemper(ArrayList<WeatherInfo> weatherInfos) {
        averTemp = 0;
        WeatherInfo wi = weatherInfos.get(0);
        try {
            maxTemper = Integer.parseInt(wi.getValueByKey(WeatherInfo.MAXTEMPER));
            minTemper = Integer.parseInt(wi.getValueByKey(WeatherInfo.MINTEMPER));
        } catch (NumberFormatException e1) {
        }
        int maxTemp = 0;
        int minTemp = 0;
        for (WeatherInfo weatherInfo : weatherInfos) {
            try {
                maxTemp = Integer.parseInt(weatherInfo
                        .getValueByKey(WeatherInfo.MAXTEMPER));
                minTemp = Integer.parseInt(weatherInfo
                        .getValueByKey(WeatherInfo.MINTEMPER));
            } catch (NumberFormatException e) {
            }
            averTemp += maxTemp;
            averTemp += minTemp;
            if (maxTemper < maxTemp) {
                maxTemper = maxTemp;
            }
            if (minTemper > minTemp) {
                minTemper = minTemp;
            }
        }
        averTemp = averTemp / (weatherInfos.size() * 2);
    }

    /**
     * 传入数据源，刷新当前view的方法
     * 
     * @param datas
     * @param singleFresh 是否只能刷新一次 true:如果以前刷新过，该次就不再刷新
     */
    public void freshViews(ArrayList<WeatherInfo> weatherInfos,
            boolean singleFresh) {
        Object tag = getTag();// tag表示view是否刷新过
        if (singleFresh && tag != null && (Boolean) tag) {
            return;
        } else {
            removeAllViews();
        }
        setTag(true);

        this.weatherInfos = weatherInfos;
        setMaxMinTemper(weatherInfos);

        // 天气趋势区域块的总高度,占屏幕的2/7
        view_height = screen_height * 2 / 7;
        // 代表一个温度差所对应的长度
        temper_Height = view_height / (maxTemper - minTemper);
        WeatherInfo weatherInfo;
        // 给xy_datas赋值
        int length = weatherInfos.size();
        xy_datas = new int[length][3];
        TemperStickView temperStickView;
        WeatherIconView weatherIconView;
        LayoutParams params;
        int maxTemper = 0;
        int minTemper = 0;
        int y_aver = 0;
        String date;
        for (int i = 0; i < length; i++) {
            weatherInfo = weatherInfos.get(i);
            try {
                maxTemper = Integer.parseInt(weatherInfo
                        .getValueByKey(WeatherInfo.MAXTEMPER));
                minTemper = Integer.parseInt(weatherInfo
                        .getValueByKey(WeatherInfo.MINTEMPER));
            } catch (NumberFormatException e) {
            }
            date = weatherInfo.getValueByKey(WeatherInfo.DATE);
            LogUtil.log("test4", "date=" + date);// //////////////////////////
            // 温度显示棒
            temperStickView = new TemperStickView(context, temper_Height
                    * (maxTemper - minTemper), maxTemper, minTemper);
            temperStickView.setTag(date);
            // 下面天气view
            weatherIconView = new WeatherIconView(context,
                    weatherInfo.getValueByKey(WeatherInfo.WEATHER_DAY), date);
            weatherIconView.setTag(date);
            // temperStickView.invalidate();
            // weatherIconView.invalidate();

            params = new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            params.leftMargin = (screen_width / (length + 1)) * (i + 1)
                    - temperStickView.getViewWidth() / 2;
            params.topMargin = temper_Height * (this.maxTemper - maxTemper);
            addView(temperStickView, params);

            params = new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            params.leftMargin = (screen_width / (length + 1)) * (i + 1)
                    - weatherIconView.getViewWidth() / 2;
            LogUtil.log("test1", "weatherIconView.getViewWidth()="
                    + weatherIconView.getViewWidth());
            params.topMargin = view_height
                    + (temperStickView.getTextHeight() + temperStickView
                            .getTextSpace()) * 2 + 68;
            addView(weatherIconView, params);

            startAnimation(ANIM_DURA * i * 3 / 4, temperStickView,
                    weatherIconView);

            // 画折线
            xy_datas[i][0] = (screen_width / (length + 1)) * (i + 1); // 温度显示棒的x坐标
            xy_datas[i][1] = temper_Height * (this.maxTemper - maxTemper)
                    + temperStickView.getTextHeight()
                    + temperStickView.getTextSpace(); // 温度显示棒的y1坐标
            xy_datas[i][2] = temper_Height
                    * (this.maxTemper - minTemper)
                    + (temperStickView.getTextHeight() + temperStickView
                            .getTextSpace()); // 温度显示棒的y2坐标
            // 画中间的平均线
            if (y_aver == 0) {
                y_aver = temper_Height
                        * (this.maxTemper - this.averTemp)
                        + (temperStickView.getTextHeight() + temperStickView
                                .getTextSpace());
            }
        }
        LineView lineView = new LineView(context, xy_datas, this.averTemp,
                y_aver, screen_width);
        // new LineView(context, xy_datas);
        addView(lineView);

        String date_yest = DateUtil.getDateAsYYYY_MM_DD(System
                .currentTimeMillis() - 24 * 60 * 60 * 1000);
        translucentChild(date_yest);
    }

    // 让昨天的天气view变透明
    public void translucentChild(String tag) {
        if (TextUtils.isEmpty(tag)) {
            return;
        }
        int childCount = getChildCount();
        View child;
        for (int i = 0; i < childCount; i++) {
            child = getChildAt(i);
            if (tag.equals(child.getTag())) {
                child.setAlpha(0.4f);
            }
        }
    }

    public void startAnimation(long delayMillis,
            final TemperStickView temperStickView,
            final WeatherIconView weatherIconView) {
        temperStickView.setVisibility(View.INVISIBLE);
        weatherIconView.setVisibility(View.INVISIBLE);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                temperStickView.setVisibility(View.VISIBLE);
                weatherIconView.setVisibility(View.VISIBLE);

                AnimationSet animationSet = getTemperStickAnim(temperStickView);
                temperStickView.setAnimation(animationSet);

                animationSet = getWeatherIconAnim(weatherIconView);
                weatherIconView.setAnimation(animationSet);
            }

        }, delayMillis);
    }

    /**
     * 获取下面天气组件的动画
     * 
     * @param weatherIconView
     * @return
     */
    private AnimationSet getWeatherIconAnim(
            final WeatherIconView weatherIconView) {
        Animation animation;
        AnimationSet animationSet;
        animationSet = new AnimationSet(true);
        animation = new AlphaAnimation(0.3f, 1.0f);
        animation.setDuration(ANIM_DURA);
        animationSet.addAnimation(animation);

        animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, -10.0f);
        animation.setDuration(ANIM_DURA / 2);
        animationSet.addAnimation(animation);

        animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 10.0f);
        animation.setStartOffset(ANIM_DURA / 2);
        animation.setDuration(ANIM_DURA / 2);
        animationSet.addAnimation(animation);
        return animationSet;
    }

    /**
     * 获取上面温度显示棒的动画
     * 
     * @param temperStickView
     * @return
     */
    private AnimationSet getTemperStickAnim(
            final TemperStickView temperStickView) {
        AnimationSet animationSet = new AnimationSet(true);

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(ANIM_DURA);
        animationSet.addAnimation(animation);

        animation = new ScaleAnimation(1.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(ANIM_DURA);
        // temperStickView.drawTemperText();
        animation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // temperStickView.drawTemperText();
            }
        });
        animationSet.addAnimation(animation);
        return animationSet;
    }

    /**
     * 画下面的天气组件
     * 
     * @param canvas
     */
    void drawClimateViews(Canvas canvas) {
        Bitmap bitmap;//

        // 画bitmap的画笔
        Paint paint_bitmap = new Paint();
        paint_bitmap.setAntiAlias(true);// 锯齿不显示

        // 写字的画笔
        Paint paint_text = new Paint();
        paint_text.setTextSize(TEXT_SIZE);
        paint_text.setColor(0x40ffffff);
        paint_text.setTextAlign(Align.CENTER);

        WeatherInfo weatherInfo;
        String date;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                DateUtil.yyyy_MM_dd);
        long mills;
        int width;
        // 字体的高度
        int text_height = TextsUtil.getTextHeight(paint_text, "11/11");
        int length = weatherInfos.size();
        for (int i = 0; i < length; i++) {
            weatherInfo = weatherInfos.get(i);
            date = weatherInfo.getValueByKey(WeatherInfo.DATE);
            try {
                mills = simpleDateFormat.parse(date).getTime();
            } catch (ParseException e) {
                mills = System.currentTimeMillis();
            }

            bitmap = BitmapFactory.decodeResource(context.getResources(),
                    ResourceUtil.getResourceByWeather(
                            weatherInfo.getValueByKey(WeatherInfo.WEATHER_DAY),
                            false));
            // bitmap =
            // ResourceUtil.getResourceByWeather(weatherInfo.getValueByKey(WeatherInfo.WEATHER),false);
            // BitmapUtil.compressBitmap(context.getResources(),
            // ResourceUtil.getResourceByWeather
            // (weatherInfo.getValueByKey(WeatherInfo.WEATHER)), ICON_HEIGHT);
            bitmaps.add(bitmap);
            width = bitmap.getWidth();
            canvas.drawBitmap(bitmap, xy_datas[i][0] + STICK_WIDTH / 2 - width
                    / 2, view_height * 4 / 3, paint_bitmap);
            canvas.drawText(DateUtil.formatDate(mills, DateUtil.MM_dd),
                    xy_datas[i][0] + STICK_WIDTH / 2, view_height * 4 / 3
                            + ICON_HEIGHT * 4 / 3, paint_text);
            canvas.drawText(DateUtil.getWeeksByDate(mills), xy_datas[i][0]
                    + STICK_WIDTH / 2, view_height * 4 / 3 + ICON_HEIGHT * 4
                    / 3 + text_height * 4 / 3, paint_text);
        }
    }

    @Override
    protected void onDetachedFromWindow() {// view销毁时调用
        super.onDetachedFromWindow();
        if (bitmaps.size() > 0) {
            for (Bitmap bitmap : bitmaps) {
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                    bitmap = null;
                }
            }
        }

    }

}
