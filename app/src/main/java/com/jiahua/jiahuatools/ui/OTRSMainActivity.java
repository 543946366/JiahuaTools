package com.jiahua.jiahuatools.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiahua.jiahuatools.R;
import com.jiahua.jiahuatools.bean.TicketTask;
import com.jiahua.jiahuatools.bean.UserAndPassword;
import com.jiahua.jiahuatools.utils.ActivityCollector;

import org.litepal.crud.DataSupport;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OTRSMainActivity extends AppCompatActivity {

    @BindView(R.id.cv_otrsMain_lock)
    CardView cvOtrsMainLock;
    @BindView(R.id.cv_otrsMain_current_task)
    CardView cvOtrsMainNewTicket;
    @BindView(R.id.cv_otrsMain_history)
    CardView cvOtrsMainHistory;
    @BindView(R.id.cv_otrsMain_out)
    CardView cvOtrsMainOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otrsmain);

        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitleTextColor(getResources().getColor(R.color.md_yellow_500));
        toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.md_yellow_500));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_yellow_24dp);

    }

    @OnClick({R.id.cv_otrsMain_lock,R.id.cv_otrsMain_history,R.id.cv_otrsMain_current_task,R.id.cv_otrsMain_out})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.cv_otrsMain_lock:
                startActivity(new Intent(OTRSMainActivity.this,OTRSLockActivity.class));
                break;

            case R.id.cv_otrsMain_history:
                break;

            case R.id.cv_otrsMain_current_task:
                startActivity(new Intent(OTRSMainActivity.this,OTRSTicketTaskActivity.class));
                break;

            case R.id.cv_otrsMain_out:
                if(DataSupport.isExist(TicketTask.class)){
                    Snackbar.make(view,"当前账号还有未完成任务，请提交当前任务后再退出！",Snackbar.LENGTH_LONG)
                            .show();
                }else {
                    new MaterialDialog.Builder(this)
                            .title("退出登录")
                            .content("退出登录将会取消自动登录，是否确认退出？")
                            .positiveText("确定退出")
                            .onPositive((dialog, which) -> {
                                DataSupport.deleteAll(UserAndPassword.class);
                                startActivity(new Intent(OTRSMainActivity.this, AccountLoginActivity.class));
                                finish();
                                ActivityCollector.finishAll();
                            })
                            .negativeText("取消")
                            .onNegative((dialog, which) -> dialog.dismiss())
                            .positiveColor(Color.RED)
                            .negativeColor(Color.RED)
                            .show();
                }
                break;

            default:
                break;
        }
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
