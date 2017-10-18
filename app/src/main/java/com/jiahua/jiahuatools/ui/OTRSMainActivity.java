package com.jiahua.jiahuatools.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.jiahua.jiahuatools.R;

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
                break;
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
}
