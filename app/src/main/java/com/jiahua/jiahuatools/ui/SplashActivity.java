package com.jiahua.jiahuatools.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.jiahua.jiahuatools.upnp.MainActivity;
import com.jiahua.jiahuatools.R;
import com.jiahua.jiahuatools.bean.UserAndPassword;
import com.orhanobut.logger.Logger;

import org.litepal.crud.DataSupport;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //占满整个界面，让SplashActivity真正占满全屏幕
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        //SplashActivityPermissionsDispatcher.jumpNavWithCheck(SplashActivity.this);
        //SplashActivityPermissionsDispatcher.needPerWithCheck(this);
        SplashActivityPermissionsDispatcher.needPerWithCheck(this);

    }

    private void jump() {
        new Handler().postDelayed(() -> {
            if (DataSupport.isExist(UserAndPassword.class)) {
                //判断本地是否有保存密码，如果本地保存好密码，则说明已经有登录过的账号，直接跳转到主界面
                startActivity(new Intent(this, MainActivity.class));
            } else {
                //如果没有本地保存密码，则跳转到登录界面
                startActivity(new Intent(SplashActivity.this, AccountLoginActivity.class));
                Logger.e("当前没有账号记录");
            }
            finish();
        }, 500);
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void needPer() {
        jump();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SplashActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    /*@NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void needPer() {
        jump();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SplashActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }*/
}
