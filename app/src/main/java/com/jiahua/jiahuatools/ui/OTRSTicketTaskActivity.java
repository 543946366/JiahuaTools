package com.jiahua.jiahuatools.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiahua.jiahuatools.upnp.MainActivity;
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

import static com.jiahua.jiahuatools.consts.Consts.INTENT_OTRSTTA_to_MA;
import static com.jiahua.jiahuatools.consts.Consts.INTENT_display_model_number;

public class OTRSTicketTaskActivity extends AppCompatActivity {

    @BindView(R.id.rv_test)
    RecyclerView rvTest;
    @BindView(R.id.cd_otrsTicketTask_tiJiao)
    CardView cdOtrsTicketTaskTiJiao;
    /**
     * 工单任务附件中需要完成的设备序列号
     */
    private String myTicketTaskSn = "Empty";
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

    }

    private void init() {
        //ticketTaskAdapter = new TicketTaskAdapter(ticketTaskList);
        ticketTaskAdapter.setOnItemClickListener((adapter, view, position) -> {
            //判断当前任务是否已完成，已完成则无法点击
            if (ticketTaskList.get(position).isAccomplish()) {
                Snackbar.make(view, "该项任务已完成，请完成其他任务！", Snackbar.LENGTH_LONG).show();
            } else {
                //如果当前任务列表中已有标记，则把所有标记回复成默认值
                if (DataSupport.isExist(TicketTask.class, "Flag=?", "1")) {
                    TicketTask ticketTask = new TicketTask();
                    ticketTask.setToDefault("Flag");
                    ticketTask.updateAll();
                }
                //给当前工单任务做标记把Flag设置成true
                TicketTask ticketTask = new TicketTask();
                ticketTask.setFlag(true);
                ticketTask.saveOrUpdate(Consts.DEVICE_SN + "=?", ticketTaskList.get(position).getSn());
                myTicketTaskSn = ticketTaskList.get(position).getSn();
                myTicketTaskPosition = position;

                //跳转到主界面进行设备扫描及接下来的设备信息录入操作
                startActivity(new Intent(OTRSTicketTaskActivity.this, MainActivity.class).putExtra(INTENT_OTRSTTA_to_MA, INTENT_OTRSTTA_to_MA)
                        .putExtra(INTENT_display_model_number, ticketTaskList.get(position).getModel_number()));
            }
        });
        rvTest.setLayoutManager(new LinearLayoutManager(OTRSTicketTaskActivity.this));
        rvTest.setAdapter(ticketTaskAdapter);
    }

    @Override
    protected void onResume() {
        cdOtrsTicketTaskTiJiao.setClickable(true);

        super.onResume();
    }

    @Override
    protected void onRestart() {
        //重新启动该界面时，如果是通过点击任务去到主界面再返回的话，则刷新列表
        if (!("Empty".equals(myTicketTaskSn) || myTicketTaskSn.isEmpty())) {
            if (DataSupport.isExist(TicketTask.class)) {
                TicketTask ticketTask = DataSupport.where("sn=?", myTicketTaskSn).findFirst(TicketTask.class);
                //根据工单是否已经完成，刷新任务列表
                if (ticketTask.isAccomplish()) {
                    ticketTaskList.get(myTicketTaskPosition).setAccomplish(true);
                    Logger.e(ticketTaskList.get(myTicketTaskPosition).isAccomplish() + "");
                    ticketTaskAdapter.notifyDataSetChanged();
                }
            }
        }
        super.onRestart();
    }

    @OnClick(R.id.cd_otrsTicketTask_tiJiao)
    public void onClick(View view) {
        cdOtrsTicketTaskTiJiao.setClickable(false);
        //遍历所有工单任务，如果还有未完成的任务则不允许提交
        for (TicketTask ticketTask : ticketTaskList) {
            if (!ticketTask.isAccomplish()) {

                new MaterialDialog.Builder(this)
                        .title("警告")
                        .content("还有工单任务未完成！\n请继续完成再提交！")
                        .positiveText("确定")
                        .positiveColor(Color.RED)
                        .cancelable(false)
                        .onPositive((dialog, which) -> dialog.dismiss())
                        .show();
                cdOtrsTicketTaskTiJiao.setClickable(true);
                return;
            }
        }
        cdOtrsTicketTaskTiJiao.setClickable(true);

        //判断本地是否保存了工单任务列表
        if (DataSupport.isExist(TicketTask.class)) {
            //获取本地保存的账号及密码
            UserAndPassword userAndPassword = DataSupport.findFirst(UserAndPassword.class);
            String user = userAndPassword.getUser();
            String password = userAndPassword.getPassword();
            String TicketId = ticketTaskList.get(0).getTicket_id();
            StringBuilder ss = new StringBuilder();
            //获取这次要提交的全部任务设备的序列号，进行拼接，方便提交到OTRS的信件中记录任务历史
            for(TicketTask ticketTask:ticketTaskList){
                ss.append("\\n").append(ticketTask.getSn());
            }
            Logger.e(ss.toString());
            String idUrl = "https://imotom01.dd.ezbox.cc:34443/otrs/nph-genericinterface.pl/Webservice/testWeb/Ticket/"
                    + TicketId + "/dd?UserLogin=" + user + "&Password=" + password;
            //更新工单信息，包括转移所有者，信件信息
            OkHttpUtils
                    .postString()
                    .content("{\"Ticket\":{\"Owner\":\"hao.xie@jiahua.win\"}," +
                            "\"Article\":{\"Subject\":\"已完成设备信息录入\",\"Body\":\"已完成的设备序列号" + ss.toString() +"\",\"ContentType\":\"text/plain; charset=utf8\"}}")
                    .url(idUrl).build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    Snackbar.make(view, "提交失败，请确认是否有网络再提交！", Snackbar.LENGTH_LONG)
                            .show();
                    Logger.d(e.getMessage());
                }

                @Override
                public void onResponse(String response, int id) {
                    Logger.d(response);
                    //提交成功后，清空本地保存的工单任务列表
                    DataSupport.deleteAll(TicketTask.class);

                    ticketTaskList.clear();
                    ticketTaskAdapter.notifyDataSetChanged();
                    Logger.e("完成");

                    Snackbar.make(view, "全部工单任务提交完成！\n请下载其他工单任务！", Snackbar.LENGTH_LONG)
                            .show();
                }
            });

        } else {
            Snackbar.make(view, "当前无任务可提交！\n请下载其他工单任务！", Snackbar.LENGTH_LONG)
                    .show();
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
