package com.jiahua.jiahuatools.presenter;
/*
 * Created by HZP on 2017/12/4.
 */

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.jiahua.jiahuatools.model.OnSubmitListener;
import com.jiahua.jiahuatools.model.SetWifiStaSettingsModel;
import com.jiahua.jiahuatools.model.imp.ISetWifiStaSettingsModel;
import com.jiahua.jiahuatools.utils.SPUtils;
import com.jiahua.jiahuatools.view.imp.ISetWifiStaSettingsView;
import com.orhanobut.logger.Logger;

public class SetWifiStaSettingsPresenter {
    private ISetWifiStaSettingsModel iSetWifiStaSettingsModel;
    private ISetWifiStaSettingsView iSetWifiStaSettingsView;
    private Handler handler = new Handler();

    public SetWifiStaSettingsPresenter(ISetWifiStaSettingsView view){
        this.iSetWifiStaSettingsView = view;
        this.iSetWifiStaSettingsModel = new SetWifiStaSettingsModel();
    }

    public void submit(){
        iSetWifiStaSettingsView.showLoading();
        if(TextUtils.isEmpty(iSetWifiStaSettingsView.getSsid()) || TextUtils.isEmpty(iSetWifiStaSettingsView.getSsidPassword())){
            iSetWifiStaSettingsView.closeLoading();
            iSetWifiStaSettingsView.showFailed();
        }else{
            iSetWifiStaSettingsModel.submit(iSetWifiStaSettingsView.getSsid(),
                    iSetWifiStaSettingsView.getSsidPassword(),
                    iSetWifiStaSettingsView.getDevIP(),
                    new OnSubmitListener() {
                        @Override
                        public void submitSuccess() {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Logger.d(SPUtils.contains((Context)iSetWifiStaSettingsView,"WIFIssid" + ""));
                                    if (SPUtils.contains((Context)iSetWifiStaSettingsView, "WIFIssid")) {
                                        SPUtils.remove((Context)iSetWifiStaSettingsView, "WIFIssid");
                                    }
                                    //本地保存WIFI ssid
                                    SPUtils.put((Context)iSetWifiStaSettingsView, "WIFIssid", iSetWifiStaSettingsView.getSsid());
                                    iSetWifiStaSettingsView.closeLoading();
                                    iSetWifiStaSettingsView.showSuccess();
                                }
                            });
                        }

                        @Override
                        public void submitFailed() {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    iSetWifiStaSettingsView.closeLoading();
                                    iSetWifiStaSettingsView.showFailed();
                                }
                            });

                        }
                    });
        }
    }

    public void destroy() {
        iSetWifiStaSettingsView = null;
        if(iSetWifiStaSettingsModel != null) {
            iSetWifiStaSettingsModel.cancleTasks();
            iSetWifiStaSettingsModel = null;
        }
    }
}
