
package com.morncloud.weatherwidget.activity;

import android.content.Intent;
import android.os.Bundle;
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

public class WidgetDialogActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showSureDialog(getResources().getString(R.string.whether_manual_add_city),
                new CommonCallback() {
                    @Override
                    public void exec() {
                        finish();
                        Intent intent = new Intent(WidgetDialogActivity.this,
                                CitySearchActivity.class);
                        intent.putExtra("isLocal", "1");
                        startActivity(intent);
                    }
                }, new CommonCallback() {
                    @Override
                    public void exec() {
                        finish();
                    }
                });
    }
}
