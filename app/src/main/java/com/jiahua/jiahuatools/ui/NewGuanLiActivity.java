package com.jiahua.jiahuatools.ui;

import android.content.Intent;
import android.os.Bundle;
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
import com.jiahua.jiahuatools.utils.DigestAuthenticationUtil;
import com.jiahua.jiahuatools.utils.SPUtils;
import com.orhanobut.logger.Logger;

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

    //设备URL及设备名和设备序列号
    private String myBaseUrl;

    public GetSystemInfoHandler getSystemInfoHandler;
    private GetCapabilityHandler getCapabilityHandler;
    public GetWifiPasswordHandler getWifiPasswordHandler = new GetWifiPasswordHandler(this);
    private String displayModelNumber;
    private String displaySerialNumber;
    private String displayFriendlyName;
    private String deviceIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_guan_li);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ButterKnife.bind(this);

        init();
    }

    /**
     * 返回界面获取新的WIFI密码
     */
    @Override
    protected void onResume() {
        Logger.d("hahaahah");
        if (SPUtils.contains(this, "WIFI密码")) {
            String WIFIMiMa = String.valueOf(SPUtils.get(NewGuanLiActivity.this, "WIFI密码", ""));
            Logger.d(WIFIMiMa);
            tvNewGuanLiShowPassword.setText(WIFIMiMa);
            Logger.d("获取WIFI密码界面");
        } else {
            Logger.d("没有本地保存密码");
        }
        super.onResume();
    }

    private void init() {
        //获取上个界面传过来的intent
        Intent intent = getIntent();
        myBaseUrl = intent.getStringExtra(Consts.INTENT_deviceURL);
        deviceIP = myBaseUrl.replaceAll(REG, "$1");
        displayFriendlyName = intent.getStringExtra(Consts.INTENT_display_friendly_name);
        displaySerialNumber = intent.getStringExtra(Consts.INTENT_display_serial_number);
        displayModelNumber = intent.getStringExtra(Consts.INTENT_display_model_number);

        getSystemInfoHandler = new GetSystemInfoHandler(this,
                displayFriendlyName, displayModelNumber, displaySerialNumber, deviceIP);
        getCapabilityHandler = new GetCapabilityHandler(this, displayModelNumber,
                displaySerialNumber, deviceIP);

        Logger.e(myBaseUrl + "===" + displayFriendlyName +
                "==" + displaySerialNumber + "==" + displayModelNumber);

        getCapability();
    }

    @OnClick({R.id.cv_new_guanLi_change_password, R.id.cv_new_guanLi_upgrad,R.id.cv_new_guanLi_ZBXX})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cv_new_guanLi_change_password:
                Intent intentXiuGai = new Intent(this, XiuGaiMiMaActivity.class);
                intentXiuGai.putExtra(Consts.INTENT_deviceURL, myBaseUrl);
                startActivity(intentXiuGai);
                break;

            case R.id.cv_new_guanLi_ZBXX:
                startActivity(new Intent(this,TianXieZhuBanXinXiActivity.class)
                        .setFlags(109).putExtra(INTENT_deviceURL, deviceIP));
                break;

            case R.id.cv_new_guanLi_upgrad:
                Intent intent;
                if(displayModelNumber.equals(MT_cheJi_model_number)){
                    intent = new Intent(this,CheckDeviceInfoActivity.class);
                }else {
                    intent = new Intent(this, CheckDevVersionActivity.class);
                }
                intent.putExtra(INTENT_deviceURL,myBaseUrl);
                intent.putExtra(INTENT_display_serial_number, displaySerialNumber);
                intent.putExtra(INTENT_display_friendly_name, displayFriendlyName);
                intent.putExtra(INTENT_display_model_number, displayModelNumber);
                startActivity(intent);
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
        }
        return super.onOptionsItemSelected(item);

    }
}
