
package com.morncloud.weatherwidget;

import android.content.Context;
import android.util.Log;

import com.morncloud.publics.util.LogUtil;

import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

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
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 * 
 * @author user
 */
public class CrashHandler implements UncaughtExceptionHandler {
    private static CrashHandler INSTANCE = new CrashHandler();

    // 系统默认的 UncaughtException 处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private Context mContext;

    // // 用来存储设备信息和异常信息
    // private Map<String, String> infos = new HashMap<String, String>();
    // // 用于格式化日期,作为日志文件名的一部分
    // private SimpleDateFormat formatter = new
    // SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    /** 保证只有一个 CrashHandler 实例 */
    private CrashHandler() {
    }

    /** 获取 CrashHandler 实例 ,单例模式 */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     * 
     * @param context
     */
    public void init(Context context) {
        mContext = context;

        // 获取系统默认的 UncaughtException 处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();

        // 设置该 CrashHandler 为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当 UncaughtException 发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            // try {
            // Thread.sleep(3000);
            // } catch (InterruptedException e) {
            // }
            // 退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    /**
     * 自定义错误处理，收集错误信息，发送错误报告等操作均在此完成
     * 
     * @param ex
     * @return true：如果处理了该异常信息；否则返回 false
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        ex.printStackTrace();
        String message = ex.getMessage();
        Log.e("error", message);
        LogUtil.log("test4", "全局异常捕获");
        // // 收集设备参数信息
        // collectDeviceInfo(mContext);
        // // 保存日志文件
        // saveCrashInfo2File(ex);
        return true;
    }

    /**
     * 收集设备参数信息
     * 
     * @param ctx
     */
    // public void collectDeviceInfo(Context ctx) {
    // try {
    // PackageManager pm = ctx.getPackageManager();
    // PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
    // PackageManager.GET_ACTIVITIES);
    //
    // if (pi != null) {
    // String versionName = pi.versionName == null ? "null" : pi.versionName;
    // String versionCode = pi.versionCode + "";
    // infos.put("versionName", versionName);
    // infos.put("versionCode", versionCode);
    // }
    // } catch (NameNotFoundException e) {
    // Log.e(TAG, "an error occured when collect package info", e);
    // }
    //
    // Field[] fields = Build.class.getDeclaredFields();
    // for (Field field : fields) {
    // try {
    // field.setAccessible(true);
    // infos.put(field.getName(), field.get(null).toString());
    // Log.d(TAG, field.getName() + " : " + field.get(null));
    // } catch (Exception e) {
    // Log.e(TAG, "an error occured when collect crash info", e);
    // }
    // }
    // }
    //
    // /**
    // * 保存错误信息到文件中
    // *
    // * @param ex
    // * @return 返回文件名称,便于将文件传送到服务器
    // */
    // private String saveCrashInfo2File(Throwable ex) {
    // StringBuffer sb = new StringBuffer();
    // for (Map.Entry<String, String> entry : infos.entrySet()) {
    // String key = entry.getKey();
    // String value = entry.getValue();
    // sb.append(key + "=" + value + "\n");
    // }
    //
    // Writer writer = new StringWriter();
    // PrintWriter printWriter = new PrintWriter(writer);
    // ex.printStackTrace(printWriter);
    // Throwable cause = ex.getCause();
    // while (cause != null) {
    // cause.printStackTrace(printWriter);
    // cause = cause.getCause();
    // }
    // printWriter.close();
    //
    // String result = writer.toString();
    // sb.append(result);
    // try {
    // long timestamp = System.currentTimeMillis();
    // String time = formatter.format(new Date());
    // String fileName = "crash-" + time + "-" + timestamp + ".log";
    //
    // if
    // (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
    // {
    // String path = "/sdcard/crash/";
    // File dir = new File(path);
    // if (!dir.exists()) {
    // dir.mkdirs();
    // }
    // FileOutputStream fos = new FileOutputStream(path + fileName);
    // fos.write(sb.toString().getBytes());
    // fos.close();
    // }
    //
    // return fileName;
    // } catch (Exception e) {
    // Log.e(TAG, "an error occured while writing file...", e);
    // }
    //
    // return null;
    // }
}
