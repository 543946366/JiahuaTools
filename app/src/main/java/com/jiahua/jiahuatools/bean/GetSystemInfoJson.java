package com.jiahua.jiahuatools.bean;
/*
 * Created by ZhiPeng Huang on 2017-06-06.
 */

import java.util.List;

public class GetSystemInfoJson {

    /**
     * sn : MT1845000003
     * hwid : MT1845V11
     * swid : 1.0.0.1
     * mac : 3c:33:00:00:00:03
     * stm32_ver : 2.0
     * model_name : 4G-Navigation
     * can_controler : xxx
     *
     */
    private String sn;
    private String hwid;
    private String swid;
    private String mac;
    private String stm32_ver;
    private String model_name;
    private String can_controler;
    //工单任务列表使用的设备类型
    private String model_number;
    private List<GetSystemInfoJson> ticketTask;

    public String getModel_number() {
        return model_number;
    }

    public void setModel_number(String model_number) {
        this.model_number = model_number;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getHwid() {
        return hwid;
    }

    public void setHwid(String hwid) {
        this.hwid = hwid;
    }

    public String getSwid() {
        return swid;
    }

    public void setSwid(String swid) {
        this.swid = swid;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getStm32_ver() {
        return stm32_ver;
    }

    public void setStm32_ver(String stm32_ver) {
        this.stm32_ver = stm32_ver;
    }

    public String getModel_name() {
        return model_name;
    }

    public void setModel_name(String model_name) {
        this.model_name = model_name;
    }

    public String getCan_controler() {
        return can_controler;
    }

    public void setCan_controler(String can_controler) {
        this.can_controler = can_controler;
    }

    public List<GetSystemInfoJson> getTicketTask() {
        return ticketTask;
    }

    public void setTicketTask(List<GetSystemInfoJson> ticketTask) {
        this.ticketTask = ticketTask;
    }
}
