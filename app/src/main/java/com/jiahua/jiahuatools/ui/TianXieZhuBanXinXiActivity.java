package com.jiahua.jiahuatools.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.jiahua.jiahuatools.R;
import com.jiahua.jiahuatools.bean.GetSystemInfoJson;
import com.jiahua.jiahuatools.bean.TicketTask;
import com.jiahua.jiahuatools.consts.Consts;
import com.jiahua.jiahuatools.utils.DigestAuthenticationUtil;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.utils.L;

import org.litepal.crud.DataSupport;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Response;


public class TianXieZhuBanXinXiActivity extends AppCompatActivity implements Consts {
    @BindView(R.id.et_TXZBXX_sn)
    EditText et_TXZBXX_sn;

    @BindView(R.id.et_TXZBXX_hwid)
    EditText et_TXZBXX_hwid;

    @BindView(R.id.tv_TXZBXX_swid)
    TextView tv_TXZBXX_swid;

    /*@BindView(R.id.et_TXZBXX_mac)
    EditText et_TXZBXX_mac;*/

    @BindView(R.id.tv_TXZBXX_stm32)
    TextView tv_TXZBXX_stm32;

    @BindView(R.id.et_TXZBXX_model_name)
    EditText et_TXZBXX_model_name;

    @BindView(R.id.spinner_txzbxx_can_controler)
    Spinner spinner_txzbxx_can_controler;

    @BindView(R.id.spinner_txzbxx_model_name)
    Spinner spinner_txzbxx_model_name;

    @BindView(R.id.spinner_txzbxx_sn)
    Spinner spinner_txzbxx_sn;

    @BindView(R.id.spinner_txzbxx_hwid)
    Spinner spinner_txzbxx_hwid;

    /*@BindView(R.id.spinner_txzbxx_mac)
    Spinner spinner_txzbxx_mac;*/

    @BindView(R.id.et_TXZBXX_can_controler)
    EditText et_TXZBXX_can_controler;

    @BindView(R.id.ll_txzbxx_modelName)
    LinearLayout ll_txzbxx_modelName;

    @BindView(R.id.ll_txzbxx_canControler)
    LinearLayout ll_txzbxx_canControler;

    @BindView(R.id.cv_TXZBXX_tiJiao)
    CardView cdTXZBXXTiJiao;
    private String data;
    private String myUrl;
    private GetSystemInfoJson getSystemInfoJson;

    private Handler getSystemInfoHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<TianXieZhuBanXinXiActivity> myActivity;

        private MyHandler(TianXieZhuBanXinXiActivity myActivity) {
            this.myActivity = new WeakReference<>(myActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TianXieZhuBanXinXiActivity activity = myActivity.get();
            switch (msg.what) {
                case OK_TEXT:
                    // 在这里可以进行UI操作
                    try {
                        int jsonSize = msg.obj.toString().indexOf("{");
                        String jsonContent;
                        if (jsonSize == 0) {
                            jsonContent = msg.obj.toString();
                        } else {
                            jsonContent = msg.obj.toString().substring(jsonSize);
                        }

                        activity.getSystemInfoJson = new GsonBuilder().create().fromJson(jsonContent, GetSystemInfoJson.class);
                        if (!activity.getSystemInfoJson.getSn().isEmpty()) {
                            activity.et_TXZBXX_sn.setText(activity.getSystemInfoJson.getSn());
                        }
                        if (!activity.getSystemInfoJson.getHwid().isEmpty()) {
                            activity.et_TXZBXX_hwid.setText(activity.getSystemInfoJson.getHwid());
                        }
                        if (!activity.getSystemInfoJson.getSwid().isEmpty()) {
                            activity.tv_TXZBXX_swid.setText(activity.getSystemInfoJson.getSwid());
                        }
                        /*if (!activity.getSystemInfoJson.getMac().isEmpty()) {
                            activity.et_TXZBXX_mac.setText(activity.getSystemInfoJson.getMac());
                        }*/
                        if (!activity.getSystemInfoJson.getStm32_ver().isEmpty()) {
                            activity.tv_TXZBXX_stm32.setText(activity.getSystemInfoJson.getStm32_ver());
                        }
                        if (!activity.getSystemInfoJson.getModel_name().isEmpty()) {
                            activity.et_TXZBXX_model_name.setText(activity.getSystemInfoJson.getModel_name());
                        }
                        if (!activity.getSystemInfoJson.getCan_controler().isEmpty()) {
                            activity.et_TXZBXX_can_controler.setText(activity.getSystemInfoJson.getCan_controler());
                        }

                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                    break;

                case NO_TEXT:
                    Toast.makeText(activity, "获取主板信息失败。", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tian_xie_zhu_ban_xin_xi);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.md_yellow_500));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_yellow_24dp);

        init();

    }

    @SuppressLint("WrongConstant")
    private void init() {
        Intent intent = getIntent();
        if (intent.getFlags() == 109) {
            //通过主界面进入，获取设备的ip地址组装路径
            myUrl = intent.getStringExtra(INTENT_deviceURL);
            String url = "http://" + myUrl + ":8199/get_system_info";
            DigestAuthenticationUtil.startDigest(url, getSystemInfoHandler, "/get_system_info");
        } else {
            String url = "http://192.168.43.1:8199/get_system_info";
            DigestAuthenticationUtil.startDigest(url, getSystemInfoHandler, "/get_system_info");
        }

        //配置设备序列号可选集合组
        String[] canControls = getResources().getStringArray(R.array.sn);
        ArrayAdapter<String> canControlAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, canControls);
        //为适配器配置字符组
        canControlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_txzbxx_sn.setAdapter(canControlAdapter);
        spinner_txzbxx_sn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                et_TXZBXX_sn.setText((String) spinner_txzbxx_sn.getSelectedItem());
                //根据序列号的选择，自动填写其他项目的数据
                if (position == 1) {
                    et_TXZBXX_hwid.setText(getResources().getStringArray(R.array.hwid)[1]);
                    //et_TXZBXX_mac.setText(getResources().getStringArray(R.array.Mac)[1]);
                    //如果是第一种选项(MT1828)，因为1828没有设备名和can，所以需要隐藏，防止误输入
                    ll_txzbxx_modelName.setVisibility(View.GONE);
                    ll_txzbxx_canControler.setVisibility(View.GONE);
                    et_TXZBXX_model_name.setText("");
                    et_TXZBXX_can_controler.setText("");

                } else if (position == 2) {
                    //第二选择类型为1845，因为1845需要填写设备名和can，所以需要显示
                    et_TXZBXX_hwid.setText(getResources().getStringArray(R.array.hwid)[2]);
                    //et_TXZBXX_mac.setText(getResources().getStringArray(R.array.Mac)[2]);
                    ll_txzbxx_modelName.setVisibility(View.VISIBLE);
                    ll_txzbxx_canControler.setVisibility(View.VISIBLE);
                    et_TXZBXX_model_name.setText(getResources().getStringArray(R.array.modelName)[2]);
                    et_TXZBXX_can_controler.setText(getResources().getStringArray(R.array.canControl)[2]);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        setAdapter(spinner_txzbxx_model_name, et_TXZBXX_model_name, R.array.modelName);

        setAdapter(spinner_txzbxx_can_controler, et_TXZBXX_can_controler, R.array.canControl);

        setAdapter(spinner_txzbxx_hwid, et_TXZBXX_hwid, R.array.hwid);

        //setAdapter(spinner_txzbxx_mac, et_TXZBXX_mac, R.array.Mac);
    }


    @Override
    protected void onResume() {
        cdTXZBXXTiJiao.setClickable(true);
        super.onResume();
    }


    private void setAdapter(Spinner spinner, EditText editText, int arrayNumber) {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(arrayNumber));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editText.setText((String) spinner.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @SuppressLint("WrongConstant")
    @OnClick(R.id.cv_TXZBXX_tiJiao)
    public void tiJiao() {
        cdTXZBXXTiJiao.setClickable(false);
        /*data = "{\"sn\":\""+et_TXZBXX_sn.getText().toString()+
                "\",\"hwid\":\""+et_TXZBXX_hwid.getText().toString()+
                //"\",\"swid\":\""+et_TXZBXX_swid.getText().toString()+
                "\",\"mac\":\""+et_TXZBXX_mac.getText().toString()+
                "\"}";*/

        //拼接需要提交的
        GetSystemInfoJson getSystemInfoJson = new GetSystemInfoJson();
        getSystemInfoJson.setSn(et_TXZBXX_sn.getText().toString());
        getSystemInfoJson.setHwid(et_TXZBXX_hwid.getText().toString());
        //getSystemInfoJson.setMac(et_TXZBXX_mac.getText().toString());
        if (!et_TXZBXX_model_name.getText().toString().isEmpty()) {
            getSystemInfoJson.setModel_name(et_TXZBXX_model_name.getText().toString());
            getSystemInfoJson.setCan_controler(et_TXZBXX_can_controler.getText().toString());

        }
        data = new GsonBuilder().create().toJson(getSystemInfoJson, GetSystemInfoJson.class);
        Intent intent = getIntent();
        String url;
        if (intent.getFlags() == 109) {
            url = "http://" + myUrl + ":8199/set_system_info";
        } else {
            url = "http://192.168.43.1:8199/set_system_info";
        }

        new Thread(() -> {
            try {
                Response response;
                response = OkHttpUtils
                        .postString()
                        .url(url)
                        .content(data)
                        .mediaType(MediaType.parse("application/json; charset=utf-8"))
                        .build()
                        .execute();
                if (response.code() == 401) {
                    Headers responseHeaders = response.headers();
                    for (int i = 0; i < responseHeaders.size(); i++) {
                        Logger.d(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }
                    String authorizationHaderValue = DigestAuthenticationUtil.startDigestPost(response.header("WWW-Authenticate"), "admin", "admin", "/set_system_info");
                    OkHttpUtils
                            .postString()
                            .url(url)
                            .content(data)
                            .addHeader("Authorization",
                                    authorizationHaderValue)
                            .mediaType(MediaType.parse("application/json; charset=utf-8"))
                            .build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    Toast.makeText(TianXieZhuBanXinXiActivity.this, "提交失败，请重试！", Toast.LENGTH_LONG).show();
                                    cdTXZBXXTiJiao.setClickable(true);
                                    L.e("TXZ_error-------------" + e.getMessage());
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    L.e("TXZ_ok-------------" + response);
                                    if (response.contains("200")) {
                                        TicketTask ticketTask = new TicketTask();
                                        ticketTask.setAccomplish(true);
                                        ticketTask.saveOrUpdate(Consts.DEVICE_SN + "=?", et_TXZBXX_sn.getText().toString());
                                        Toast.makeText(TianXieZhuBanXinXiActivity.this, "修改成功。", Toast.LENGTH_LONG).show();
                                        cdTXZBXXTiJiao.setClickable(true);

                                    }
                                }
                            });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_daoRu:
                //执行导入数据
                saveData();
                return true;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    private void saveData() {
        if (DataSupport.isExist(TicketTask.class, "Flag=?", "1")) {
            TicketTask ticketTask = DataSupport.where("Flag=?", "1").findFirst(TicketTask.class);
            Logger.e(ticketTask.getSn());
            ticketTask.setFlag(false);
            ticketTask.saveOrUpdate(Consts.DEVICE_SN + "=?", ticketTask.getSn());
            et_TXZBXX_sn.setText(ticketTask.getSn());
            et_TXZBXX_hwid.setText(ticketTask.getHwid());
            //et_TXZBXX_mac.setText(ticketTask.getMac());
            et_TXZBXX_model_name.setVisibility(View.GONE);
            et_TXZBXX_can_controler.setVisibility(View.GONE);
            Toast.makeText(this, "导入数据成功，确认无误后请点击提交！", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "当前无数据可导入，请手动填写！", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_tian_xie_zhu_ban_xin_xi, menu);
        return true;
    }

}
