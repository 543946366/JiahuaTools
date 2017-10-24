package com.jiahua.jiahuatools.ui;

import android.app.AlertDialog;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.jiahua.jiahuatools.R;
import com.jiahua.jiahuatools.bean.DeviceOffLine;
import com.jiahua.jiahuatools.bean.GetSystemInfoJson;
import com.jiahua.jiahuatools.consts.Consts;
import com.jiahua.jiahuatools.service.DownloadFileService;
import com.jiahua.jiahuatools.utils.DigestAuthenticationUtil;
import com.jiahua.jiahuatools.utils.SPUtils;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Request;

public class ShengJiActivity extends AppCompatActivity implements View.OnClickListener, Consts {

    //设置当前是否在下载状态
    private boolean isDownload;
    //升级按钮
    @BindView(R.id.btn_shengJi)
    Button btn_shengJi;
    //下载固件按钮
    @BindView(R.id.btn_xiaZai_shengJi)
    Button btn_xiaZai;
    @BindView(R.id.tv_shengJi_log)
    TextView tv_log;
    //进度条
    @BindView(R.id.pb_chuanSong_shengJi)
    ProgressBar mProgressBar;
    //设备URL
    private String myBaseUrl;
    //设备序列号
    private String displaySerialNumber;
    private String displayFriendlyName;
    private String displayModelNumber;
    //记录错误文本
    private String error_text;
    //固件版本号
    private String guJianName;
    //当前版本号 -- 通过访问车机设备获取
    private String dangQianBanBenName;
    //handler的类型（传送或者下载）
    private int TYPE;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            shuaXin();
            //延时1秒post
            handler.postDelayed(this, 10000);
        }
    };

    /**
     * 广播接收者
     */
    private BroadcastReceiver receiver;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_shengJi:

                if (isDownload) {
                } else {
                    dianJiShengJi();
                }
                break;

            case R.id.btn_xiaZai_shengJi:
                mProgressBar.setVisibility(View.VISIBLE);
                //downloadFile();
                //btn_xiaZai.setVisibility(View.INVISIBLE);
                /*
                 * 向Service发送点击了下载按钮的广播
                 */
                //sendBroadcast(new Intent().setAction(ACTION_DOWNLOAD_OR_PAUSE).putExtra(EXTRA_DOWNLOAD_GUJIAN_NAME_TEXT, guJianName));
                sendBroadcast(new Intent().setAction(ACTION_DOWNLOAD_OR_PAUSE).putExtra(EXTRA_DOWNLOAD_GUJIAN_NAME_TEXT, "MT1828/"+guJianName));

                break;

            default:
                break;
        }
    }

    /**
     * 自定义的网络回调
     */
    private class MyStringCallback extends StringCallback {
        @Override
        public void onBefore(Request request, int id) {
            //setTitle("loading...");
            if (TYPE == CHUANG_SONG) {
                tv_log.setText("传送固件中，请勿进行其他操作！");
            } else if (TYPE == XIA_ZAI) {
                tv_log.setText("下载固件中，请勿进行其他操作！");
            }

        }

        @Override
        public void onAfter(int id) {
            //setTitle("Sample-okHttp");
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            mProgressBar.setVisibility(View.INVISIBLE);

            tv_log.setText(String.valueOf("失败！失败信息:" + e.getMessage()));
            btn_shengJi.setClickable(true);
        }

        @Override
        public void onResponse(String response, int id) {
            mProgressBar.setVisibility(View.INVISIBLE);
            if (TYPE == CHUANG_SONG) {
                tv_log.setText("传送固件成功！");
                //showMyDialog();
                chongQi();
            } else if (TYPE == XIA_ZAI) {
                tv_log.setText("下载固件:" + guJianName + "成功！正在传送固件");

            }
        }

        @Override
        public void inProgress(float progress, long total, int id) {
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.setProgress((int) (100 * progress));
        }
    }

    private Handler logTextHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<ShengJiActivity> myActivity;

        private MyHandler(ShengJiActivity myActivity) {
            this.myActivity = new WeakReference<>(myActivity);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ShengJiActivity activity = myActivity.get();
            switch (msg.what) {
                case OK_TEXT:
                    //json测试，版本号显示，现在格式暂时自定
                    /*if(activity.displayModelNumber.equals(MT_guoKe_model_number)){
                        activity.dangQianBanBenName = String.valueOf(msg.obj).split(":")[2].substring(0, ((String) msg.obj).split(":")[2].length() - 2);

                    }else if(activity.displayModelNumber.equals(MT_cheJi_model_number)){
                        activity.dangQianBanBenName = String.valueOf(msg.obj).split(":")[2].substring(1, ((String) msg.obj).split(":")[2].length() - 2);

                    }
                    activity.tvLog.setText(String.valueOf("当前版本为:" + activity.dangQianBanBenName));

                    DeviceOffLine deviceOffLine = new DeviceOffLine();
                    deviceOffLine.setDevice_friendly_name(activity.displayFriendlyName);
                    deviceOffLine.setDevice_model_number(activity.displayModelNumber);
                    deviceOffLine.setDevice_model_number_add_serial_number(activity.displayModelNumber+activity.displaySerialNumber);
                    deviceOffLine.setDevice_guoke_version_number(activity.dangQianBanBenName);
                    deviceOffLine.saveOrUpdate(DEVICE_MODEL_NUMBER_ADD_SERIAL_NUMBER+"=?",deviceOffLine.getDevice_model_number_add_serial_number());
*/
                    try {
                        Logger.e(msg.obj.toString());
                        DeviceOffLine deviceOffLine = new DeviceOffLine();
                        StringBuilder systemInfo = new StringBuilder();

                        int jsonSize = msg.obj.toString().indexOf("{");
                        String jsonContent ;
                        if(jsonSize == 0){
                            jsonContent = msg.obj.toString();
                        }else {
                            jsonContent = msg.obj.toString().substring(jsonSize);
                        }

                        GetSystemInfoJson getSystemInfoJson = new GsonBuilder().create().fromJson(jsonContent, GetSystemInfoJson.class);
                        if (!getSystemInfoJson.getSn().isEmpty()) {
                            systemInfo.append("序列号：").append(getSystemInfoJson.getSn()).append("\n");
                            deviceOffLine.setDevice_serial_number(getSystemInfoJson.getSn());
                        }
                        if (!getSystemInfoJson.getHwid().isEmpty()) {
                            systemInfo.append("硬件号：").append(getSystemInfoJson.getHwid()).append("\n");
                            deviceOffLine.setDevice_hwid(getSystemInfoJson.getHwid());
                        }

                        if (!getSystemInfoJson.getMac().isEmpty()) {
                            systemInfo.append("Mac地址：").append(getSystemInfoJson.getMac()).append("\n");
                            deviceOffLine.setDevice_Mac(getSystemInfoJson.getMac());
                        }
                        if (!getSystemInfoJson.getSwid().isEmpty()) {
                            systemInfo.append("软件号：").append(getSystemInfoJson.getSwid()).append("\n");
                            deviceOffLine.setDevice_swid(getSystemInfoJson.getSwid());
                            Logger.d(getSystemInfoJson.getSwid());

                            /*if(displayModelNumber.equals(MT_guoKe_model_number)){
                                guoKeRuanJianBaoShengJiChaXun(getSystemInfoJson.getSwid());
                            }else if(displayModelNumber.equals(MT_cheJi_model_number)){
                                cheJiRuanJianBaoChaXun();
                            }*/

                        }
                        if (getSystemInfoJson.getStm32_ver() != null && !getSystemInfoJson.getStm32_ver().isEmpty()) {
                            systemInfo.append("单片机版本：").append(getSystemInfoJson.getStm32_ver()).append("\n");
                            deviceOffLine.setDevice_cheji_stm32ver(getSystemInfoJson.getStm32_ver());
                        }

                        Logger.d(systemInfo.toString());
                        /*if(!systemInfo.toString().isEmpty()) {
                            activity.tvNewGuanLiDevInfo.setText(systemInfo.toString());
                        }*/

                        /*deviceOffLine.setDevice_friendly_name(displayFriendlyName);
                        deviceOffLine.setDevice_model_number(displayModelNumber);
                        deviceOffLine.setDevice_url(displayIP);
                        deviceOffLine.setDevice_model_number_add_serial_number(displayModelNumber+displaySerialNumber);
                        deviceOffLine.saveOrUpdate(DEVICE_MODEL_NUMBER_ADD_SERIAL_NUMBER+"=?",deviceOffLine.getDevice_model_number_add_serial_number());
*/
                        //这里处理升级逻辑
                        activity.xiaBanBenShengJi(activity.dangQianBanBenName);

                    } catch (Exception e) {
                        Logger.d(e.getMessage());
                    }
                    break;

                case NO_TEXT:
                    activity.tv_log.setText("失败");
                    break;

                //下个版本信息
                case NEXT_BANBENHAO_TEXT:

                    //如果之前已保存过，则删除
                    if (SPUtils.contains(activity, "升级版本号")) {
                        SPUtils.remove(activity, "升级版本号");
                    }

                    if ("当前已是最新版本，无需升级！".equals(String.valueOf(msg.obj))) {
                        activity.tv_log.setText("当前版本为:" + activity.dangQianBanBenName + "\n" + "当前已是最新版本，无需升级！");
                        SPUtils.put(activity, "升级版本号", "已是最新");
                    } else {
                        activity.tv_log.setText("当前版本为:" + activity.dangQianBanBenName + "\n" + "有新版本：" + activity.guJianName);
                        activity.btn_shengJi.setVisibility(View.VISIBLE);

                        //本地保存下个固件升级版本号

                        SPUtils.put(activity, "升级版本号", activity.guJianName);
                    }
                    break;
                default:
                    break;
            }
        }
    }
    /**
     * 根据国科提供的版本号再查询网络看下个升级的版本是什么
     *
     * @param dangQianBanBenName 当前国科版本号
     */
    private void xiaBanBenShengJi(final String dangQianBanBenName) {

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
                                if ("new".equals(str.split(":")[1])) {
                                    guJianName = "当前已是最新版本，无需升级！";
                                } else {
                                    guJianName = str.split(":")[1] + ".bin";
                                }
                                Message message = new Message();
                                message.what = NEXT_BANBENHAO_TEXT;
                                message.obj = guJianName;
                                // 将Message对象发送出去
                                logTextHandler.sendMessage(message);
                                return;
                            } else {
                                //当网络上无此版本号时，提示无需升级
                                guJianName = "当前已是最新版本，无需升级！";
                                Message message = new Message();
                                message.what = NEXT_BANBENHAO_TEXT;
                                message.obj = guJianName;
                                // 将Message对象发送出去
                                logTextHandler.sendMessage(message);
                            }
                        }

                    }
                });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheng_ji);

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
        //获取上一个界面传来的URL
        Intent intent = getIntent();
        myBaseUrl = intent.getStringExtra(INTENT_deviceURL);
        //获取型号，再区分是国科设备还是车机设备
        displayFriendlyName = intent.getStringExtra(INTENT_display_friendly_name);
        displaySerialNumber = intent.getStringExtra(INTENT_display_serial_number);
        displayModelNumber = intent.getStringExtra(INTENT_display_model_number);

        banBenXinxi();

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

    private void initView() {
        btn_shengJi.setOnClickListener(this);
        btn_xiaZai.setOnClickListener(this);
        mProgressBar.setMax(100);
    }

    private void banBenXinxi() {
        //创建一个Request  "http://192.168.63.9:8199/get_version"  正则表达式获取IP，再加端口号处理
        //myBaseUrl.replaceAll(reg, "$1");
        String myUrl = "http://" + myBaseUrl.replaceAll(REG, "$1") + ":8199/";
        String url = myUrl + "get_system_info";
        DigestAuthenticationUtil.startDigest(url,logTextHandler,"/get_system_info");
    }

    /**
     * 升级时刷新升级信息
     */
    private void shuaXin() {
        //String url = myBaseUrl + "upgrade";
        String url = myBaseUrl + "reboot";
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        stopHandleLoop();
                        tv_log.setText("升级失败！\n" + "失败信息：" + error_text + "\n" + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        //tvLog.setText(response);
                        error_text = response;
                        if (response.contains("固件导入成功")) {
                            stopHandleLoop();
                            tv_log.setText("固件导入成功！需立即重启以使用新系统！");
                            chongQi();
                        }

                    }
                });
    }

    /**
     * 向设备发送重启信息
     */
    private void chongQi() {
        final String url = myBaseUrl + "reboot";
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setCancelable(false);
        dialog.setTitle("成功");
        dialog.setMessage("固件升级成功！需要立即重启!请点击重启车机设备！");
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "重启", (d, i) -> {

            OkHttpUtils
                    .post()
                    .url(url)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            //tvLog.setText("重启失败！\n" + "失败信息：" + e.toString());
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            //tvLog.setText("重启设备成功！\n" + response);
                        }

                    });

            tv_log.setText("重启成功!");
        });
        dialog.show();
        btn_shengJi.setClickable(true);
    }

    /**
     * 点击升级的操作
     */
    private void dianJiShengJi() {
        if ("当前已是最新版本，无需升级！".equals(guJianName)) {
            tv_log.setText(guJianName);
        } else {

            File file = new File(Environment.getExternalStorageDirectory(), guJianName);
            //判断是否已下载固件
            if (!file.exists()) {
                tv_log.setText("还没下载最新固件，请下载固件后再进行升级！" + "\n" + "升级文件较大，建议在WIFI环境下再下载！");
                btn_xiaZai.setVisibility(View.VISIBLE);
            } else {
                chuanSongGuJian(file);
            }
        }
    }

    /**
     * 传送固件
     */
    private void chuanSongGuJian(File file) {
        btn_shengJi.setClickable(false);
        TYPE = CHUANG_SONG;
        /*Map<String, String> params = new HashMap<>();
        params.put("username", "目拓");*/

        Map<String, String> headers = new HashMap<>(16);
        headers.put("APP-Key", "APP-Secret222");
        headers.put("APP-Secret", "APP-Secret111");
        //headers.put("Cookie", JSESSIONID);

        String url = myBaseUrl + "upload";
        OkHttpUtils.post()
                .addFile("uploadFile", guJianName, file)
                .url(url)
                .params(null)
                .headers(headers)
                .build()
                .execute(new MyStringCallback());
    }

    /**
     * 停止循环
     */
    private void stopHandleLoop() {
        handler.removeCallbacks(runnable);
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
                Toast.makeText(ShengJiActivity.this, "下载成功", Toast.LENGTH_SHORT).show();

                /*// 取消注册广播接收者
                unregisterReceiver(receiver);*/
                // 停止Service
                Intent intentStop = new Intent(ShengJiActivity.this, DownloadFileService.class);
                stopService(intentStop);
                btn_xiaZai.setText("开始下载");

                tv_log.setText("固件:" + guJianName + "下载成功！可以开始升级车机固件！");
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
