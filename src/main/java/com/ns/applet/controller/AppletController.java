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

    /**
     * 检测会员是否绑定手机号码了
     */
    public void checkMobileBind() {
        renderJson(JsonResult.newJsonResult(AppletService.getInstance().isMobileBind((String) Redis.use().hmget(getHeader("sk")).get(0))));
    }

    /**
     * 绑定手机号码
     */
    public void bindMobile() {
        Map params = getRequestObject(getRequest(), HashMap.class);
        renderJson(JsonResult.newJsonResult(AppletService.getInstance().bindMobilePhone((String) params.get("mobile"), (String) params.get("code"), (String) Redis.use().hmget(getHeader("sk")).get(0))));
    }
}
