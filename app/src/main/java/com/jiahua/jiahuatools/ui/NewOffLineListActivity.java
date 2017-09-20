package com.jiahua.jiahuatools.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.jiahua.jiahuatools.R;
import com.jiahua.jiahuatools.bean.DeviceOffLine;
import com.jiahua.jiahuatools.consts.Consts;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class NewOffLineListActivity extends AppCompatActivity implements Consts{

    @BindView(R.id.tv_new_offLineList_dev_info)
    TextView tvNewOffLineListDevInfo;
    @BindView(R.id.tv_new_offLineList_dev_newVersionInfo)
    TextView tvNewOffLineListDevNewVersionInfo;
    /*@BindView(R.id.rv_newOffLineList)
    RecyclerView rvNewOffLineList;*/

    private String display_model_number_add_serial_number;
    private String displayIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_off_line_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ButterKnife.bind(this);

        init();
    }

    private void init() {
        display_model_number_add_serial_number = getIntent().getStringExtra(INTENT_display_model_number_add_serial_number);
        List<DeviceOffLine> deviceOffLineList = DataSupport
                .where(DEVICE_MODEL_NUMBER_ADD_SERIAL_NUMBER + "=?",
                        display_model_number_add_serial_number).find(DeviceOffLine.class);
        DeviceOffLine deviceOffLine = deviceOffLineList.get(0);
        StringBuilder systemInfo = new StringBuilder();
        if (!deviceOffLine.getDevice_serial_number().isEmpty()) {
            systemInfo.append("序列号：").append(deviceOffLine.getDevice_serial_number()).append("\n");
        }
        if (!deviceOffLine.getDevice_hwid().isEmpty()) {
            systemInfo.append("硬件号：").append(deviceOffLine.getDevice_hwid()).append("\n");
        }

        if (!deviceOffLine.getDevice_Mac().isEmpty()) {
            systemInfo.append("Mac地址：").append(deviceOffLine.getDevice_Mac()).append("\n");
        }
        if (!deviceOffLine.getDevice_swid().isEmpty()) {
            systemInfo.append("软件号：").append(deviceOffLine.getDevice_swid()).append("\n");

            if(deviceOffLine.getDevice_model_number() != null && !deviceOffLine.getDevice_model_number().isEmpty()) {
                if (deviceOffLine.getDevice_model_number().equals(MT_guoKe_model_number)){
                    guoKeRuanJianBaoShengJiChaXun(deviceOffLine.getDevice_swid());
                }else if (deviceOffLine.getDevice_model_number().equals(MT_cheJi_model_number)) {
                    cheJiRuanJianBaoChaXun();
                }
            }

        }
        if (deviceOffLine.getDevice_cheji_stm32ver() != null && !deviceOffLine.getDevice_cheji_stm32ver().isEmpty()) {
            systemInfo.append("单片机版本：").append(deviceOffLine.getDevice_cheji_stm32ver()).append("\n");
        }
        if (!deviceOffLine.getDevice_url().isEmpty()) {
            displayIP = deviceOffLine.getDevice_url();
        }

        Logger.d(systemInfo.toString());
        if (!systemInfo.toString().isEmpty()) {
            tvNewOffLineListDevInfo.setText(systemInfo.toString());
        }

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

    //国科升级软件包查询
    private void guoKeRuanJianBaoShengJiChaXun(final String dangQianBanBenName) {
        //String url = "http://120.27.94.20:10080/vendor/imotom/MT1828/upgrade.txt";
        OkHttpUtils
                .get()
                .url(URL_inquire_guoKe_dev_update_doc)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d("TAG", e.getMessage());

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d("TAG", response);
                        //String a = "AAA6";
                        for (String str : response.split(";")) {
                            Log.d("TAG", "当前坂本为：" + str.split(":")[0].trim());
                            if (str.split(":")[0].trim().equals(dangQianBanBenName)) {
                                Log.d("TAG", "下个升级的坂本为：" + str.split(":")[1]);
                                if (str.split(":")[1].equals("new")) {
                                    tvNewOffLineListDevNewVersionInfo.setText("当前已是最新版本，无需升级！");
                                } else {
                                    tvNewOffLineListDevNewVersionInfo.setText("有新版本：" + str.split(":")[1] + "\n请尽快联系4S店或者厂家升级！");
                                }
                                return;
                            } else {
                                //当网络上无此版本号时，提示无需升级
                                tvNewOffLineListDevNewVersionInfo.setText("当前已是最新版本，无需升级！");
                            }
                        }

                    }
                });
    }


    //车机升级软件包查询
    private void cheJiRuanJianBaoChaXun() {
        //String url = "http://120.27.94.20:10080/vendor/imotom/MT1845/upgrade.txt";
        OkHttpUtils
                .get()
                .url(URL_inquire_cheJi_dev_update_doc)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d("TAG", e.getMessage());

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d("TAG", response);
                        tvNewOffLineListDevNewVersionInfo.setText(String.valueOf("最新固件包日期为："+response + "\n如需要更新，请联系4S店或者厂家！"));

                    }
                });
    }
}
