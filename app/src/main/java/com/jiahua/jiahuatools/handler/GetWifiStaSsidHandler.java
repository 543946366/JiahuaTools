package com.jiahua.jiahuatools.handler;
/*
 * Created by ZhiPeng Huang on 2017-08-24.
 */

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.google.gson.GsonBuilder;
import com.jiahua.jiahuatools.bean.WifiStaSettingsJson;
import com.jiahua.jiahuatools.consts.Consts;
import com.jiahua.jiahuatools.ui.NewGuanLiActivity;
import com.jiahua.jiahuatools.utils.SPUtils;
import com.orhanobut.logger.Logger;

import java.lang.ref.WeakReference;

public class GetWifiStaSsidHandler extends Handler implements Consts {

    //private WeakReference<NewGuanLiActivity> myActivity;
    private Context context;

    public GetWifiStaSsidHandler(WeakReference<Context> weakContext) {
        this.context = weakContext.get();
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        NewGuanLiActivity activity = (NewGuanLiActivity) context;
        switch (msg.what) {
            case OK_TEXT:
// 在这里可以进行UI操作
                //String WIFIpassword = msg.obj.toString().split(":")[1].substring(1, msg.obj.toString().split(":")[1].length() - 2);
                WifiStaSettingsJson wifiStaSettingsJson = new GsonBuilder().create().fromJson(msg.obj.toString(), WifiStaSettingsJson.class);
                String wifiSsid = wifiStaSettingsJson.getSsid();

                activity.tvNewGuanLiShowSsid.setText(String.valueOf(wifiSsid));
                if (SPUtils.contains(activity, "WIFIssid")) {
                    SPUtils.remove(activity, "WIFIssid");
                }
                SPUtils.put(activity, "WIFIssid", wifiSsid);
                break;

            case NO_TEXT:
                activity.tvNewGuanLiShowSsid.setText("获取失败，请重试！");
                Logger.d("获取失败，请重试！");
                break;
            default:
                break;
        }

    }
}
