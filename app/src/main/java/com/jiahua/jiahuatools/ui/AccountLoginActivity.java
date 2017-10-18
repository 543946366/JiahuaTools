package com.jiahua.jiahuatools.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.dd.CircularProgressButton;
import com.jiahua.jiahuatools.MainActivity;
import com.jiahua.jiahuatools.R;
import com.jiahua.jiahuatools.bean.UserAndPassword;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AccountLoginActivity extends AppCompatActivity {

    @BindView(R.id.et_accountLogin_username)
    EditText etAccountLoginUsername;
    @BindView(R.id.et_accountLogin_password)
    EditText etAccountLoginPassword;
    @BindView(R.id.btn_accountLogin_dengLu)
    CircularProgressButton btnAccountLoginDengLu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //占满整个界面，让SplashActivity真正占满全屏幕
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_account_login);
        ButterKnife.bind(this);

        //沉浸式设置
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        initView();
    }

    private void initView() {
        btnAccountLoginDengLu.setIndeterminateProgressMode(true);

    }

    @Override
    protected void onResume() {
        btnAccountLoginDengLu.setProgress(0);
        btnAccountLoginDengLu.setClickable(true);

        super.onResume();
    }

    @OnClick(R.id.btn_accountLogin_dengLu)
    public void onclick(View view) {
        btnAccountLoginDengLu.setProgress(50);
        btnAccountLoginDengLu.setClickable(false);
        String user = etAccountLoginUsername.getText().toString();
        String password = etAccountLoginPassword.getText().toString();
        UserAndPassword userAndPassword = new UserAndPassword();
        userAndPassword.setUser(user);
        userAndPassword.setPassword(password);
        userAndPassword.saveOrUpdate("id=?","1");
        //userAndPassword.saveOrUpdateAsync("id=1");


        new Handler().postDelayed(() -> {
            btnAccountLoginDengLu.setProgress(100);
            startActivity(new Intent(AccountLoginActivity.this, MainActivity.class));
            finish();
        },1000);
        //new Handler().postDelayed(() -> {btnAccountLoginDengLu.setProgress(-1);btnAccountLoginDengLu.setClickable(true);},6000);
    }
}
