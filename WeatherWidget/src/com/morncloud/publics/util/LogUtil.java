
package com.morncloud.publics.util;

import android.util.Log;

/**
 * Copyright (C) 2013 MORNCLOUD All Rights Reserved This program contains
 * proprietary information which is a trade secret of MORNCLOUD and/or its
 * affiliates and also is protected as an unpublished work under applicable
 * Copyright laws. Recipient is to retain this program in confidence and is not
 * permitted to use or make copies thereof other than as permitted in a written
 * agreement with MORNCLOUD, unless otherwise expressly allowed by applicable
 * laws
 */

public class LogUtil {
    static final boolean isLog = false;

    public static void log(String tag, String msg) {
        if (isLog) {
            Log.v(tag, msg);
        }
    }
}
