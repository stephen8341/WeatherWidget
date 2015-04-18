
package com.morncloud.weatherservice.location;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.morncloud.publics.WeatherConstant;
import com.morncloud.publics.util.ConnectUtil;
import com.morncloud.publics.util.LogUtil;
import com.morncloud.publics.util.Utils;

public class LocationController {
    LocationClient mLocationClient;
    Handler handler;
    public static final long LOCATION_TIMEOUT = 1000 * 60;
    boolean isLocationSuccess = false;
    Context context;
    public static LocationController locationController;
    private boolean active_fresh = false;

    public static LocationController getInstance(Context context,
            Handler handler) {
        if (locationController == null) {
            synchronized (LocationController.class) {
                if (locationController == null) {
                    locationController = new LocationController(context,
                            handler);
                }
            }
        }
        return locationController;
    }

    /**
     * 定位管理类
     * 
     * @param context
     */
    private LocationController(Context context, Handler handler) {
        this.context = context;
        mLocationClient = new LocationClient(context);
        mLocationClient.setAK(WeatherConstant.BAIDU_LOCATION_KEY);
        mLocationClient.registerLocationListener(new MyLocationListenner());
        this.handler = handler;
    }

    /**
     * @param active_fresh weather fresh location actively
     */
    public void startLocation(boolean active_fresh) {
        if (!ConnectUtil.isNetworkConnected(context)) {
            return;
        }
        this.active_fresh = active_fresh;
        isLocationSuccess = false;
        setLocationOption();
        mLocationClient.start();
        // 设置一个超时机制
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isLocationSuccess) {// 超时了
                    Intent intent = new Intent(
                            WeatherConstant.ACTION_MORNCLOUD_LOCATION_FAILURE);
                    intent.putExtra(WeatherConstant.DATA_ACTIVE_FRESH_LOCATION,
                            LocationController.this.active_fresh);
                    context.sendBroadcast(intent);
                    try {
                        mLocationClient.stop();
                    } catch (Exception e) {
                    }
                }
            }
        }, LOCATION_TIMEOUT);
    }

    // 设置相关参数
    private void setLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(false); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setServiceName("com.baidu.location.service_v2.9");
        option.setPoiExtraInfo(true);
        option.setAddrType("all");
        option.setScanSpan(3000);

        option.setPriority(LocationClientOption.NetWorkFirst); // 设置网络优先

        option.setPoiNumber(10);
        option.disableCache(true);
        mLocationClient.setLocOption(option);
    }

    /**
     * 监听函数，有更新位置的时候，格式化成字符串，输出到屏幕中
     */
    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return;
            LogUtil.log("test", "location=" + location.getLongitude());
            String city = location.getCity();
            String province = location.getProvince();
            if (!TextUtils.isEmpty(city)) {
                // 发送广播
                Intent intent = new Intent(
                        WeatherConstant.ACTION_MORNCLOUD_LOCATION_SUCCESS);
                if (city.endsWith("市") && city.length() > 2) {
                    city = city.substring(0, city.length() - 1);
                }
                city = Utils.formatProvince(province) + "|" + city;
                intent.putExtra("city", city);
                intent.putExtra(WeatherConstant.DATA_ACTIVE_FRESH_LOCATION, active_fresh);
                context.sendBroadcast(intent);
                isLocationSuccess = true;
                mLocationClient.stop();
            }
        }

        @Override
        public void onReceivePoi(BDLocation arg0) {

        }
    }
}
