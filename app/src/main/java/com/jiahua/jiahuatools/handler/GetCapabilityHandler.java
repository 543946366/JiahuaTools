package com.jiahua.jiahuatools.handler;
/*
 * Created by ZhiPeng Huang on 2017-08-24.
 */

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.google.gson.GsonBuilder;
import com.jiahua.jiahuatools.bean.GetCapabilityJson;
import com.jiahua.jiahuatools.consts.Consts;
import com.jiahua.jiahuatools.ui.NewGuanLiActivity;
import com.jiahua.jiahuatools.utils.DigestAuthenticationUtil;
import com.orhanobut.logger.Logger;

import java.lang.ref.WeakReference;

public class GetCapabilityHandler extends Handler implements Consts {

    //private WeakReference<NewGuanLiActivity> myActivity;
    private Context context;
    private String displayModelNumber;
    private String displaySerialNumber;
    private String deviceIP;


    public GetCapabilityHandler(WeakReference<Context> weakContext, String displayModelNumber, String displaySerialNumber, String deviceIP) {
        this.context = weakContext.get();
        this.displayModelNumber = displayModelNumber;
        this.displaySerialNumber = displaySerialNumber;
        this.deviceIP = deviceIP;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        NewGuanLiActivity activity = (NewGuanLiActivity) context;
        switch (msg.what) {
            case OK_TEXT:
                // 在这里可以进行UI操作
                try {
                    String fuWuList = "";

                    int jsonSize = msg.obj.toString().indexOf("{");
                    String jsonContent ;
                    if(jsonSize == 0){
                        jsonContent = msg.obj.toString();
                    }else {
                        jsonContent = msg.obj.toString().substring(jsonSize);
                    }

                    GetCapabilityJson getCapabilityJson = new GsonBuilder().create().fromJson(jsonContent, GetCapabilityJson.class);
                    for (String t : getCapabilityJson.getCapability()) {
                        Log.d("TAG", t);
                        fuWuList = fuWuList.concat(t);

                    }

                    Logger.d(fuWuList);
                    if (fuWuList.contains("get_version") || fuWuList.contains("get_system_info")) {
                        activity.cvNewGuanLiDevInfo.setVisibility(View.VISIBLE);
                        DigestAuthenticationUtil.startDigest("http://" + deviceIP + ":8199/get_system_info", activity.getSystemInfoHandler, "/get_system_info");
                    }
                    if (fuWuList.contains("wifi_pwd_retrieve")) {
                        activity.cvNewGuanLiWifiPassword.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(() -> DigestAuthenticationUtil.startDigest("http://" + deviceIP + ":8199/wifi_pwd_retrieve",activity.getWifiPasswordHandler,"/wifi_pwd_retrieve"),2000);
                        }
                    if (fuWuList.contains("wifi_pwd_update")) {
                        activity.cvNewGuanLiChangePassword.setVisibility(View.VISIBLE);
                    }
                    if (fuWuList.contains("update_time")) {
                        //TODO 国科设备专用的服务，如果有则提供修改设备时间界面
                    }
                    //new Handler().postDelayed(() -> checkSupportApp(activity),3000);


                } catch (Exception e) {
                    //e.printStackTrace();
                }
                break;

            case NO_TEXT:
                break;

            default:
                break;
        }
    }


}
