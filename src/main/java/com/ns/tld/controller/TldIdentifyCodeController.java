/**
 * project name: ns-api
 * file name:TldIdentifyCodeController
 * package name:com.ns.tld.controller
 * date:2018-04-14 16:50
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.tld.controller;

import com.cedarsoftware.util.io.JsonObject;
import com.jfinal.plugin.redis.Redis;
import com.ns.common.base.BaseController;
import com.ns.common.constant.RedisKeyDetail;
import com.ns.common.json.JsonResult;
import com.ns.tld.service.TldIdentifyCodeService;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.util.HashMap;
import java.util.Map;

/**
 * description: //TODO <br>
 * date: 2018-04-14 16:50
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class TldIdentifyCodeController extends BaseController {
    static TldIdentifyCodeService identifyCodeService = TldIdentifyCodeService.me;

    @Before(Tx.class)
    public void getCode() throws Exception {
        Map params = getRequestObject(getRequest(), HashMap.class);
        String mobile = (String) params.get("mobile");
        String conId = (String) Redis.use().hmget(getHeader("sk"), RedisKeyDetail.CON_ID).get(0);
        Integer type = (Integer) params.get("type");
        renderJson(JsonResult.newJsonResult(identifyCodeService.getCode(mobile, conId, type)));
    }
}
