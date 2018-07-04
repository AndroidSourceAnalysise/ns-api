/**
 * project name: hdy_project
 * file name:BasCustomerController
 * package name:com.ns.customer.controller
 * date:2018-02-01 17:24
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.customer.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ns.applet.services.AppletService;
import com.ns.common.MyConfig;
import com.ns.common.base.BaseController;
import com.ns.common.constant.RedisKeyDetail;
import com.ns.common.exception.CustException;
import com.ns.common.json.JsonResult;
import com.ns.common.model.BasCustomer;
import com.ns.common.task.Task;
import com.ns.common.task.TaskQueuePlugin;
import com.ns.common.utils.GUIDUtil;
import com.ns.common.utils.Util;
import com.ns.customer.service.BasCustomerService;
import com.ns.customer.task.CreateCustomerTask;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.jfinal.weixin.sdk.api.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * description: //TODO <br>
 * date: 2018-02-01 17:24
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class BasCustomerController extends BaseController {
    static BasCustomerService service = new BasCustomerService();

    public void checkReferee() {
        renderJson(JsonResult.newJsonResult(BasCustomerService.me.checkReferee((String) Redis.use().hmget(getHeader("sk"), RedisKeyDetail.CON_ID).get(0))));
    }

    public void updateReferee() {
        Map params = getRequestObject(getRequest(), HashMap.class);
        final String refereeNo = (String) params.get("referee_no");
        if (StrKit.isBlank(refereeNo)) {
            throw new CustException("推荐人会员不能为空!");
        }
        renderJson(JsonResult.newJsonResult(BasCustomerService.me.updateReferee((String) Redis.use().hmget(getHeader("sk"), RedisKeyDetail.CON_ID).get(0), refereeNo)));
    }


    public void getRefereeBaseInfo() {
        Map params = getRequestObject(getRequest(), HashMap.class);
        final String refereeNo = (String) params.get("referee_no");
        if (StrKit.isBlank(refereeNo)) {
            throw new CustException("推荐人会员不能为空!");
        }
        renderJson(JsonResult.newJsonResult(BasCustomerService.me.getRefereeBaseInfo(refereeNo)));
    }

    public void autoReferee() {
        renderJson(JsonResult.newJsonResult(BasCustomerService.me.autoReferee((String) Redis.use().hmget(getHeader("sk"), RedisKeyDetail.CON_ID).get(0))));
    }

    public void test() {
        //oCMeO0qn2r-PEEuLFsNdWoebMq_g
        ApiConfigKit.putApiConfig(MyConfig.getApiConfig());
        ApiResult apiResult = UserApi.getUserInfo("oCMeO0qn2r-PEEuLFsNdWoebMq_g");
        renderText(apiResult.getJson());
    }

    public void getById() {
        renderJson(JsonResult.newJsonResult(service.getCustomerByIdNotNull((String) Redis.use().hmget(getHeader("sk"), RedisKeyDetail.CON_ID).get(0))));
    }

    public void getCustomerBaseInfo() {
        renderJson(JsonResult.newJsonResult(service.getBaseCustomerByIdNotNull((String) Redis.use().hmget(getHeader("sk"), RedisKeyDetail.CON_ID).get(0))));
    }


    public void getByConNo() {
        renderJson(JsonResult.newJsonResult(service.getCustomerByConNoNotNull(getPara("conNo"))));
    }

    public void update() {
        JSONObject jsonObject = Util.getRequestObject(getRequest(), JSONObject.class);
        BasCustomer customer = JSON.toJavaObject(jsonObject, BasCustomer.class);
        renderJson(JsonResult.newJsonResult(service.update(customer, jsonObject.getString("CODE"))));
    }


    /**
     * 检测会员是否绑定手机号码了
     */
    public void checkMobileBind() {
        renderJson(JsonResult.newJsonResult(service.isMobileBind((String) Redis.use().hmget(getHeader("sk"),RedisKeyDetail.CON_ID).get(0))));
    }

    /**
     * 绑定手机号码
     */
    public void bindMobile() {
        Map params = getRequestObject(getRequest(), HashMap.class);
        renderJson(JsonResult.newJsonResult(service.bindMobile((String) params.get("mobile"), (String) params.get("code"), (String) Redis.use().hmget(getHeader("sk"),RedisKeyDetail.CON_ID).get(0))));
    }

    public void getByOpenId() {
        renderJson(JsonResult.newJsonResult(service.getCustomerByOpenIdNotNull(getPara("openId"))));
    }

    public void dg() {
        renderJson(JsonResult.newJsonResult(service.a(getPara("id"))));
    }

    public void getConId() {
        String redirect_uri = PropKit.get("serverUrl") + "api/customer/authorize";
        String _wxShareUrl = getRequest().getHeader("Referer");
        redirect(SnsAccessTokenApi.getAuthorizeURL(PropKit.get("appId"), redirect_uri, _wxShareUrl, true));
    }

    public void authorize() {
        String code = getPara("code");
        String state = getPara("state");
        SnsAccessToken snsAccessToken = SnsAccessTokenApi.getSnsAccessToken(PropKit.get("appId"), PropKit.get("appSecret"), code);
        String openid = snsAccessToken.getOpenid();
        setCustomerCookie(getResponse(), service.getCustomerByOpenIdNotNull(openid).getID());
        redirect(state + "?conId=" + service.getCustomerByOpenIdNotNull(openid).getID());
    }

    public static String setCustomerCookie(HttpServletResponse response, String userId) {
        String uuid = generateSessionId(userId);
        Cookie cookie = new Cookie("a", uuid);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 30);
        response.addCookie(cookie);
        return uuid;
    }

    public static String generateSessionId(String userId) {
        String uuid = UUID.randomUUID().toString();
        Cache cache = Redis.use();
        cache.set(uuid, userId);
        cache.expire(uuid, 1800);//设置过期时间为30分钟
        return uuid;
    }


    public static void main(String[] args) {
        System.out.println(GUIDUtil.getGUID());
        System.out.println(GUIDUtil.getGUID());
        System.out.println(GUIDUtil.getGUID());
        System.out.println(GUIDUtil.getGUID());
        System.out.println(GUIDUtil.getGUID());
        System.out.println(GUIDUtil.getGUID());
    }


}
