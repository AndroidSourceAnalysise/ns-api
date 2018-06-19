package com.ns.applet.controller;

import com.ns.applet.services.AppletService;
import com.ns.common.base.BaseController;
import com.ns.common.json.JsonResult;
import com.ns.common.utils.Util;


public class AppletController extends BaseController {
    /**
     * 登录
     */
    public void login() {
        final String code = getPara("code");
        final String oldSK = getRequest().getHeader("sk");
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
