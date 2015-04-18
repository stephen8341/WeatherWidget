
package com.morncloud.weatherwidget.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.morncloud.publics.util.TextsUtil;

/**
 * 温度显示棒自定义view
 */
public class TemperStickView extends View {
    Paint p;
    Paint paint_text;
    static final int WIDTH = 12;
    static final int TEXT_SIZE = 25;// 字体的大小
    static final int TEXT_SPACE = 10;// 字体与显示棒之间的间距
    static final int TEMPER_COLOR = 0xddffffff;// 字体的颜色
    int height;
    int maxTemper;
    int minTemper;
    int text_height;
    int text_width;
    int x1;// 显示棒中心的x坐标
    boolean isDrawText = true;

    public TemperStickView(Context context, int height, int maxTemper,
            int minTemper) {
        super(context);
        initView(maxTemper, minTemper);

        this.height = height;
        this.maxTemper = maxTemper;
        this.minTemper = minTemper;
    }

    private void initView(int maxTemper, int minTemper) {
        p = new Paint();
        p.setAntiAlias(true);// 锯齿不显示
        paint_text = new Paint();
        // 写字的画笔
        paint_text.setTextSize(TEXT_SIZE);
        paint_text.setColor(TEMPER_COLOR);
        paint_text.setTextAlign(Align.CENTER);

        int max = String.valueOf(maxTemper).length() > String
                .valueOf(minTemper).length() ? maxTemper : minTemper;
        text_height = TextsUtil.getTextHeight(paint_text, max + "°");
        text_width = TextsUtil.getTextWidth(paint_text, max + "°");
        x1 = text_width > WIDTH ? text_width / 2 : WIDTH / 2;
    }

    public TemperStickView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isDrawText) {
            // 显示最高温度最低温度
            canvas.drawText(maxTemper + "°", x1, text_height, paint_text);
            canvas.drawText(minTemper + "°", x1, height + 2
                    * (TEXT_SPACE + text_height), paint_text);
        }

        // 画温度显示棒的画笔
        drawTemperStick(canvas, p, x1 - WIDTH / 2, x1 + WIDTH / 2, TEXT_SPACE
                + text_height, height + TEXT_SPACE + text_height);
    }

    /**
     * 画温度显示棒
     * 
     * @param canvas
     * @param x
     * @param y1
     * @param y2
     */
    private void drawTemperStick(Canvas canvas, Paint p, int x1, int x2,
            int y1, int y2) {
        Shader shader = new LinearGradient(0, y1, 0, y2, new int[] {
                0XFFFCC82B, 0XFF009FFF
        }, null, Shader.TileMode.MIRROR);
        p.setShader(shader);
        RectF rectF = new RectF(x1, y1, x2, y2);
        canvas.drawRoundRect(rectF, (x2 - x1) / 2, (x2 - x1) / 2, p);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(3 * x1, height + 2 * (TEXT_SPACE + text_height));
    }

    /**
     * 显示温度文字
     */
    public void drawTemperText() {
        isDrawText = true;
        invalidate();
    }

    public int getViewWidth() {
        return 2 * x1;
    }

    public int getViewHeight() {
        return height + 2 * (TEXT_SPACE + text_height);
    }

    public int getTextHeight() {
        return text_height;
    }

    public int getTextSpace() {
        return TEXT_SPACE;
    }
}
