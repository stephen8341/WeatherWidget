
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
import android.util.AttributeSet;
import android.view.View;
import com.morncloud.publics.bean.WeatherInfo;
import com.morncloud.publics.util.DateUtil;
import com.morncloud.publics.util.LogUtil;
import com.morncloud.publics.util.ResourceUtil;
import com.morncloud.publics.util.ScreenPixelUtil;
import com.morncloud.publics.util.TextsUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * 自定义天气变化趋势的组件
 * 
 * @author wuheng
 */
public class ClimateView extends View {

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

    public ClimateView(Context context, ArrayList<WeatherInfo> weatherInfos) {
        super(context);

        this.weatherInfos = weatherInfos;
        setMaxMinTemper(weatherInfos);
        screen_width = ScreenPixelUtil.getWidth((Activity) context);
        screen_height = ScreenPixelUtil.getHeight((Activity) context);
        this.context = (Activity) context;
    }

    public ClimateView(Context context, AttributeSet attrs) {
        super(context, attrs);

        screen_width = ScreenPixelUtil.getWidth((Activity) context);
        screen_height = ScreenPixelUtil.getHeight((Activity) context);
        this.context = (Activity) context;
    }

    public ClimateView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        screen_width = ScreenPixelUtil.getWidth((Activity) context);
        screen_height = ScreenPixelUtil.getHeight((Activity) context);
        this.context = (Activity) context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawTemperViews(canvas);
        drawClimateViews(canvas);
    }

    /**
     * 画温度趋势的view
     * 
     * @param canvas
     */
    void drawTemperViews(Canvas canvas) {
        if (weatherInfos == null) {
            return;
        }

        // 画温度显示棒的画笔
        Paint paint_stick = new Paint();
        paint_stick.setAntiAlias(true);// 锯齿不显示

        // 画折线的画笔
        Paint paint_line = new Paint();
        paint_line.setStrokeWidth(0.3f);// 笔宽5像素
        paint_line.setColor(0x40ffffff);
        paint_line.setAntiAlias(true);// 锯齿不显示

        // 写字的画笔
        Paint paint_text = new Paint();
        paint_text.setTextSize(TEXT_SIZE);
        paint_text.setColor(TEMPER_COLOR);
        paint_text.setTextAlign(Align.CENTER);

        // 字体的高度
        int text_height = TextsUtil.getTextHeight(paint_text, "12°");

        // 天气趋势区域块的总高度,占屏幕的2/7
        view_height = screen_height * 2 / 7;

        // 代表一个温度差所对应的长度
        temper_Height = view_height / (maxTemper - minTemper);
        LogUtil.log("test", "temper_Height=" + temper_Height);
        LogUtil.log("test", "maxTemper=" + maxTemper);
        LogUtil.log("test", "minTemper=" + minTemper);

        xy_datas = new int[weatherInfos.size()][3];

        // 给xy_datas赋值
        int length = weatherInfos.size();
        WeatherInfo weatherInfo;
        int maxTemper = 0;
        int minTemper = 0;
        for (int i = 0; i < length; i++) {
            weatherInfo = weatherInfos.get(i);
            try {
                maxTemper = Integer.parseInt(weatherInfo
                        .getValueByKey(WeatherInfo.MAXTEMPER));
                minTemper = Integer.parseInt(weatherInfo
                        .getValueByKey(WeatherInfo.MINTEMPER));
            } catch (NumberFormatException e) {
            }
            xy_datas[i][0] = (screen_width / (length + 1)) * (i + 1); // 温度显示棒的x坐标
            xy_datas[i][1] = temper_Height * (this.maxTemper - maxTemper)
                    + (text_height + TEXT_SPACE); // 温度显示棒的y1坐标
            xy_datas[i][2] = temper_Height * (this.maxTemper - minTemper)
                    + (text_height + TEXT_SPACE); // 温度显示棒的y2坐标
        }

        /*
         * 利用xy_datas里面的数据来画出每天的温度温度显示棒和折线
         */

        for (int i = 0; i < length; i++) {
            weatherInfo = weatherInfos.get(i);
            try {
                maxTemper = Integer.parseInt(weatherInfo
                        .getValueByKey(WeatherInfo.MAXTEMPER));
                minTemper = Integer.parseInt(weatherInfo
                        .getValueByKey(WeatherInfo.MINTEMPER));
            } catch (NumberFormatException e) {
            }
            // 画每个温度显示棒
            drawTempStick(canvas, paint_stick, xy_datas[i][0], xy_datas[i][1],
                    xy_datas[i][2]);

            // 画折线
            if (i < length - 1) {
                canvas.drawLine(xy_datas[i][0] + STICK_WIDTH / 2,
                        xy_datas[i][1], xy_datas[i + 1][0] + STICK_WIDTH / 2,
                        xy_datas[i + 1][1], paint_line);
                canvas.drawLine(xy_datas[i][0] + STICK_WIDTH / 2,
                        xy_datas[i][2], xy_datas[i + 1][0] + STICK_WIDTH / 2,
                        xy_datas[i + 1][2], paint_line);
            }

            // 显示最高温度最低温度
            canvas.drawText(maxTemper + "°", xy_datas[i][0] + STICK_WIDTH / 2,
                    xy_datas[i][1] - TEXT_SPACE, paint_text);
            canvas.drawText(minTemper + "°", xy_datas[i][0] + STICK_WIDTH / 2,
                    xy_datas[i][2] + (TEXT_SPACE + text_height), paint_text);

        }
        // 画中间的平均线
        int y_aver = temper_Height * (this.maxTemper - this.averTemp)
                + (text_height + TEXT_SPACE);
        canvas.drawLine(80, y_aver, screen_width - 80, y_aver, paint_line);

        // 显示平均温度
        int text_width = TextsUtil.getTextWidth(paint_text, averTemp + "°");
        canvas.drawText(averTemp + "°", screen_width - 80 + text_width, y_aver
                + text_height / 2, paint_text);
    }

    /**
     * 画温度显示棒
     * 
     * @param canvas
     * @param x
     * @param y1
     * @param y2
     */
    private void drawTempStick(Canvas canvas, Paint p, int x, int y1, int y2) {
        Shader shader = new LinearGradient(0, y1, 0, y2, new int[] {
                0XFFFCC82B, 0XFF009FFF
        }, null, Shader.TileMode.MIRROR);
        p.setShader(shader);
        RectF rectF = new RectF(x, y1, x + STICK_WIDTH, y2);
        canvas.drawRoundRect(rectF, STICK_WIDTH / 2, STICK_WIDTH / 2, p);
    }

    /**
     * 获取五天中的最低最高温度
     * 
     * @param datas
     */
    void setMaxMinTemper(ArrayList<WeatherInfo> weatherInfos) {
        averTemp = 0;
        WeatherInfo wi = weatherInfos.get(0);
        maxTemper = Integer.parseInt(wi.getValueByKey(WeatherInfo.MAXTEMPER));
        minTemper = Integer.parseInt(wi.getValueByKey(WeatherInfo.MINTEMPER));
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
     */
    public void freshViews(ArrayList<WeatherInfo> weatherInfos) {
        this.weatherInfos = weatherInfos;
        setMaxMinTemper(weatherInfos);
        invalidate();
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
