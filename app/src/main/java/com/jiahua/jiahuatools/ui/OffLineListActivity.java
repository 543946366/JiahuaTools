package com.jiahua.jiahuatools.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.jiahua.jiahuatools.R;
import com.jiahua.jiahuatools.consts.Consts;
import com.jiahua.jiahuatools.utils.FileUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class OffLineListActivity extends AppCompatActivity implements Consts{

    @BindView(R.id.btn_offLineList_downloadGuoKeAPP)
    Button btn_guoKe;
    @BindView(R.id.btn_offLineList_downloadDaoHang)
    Button btn_daoHang;
    @BindView(R.id.btn_offLineList_downloadApp1845)
    Button btn_app1845;

    private String display_model_number_add_serial_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_off_line_list);

        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        display_model_number_add_serial_number = getIntent().getStringExtra(INTENT_display_model_number_add_serial_number);

        if(getIntent().getFlags() == 1845){
            btn_daoHang.setVisibility(View.VISIBLE);
            btn_app1845.setVisibility(View.VISIBLE);

        }else  if (getIntent().getFlags() == 1828){
            btn_guoKe.setVisibility(View.VISIBLE);
        }
    }


    @OnClick({R.id.btn_offLineList_checkDevVersion,R.id.btn_offLineList_downloadGuoKeAPP,R.id.btn_offLineList_downloadDaoHang,R.id.btn_offLineList_downloadApp1845})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_offLineList_checkDevVersion:
                //查看设备版本
                if(getIntent().getFlags() == 1845){
                    startActivity(new Intent(OffLineListActivity.this,OffLineCheckDevVersionActivity.class)
                            .putExtra(INTENT_display_model_number_add_serial_number,
                                    getIntent().getStringExtra(INTENT_display_model_number_add_serial_number))
                            .setFlags(1845));
                }else  if (getIntent().getFlags() == 1828){
                    startActivity(new Intent(OffLineListActivity.this,OffLineCheckDevVersionActivity.class)
                            .putExtra(INTENT_display_model_number_add_serial_number,
                                    getIntent().getStringExtra(INTENT_display_model_number_add_serial_number))
                    .setFlags(1828));
                }


                break;

            case R.id.btn_offLineList_downloadGuoKeAPP:
                //startActivity(new Intent(OffLineListActivity.this,DownAPPActivity.class));
                break;

            case R.id.btn_offLineList_downloadDaoHang:
                //startActivity(new Intent(OffLineListActivity.this,DownloadNavigationAPPActivity.class));
                break;

            case R.id.btn_offLineList_downloadApp1845:
                FileUtils.writeTxtToFile(display_model_number_add_serial_number, Environment.getExternalStorageDirectory().getPath() + "/imotom/", "DeviceOffLineNumber.txt");

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
