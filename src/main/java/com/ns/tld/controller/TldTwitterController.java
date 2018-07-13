/**
 * project name: ns-api
 * file name:TldOrdersController
 * package name:com.ns.tld.controller
 * date:2018-03-10 14:46
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.tld.controller;

import java.util.HashMap;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.kit.HttpKit;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.redis.Redis;
import com.ns.common.Ytapi;
import com.ns.common.base.BaseController;
import com.ns.common.constant.RedisKeyDetail;
import com.ns.common.json.JsonResult;
import com.ns.tld.service.TldOrdersService;
import com.ns.tld.service.TldTwitterService;

/**
 * description: //TODO <br>
 * date: 2018-03-10 14:46
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class TldTwitterController extends BaseController {
    static TldTwitterService tldTwitterService = TldTwitterService.me;


    public void getCurrMonthSacleList(){
    	int pageNumber = 1;
        int pageSize = 20;
        renderJson(JsonResult.newJsonResult(tldTwitterService.getMonthScaleList(pageNumber, pageSize)));

    }
    public static void main(String[] args) {
        Map map = new HashMap();
        map.put("id", "02988D03C7834E3997B70981E9A937D6");
        map.put("amount", "134");
        map.put("checkResult", "1");
        map.put("checkRemark", "中文");
        System.out.println(HttpKit.post("http://127.0.0.1:8110/asset/recharge/rechargeCheck", map, ""));
    }
}
