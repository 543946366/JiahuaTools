package com.jiahua.jiahuatools.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.jiahua.jiahuatools.MainActivity;
import com.jiahua.jiahuatools.R;
import com.jiahua.jiahuatools.bean.UserAndPassword;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * @author Administrator
 */
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

        String url = "https://imotom01.dd.ezbox.cc:34443/otrs/nph-genericinterface.pl/Webservice/testWeb/Ticket?UserLogin=" + user + "&Password=" + password;
        OkHttpUtils.get().url(url).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Toast.makeText(AccountLoginActivity.this, "登录失败，请重试！", Toast.LENGTH_SHORT).show();
                btnAccountLoginDengLu.setProgress(0);
                btnAccountLoginDengLu.setClickable(true);
            }

            @Override
            public void onResponse(String response, int id) {
                if (response.contains("TicketSearch.AuthFail")) {
                    btnAccountLoginDengLu.setProgress(0);
                    btnAccountLoginDengLu.setClickable(true);
                    Toast.makeText(AccountLoginActivity.this, "登录失败，账号或者密码错误！", Toast.LENGTH_SHORT).show();
                } else {
                    UserAndPassword userAndPassword = new UserAndPassword();
                    userAndPassword.setUser(user);
                    userAndPassword.setPassword(password);
                    userAndPassword.saveOrUpdate("id=?", "1");

                    btnAccountLoginDengLu.setProgress(100);
                    startActivity(new Intent(AccountLoginActivity.this, MainActivity.class));
                    finish();
                }

            }
        });
    }
}
