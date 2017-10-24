package com.jiahua.jiahuatools.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jiahua.jiahuatools.R;
import com.jiahua.jiahuatools.bean.DeviceOffLine;
import com.jiahua.jiahuatools.consts.Consts;
import com.jiahua.jiahuatools.service.DownloadFileService;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class LiXianGuJianDownloadActivity extends AppCompatActivity implements View.OnClickListener, Consts {

    @BindView(R.id.tv_dangQianBanBen_liXian)
    TextView tv_dangQianBanBen_liXian;
    @BindView(R.id.tv_log_liXian)
    TextView tv_log_liXian;
    @BindView(R.id.btn_xiaZai_liXian)
    Button btn_xiaZai_liXian;
    //进度条
    @BindView(R.id.pb_guJianXiaZai_LiXian)
    ProgressBar mProgressBar;
    //网络上新的版本号
    private String newBanBen;

    private boolean isDownload;

    /**
     * 广播接收者
     */
    private BroadcastReceiver receiver;

    /*public static final int NEXT_BANBENHAO_TEXT = 1;*/
    private Handler logTextHandler = new MyHandler(this);

       /* {public void handleMessage(Message msg) {
            switch (msg.what) {
                //下个版本信息
                case NEXT_BANBENHAO_TEXT:
                    if (String.valueOf(msg.obj).equals("当前已是最新版本，无需升级！")) {
                        tv_log_liXian.setText("当前已是最新版本，无需升级！");

                    } else {
                        tv_log_liXian.setText(String.valueOf("有新版本：" + newBanBen));
                        File file = new File(Environment.getExternalStorageDirectory(), newBanBen);
                        //判断是否已下载固件
                        if (!file.exists()) {
                            tv_log_liXian.setText("还没下载最新固件，请下载固件后再进行升级！"+"\n" + "升级文件较大，建议在WIFI环境下再下载！");
                            btn_xiaZai_liXian.setVisibility(View.VISIBLE);
                        } else {
                            if(isDownload){
                                tv_log_liXian.setText(String.valueOf(newBanBen + "固件下载中..."));
                            }else {
                                tv_log_liXian.setText("本地已有新版本:"+newBanBen+"，无需再下载，请连接设备升级！");
                            }

                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };*/

    private static class MyHandler extends Handler {
        private final WeakReference<LiXianGuJianDownloadActivity> myActivity;

        private MyHandler(LiXianGuJianDownloadActivity myActivity) {
            this.myActivity = new WeakReference<>(myActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            LiXianGuJianDownloadActivity activity = myActivity.get();
            switch (msg.what) {
                //下个版本信息
                case NEXT_BANBENHAO_TEXT:
                    if ("当前已是最新版本，无需升级！".equals(String.valueOf(msg.obj))) {
                        activity.tv_log_liXian.setText("当前已是最新版本，无需升级！");

                    } else {
                        activity.tv_log_liXian.setText(String.valueOf("有新版本：" + activity.newBanBen));
                        File file = new File(Environment.getExternalStorageDirectory(), activity.newBanBen);
                        //判断是否已下载固件
                        if (!file.exists()) {
                            activity.tv_log_liXian.setText("还没下载最新固件，请下载固件后再进行升级！" + "\n" + "升级文件较大，建议在WIFI环境下再下载！");
                            activity.btn_xiaZai_liXian.setVisibility(View.VISIBLE);
                        } else {
                            if (activity.isDownload) {
                                activity.tv_log_liXian.setText(String.valueOf(activity.newBanBen + "固件下载中..."));
                            } else {
                                activity.tv_log_liXian.setText("本地已有新版本:" + activity.newBanBen + "，无需再下载，请连接设备升级！");
                            }

                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_li_xian_gu_jian_download);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        init();
        initView();
    }

    private void init() {
        Intent intentGetFlags = getIntent();
        //新的业务逻辑获取本地保存的版本号

        List<DeviceOffLine> deviceOffLineList = DataSupport
                .where(DEVICE_MODEL_NUMBER_ADD_SERIAL_NUMBER + "=?",
                        intentGetFlags.getStringExtra(INTENT_display_model_number_add_serial_number)).find(DeviceOffLine.class);
        DeviceOffLine deviceOffLine = deviceOffLineList.get(0);
        Logger.e("" + deviceOffLine.getDevice_guoke_version_number());
        Logger.e(deviceOffLine.getDevice_model_number_add_serial_number());
        Logger.d(deviceOffLineList.size() + "_______180_________" + deviceOffLineList.get(0).getDevice_guoke_version_number() + "____" + intentGetFlags.getStringExtra(INTENT_display_model_number_add_serial_number));
        String dangQianBanBen = deviceOffLine.getDevice_guoke_version_number();
        tv_dangQianBanBen_liXian.setText(String.valueOf("当前版本：" + dangQianBanBen));

        xiaBanBenShengJi(dangQianBanBen);

        // 激活Service
        Intent intent = new Intent(this, DownloadFileService.class);
        startService(intent);

        // 注册Service广播的接收者
        receiver = new InnerReceiver();
        IntentFilter filter = new IntentFilter();
        //接收
        filter.addAction(ACTION_SET_DOWNLOAD_STATE);
        //接收取消下载广播
        filter.addAction(ACTION_SET_PAUSE_STATE);
        //接收下载进度广播
        filter.addAction(ACTION_UPDATE_PROGRESS);
        //接收下载成功广播
        filter.addAction(ACTION_DOWNLOAD_SUCCEED);
        //接收下载失败广播
        filter.addAction(ACTION_DOWNLOAD_ERROR);
        registerReceiver(receiver, filter);
/*
 * 发送询问是否在下载状态的广播
 */
        sendBroadcast(new Intent().setAction(ACTION_IS_DOWNLOAD));
    }

    private void initView() {
        btn_xiaZai_liXian.setOnClickListener(this);
        mProgressBar.setMax(100);

    }

    /**
     * 根据国科提供的版本号再查询网络看下个升级的版本是什么
     *
     * @param dangQianBanBen 当前国科设备的版本号
     */
    private void xiaBanBenShengJi(final String dangQianBanBen) {
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
                            if (str.split(":")[0].trim().equals(dangQianBanBen)) {
                                Log.d("TAG", "下个升级的坂本为：" + str.split(":")[1]);
                                if ("new".equals(str.split(":")[1])) {
                                    newBanBen = "当前已是最新版本，无需升级！";
                                } else {
                                    newBanBen = str.split(":")[1] + ".bin";
                                }
                                Message message = new Message();
                                message.what = NEXT_BANBENHAO_TEXT;
                                message.obj = newBanBen;
                                // 将Message对象发送出去
                                logTextHandler.sendMessage(message);
                                return;
                            } else {
                                //当网络上无此版本号时，提示无需升级
                                newBanBen = "当前已是最新版本，无需升级！";
                                Message message = new Message();
                                message.what = NEXT_BANBENHAO_TEXT;
                                message.obj = newBanBen;
                                // 将Message对象发送出去
                                logTextHandler.sendMessage(message);
                            }
                        }

                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_xiaZai_liXian:
                mProgressBar.setVisibility(View.VISIBLE);
                //downloadFile();
                //btn_xiaZai.setVisibility(View.INVISIBLE);
                /*
                 * 向Service发送点击了下载按钮的广播
                 */
                //TODO
                sendBroadcast(new Intent().setAction(ACTION_DOWNLOAD_OR_PAUSE).putExtra(EXTRA_DOWNLOAD_GUJIAN_NAME_TEXT, "MT1828/"+newBanBen));

                break;

            default:
                break;
        }
    }

    /**
     * Service广播的接收者
     */
    private class InnerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 获取Action
            String action = intent.getAction();
            // 判断Action
            //接收 更新进度的广播后的逻辑
            if (ACTION_UPDATE_PROGRESS.equals(action)) {
                // 更新播放进度
                int percent = intent.getIntExtra(EXTRA_PERCENT, 0);
                Log.d("TAG", percent + "");
                mProgressBar.setProgress(percent);
                //接收 固件下载成功的广播后的逻辑
            } else if (ACTION_DOWNLOAD_SUCCEED.equals(action)) {
                mProgressBar.setProgress(0);
                Toast.makeText(LiXianGuJianDownloadActivity.this, "下载成功", Toast.LENGTH_SHORT).show();

                /*// 取消注册广播接收者
                unregisterReceiver(receiver);*/
                // 停止Service
                Intent intentStop = new Intent(LiXianGuJianDownloadActivity.this, DownloadFileService.class);
                stopService(intentStop);
                btn_xiaZai_liXian.setText("开始下载");

                tv_log_liXian.setText("固件:" + newBanBen + "下载成功！请连接设备升级！");
                mProgressBar.setVisibility(View.INVISIBLE);
                btn_xiaZai_liXian.setVisibility(View.INVISIBLE);

                isDownload = false;
                //接收 正在下载状态的广播 后的逻辑
            } else if (ACTION_SET_DOWNLOAD_STATE.equals(action)) {
                btn_xiaZai_liXian.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                tv_log_liXian.setText("下载固件中");
                btn_xiaZai_liXian.setText("取消下载");
                isDownload = true;
                //接收 取消下载状态的广播 后的逻辑
            } else if (ACTION_SET_PAUSE_STATE.equals(action)) {
                btn_xiaZai_liXian.setText("重新下载");
                mProgressBar.setProgress(0);
                mProgressBar.setVisibility(View.INVISIBLE);
                isDownload = false;
                //接收 固件下载失败的广播 后的逻辑
            } else if (ACTION_DOWNLOAD_ERROR.equals(action)) {
                String error_text = intent.getStringExtra(EXTRA_DOWNLOAD_ERROR_TEXT);
                tv_log_liXian.setText(String.valueOf("固件下载失败！请重试！\n失败信息:" + error_text));
                isDownload = false;
            }
        }
    }

    @Override
    protected void onDestroy() {
        //取消广播接收者
        unregisterReceiver(receiver);

        if (!isDownload) {
            // 停止Service
            Intent intentStop = new Intent(LiXianGuJianDownloadActivity.this, DownloadFileService.class);
            stopService(intentStop);
        }
        super.onDestroy();
    }

}
