
package com.morncloud.weatherwidget;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.morncloud.publics.WeatherConstant;
import com.morncloud.publics.util.LogUtil;
import com.morncloud.weatherservice.service.UpdateService;

/**
 * Copyright (C) 2013 MORNCLOUD All Rights Reserved This program contains
 * proprietary information which is a trade secret of MORNCLOUD and/or its
 * affiliates and also is protected as an unpublished work under applicable
 * Copyright laws. Recipient is to retain this program in confidence and is not
 * permitted to use or make copies thereof other than as permitted in a written
 * agreement with MORNCLOUD, unless otherwise expressly allowed by applicable
 * laws
 */

public class WidgetApplication extends Application {
    private static WidgetApplication widgetApplication;

    public static WidgetApplication getInstance() {
        return widgetApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        widgetApplication = (WidgetApplication) getApplicationContext();
        startService(new Intent(this, UpdateService.class));

        // 全局异常捕获
//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init(getApplicationContext());
//        LogUtil.log("test", "WidgetApplication onCreate");
    }

}
