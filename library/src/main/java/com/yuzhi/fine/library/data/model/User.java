package com.yuzhi.fine.library.data.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yuzhi.fine.library.data.constant.Constants;
import com.yuzhi.fine.library.http.model.Header;
import com.yuzhi.fine.library.utils.SPUtils;
import com.yuzhi.fine.library.utils.StringUtils;

import java.io.Serializable;

public class User implements Serializable {

    public String token = "";   // 登录Token
    public String imToken = ""; // 融云 IM Token
    public long userId = 0;    // 业务用户ID，也是融云IM用户ID
    public int status = 1;     // 用户资料完善状态，1：未完善，2：已完善；
    public boolean isLogin = false;

    private User() {}

    private static class Holder {
        private static final User INSTANT = new User();
    }

    public static User getInstance() {
        return Holder.INSTANT;
    }

    public User init() {
        String response = SPUtils.getInstance().getString(Constants.SharedPreferences.USER, "");
        if(!StringUtils.isEmpty(response)) {
            JSONObject object = JSON.parseObject(response);
            this.token = object.getString("token");
            this.imToken = object.getString("imToken");
            this.userId = object.getLongValue("userId");
            this.status = object.getIntValue("status");
            this.isLogin = true;
            Header.token = this.token;
        }
        return getInstance();
    }

    public void updateStatus(int status) {
        this.status = status;
        String response = SPUtils.getInstance().getString(Constants.SharedPreferences.USER, "");
        if(!StringUtils.isEmpty(response)) {
            JSONObject object = JSON.parseObject(response);
            object.put("status", status);
            SPUtils.getInstance().put(Constants.SharedPreferences.USER, object.toJSONString());
        }
    }
}
