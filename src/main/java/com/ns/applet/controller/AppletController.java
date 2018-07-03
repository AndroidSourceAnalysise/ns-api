package com.ns.applet.controller;

import com.jfinal.plugin.redis.Redis;
import com.ns.applet.services.AppletService;
import com.ns.common.base.BaseController;
import com.ns.common.json.JsonResult;
import com.ns.common.utils.Util;

import java.util.HashMap;
import java.util.Map;


public class AppletController extends BaseController {
    /**
     * 登录
     */
    public void login() {
        final Map params = getRequestObject(getRequest(), HashMap.class);
        final String code = (String) params.get("code");
        final String oldSK = getHeader("sk");
        renderJson(JsonResult.newJsonResult(AppletService.getInstance().login(code, oldSK)));
    }

    /**
     * 保存会员信息
     */
    public void saveCustomerInfo() {
        final String sk = getRequest().getHeader("sk");
        final String body = Util.getRequestBytes(getRequest());
        renderJson(JsonResult.newJsonResult(AppletService.getInstance().saveCustomerInfo(sk, body)));
    }


}
