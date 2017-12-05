package com.jiahua.jiahuatools.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.jiahua.jiahuatools.R;
import com.jiahua.jiahuatools.consts.Consts;
import com.jiahua.jiahuatools.handler.GetCapabilityHandler;
import com.jiahua.jiahuatools.handler.GetSystemInfoHandler;
import com.jiahua.jiahuatools.handler.GetWifiPasswordHandler;
import com.jiahua.jiahuatools.handler.GetWifiStaSsidHandler;
import com.jiahua.jiahuatools.utils.DigestAuthenticationUtil;
import com.jiahua.jiahuatools.utils.SPUtils;
import com.jiahua.jiahuatools.view.SetWifiStaSettingsActivity;
import com.orhanobut.logger.Logger;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewGuanLiActivity extends AppCompatActivity implements Consts {

    @BindView(R.id.tv_new_guanLi_dev_info)
    public
    TextView tvNewGuanLiDevInfo;
    @BindView(R.id.tv_new_guanLi_show_password)
    public
    TextView tvNewGuanLiShowPassword;
    @BindView(R.id.tv_new_guanLi_dev_newVersionInfo)
    public TextView tvNewGuanLiDevVersion;
    @BindView(R.id.cv_new_guanLi_wifiPassword)
    public
    CardView cvNewGuanLiWifiPassword;
    @BindView(R.id.cv_new_guanLi_devInfo)
    public
    CardView cvNewGuanLiDevInfo;
    @BindView(R.id.cv_new_guanLi_change_password)
    public CardView cvNewGuanLiChangePassword;
    @BindView(R.id.cv_new_guanLi_upgrad)
    CardView cvNewGuanLiUpgrade;
    @BindView(R.id.cv_new_guanLi_ZBXX)
    CardView cvNewGuanLiZBXX;
    @BindView(R.id.tv_new_guanLi_show_ssid)
    public TextView tvNewGuanLiShowSsid;

    //设备URL及设备名和设备序列号
    private String myBaseUrl;

    public GetSystemInfoHandler getSystemInfoHandler;
    private GetCapabilityHandler getCapabilityHandler;
    public GetWifiPasswordHandler getWifiPasswordHandler = new GetWifiPasswordHandler(new WeakReference<>(this));
    public GetWifiStaSsidHandler getWifiStaSsidHandler = new GetWifiStaSsidHandler(new WeakReference<>(this));
    private String displayModelNumber;
    private String displaySerialNumber;
    private String displayFriendlyName;
    private String deviceIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_guan_li);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.md_yellow_500));
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_yellow_24dp);

        ButterKnife.bind(this);

        init();
    }

    /**
     * 返回界面获取新的WIFI密码
     */
    @Override
    protected void onResume() {
        if (SPUtils.contains(this, "WIFI密码")) {
            String WIFIMiMa = String.valueOf(SPUtils.get(NewGuanLiActivity.this, "WIFI密码", ""));
            Logger.d(WIFIMiMa);
            tvNewGuanLiShowPassword.setText(WIFIMiMa);
            Logger.d("获取WIFI密码界面");
        } else {
            Logger.d("没有本地保存密码");
        }

        if (SPUtils.contains(this,"WIFIssid")) {
           String wifiSsid = String.valueOf(SPUtils.get(NewGuanLiActivity.this,"WIFIssid",""));
           tvNewGuanLiShowSsid.setText(wifiSsid);
        }
        super.onResume();
    }

    private void init() {
        //获取上个界面传过来的intent
        Intent intent = getIntent();
        //获取设备URL
        myBaseUrl = intent.getStringExtra(Consts.INTENT_deviceURL);
        //通过正则表达式获取设备IP地址
        deviceIP = myBaseUrl.replaceAll(REG, "$1");
        //获取设备友好名
        displayFriendlyName = intent.getStringExtra(Consts.INTENT_display_friendly_name);
        //获取设备序列号
        displaySerialNumber = intent.getStringExtra(Consts.INTENT_display_serial_number);
        //获取设备型号
        displayModelNumber = intent.getStringExtra(Consts.INTENT_display_model_number);
        //获取系统信息
        getSystemInfoHandler = new GetSystemInfoHandler(new WeakReference<>(this),
                displayFriendlyName, displayModelNumber, displaySerialNumber, deviceIP);
        //获取设备服务能力
        getCapabilityHandler = new GetCapabilityHandler(new WeakReference<>(this), displayModelNumber,
                displaySerialNumber, deviceIP);

        Logger.e(myBaseUrl + "===" + displayFriendlyName +
                "==" + displaySerialNumber + "==" + displayModelNumber);

        getCapability();
    }

    @SuppressLint("WrongConstant")
    @OnClick({R.id.cv_new_guanLi_change_password, R.id.cv_new_guanLi_upgrad, R.id.cv_new_guanLi_ZBXX, R.id.cv_new_guanLi_change_hotspot})
    public void onClick(View v) {
        switch (v.getId()) {
            //修改WiFi密码按钮
            case R.id.cv_new_guanLi_change_password:
                Intent intentXiuGai = new Intent(this, XiuGaiMiMaActivity.class);
                intentXiuGai.putExtra(Consts.INTENT_deviceURL, myBaseUrl);
                startActivity(intentXiuGai);
                break;

            //填写主板信息按钮
            case R.id.cv_new_guanLi_ZBXX:
                startActivity(new Intent(this, TianXieZhuBanXinXiActivity.class)
                        .setFlags(109).putExtra(INTENT_deviceURL, deviceIP));
                break;

            //软件升级按钮
            case R.id.cv_new_guanLi_upgrad:
                Intent intent;
                if (displayModelNumber.equals(MT_cheJi_model_number)) {
                    intent = new Intent(this, CheckDeviceInfoActivity.class);
                } else {
                    intent = new Intent(this, CheckDevVersionActivity.class);
                }
                //传入设备URL
                intent.putExtra(INTENT_deviceURL, myBaseUrl);
                //传入设备序列号
                intent.putExtra(INTENT_display_serial_number, displaySerialNumber);
                //传入设备别名
                intent.putExtra(INTENT_display_friendly_name, displayFriendlyName);
                //传入设备类型
                intent.putExtra(INTENT_display_model_number, displayModelNumber);
                startActivity(intent);
                break;

            case R.id.cv_new_guanLi_change_hotspot:
                startActivity(new Intent(this, SetWifiStaSettingsActivity.class).putExtra(Consts.INTENT_deviceURL, deviceIP));
                /*WifiStaSettingsJson setWifiStaSettingsJson = new WifiStaSettingsJson();
                setWifiStaSettingsJson.setSsid(etNewGuanLiSsid.getText().toString());
                setWifiStaSettingsJson.setPassword(etNewGuanLiHotspotPassword.getText().toString());
                String ss = new GsonBuilder().create().toJson(setWifiStaSettingsJson, WifiStaSettingsJson.class);
                //String ss = "{\"ssid\":\"Honor 6X\",\"password\":\"88888888\"}";
                Logger.e(ss);
                String url = "http://" + myBaseUrl.replaceAll(REG, "$1") + ":8199/set_wifi_sta_settings";

                new Thread(() -> {
                    try {
                        Response response;
                        response = OkHttpUtils
                                .postString()
                                .url(url)
                                .content(ss)
                                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                                .build()
                                .execute();
                        if (response.code() == 401) {
                            Headers responseHeaders = response.headers();
                            for (int i = 0; i < responseHeaders.size(); i++) {
                                Logger.d(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                            }
                            String authorizationHaderValue = DigestAuthenticationUtil
                                    .startDigestPost(response.header("WWW-Authenticate"), "admin", "admin", "/set_wifi_sta_settings");
                            OkHttpUtils
                                    .postString()
                                    .url(url)
                                    .content(ss)
                                    .addHeader("Authorization",
                                            authorizationHaderValue)
                                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
                                    .build()
                                    .execute(new StringCallback() {
                                        @Override
                                        public void onError(Call call, Exception e, int id) {
                                            //Toast.makeText(NewGuanLiActivity.this, "错误" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            Snackbar.make(v,"热点修改失败！请重试！",Snackbar.LENGTH_LONG).show();
                                        }

                                        @Override
                                        public void onResponse(String response, int id) {
                                            Log.e("TAG",response);
                                            //Toast.makeText(NewGuanLiActivity.this, "返回成功" + response, Toast.LENGTH_SHORT).show();
                                            Snackbar.make(v,"热点修改成功！",Snackbar.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }).start();*/
                break;

            default:
                break;
        }
    }

    //获取设备服务
    private void getCapability() {
        //创建一个Request  "http://192.168.63.9:8199/get_capability"  正则表达式获取IP，再加端口号处理
        //String reg = ".*\\/\\/([^\\/\\:]*).*";
        String myUrl = "http://" + myBaseUrl.replaceAll(REG, "$1") + ":8199/";
        String url = myUrl + "get_capability";
        DigestAuthenticationUtil.startDigest(url, getCapabilityHandler, "/get_capability");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);

    }
}
