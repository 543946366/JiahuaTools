package com.jiahua.jiahuatools.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.gson.GsonBuilder;
import com.jiahua.jiahuatools.R;
import com.jiahua.jiahuatools.bean.GetCapabilityJson;
import com.jiahua.jiahuatools.consts.Consts;
import com.jiahua.jiahuatools.utils.DigestAuthenticationUtil;
import com.jiahua.jiahuatools.utils.FileUtils;
import com.orhanobut.logger.Logger;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GuanLiActivity extends AppCompatActivity implements View.OnClickListener, Consts {

    @BindView(R.id.ll_checkDevVersion_guanLi)
    LinearLayout ll_checkDevVersion_guanLi;
    @BindView(R.id.ll_huoQuWIFIMiMa_guanLi)
    LinearLayout ll_huoQuWIFIMiMa_guanLi;
    @BindView(R.id.ll_xiuGaiWIFIMiMa_guanLi)
    LinearLayout ll_xiuGaiWIFIMiMa_guanLi;
    @BindView(R.id.ll_GKDVRAPP_guanLi)
    LinearLayout ll_GKDVRAPP_guanLi;
    @BindView(R.id.ll_carDev_guanLi)
    LinearLayout ll_carDev_guanLi;

    //设备URL及设备名和设备序列号
    private String myBaseUrl;
    String displayFriendlyName;
    private String displaySerialNumber;
    private String displayModelNumber;

    static GetCapabilityJson getCapabilityJson;

    private Handler logTextHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<GuanLiActivity> myActivity;

        private MyHandler(GuanLiActivity myActivity) {
            this.myActivity = new WeakReference<>(myActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            GuanLiActivity activity = myActivity.get();
            switch (msg.what) {
                case OK_TEXT:
                    // 在这里可以进行UI操作
                    // new DownloadTask().execute();
                    try {
                        String fuWuList = "";

                        int jsonSize = msg.obj.toString().indexOf("{");
                        String jsonContent ;
                        if(jsonSize == 0){
                            jsonContent = msg.obj.toString();
                        }else {
                            jsonContent = msg.obj.toString().substring(jsonSize);
                        }

                        getCapabilityJson = new GsonBuilder().create().fromJson(jsonContent, GetCapabilityJson.class);
                        for (String t : getCapabilityJson.getCapability()) {
                            Log.d("TAG", t);
                            fuWuList = fuWuList.concat(t);
                        }

                        Logger.d(fuWuList);
                        if (fuWuList.contains("get_version")) {
                            activity.ll_checkDevVersion_guanLi.setVisibility(View.VISIBLE);
                        }
                        if (fuWuList.contains("wifi_pwd_retrieve")) {
                            activity.ll_huoQuWIFIMiMa_guanLi.setVisibility(View.VISIBLE);
                        }
                        if (fuWuList.contains("wifi_pwd_update")) {
                            activity.ll_xiuGaiWIFIMiMa_guanLi.setVisibility(View.VISIBLE);
                        }

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

    public String test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guan_li);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ButterKnife.bind(this);

        init();
        initView();
    }

    private void init() {
        //获取上个界面传过来的intent
        Intent intent = getIntent();
        myBaseUrl = intent.getStringExtra(Consts.INTENT_deviceURL);
        displayFriendlyName = intent.getStringExtra(Consts.INTENT_display_friendly_name);
        displaySerialNumber = intent.getStringExtra(Consts.INTENT_display_serial_number);
        displayModelNumber = intent.getStringExtra(Consts.INTENT_display_model_number);

        //新的业务逻辑
        if(displayModelNumber.equals(MT_guoKe_model_number)){
            ll_carDev_guanLi.setVisibility(View.GONE);
            ll_GKDVRAPP_guanLi.setVisibility(View.VISIBLE);
        }
        if(displayModelNumber.equals(MT_cheJi_model_number)){
            ll_carDev_guanLi.setVisibility(View.VISIBLE);
            ll_GKDVRAPP_guanLi.setVisibility(View.GONE);
        }


        getCapability();
    }

    private void initView() {
        ll_checkDevVersion_guanLi.setOnClickListener(this);
        ll_huoQuWIFIMiMa_guanLi.setOnClickListener(this);
        ll_xiuGaiWIFIMiMa_guanLi.setOnClickListener(this);
        ll_GKDVRAPP_guanLi.setOnClickListener(this);
        ll_carDev_guanLi.setOnClickListener(this);
    }

    private void getCapability() {
        //创建一个Request  "http://192.168.63.9:8199/get_capability"  正则表达式获取IP，再加端口号处理
        //String reg = ".*\\/\\/([^\\/\\:]*).*";
        String myUrl = "http://" + myBaseUrl.replaceAll(REG, "$1") + ":8199/";
        String url = myUrl + "get_capability";
        DigestAuthenticationUtil.startDigest(url, logTextHandler, "/get_capability");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_checkDevVersion_guanLi:
                Intent intent = null;
                if(displayModelNumber.equals(MT_guoKe_model_number)) {
                    intent = new Intent(this, CheckDevVersionActivity.class);

                }else if(displayModelNumber.equals(MT_cheJi_model_number)){
                    intent = new Intent(this,CheckDeviceInfoActivity.class);

                }
                if (intent != null) {
                    intent.putExtra(INTENT_deviceURL,myBaseUrl);
                    intent.putExtra(INTENT_display_serial_number, displaySerialNumber);
                    intent.putExtra(INTENT_display_friendly_name, displayFriendlyName);
                    intent.putExtra(INTENT_display_model_number, displayModelNumber);
                    startActivity(intent);
                }

                break;

            case R.id.ll_huoQuWIFIMiMa_guanLi:
                Intent intentHuoQu = new Intent(this, HuoQuWIFIMiMaActivity.class);
                intentHuoQu.putExtra(Consts.INTENT_deviceURL, myBaseUrl);
                startActivity(intentHuoQu);
                break;

            case R.id.ll_xiuGaiWIFIMiMa_guanLi:
                Intent intentXiuGai = new Intent(this, XiuGaiMiMaActivity.class);
                intentXiuGai.putExtra(Consts.INTENT_deviceURL, myBaseUrl);
                startActivity(intentXiuGai);
                break;

            case R.id.ll_GKDVRAPP_guanLi:
                break;

            case R.id.ll_carDev_guanLi:
                //本地保存设备IP地址
                //String reg = ".*\\/\\/([^\\/\\:]*).*";
                FileUtils.writeTxtToFile(myBaseUrl.replaceAll(REG, "$1"), Environment.getExternalStorageDirectory().getPath() + "/imotom/", "DeviceOffLine.txt");
                break;
        }
    }



}
