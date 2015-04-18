
package com.morncloud.weatherwidget.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.morncloud.publics.DatabaseHelper;
import com.morncloud.publics.CommonCallback;
import com.morncloud.publics.util.ConnectUtil;
import com.morncloud.publics.util.LogUtil;
import com.morncloud.weatherwidget.R;

/**
 * Copyright (C) 2013 MORNCLOUD 
 * All Rights Reserved 
 * 
 * This program contains proprietary information which is a trade secret of MORNCLOUD and/or its
 * affiliates and also is protected as an unpublished work under applicable
 * Copyright laws. Recipient is to retain this program in confidence and is not
 * permitted to use or make copies thereof other than as permitted in a written
 * agreement with MORNCLOUD, unless otherwise expressly allowed by applicable
 * laws
 */

/**
 * 城市搜索的界面
 * 
 * @author wuheng
 */

public class CitySearchActivity extends BaseActivity implements OnClickListener {
    AutoCompleteTextView et_search_city;
    GridView gv_search_city;
    ImageView ivb_search_city;
    Drawable loc_transparent;
    Drawable loc_opaque;
    String[] hotCitys;
    String[] chinaCitys;
    String isLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loc_search);

        isLocal = getIntent().getStringExtra("isLocal");
        initView();
        LogUtil.log("test", "onCreate");
    }

    private void initView() {
        et_search_city = (AutoCompleteTextView) findViewById(R.id.et_search_city);
        gv_search_city = (GridView) findViewById(R.id.gv_search_city);
        ivb_search_city = (ImageView) findViewById(R.id.ivb_search_city);

        asyncLoad(null, new CommonCallback() {
            @Override
            public void exec() {
                hotCitys = getResources().getString(R.string.hot_citys_list)
                        .split(",");
                chinaCitys = DatabaseHelper.getChinaCitys();
            }
        }, new CommonCallback() {
            @Override
            public void exec() {
                if (chinaCitys != null && chinaCitys.length > 0) {
                    ArrayAdapter<String> av = new ArrayAdapter<String>(
                            CitySearchActivity.this,
                            R.layout.simple_dropdown_item, chinaCitys);
                    et_search_city.setAdapter(av);
                } else {
                    ivb_search_city.setOnClickListener(CitySearchActivity.this);
                }

                initlocDrawable();
                gv_search_city.setAdapter(new MyAdapter<String>(
                        CitySearchActivity.this, R.layout.tv_city_gv,
                        R.id.tv_city, hotCitys));
                et_search_city
                        .setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent,
                                    View view, int position, long id) {
                                if (ConnectUtil
                                        .isNetworkConnected(CitySearchActivity.this)) {
                                    String loc = et_search_city.getText()
                                            .toString();
                                    if (!TextUtils.isEmpty(loc)) {
                                        String[] locs = loc.split("-");
                                        String city = locs[0];
                                        String provi = locs[1];
                                        // 请求城市天气
                                        askWeather(provi + "|" + city);
                                    }
                                } else {
                                    showToast(getResources().getString(
                                            R.string.no_network));
                                }
                            }

                        });
                gv_search_city
                        .setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent,
                                    View view, int position, long id) {
                                if (ConnectUtil
                                        .isNetworkConnected(CitySearchActivity.this)) {
                                    askWeather(hotCitys[position]);
                                } else {
                                    showToast(getResources().getString(
                                            R.string.no_network));
                                }

                            }
                        });
            }

            private void initlocDrawable() {
                loc_transparent = getResources().getDrawable(
                        R.drawable.weather_widget_icon_where_place);
                loc_opaque = getResources().getDrawable(
                        R.drawable.weather_widget_icon_where);
                loc_transparent.setBounds(0, 0,
                        loc_transparent.getMinimumWidth(),
                        loc_transparent.getMinimumHeight());
                loc_opaque.setBounds(0, 0, loc_opaque.getMinimumWidth(),
                        loc_opaque.getMinimumHeight());
            }
        });

    }

    private void askWeather(String city) {
        if (!TextUtils.isEmpty(city)) {
            city = city.trim();

            if (DatabaseHelper.isCityAdd(this, city)) {
                showToast(getResources().getString(R.string.city_already_add));
                return;
            }

            finish();
            Intent i = new Intent(CitySearchActivity.this,
                    WeatherActivity.class);
            i.putExtra("city", city);
            i.putExtra("isLocal", isLocal);
            startActivity(i);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivb_search_city:// 搜索按钮
                String city = et_search_city.getText().toString();
                askWeather(city);
                break;
            default:
                break;
        }

    }

    class MyAdapter<T> extends ArrayAdapter<T> {

        public MyAdapter(Context context, int resource, int textViewResourceId,
                T[] objects) {
            super(context, resource, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = (TextView) super.getView(position, convertView,
                    parent);
            String city = tv.getText().toString().trim();
            tv.setText(city.substring(city.indexOf("|") + 1, city.length()));
            if (DatabaseHelper.isCityAdd(CitySearchActivity.this, city)) {
                if (DatabaseHelper.isLocalCity(CitySearchActivity.this, city)) {
                    tv.setTextColor(0xffffffff);
                    tv.setCompoundDrawables(loc_opaque, null, loc_transparent,
                            null); // 设置左图标
                } else {
                    tv.setTextColor(0x50ffffff);
                    tv.setCompoundDrawables(loc_transparent, null,
                            loc_transparent, null);
                }
            } else {
                tv.setTextColor(0xffffffff);
                tv.setCompoundDrawables(loc_transparent, null, loc_transparent,
                        null);
            }

            return tv;
        }

    }

}
