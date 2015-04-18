
package com.morncloud.publics.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

/**
 * Copyright (C) 2013 MORNCLOUD All Rights Reserved This program contains
 * proprietary information which is a trade secret of MORNCLOUD and/or its
 * affiliates and also is protected as an unpublished work under applicable
 * Copyright laws. Recipient is to retain this program in confidence and is not
 * permitted to use or make copies thereof other than as permitted in a written
 * agreement with MORNCLOUD, unless otherwise expressly allowed by applicable
 * laws
 */

public class BitmapUtil {
    /**
     * @param height 
     */
    public static Bitmap compressBitmap(String filePath, int height) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        float scale = calculateScale(options, height);
        options.inJustDecodeBounds = false;

        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap dstbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
                bmp.getHeight(), matrix, true);
        return dstbmp;
    }

    /**
     * @param height 
     */
    public static Bitmap compressBitmap(Resources res, int id, int height) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, id, options);
        float scale = calculateScale(options, height);

        Bitmap bmp = BitmapFactory.decodeResource(res, id);

        LogUtil.log("test", "bmp.getWidth()=" + bmp.getWidth()
                + ";bmp.getHeight()=" + bmp.getHeight());

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap dstbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
                bmp.getHeight(), matrix, true);
        return dstbmp;
    }

    private static float calculateScale(BitmapFactory.Options options,
            int reqHeight) {
        final int height = options.outHeight;
        float scale = 1.0f;
        if (height > reqHeight) {
            float heightRatio = (float) reqHeight / (float) height;
            scale = heightRatio;
        }
        return scale;
    }

    public static Bitmap rotateBmp(Bitmap bmp, int degree) {
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        Matrix mtx = new Matrix();
        mtx.postRotate(degree);
        Bitmap rotatedBMP = Bitmap.createBitmap(bmp, 0, 0, w, h, mtx, true);
        return rotatedBMP;
    }
}
