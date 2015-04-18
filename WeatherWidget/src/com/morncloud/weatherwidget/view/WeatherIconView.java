
package com.morncloud.weatherwidget.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.view.View;
import com.morncloud.publics.util.DateUtil;
import com.morncloud.publics.util.LogUtil;
import com.morncloud.publics.util.ResourceUtil;
import com.morncloud.publics.util.TextsUtil;

/**
 * Copyright (C) 2013 MORNCLOUD All Rights Reserved This program contains
 * proprietary information which is a trade secret of MORNCLOUD and/or its
 * affiliates and also is protected as an unpublished work under applicable
 * Copyright laws. Recipient is to retain this program in confidence and is not
 * permitted to use or make copies thereof other than as permitted in a written
 * agreement with MORNCLOUD, unless otherwise expressly allowed by applicable
 * laws
 */

public class WeatherIconView extends View {
    Bitmap bitmap;
    String weather;
    String date;
    Paint paint_bitmap = new Paint();
    Paint paint_text = new Paint();
    static final int TEXT_SIZE = 20;// 字体的大小
    static final int TEXT_COLOR = 0xddffffff;// 字体的颜色
    static final int TEXT_SPACE = 10;// 字体之间的间距
    Context context;
    int text_height;
    int bitmap_width;
    int bitmap_height;
    boolean isDrawText = true;

    public WeatherIconView(Context context, String weather, String date) {
        super(context);
        this.weather = weather;
        this.date = date;
        this.context = context;
        initView(context, weather);
    }

    private void initView(Context context, String weather) {
        // 画bitmap的画笔
        paint_bitmap.setAntiAlias(true);// 锯齿不显示

        paint_text.setTextSize(TEXT_SIZE);
        paint_text.setColor(TEXT_COLOR);
        paint_text.setTextAlign(Align.CENTER);
        // 字体的高度
        text_height = TextsUtil.getTextHeight(paint_text, "周六");
        initBitmap();
    }

    void drawClimateViews(Canvas canvas) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                DateUtil.yyyy_MM_dd);
        long mills;
        try {
            mills = simpleDateFormat.parse(date).getTime();
        } catch (ParseException e) {
            mills = System.currentTimeMillis();
        }

        canvas.drawBitmap(bitmap, 0, 0, paint_bitmap);

        if (isDrawText) {
            canvas.drawText(DateUtil.formatDate(mills, DateUtil.MM_dd),
                    bitmap_width / 2, bitmap_height + text_height, paint_text);
            canvas.drawText(DateUtil.getWeeksByDate(mills), bitmap_width / 2,
                    bitmap_height + text_height * 2 + TEXT_SPACE, paint_text);
            LogUtil.log("test", "bitmap_height=" + bitmap_height);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawClimateViews(canvas);
        LogUtil.log("test1", "onDraw");
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (bitmap == null) {
            initBitmap();
        }
    }

    private void initBitmap() {
        bitmap = BitmapFactory.decodeResource(context.getResources(),
                ResourceUtil.getResourceByWeather(weather, false));
        bitmap_width = bitmap.getWidth();
        bitmap_height = bitmap.getHeight();
    }

    @Override
    protected void onDetachedFromWindow() {// view销毁时调用
        super.onDetachedFromWindow();
        recycleBitmap();
    }

    private void recycleBitmap() {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(bitmap_width, bitmap_height + text_height * 2
                + TEXT_SPACE + 3);
    }

    public int getViewWidth() {
        return bitmap_width;
    }

    public int getViewHeight() {
        return bitmap_height + text_height * 2 + TEXT_SPACE;
    }

    /**
     * 显示文字
     */
    public void showText() {
        isDrawText = true;
        invalidate();
    }
}
