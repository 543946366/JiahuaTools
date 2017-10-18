package com.jiahua.jiahuatools.bean;
/*
 * Created by ZhiPeng Huang on 2017-10-14.
 */

import org.litepal.crud.DataSupport;

public class TicketTask extends DataSupport {
    private String ticket_id;
    private String model_number;
    private String swid;
    private String hwid;
    private String sn;
    private String mac;
    //是否已经完成
    private boolean accomplish;

    public String getTicket_id() {
        return ticket_id;
    }

    public void setTicket_id(String ticket_number) {
        this.ticket_id = ticket_number;
    }

    public String getModel_number() {
        return model_number;
    }

    public void setModel_number(String model_number) {
        this.model_number = model_number;
    }

    public String getSwid() {
        return swid;
    }

    public void setSwid(String swid) {
        this.swid = swid;
    }

    public String getHwid() {
        return hwid;
    }

    public void setHwid(String hwid) {
        this.hwid = hwid;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public boolean isAccomplish() {
        return accomplish;
    }

    public void setAccomplish(boolean accomplish) {
        this.accomplish = accomplish;
    }
}
