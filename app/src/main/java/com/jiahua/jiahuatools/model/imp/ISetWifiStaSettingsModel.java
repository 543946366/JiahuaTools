package com.jiahua.jiahuatools.model.imp;
/*
 * Created by HZP on 2017/12/4.
 */

import com.jiahua.jiahuatools.model.OnSubmitListener;

public interface ISetWifiStaSettingsModel {
    void submit(String ssid, String ssidPassword, String url, OnSubmitListener onSubmitListener);
    void cancleTasks();
}
