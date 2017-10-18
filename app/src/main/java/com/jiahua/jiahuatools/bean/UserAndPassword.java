package com.jiahua.jiahuatools.bean;
/*
 * Created by ZhiPeng Huang on 2017-10-10.
 */

import org.litepal.crud.DataSupport;

public class UserAndPassword extends DataSupport{
    private String user;
    private String password;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
