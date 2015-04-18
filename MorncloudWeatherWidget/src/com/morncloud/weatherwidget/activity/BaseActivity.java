
package com.morncloud.weatherwidget.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.morncloud.publics.CommonCallback;
import com.morncloud.weatherwidget.R;

/**
 * Copyright (C) 2013 MORNCLOUD All Rights Reserved This program contains
 * proprietary information which is a trade secret of MORNCLOUD and/or its
 * affiliates and also is protected as an unpublished work under applicable
 * Copyright laws. Recipient is to retain this program in confidence and is not
 * permitted to use or make copies thereof other than as permitted in a written
 * agreement with MORNCLOUD, unless otherwise expressly allowed by applicable
 * laws
 */

public class BaseActivity extends Activity {
    Dialog dialog;

    public void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    public void showToastLong(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    public void showProgressDialog() {
        if (dialog == null) {
            synchronized (BaseActivity.class) {
                if (dialog == null) {
                    dialog = new Dialog(this, R.style.Progress_Dialog_Style);
                    Window dialogWindow = dialog.getWindow();
                    dialogWindow.setGravity(Gravity.RIGHT | Gravity.TOP);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setContentView(R.layout.custom_progress_view);
                }
            }
        }
        dialog.show();

    }

    public void hideProgressDialog() {
        if (dialog != null) {
            dialog.hide();
        }
    }

    public void dismissProgressDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }

    // public void showDialog(String msg, final CommonCallback callback,
    // final CommonCallback negative_callback) {
    // new AlertDialog.Builder(this)
    // .setTitle(getResources().getString(R.string.promt))
    // .setMessage(msg)
    // .setCancelable(false)
    // .setPositiveButton(getResources().getString(R.string.yes),
    // new OnClickListener() {
    // @Override
    // public void onClick(DialogInterface dialog,
    // int which) {
    // if (callback != null) {
    // callback.exec();
    // }
    // }
    // })
    // .setNegativeButton(getResources().getString(R.string.no),
    // new OnClickListener() {
    // @Override
    // public void onClick(DialogInterface dialog,
    // int which) {
    // negative_callback.exec();
    // }
    // }).create().show();
    // }

    // 进入activity处理耗时操作的方法
    public void asyncLoad(final CommonCallback pre_callback,
            final CommonCallback in_callback, final CommonCallback post_callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showProgressDialog();
                if (pre_callback != null)
                    pre_callback.exec();
            }

            @Override
            protected Void doInBackground(Void... params) {
                if (in_callback != null)
                    in_callback.exec();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                hideProgressDialog();
                if (post_callback != null)
                    post_callback.exec();
            }
        }.execute();
    }

    public void showSureDialog(String title, final CommonCallback callback,
            final CommonCallback negative_callback) {

        final Dialog dialog = new Dialog(this, R.style.Dialog_Style);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_simple_sure, null);

        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        Button bt_sure = (Button) view.findViewById(R.id.bt_sure);
        Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

        dialog.setContentView(view);
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

        tv_title.setText(title);
        bt_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.exec();
                }
                dialog.dismiss();
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (negative_callback != null) {
                    negative_callback.exec();
                }
                dialog.dismiss();
            }
        });

    }

}
