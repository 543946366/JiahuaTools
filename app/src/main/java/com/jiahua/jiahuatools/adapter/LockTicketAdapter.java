package com.jiahua.jiahuatools.adapter;
/*
 * Created by ZhiPeng Huang on 2017-10-11.
 */

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiahua.jiahuatools.R;
import com.jiahua.jiahuatools.bean.TicketInfoJson;

import java.util.List;

public class LockTicketAdapter extends BaseQuickAdapter<TicketInfoJson.TicketBean, BaseViewHolder> {

    public LockTicketAdapter(@Nullable List<TicketInfoJson.TicketBean> data) {
        super(R.layout.item_lock_ticket,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TicketInfoJson.TicketBean item) {
        helper.setText(R.id.tv_item_lock_ticket_number,"工单编号：\n" + item.getTicketNumber())
                .setText(R.id.tv_item_lock_ticket_title,"工单标题：\n" + item.getTitle())
                .addOnClickListener(R.id.cv_item_lock_ticket_task);

    }

}
