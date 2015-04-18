
package com.morncloud.weatherservice.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import org.json.JSONException;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Base64;
import com.morncloud.publics.DatabaseHelper;
import com.morncloud.publics.WeatherConstant;
import com.morncloud.publics.bean.WeatherInfo;
import com.morncloud.publics.util.ConnectUtil;
import com.morncloud.publics.util.LogUtil;

public class WeathersManager {
    /**
     * 根据city来组拼请求url
     * 
     * @param city
     * @return
     */
    public static String getRequestUri(String city) {
        String encodeCity = null;
        try {
            encodeCity = new String(Base64.encode(
                    URLEncoder.encode(city, "UTF8").getBytes(), Base64.NO_WRAP));
        } catch (UnsupportedEncodingException e) {
        }
        return WeatherConstant.WEATHER_SER_URL + encodeCity;
    }

    /**
     * 更新所有城市的天气情况
     * 
     * @param context
     * @return
     */
    public static void updateAll(final Context context) {
        String[] citys = DatabaseHelper.getCitys(context);
        String requsetUrl;
        FinalHttp fh = new FinalHttp();

        if (citys != null && citys.length > 0) {
            // 删除老数据，即5天前的记录
            DatabaseHelper.deleteOldWeathers(context);

            for (final String city : citys) {
                requsetUrl = getRequestUri(city);
                LogUtil.log("test",
                        "requsetUrl:" + requsetUrl);
                fh.get(requsetUrl, new AjaxCallBack<String>() {
                    @Override
                    public void onSuccess(String result) {// 成功时回调
                        // 解析相关的天气数据插入数据库中
                        ArrayList<WeatherInfo> weaherInfos;
                        try {
                            weaherInfos = WeatherUtil.parseWeaherInfo(result);
                            if (weaherInfos == null || weaherInfos.size() == 0) {

                                context.sendBroadcast(new Intent(
                                        WeatherConstant.ACTION_WEATHER_UPDATE_FAILURE));
                            } else {
                                DatabaseHelper.writeWeather(context,
                                        weaherInfos);
                                // if (DatabaseHelper.isLocalCity(context,
                                // city)) {
                                Intent i = new Intent(
                                        WeatherConstant.ACTION_WEATHER_UPDATE_SUCCESS);
                                i.putExtra("city", city);
                                context.sendBroadcast(i);
                                // context.sendBroadcast(new Intent(
                                // WeatherConstant.ACTION_WEATHER_UPDATE_SUCCESS));
                                // }
                            }
                            LogUtil.log("test", "FinalHttp onSuccess:" + result);
                        } catch (JSONException e) {
                            context.sendBroadcast(new Intent(
                                    WeatherConstant.ACTION_WEATHER_UPDATE_FAILURE));
                            // Toast.makeText(context, "数据解析失败", 0).show();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t, int errorNo,
                            String strMsg) {// 失败时回调
                        context.sendBroadcast(new Intent(
                                WeatherConstant.ACTION_WEATHER_UPDATE_FAILURE));
                        LogUtil.log("test", "FinalHttp onFailure=" + strMsg
                                + " errorNo=" + errorNo);
                    }

                    @Override
                    public void onLoading(long count, long current) { // 每1秒钟自动被回调一次
                    }

                    @Override
                    public void onStart() {
                    }
                });
            }
        }
    }

    /**
     * 获取指定城市的天气情况 重新写，采用异步管理
     * 
     * @param isLocal "1":当前城市 "0"：非当前城市
     * @return
     */
    public static void updateCity(final Context context, final String city,
            final String isLocal) {
        if (TextUtils.isEmpty(city)) {
            return;
        }

        FinalHttp fh = new FinalHttp();
        String requsetUrl = getRequestUri(city);
        LogUtil.log("test", "requsetUrl=" + requsetUrl);
        fh.get(requsetUrl, new AjaxCallBack<String>() {
            @Override
            public void onSuccess(String result) {// 成功时回调
                LogUtil.log("test", "FinalHttp onSuccess=" + result);
                // 解析相关的天气数据插入数据库中
                try {
                    ArrayList<WeatherInfo> weaherInfos = WeatherUtil
                            .parseWeaherInfo(result);
                    Intent i;
                    if (weaherInfos == null || weaherInfos.size() == 0) {
                        i = new Intent(
                                WeatherConstant.ACTION_WEATHER_UPDATE_NULL);
                        i.putExtra("city", city);
                        context.sendBroadcast(i);
                    } else {
                        DatabaseHelper.writeWeather(context, weaherInfos);
                        DatabaseHelper.insertCity(context, city, weaherInfos
                                .get(0).getValueByKey(WeatherInfo.CITY_ID),
                                isLocal);
                        i = new Intent(
                                WeatherConstant.ACTION_WEATHER_UPDATE_SUCCESS);
                        i.putExtra("city", city);
                        context.sendBroadcast(i);

                    }
                } catch (JSONException e) {
                    context.sendBroadcast(new Intent(
                            WeatherConstant.ACTION_WEATHER_UPDATE_FAILURE));
                }

            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {// 失败时回调
                LogUtil.log("test", "FinalHttp onFailure=" + strMsg
                        + " errorNo=" + errorNo);
                Intent i = new Intent(
                        WeatherConstant.ACTION_WEATHER_UPDATE_FAILURE);
                i.putExtra("city", city);
                context.sendBroadcast(i);
            }

            @Override
            public void onLoading(long count, long current) { // 每1秒钟自动被回调一次
            }

            @Override
            public void onStart() {
            }
        });
    }
}
