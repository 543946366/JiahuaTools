package com.jiahua.jiahuatools.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jiahua.jiahuatools.consts.Consts;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.request.RequestCall;
import com.zhy.http.okhttp.utils.L;

import java.io.File;

import okhttp3.Call;
import okhttp3.Request;


/**
 * Created by Administrator on 2017-02-11. 后台下载固件的服务
 */

public class DownloadFileService extends Service implements Consts {
    private int Progress;

    /**
     * 广播接收者
     */
    private BroadcastReceiver receiver;
    /**
     * 提交下载进度的线程的循环条件
     */
    private boolean isRunning;
    /**
     * 当前是否为下载状态
     */
    private boolean isDownload;
    /**
     * 更新进度的线程
     */
    private UpdateThread updateThread;
    /**
     * OKHttp网络申请回调
     */
    private RequestCall call;
    /**
     * 下载固件版本名
     */
    private String guJianName;
    private String upgradeText;
    /**
     * 下载固件的文件大小
     */
    private long wenJianDaXiao;
    @Override
    public void onCreate() {
        // 注册接收activity的广播接收者
        receiver = new InnerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_DOWNLOAD_OR_PAUSE);
        filter.addAction(ACTION_IS_DOWNLOAD);
        registerReceiver(receiver, filter);

    }


    /**
     * 广播接收者，接收activity后进行的操作
     */
    private class InnerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 获取广播中的Intent的Action
            String action = intent.getAction();
            // 判断Action
            if (ACTION_DOWNLOAD_OR_PAUSE.equals(action)) {
                // 判断当前是否在下载状态
                if(isDownload){

                    // 发送广播，告知Activity已经暂停
                    Intent intentStop = new Intent();
                    intentStop.setAction(ACTION_SET_PAUSE_STATE);
                    sendBroadcast(intentStop);
//取消下载的网络请求
                    call.cancel();
                    isDownload = false;
                }else {
                    //旧的 guJianName = intent.getStringExtra(EXTRA_DOWNLOAD_GUJIAN_NAME_TEXT);
                    String s = intent.getStringExtra(EXTRA_DOWNLOAD_GUJIAN_NAME_TEXT);
                    L.e("升级路径"+s.split("/").length);
                    upgradeText = s;
                    guJianName = s.split("/")[s.split("/").length-1];
                    L.e("固件名字"+guJianName);
                    downloadFile();
                }
            //回应activity询问当前是否在下载状态的请求
            }else if(ACTION_IS_DOWNLOAD.equals(action)){
                if (isDownload){
                    sendBroadcast(new Intent().setAction(ACTION_SET_DOWNLOAD_STATE));
                }else {
                    sendBroadcast(new Intent().setAction(ACTION_SET_PAUSE_STATE));
                }
            }
        }

    }

    /**
     * 开启更新进度的线程
     */
    private void startUpdateThread() {
        if (updateThread == null) {
            updateThread = new UpdateThread();
            isRunning = true;
            updateThread.start();
        }
    }

    /**
     * 停止更新进度的线程
     */
    private void stopUpdateThread() {
        if (updateThread != null) {
            isRunning = false;
            isDownload = false;
            updateThread = null;
        }
    }

    private class UpdateThread extends Thread {
        @Override
        public void run() {
            Intent intent = new Intent();
            while (isRunning) {

                //发送下载进度百分比的广播
                sendBroadcast(intent.setAction(ACTION_UPDATE_PROGRESS).putExtra(EXTRA_PERCENT, Progress));

                //让线程睡1秒
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 设置当前Service为非粘性的
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        // 停止更新进度的线程
        stopUpdateThread();
        // 取消注册广播接收者
        unregisterReceiver(receiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void downloadFile() {

        //String url = "http://120.27.94.20:10080/vendor/imotom/" + guJianName;
        String url = "http://120.27.94.20:10080/vendor/imotom/" + upgradeText;
        call = OkHttpUtils
                .get()
                .url(url)
                .build();
        call.execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), guJianName)
        {

            @Override
            public void onBefore(Request request, int id) {
                // tv_log.setText("开始下载固件");
                Log.d("MyService", "开始下载:"+guJianName);

                // 发送广播，告知Activity正在下载
                sendBroadcast(new Intent().setAction(ACTION_SET_DOWNLOAD_STATE));

                isDownload = true;
                // 开启更新进度的线程
                startUpdateThread();

                //初始化文件大小数值
                wenJianDaXiao = -1;
            }

            @Override
            public void inProgress(float progress, long total, int id) {
                // mProgressBar.setProgress((int) (100 * progress));
                Progress = (int) (100 * progress);

                //记录固件完整的大小
                if (wenJianDaXiao == -1) {
                    wenJianDaXiao = total;
                    Log.d("TAG", "下载的固件大小"+wenJianDaXiao);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                //tv_log.setText("固件下载失败！请重试！\n失败信息:" + e.getMessage());
                //btn_xiaZai.setVisibility(View.VISIBLE);
                Log.d("MyService", e.getMessage());

                // 停止更新进度的线程
                stopUpdateThread();
                sendBroadcast(new Intent().setAction(ACTION_DOWNLOAD_ERROR).putExtra(EXTRA_DOWNLOAD_ERROR_TEXT,e.getMessage()));

                //获取固件文件地址及判断下载的固件文件大小是否完整，不完整则删除刚下载的固件
                final File file = new File(Environment.getExternalStorageDirectory(), guJianName);
                long ll = file.length();
                if (ll != wenJianDaXiao) {
                    file.delete();
                    Log.d("TAG", "__" + ll);
                }
            }

            @Override
            public void onResponse(File file, int id) {
                        /*tv_log.setText("固件:"+guJianName+"下载成功！可以开始升级车机固件！");
                        mProgressBar.setVisibility(View.INVISIBLE);
                        btn_xiaZai.setVisibility(View.INVISIBLE);*/
                Log.d("MyService", "下载成功");


                // 停止更新进度的线程

                stopUpdateThread();
                sendBroadcast(new Intent().setAction(ACTION_DOWNLOAD_SUCCEED));
            }
        });
    }
}
