package com.jiahua.jiahuatools.adapter;
/*
 * Created by ZhiPeng Huang on 2017-10-13.
 */

import android.graphics.Color;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiahua.jiahuatools.R;
import com.jiahua.jiahuatools.bean.TicketTask;

import java.util.List;


public class TicketTaskAdapter extends BaseQuickAdapter<TicketTask,BaseViewHolder> {
    public TicketTaskAdapter(@Nullable List<TicketTask> data) {
        super(R.layout.item_ticket_task,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TicketTask item) {
        helper.setText(R.id.tv_item_ticket_task_type,item.getModel_number())
                .setText(R.id.tv_item_ticket_task_swid,item.getSwid())
                .setText(R.id.tv_item_ticket_task_hwid,item.getHwid())
                .setText(R.id.tv_item_ticket_task_sn,item.getSn());
        if(item.isAccomplish()){
            helper.setImageResource(R.id.iv_item_ticket_task,android.R.drawable.checkbox_on_background)
                    .setBackgroundColor(R.id.ll_item_ticket_task, Color.GREEN);
        }else{
            helper.setImageResource(R.id.iv_item_ticket_task,android.R.drawable.checkbox_off_background)
            .setBackgroundColor(R.id.ll_item_ticket_task, Color.GRAY);
        }

    }

}
