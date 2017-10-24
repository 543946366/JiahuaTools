package com.jiahua.jiahuatools.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dd.CircularProgressButton;
import com.jiahua.jiahuatools.MainActivity;
import com.jiahua.jiahuatools.R;
import com.jiahua.jiahuatools.adapter.TicketTaskAdapter;
import com.jiahua.jiahuatools.bean.TicketTask;
import com.jiahua.jiahuatools.bean.UserAndPassword;
import com.jiahua.jiahuatools.consts.Consts;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

import static com.jiahua.jiahuatools.consts.Consts.INTENT_display_model_number;

public class OTRSTicketTaskActivity extends AppCompatActivity {

    @BindView(R.id.rv_test)
    RecyclerView rvTest;
    @BindView(R.id.cpb_otrsTicketTask_ok)
    CircularProgressButton cpbOtrsTicketTaskOk;
    private String myTicketTaskSn;
    private int myTicketTaskPosition;

    private List<TicketTask> ticketTaskList = DataSupport.findAll(TicketTask.class);
    private TicketTaskAdapter ticketTaskAdapter = new TicketTaskAdapter(ticketTaskList);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otrsticket_task);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.md_yellow_500));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_yellow_24dp);

        init();
        cpbOtrsTicketTaskOk.setIndeterminateProgressMode(true);

    }

    private void init() {
        String TicketNumber = getIntent().getStringExtra("TicketNumber");

        //ticketTaskAdapter = new TicketTaskAdapter(ticketTaskList);

        ticketTaskAdapter.setOnItemClickListener((adapter, view, position) -> {
            if(ticketTaskList.get(position).isAccomplish()){
                Snackbar.make(view,"该项任务已完成，请完成其他任务！",Snackbar.LENGTH_LONG).show();
            }else {
                //TODO 给当前工单任务做标记
                TicketTask ticketTask = new TicketTask();
                ticketTask.setFlag(true);
                ticketTask.saveOrUpdate(Consts.DEVICE_SN + "=?", ticketTaskList.get(position).getSn());
                //ticketTaskList.get(position).setAccomplish(true);
                //ticketTaskAdapter.notifyDataSetChanged();
                myTicketTaskSn = ticketTaskList.get(position).getSn();
                myTicketTaskPosition = position;

                startActivity(new Intent(OTRSTicketTaskActivity.this, MainActivity.class).setFlags(Consts.OTRSTTA_to_MA)
                        .putExtra(INTENT_display_model_number, ticketTaskList.get(position).getModel_number()));
            }
        });
        rvTest.setLayoutManager(new LinearLayoutManager(OTRSTicketTaskActivity.this));
        rvTest.setAdapter(ticketTaskAdapter);
    }

    @Override
    protected void onResume() {
        cpbOtrsTicketTaskOk.setProgress(0);
        cpbOtrsTicketTaskOk.setClickable(true);

        super.onResume();
    }

    @Override
    protected void onRestart() {
        if(DataSupport.isExist(TicketTask.class)) {
            TicketTask ticketTask = DataSupport.where("sn=?", myTicketTaskSn).findFirst(TicketTask.class);
            if (ticketTask.isAccomplish()) {
                ticketTaskList.get(myTicketTaskPosition).setAccomplish(true);
                Logger.e(ticketTaskList.get(myTicketTaskPosition).isAccomplish() + "");
                ticketTaskAdapter.notifyDataSetChanged();
            }
        }
        super.onRestart();
    }

    @OnClick(R.id.cpb_otrsTicketTask_ok)
    public void onClick(View view){
        cpbOtrsTicketTaskOk.setProgress(50);
        cpbOtrsTicketTaskOk.setClickable(false);
        for(TicketTask ticketTask : ticketTaskList){
            if(!ticketTask.isAccomplish()){

                new MaterialDialog.Builder(this)
                        .title("警告")
                        .content("还有工单任务未完成！\n请继续完成再提交！")
                        .positiveText("确定")
                        .positiveColor(Color.RED)
                        .cancelable(false)
                        .onPositive((dialog, which) -> dialog.dismiss())
                        .show();
                cpbOtrsTicketTaskOk.setProgress(0);
                cpbOtrsTicketTaskOk.setClickable(true);
                return;
            }
        }
        cpbOtrsTicketTaskOk.setClickable(true);

        if(DataSupport.isExist(TicketTask.class)) {
            UserAndPassword userAndPassword = DataSupport.findFirst(UserAndPassword.class);
            String user = userAndPassword.getUser();
            String password = userAndPassword.getPassword();
            String TicketId = ticketTaskList.get(0).getTicket_id();
            String idUrl = "https://imotom01.dd.ezbox.cc:34443/otrs/nph-genericinterface.pl/Webservice/testWeb/Ticket/"
                    + TicketId + "/dd?UserLogin=" + user + "&Password=" + password;
            OkHttpUtils
                    .postString()
                    .content("{\"Ticket\":{\"Owner\":\"hao.xie@jiahua.win\"}}")
                    .url(idUrl).build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    Snackbar.make(view, "提交失败，请确认是否有网络再提交！", Snackbar.LENGTH_LONG)
                            .show();
                    cpbOtrsTicketTaskOk.setProgress(-1);
                    Logger.d(e.getMessage());
                }

                @Override
                public void onResponse(String response, int id) {
                    Logger.d(response);
                    DataSupport.deleteAll(TicketTask.class);

                    ticketTaskList.clear();
                    ticketTaskAdapter.notifyDataSetChanged();
       /* new MaterialDialog.Builder(this)
                .title("提交成功")
                .content("全部工单任务提交完成！\n请下载其他工单任务！")
                .positiveText("确定")
                .positiveColor(Color.RED)
                .cancelable(false)
                .onPositive((dialog, which) -> dialog.dismiss()).show();*/
                    Logger.e("完成");

                    Snackbar.make(view, "全部工单任务提交完成！\n请下载其他工单任务！", Snackbar.LENGTH_LONG)
                            .show();
                    cpbOtrsTicketTaskOk.setProgress(100);
                }
            });

            cpbOtrsTicketTaskOk.setProgress(0);
        }else {
            Snackbar.make(view, "当前无任务可提交！\n请下载其他工单任务！", Snackbar.LENGTH_LONG)
                    .show();
            cpbOtrsTicketTaskOk.setProgress(0);
        }
    }
}
