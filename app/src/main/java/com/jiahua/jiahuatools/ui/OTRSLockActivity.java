package com.jiahua.jiahuatools.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.GsonBuilder;
import com.jiahua.jiahuatools.R;
import com.jiahua.jiahuatools.adapter.LockTicketAdapter;
import com.jiahua.jiahuatools.bean.GetSystemInfoJson;
import com.jiahua.jiahuatools.bean.GetTicketIDJson;
import com.jiahua.jiahuatools.bean.TicketInfoJson;
import com.jiahua.jiahuatools.bean.TicketTask;
import com.jiahua.jiahuatools.bean.UserAndPassword;
import com.jiahua.jiahuatools.consts.Consts;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class OTRSLockActivity extends AppCompatActivity implements Consts {

    @BindView(R.id.rv_otrsLock_Ticket)
    RecyclerView rvOtrsLockTicket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otrslock);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.md_yellow_500));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_yellow_24dp);

        init(toolbar);
    }

    private void init(Toolbar toolbar) {
        UserAndPassword userAndPassword = DataSupport.findFirst(UserAndPassword.class);
        String user = userAndPassword.getUser();
        String password = userAndPassword.getPassword();
        String OwnerID = null;
        if("gzjh100001@jiahua.win".equals(user)){
            OwnerID = "13";
        }else if("gzjh100002@jiahua.win".equals(user)){
            OwnerID = "14";
        }else if("zhipeng.huang@jiahua.win".equals(user)){
            OwnerID = "3";
        }
        String searchUrl = "https://imotom01.dd.ezbox.cc:34443/otrs/nph-genericinterface.pl/Webservice/testWeb/Ticket?UserLogin=" +
                user + "&Password=" + password + "&Locks=lock&States=open&OwnerIDs="+OwnerID;

        OkHttpUtils.get().url(searchUrl).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Logger.e(e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                Logger.e(response);
                if("{}".equals(response)){
                    Snackbar.make(toolbar, "当前无锁定工单任务", Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }
                String idUrl;
                List<TicketInfoJson.TicketBean> ticketBeanList = new ArrayList<>();
                GetTicketIDJson getTicketIDJson = new GsonBuilder().create().fromJson(response, GetTicketIDJson.class);
                for (String ID : getTicketIDJson.getTicketID()) {
                    idUrl = "https://imotom01.dd.ezbox.cc:34443/otrs/nph-genericinterface.pl/Webservice/testWeb/Ticket/"
                            + ID + "?UserLogin=" + user + "&Password=" + password;
                    OkHttpUtils.get().url(idUrl).build().execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {

                        }

                        @Override
                        public void onResponse(String response, int id) {
                            TicketInfoJson ticketInfoJson = new GsonBuilder().create().fromJson(response, TicketInfoJson.class);
                            ticketBeanList.add(ticketInfoJson.getTicket().get(0));

                            if (ticketBeanList.size() == getTicketIDJson.getTicketID().size()) {
                                LockTicketAdapter lockTicketAdapter = new LockTicketAdapter(ticketBeanList);
                                lockTicketAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
                                lockTicketAdapter.setOnItemClickListener((adapter1, view, position) ->
                                        {
                                            Logger.e(ticketBeanList.get(position).getTicketNumber());
                                            startActivity(new Intent(OTRSLockActivity.this, TicketWebActivity.class)
                                                    .putExtra("TicketID", ticketBeanList.get(position).getTicketID()));
                                        }
                                );
                                lockTicketAdapter.setOnItemChildClickListener((adapter, view, position) ->
                                {
                                    /*startActivity(new Intent(OTRSLockActivity.this,OTRSTicketTaskActivity.class)
                                            .putExtra("TicketNumber",ticketBeanList.get(position).getTicketNumber()));*/
                                    saveTicketTask(view,ticketBeanList.get(position).getTicketNumber(),
                                            ticketBeanList.get(position).getTicketID());

                                });

                                rvOtrsLockTicket.setLayoutManager(new LinearLayoutManager(OTRSLockActivity.this));
                                rvOtrsLockTicket.setAdapter(lockTicketAdapter);
                            }
                        }
                    });
                }
            }
        });
    }

    private void saveTicketTask(View view, String ticketNumber, String ticketID) {
        DataSupport.deleteAll(TicketTask.class);

        /*String jsontest = "{\"ticketTask\":[\n" +
                "{\"model_number\":\"MT1767\",\"swid\":\"1.0.0.1\",\"hwid\":\"MT1767\",\"sn\":\"MT1767000001\",\"mac\":\"3c:33:00:00:00:01\"},\n" +
                "{\"model_number\":\"MT1767\",\"swid\":\"1.0.0.1\",\"hwid\":\"MT1767V12\",\"sn\":\"MT1767000002\",\"mac\":\"00:20:18:00:00:01\"},\n" +
                "{\"model_number\":\"MT1767\",\"swid\":\"1.0.0.1\",\"hwid\":\"MT1767V12\",\"sn\":\"MT1767000003\",\"mac\":\"00:20:18:00:00:01\"},\n" +
                "{\"model_number\":\"MT1767\",\"swid\":\"1.0.0.1\",\"hwid\":\"MT1767V12\",\"sn\":\"MT1767000004\",\"mac\":\"00:20:18:00:00:01\"}\n" +
                "]}";*/
        String url = URL_OTRS_TICKET_TASK_BASE_URL + ticketNumber + ".txt";
        OkHttpUtils.get().url(url).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Snackbar.make(view,"没有成功获取到工单任务，请重试！",Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(String response, int id) {
                GetSystemInfoJson getSystemInfoJson = new GsonBuilder().create().fromJson(response, GetSystemInfoJson.class);
                List<GetSystemInfoJson> getSystemInfoJsonList = getSystemInfoJson.getTicketTask();
                for (GetSystemInfoJson getSystemInfoJson1 : getSystemInfoJsonList) {
                    TicketTask ticketTask = new TicketTask();
                    ticketTask.setModel_number(getSystemInfoJson1.getModel_number());
                    ticketTask.setSwid(getSystemInfoJson1.getSwid());
                    ticketTask.setHwid(getSystemInfoJson1.getHwid());
                    ticketTask.setSn(getSystemInfoJson1.getSn());
                    ticketTask.setMac(getSystemInfoJson1.getMac());
                    ticketTask.setAccomplish(false);
                    ticketTask.setTicket_id(ticketID);
                    ticketTask.saveOrUpdate(DEVICE_SN + "=?", getSystemInfoJson1.getSn());
                    Logger.e(getSystemInfoJson1.getSn());
                }

                Snackbar.make(view, "刷新在线设备列表完成", Snackbar.LENGTH_LONG)
                        .setAction("跳转到当前任务", v -> startActivity(new Intent(OTRSLockActivity.this,OTRSTicketTaskActivity.class)))
                        .show();
            }
        });

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