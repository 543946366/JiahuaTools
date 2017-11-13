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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.GsonBuilder;
import com.jiahua.jiahuatools.R;
import com.jiahua.jiahuatools.bean.DeviceOffLine;
import com.jiahua.jiahuatools.bean.GetSystemInfoJson;
import com.jiahua.jiahuatools.consts.Consts;
import com.jiahua.jiahuatools.service.DownloadFileService;
import com.jiahua.jiahuatools.utils.DigestAuthenticationUtil;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.utils.L;

import java.io.File;
import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Request;

public class CarDevUpgradeActivity extends AppCompatActivity implements Consts {
    @BindView(R.id.tv_car_dev_upgrade_log)
    TextView tvLog;
    //升级按钮
    @BindView(R.id.btn_car_dev_upgrade_shengJi)
    Button btn_shengJi;
    //下载固件按钮
    @BindView(R.id.btn_car_dev_upgrade_xiaZai)
    Button btn_xiaZai;
    //进度条
    @BindView(R.id.pb_car_dev_upgrade_chuanSong)
    ProgressBar mProgressBar;

    /**
     * 设置当前是否在下载状态
     */
    private boolean isDownload;

    private String myBaseUrl;
    /**
     * 设备序列号
     */
    private String displaySerialNumber;
    private String displayFriendlyName;
    private String displayModelNumber;
    private String upgradeText;
    /**
     * handler的类型（传送或者下载）
     */
    private int TYPE;
    /**
     * 固件版本号
     */
    private String guJianName = "rtthread.tar.gz";
    private String upload = "upload_stm32";

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            shuaXin();
            //延时5秒post
            handler.postDelayed(this, 5000);
        }
    };

    /**
     * 自定义的网络回调
     */
    private class MyStringCallback extends StringCallback {
        @Override
        public void onBefore(Request request, int id) {
            //setTitle("loading...");
            L.e("nihao "+request.toString());
            if (TYPE == CHUANG_SONG) {
                tvLog.setText("传送固件中，请勿进行其他操作！");
            } else if (TYPE == XIA_ZAI) {
                tvLog.setText("下载固件中，请勿进行其他操作！");
            }

        }

        @Override
        public void onAfter(int id) {
            //setTitle("Sample-okHttp");
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            L.e("error"+e.getMessage());
            mProgressBar.setVisibility(View.INVISIBLE);

            tvLog.setText(String.valueOf("失败！失败信息:" + e.getMessage()));
            btn_shengJi.setVisibility(View.VISIBLE);
            btn_shengJi.setClickable(true);
        }

        @Override
        public void onResponse(String response, int id) {
            L.e("response"+ response);
            mProgressBar.setVisibility(View.INVISIBLE);
            if (TYPE == CHUANG_SONG) {
                tvLog.setText("传送固件成功！");
                showMyDialog();
                //chongQi();
            } else if (TYPE == XIA_ZAI) {
                tvLog.setText(String.valueOf("下载固件:" + guJianName + "成功！正在传送固件"));

            }
        }

        @Override
        public void inProgress(float progress, long total, int id) {
            L.e("~~~"+progress + ".........");
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.setProgress((int) (100 * progress));
        }
    }

    private GetSystemInfoJson getSystemInfoJson;
    private String swid;
    private String Stm32_ver;

    private Handler getSystemInfoHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<CarDevUpgradeActivity> myActivity;

        private MyHandler(CarDevUpgradeActivity myActivity) {
            this.myActivity = new WeakReference<>(myActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CarDevUpgradeActivity activity = myActivity.get();
            switch (msg.what) {
                case OK_TEXT:
                    // 在这里可以进行UI操作
                    try {
                        activity.getSystemInfoJson = new GsonBuilder().create().fromJson(String.valueOf(msg.obj.toString().substring(4)), GetSystemInfoJson.class);
                        if (!activity.getSystemInfoJson.getSwid().isEmpty()) {
                            activity.swid = activity.getSystemInfoJson.getSwid();
                        }
                        if (!activity.getSystemInfoJson.getStm32_ver().isEmpty()) {
                            activity.Stm32_ver = activity.getSystemInfoJson.getStm32_ver();
                        }

                        activity.tvLog.setText(String.valueOf("单片机当前版本为:" + activity.Stm32_ver));

                        DeviceOffLine deviceOffLine = new DeviceOffLine();
                        deviceOffLine.setDevice_friendly_name(activity.displayFriendlyName);
                        deviceOffLine.setDevice_model_number(activity.displayModelNumber);
                        deviceOffLine.setDevice_model_number_add_serial_number(activity.displayModelNumber+activity.displaySerialNumber);
                        deviceOffLine.setDevice_cheji_Swid(activity.getSystemInfoJson.getSwid());
                        deviceOffLine.setDevice_cheji_stm32ver(activity.getSystemInfoJson.getStm32_ver());
                        deviceOffLine.saveOrUpdate(DEVICE_MODEL_NUMBER_ADD_SERIAL_NUMBER+"=?",deviceOffLine.getDevice_model_number_add_serial_number());

                    } catch (Exception e) {
                        Logger.d(e.getMessage());
                    }
                    break;

                case NO_TEXT:
                    Toast.makeText(activity, "获取设备信息失败。", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * 根据国科提供的版本号再查询网络看下个升级的版本是什么
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

    private void showMyDialog() {
        new MaterialDialog.Builder(CarDevUpgradeActivity.this)
                .title("固件上传成功")
                .content("固件上传成功，是否马上进行设备升级？")
                .positiveText("升级")
                .onPositive(
                        (dialog,which) ->
                            OkHttpUtils.post()
                                    .url(myBaseUrl + "upgrade")
                                    .build()
                                    .execute(new StringCallback() {
                                        @Override
                                        public void onError(Call call, Exception e, int id) {
                                            L.e(e.getMessage());
                                            tvLog.setText(String.valueOf("升级失败!失败信息："+e.toString()));
                                            btn_shengJi.setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void onResponse(String response, int id) {
                                            L.e(response);
                                            tvLog.setText("固件升级中...\n");
                                            startHandleLoop();

                                        }
                                    })

                )
                .negativeText("取消")
                .onNegative(
                        (dialog,which) -> dialog.dismiss()
                )
                .show();
    }

    /**
     * 升级时刷新升级信息
     */
    private void shuaXin() {
        String url = myBaseUrl + "upgrade";
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        stopHandleLoop();
                        tvLog.setText(String.valueOf("升级失败！\n" + "失败信息：\n" + e.getMessage()));
                        btn_shengJi.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        L.e(response);
                        if (response.contains("固件导入成功")) {
                            stopHandleLoop();
                            tvLog.setText("固件导入成功！需立即重启以使用新系统！");
                            chongQi();
                        }

                    }
                });
    }

    private void chongQi() {
        final String url = myBaseUrl + "reboot";
        new MaterialDialog.Builder(this)
                .title("升级成功")
                .content("固件升级成功！需要立即重启!请点击重启车机设备！")
                .positiveText("重启")
                .onPositive((dialog,which) ->
                        OkHttpUtils
                                .post()
                                .url(url)
                                .build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        tvLog.setText(String.valueOf("重启失败！\n" + "失败信息：" + e.getMessage()));
                                        btn_shengJi.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onResponse(String response, int id) {
                                        tvLog.setText(String.valueOf("重启设备成功！\n" + response));
                                    }

                                })

                )
                .show();
        btn_shengJi.setClickable(true);
    }

    /**
     * 广播接收者
     */
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_dev_upgrade);

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
        //获取上一个界面传来的URL
        Intent intent = getIntent();
        myBaseUrl = intent.getStringExtra(INTENT_deviceURL);
        tvLog.setText("单片机升级");

        btn_shengJi.setOnClickListener((v) -> {
            if (isDownload) {
                Toast.makeText(this, "固件正在下载中，请下载完成后再点击升级！", Toast.LENGTH_SHORT).show();
            } else {
                dianJiShengJi();
            }
        });
        //获取型号，再区分是国科设备还是车机设备
        displayFriendlyName = intent.getStringExtra(INTENT_display_friendly_name);
        displaySerialNumber = intent.getStringExtra(INTENT_display_serial_number);
        displayModelNumber = intent.getStringExtra(INTENT_display_model_number);

        xiaBanBenShengJi();
        BanBenXinXi();

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

    private void BanBenXinXi() {
        String myUrl = "http://" + myBaseUrl.replaceAll(REG, "$1") + ":8199/";
        String url = myUrl + "get_system_info";
        DigestAuthenticationUtil.startDigest(url, getSystemInfoHandler, "/get_system_info");
    }

    /**
     * 点击升级的操作
     */
    private void dianJiShengJi() {

        /*switch (hint) {
            case 0:
                guJianName = "rtthread.tar.gz";
                upload = "upload_stm32";
                break;

            case 1:
                guJianName = "system.tar.gz";
                upload = "upload_t3_system";
                break;

            case 2:
                guJianName = "boot.img";
                upload = "upload_t3_boot";
                break;
        }*/
        File file = new File(Environment.getExternalStorageDirectory(), guJianName);
        //判断是否已下载固件
        if (!file.exists()) {
            tvLog.setText(String.valueOf("还没下载最新固件，请下载固件后再进行升级！" + "\n" + "升级文件较大，建议在WIFI环境下再下载！"));
        } else {
            L.e("+++++++++++");
            chuanSongGuJian(file);
            btn_shengJi.setVisibility(View.INVISIBLE);
        }

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
                tvLog.setText(String.valueOf("单片机当前版本为:" + Stm32_ver));
                guJianName = "rtthread.tar.gz";
                upload = "upload_stm32";
                btn_shengJi.setVisibility(View.VISIBLE);
                btn_xiaZai.setVisibility(View.VISIBLE);
                return true;

            case R.id.action_t3_system:
                Toast.makeText(this, "系统文件升级", Toast.LENGTH_SHORT).show();
                //hint = 1;
                tvLog.setText(String.valueOf("系统文件当前版本为:" + swid));
                guJianName = "system.tar.gz";
                upload = "upload_t3_system";
                btn_shengJi.setVisibility(View.VISIBLE);
                btn_xiaZai.setVisibility(View.VISIBLE);
                return true;

            case R.id.action_t3_boot:
                Toast.makeText(this, "内核升级", Toast.LENGTH_SHORT).show();
                //hint = 2;
                tvLog.setText("内核升级升级");
                guJianName = "boot.img";
                upload = "upload_t3_boot";
                btn_shengJi.setVisibility(View.VISIBLE);
                btn_xiaZai.setVisibility(View.VISIBLE);
                return true;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    /**
     * 传送固件
     */
    private void chuanSongGuJian(File file) {
        //getAuthorizationHaderValue(file);
        TYPE = CHUANG_SONG;

        String url = myBaseUrl + upload;
        OkHttpUtils.post()
                .addFile("firmware", guJianName, file)
                .url(url)
                .build()
                .execute(new MyStringCallback());
    }

    @OnClick(R.id.btn_car_dev_upgrade_xiaZai)
    public void download(){
        new MaterialDialog.Builder(CarDevUpgradeActivity.this)
                .title("固件信息")
                .content("固件包版本日期为：" + upgradeText + "\n" + "是否马上下载？")
                .positiveText("下载")
                .onPositive(
                        (dialog,which) ->{
                            mProgressBar.setVisibility(View.VISIBLE);
                            sendBroadcast(new Intent().setAction(ACTION_DOWNLOAD_OR_PAUSE).putExtra(EXTRA_DOWNLOAD_GUJIAN_NAME_TEXT, "MT1845/"+upgradeText+"/"+guJianName));

                        }
                )
                .negativeText("取消")
                .onNegative(
                        (dialog,which) -> dialog.dismiss()
                ).show();
    }
    /*private void getAuthorizationHaderValue(File file) {
        //网页请求URL
        final String url = myBaseUrl + "upload_t3_boot";
        L.e("========="+url);
        //final String url = "http://192.168.1.183:8080/Duang/protect/protect.jsp";
        new Thread(() -> {

            try {
                OkHttpUtils.post()//
                        .addFile("firmware","boot.img",file)
                        .url(url)//
                        .build()//
                        .execute(new MyStringCallback());
                //Log.e("TAG","___________________"+responseq.code());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }*/

    /**
     *广播的接收者
     *
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
                Toast.makeText(CarDevUpgradeActivity.this, "下载成功", Toast.LENGTH_SHORT).show();

                /*// 取消注册广播接收者
                unregisterReceiver(receiver);*/
                // 停止Service
                /*Intent intentStop = new Intent(CarDevUpgradeActivity.this, DownloadFileService.class);
                stopService(intentStop);*/
                btn_xiaZai.setText("开始下载");

                tvLog.setText(String.valueOf("固件:" + guJianName + "下载成功！可以开始升级车机固件！"));
                mProgressBar.setVisibility(View.INVISIBLE);
                btn_xiaZai.setVisibility(View.INVISIBLE);

                isDownload = false;
                //接收 正在下载状态的广播 后的逻辑
            } else if (ACTION_SET_DOWNLOAD_STATE.equals(action)) {
                btn_xiaZai.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                tvLog.setText("下载固件中");
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
                tvLog.setText(String.valueOf("固件下载失败！请重试！\n失败信息:" + error_text));

                isDownload = false;
            }
        }
    }

    /**
     * 开始循环
     */
    private void startHandleLoop() {
        //延时5秒post
        handler.postDelayed(runnable, 5000);
    }

    /**
     * 停止循环
     */
    private void stopHandleLoop() {
        handler.removeCallbacks(runnable);
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
