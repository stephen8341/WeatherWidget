
package com.morncloud.weatherwidget.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Paint.Align;
import android.view.View;

import com.morncloud.publics.util.LogUtil;
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

public class LineView extends View {
    int[][] xy_datas;
    int averTemp, y_aver, screen_width;// averTemp:平均温度 y_aver：平均温度y坐标
                                       // screen_width:屏幕宽度
    // 画折线的画笔
    Paint paint_line = new Paint();
    // 画平均线的画笔
    Paint paint_aver_line = new Paint();
    // 写字的画笔
    Paint paint_text = new Paint();
    static final int TEXT_SIZE = 25;// 字体的大小
    static final int TEXT_SPACE = 10;// 字体与显示棒之间的间距
    static final int TEMPER_COLOR = 0xddffffff;// 字体的颜色
    int text_height;
    static final int AVER_BOUND_DIST = 70;// 平均线距离边界多少

    public LineView(Context context, int[][] xy_datas, int averTemp,
            int y_aver, int screen_width) {
        super(context);
        this.xy_datas = xy_datas;
        this.averTemp = averTemp;
        this.y_aver = y_aver;
        this.screen_width = screen_width;

        paint_line.setStrokeWidth(2.0f);// 笔宽5像素
        paint_line.setColor(0x10ffffff);
        paint_line.setAntiAlias(true);// 锯齿不显示

        paint_aver_line.setStrokeWidth(3.0f);// 笔宽5像素
        paint_aver_line.setColor(0x30ffffff);

        Shader shader = new LinearGradient(AVER_BOUND_DIST, y_aver,
                screen_width - AVER_BOUND_DIST, y_aver, new int[] {
                        0x20ffffff,
                        0xaaffffff, 0x20ffffff
                }, new float[] {
                        0.1f, 0.5f,
                        0.9f
                }, Shader.TileMode.MIRROR);
        paint_aver_line.setShader(shader);
        paint_aver_line.setAntiAlias(true);// 锯齿不显示

        paint_text.setTextSize(TEXT_SIZE);
        paint_text.setColor(TEMPER_COLOR);
        paint_text.setTextAlign(Align.CENTER);

        text_height = TextsUtil.getTextHeight(paint_text, "12°");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int length = xy_datas.length;
        for (int i = 0; i < length; i++) {
            // 画折线
            if (i < length - 1) {
                canvas.drawLine(xy_datas[i][0], xy_datas[i][1],
                        xy_datas[i + 1][0], xy_datas[i + 1][1], paint_line);
                canvas.drawLine(xy_datas[i][0], xy_datas[i][2],
                        xy_datas[i + 1][0], xy_datas[i + 1][2], paint_line);
            }
        }
        // 画中间的平均线
        canvas.drawLine(AVER_BOUND_DIST, y_aver,
                screen_width - AVER_BOUND_DIST, y_aver, paint_aver_line);

        // 显示平均温度
        int text_width = TextsUtil.getTextWidth(paint_text, averTemp + "°");
        canvas.drawText(averTemp + "°", screen_width - 80 + text_width, y_aver
                + text_height / 2, paint_text);
        LogUtil.log("test", "averTemp" + averTemp + "°");
    }

}
