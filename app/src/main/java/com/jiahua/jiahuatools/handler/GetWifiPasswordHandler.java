package com.jiahua.jiahuatools.handler;
/*
 * Created by ZhiPeng Huang on 2017-08-24.
 */

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.jiahua.jiahuatools.consts.Consts;
import com.jiahua.jiahuatools.ui.NewGuanLiActivity;
import com.jiahua.jiahuatools.utils.SPUtils;
import com.orhanobut.logger.Logger;

import java.lang.ref.WeakReference;

public class GetWifiPasswordHandler extends Handler implements Consts {

    //private WeakReference<NewGuanLiActivity> myActivity;
    private Context context;

    public GetWifiPasswordHandler(WeakReference<Context> weakContext) {
        this.context = weakContext.get();
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        NewGuanLiActivity activity = (NewGuanLiActivity) context;
        switch (msg.what) {
            case OK_TEXT:
// 在这里可以进行UI操作
                String WIFIpassword = msg.obj.toString().split(":")[1].substring(1, msg.obj.toString().split(":")[1].length() - 2);

                activity.tvNewGuanLiShowPassword.setText(String.valueOf(WIFIpassword));
                if (SPUtils.contains(activity, "WIFI密码")) {
                    SPUtils.remove(activity, "WIFI密码");
                }
                SPUtils.put(activity, "WIFI密码", WIFIpassword);
                Logger.d("4444444444");
                break;

            case NO_TEXT:
                activity.tvNewGuanLiShowPassword.setText("获取失败，请重试！");
                Logger.d("获取失败，请重试！");
                break;
            default:
                break;
        }

    }
}
