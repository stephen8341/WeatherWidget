
package com.morncloud.weatherservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.morncloud.publics.WeatherConstant;
import com.morncloud.publics.util.LogUtil;

/**
 * Copyright (C) 2013 MORNCLOUD All Rights Reserved This program contains
 * proprietary information which is a trade secret of MORNCLOUD and/or its
 * affiliates and also is protected as an unpublished work under applicable
 * Copyright laws. Recipient is to retain this program in confidence and is not
 * permitted to use or make copies thereof other than as permitted in a written
 * agreement with MORNCLOUD, unless otherwise expressly allowed by applicable
 * laws
 */
public class ServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.log("test", "intent.getAction()" + intent.getAction());
        context.sendBroadcast(new Intent(WeatherConstant.ACTION_WEATHER_INIT_UPDATE));
    }

}
