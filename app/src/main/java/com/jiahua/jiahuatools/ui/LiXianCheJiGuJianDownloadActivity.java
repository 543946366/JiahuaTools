package com.jiahua.jiahuatools.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiahua.jiahuatools.R;
import com.jiahua.jiahuatools.consts.Consts;
import com.jiahua.jiahuatools.service.DownloadFileService;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;


public class LiXianCheJiGuJianDownloadActivity extends AppCompatActivity implements Consts{

    @BindView(R.id.tv_LXCJD_log)
    TextView tv_log;
    @BindView(R.id.pb_LXCJD_chuanSong)
    ProgressBar mProgressBar;
    @BindView(R.id.btn_LXCJD_xiaZai)
    Button btn_xiaZai;
    //固件版本号
    private String guJianName = "rtthread.tar.gz";
    private String upgradeText;
    private boolean isDownload;
    /**
     * 广播接收者
     */
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_li_xian_che_ji_gu_jian_download);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        init();
    }

    private void init() {
        tv_log.setText(String.valueOf("单片机固件下载"));
        xiaBanBenShengJi();
        // 激活Service
        startService(new Intent(this, DownloadFileService.class));

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

        /**
         * 根据国科提供的版本号再查询网络看下个升级的版本是什么
         *
         *
         */
        private void xiaBanBenShengJi() {

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
                            upgradeText = response;

                        }
                    });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //设置右上菜单栏
        getMenuInflater().inflate(R.menu.activity_car_dev_upgrade, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_stm32:
                Toast.makeText(this, "单片机升级", Toast.LENGTH_SHORT).show();
                // = 0;
                tv_log.setText(String.valueOf("单片机固件下载"));
                guJianName = "rtthread.tar.gz";
                btn_xiaZai.setVisibility(View.VISIBLE);
                return true;

            case R.id.action_t3_system:
                Toast.makeText(this, "系统文件升级", Toast.LENGTH_SHORT).show();
                //hint = 1;
                tv_log.setText(String.valueOf("系统文件固件下载" ));
                guJianName = "system.tar.gz";
                btn_xiaZai.setVisibility(View.VISIBLE);
                return true;

            case R.id.action_t3_boot:
                Toast.makeText(this, "内核固件下载", Toast.LENGTH_SHORT).show();
                //hint = 2;
                tv_log.setText("内核固件下载");
                guJianName = "boot.img";
                btn_xiaZai.setVisibility(View.VISIBLE);
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @OnClick(R.id.btn_LXCJD_xiaZai)
    public void download(){
        new MaterialDialog.Builder(this)
                .title("固件信息")
                .content("固件包版本日期为：" + upgradeText + "\n" + "是否马上下载？")
                .positiveText("下载")
                .onPositive(
                        (dialog,which) ->{
                            //TODo
                            mProgressBar.setVisibility(View.VISIBLE);
                            sendBroadcast(new Intent().setAction(ACTION_DOWNLOAD_OR_PAUSE).putExtra(EXTRA_DOWNLOAD_GUJIAN_NAME_TEXT, "MT1845/"+upgradeText+"/"+guJianName));

                        }
                )
                .negativeText("取消")
                .onNegative(
                        (dialog,which) -> dialog.dismiss()
                ).show();
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
                Toast.makeText(LiXianCheJiGuJianDownloadActivity.this, "下载成功", Toast.LENGTH_SHORT).show();

                /*// 取消注册广播接收者
                unregisterReceiver(receiver);*/
                // 停止Service
                /*Intent intentStop = new Intent(LiXianCheJiGuJianDownloadActivity.this, DownloadFileService.class);
                stopService(intentStop);*/
                btn_xiaZai.setText("开始下载");

                tv_log.setText("固件:" + guJianName + "下载成功！");
                mProgressBar.setVisibility(View.INVISIBLE);
                btn_xiaZai.setVisibility(View.INVISIBLE);

                isDownload = false;
                //接收 正在下载状态的广播 后的逻辑
            } else if (ACTION_SET_DOWNLOAD_STATE.equals(action)) {
                btn_xiaZai.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                tv_log.setText("下载固件中");
                btn_xiaZai.setText("取消下载");

                isDownload = true;
                //接收 取消下载状态的广播 后的逻辑
            } else if (ACTION_SET_PAUSE_STATE.equals(action)) {
                btn_xiaZai.setText("开始下载");
                mProgressBar.setProgress(0);
                mProgressBar.setVisibility(View.INVISIBLE);
                btn_xiaZai.setVisibility(View.INVISIBLE);

                isDownload = false;
                //接收 固件下载失败的广播 后的逻辑
            } else if (ACTION_DOWNLOAD_ERROR.equals(action)) {
                String error_text = intent.getStringExtra(EXTRA_DOWNLOAD_ERROR_TEXT);
                tv_log.setText(String.valueOf("固件下载失败！请重试！\n失败信息:" + error_text));
                isDownload = false;
            }
        }
    }

    @Override
    protected void onDestroy() {
        //取消广播接收者
        unregisterReceiver(receiver);
        if(!isDownload){
            // 停止Service
            Intent intentStop = new Intent(this, DownloadFileService.class);
            stopService(intentStop);
        }
        super.onDestroy();
    }
}
