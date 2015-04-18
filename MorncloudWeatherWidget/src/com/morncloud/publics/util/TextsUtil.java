
package com.morncloud.publics.util;

import android.graphics.Paint;
import android.graphics.Rect;

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

/**
 * 
 * @author PC-0051
 */
public class TextsUtil {
    public static int getTextHeight(Paint p, String text) {
        Rect rect = new Rect();
        p.getTextBounds(text, 0, text.length(), rect);
        return rect.height();
    }

    public static int getTextWidth(Paint p, String text) {
        Rect rect = new Rect();
        p.getTextBounds(text, 0, text.length(), rect);
        return rect.width();
    }

    public static int getTextHeight(String text) {
        Paint p = new Paint();
        Rect rect = new Rect();
        p.getTextBounds(text, 0, text.length(), rect);
        return rect.height();
    }

    public static int getTextWidth(String text) {
        Paint p = new Paint();
        Rect rect = new Rect();
        p.getTextBounds(text, 0, text.length(), rect);
        return rect.width();
    }
}
