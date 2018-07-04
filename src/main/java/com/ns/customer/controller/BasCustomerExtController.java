/**
 * project name: hdy_project
 * file name:BasCustomerExtController
 * package name:com.ns.customer.controller
 * date:2018-02-01 21:49
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.customer.controller;

import com.jfinal.plugin.redis.Redis;
import com.ns.common.base.BaseController;
import com.ns.common.constant.RedisKeyDetail;
import com.ns.common.json.JsonResult;
import com.ns.customer.service.BasCustPointsService;
import com.ns.customer.service.BasCustomerExtService;
import com.jfinal.core.Controller;

import java.util.HashMap;
import java.util.Map;

/**
 * description: //TODO <br>
 * date: 2018-02-01 21:49
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class BasCustomerExtController extends BaseController {
    static BasCustomerExtService extService = BasCustomerExtService.me;
    static BasCustPointsService pointsService = BasCustPointsService.me;

    public void getByConId() {
        final String conId = (String) Redis.use().hmget(getHeader("sk"),RedisKeyDetail.CON_ID).get(0);
        renderJson(JsonResult.newJsonResult(extService.getByConId(conId)));
    }

    public void getMyPoints() {
        final String conId = (String) Redis.use().hmget(getHeader("sk"),RedisKeyDetail.CON_ID).get(0);
        renderJson(JsonResult.newJsonResult(extService.getMyPoints(conId)));
    }

    public void pointsRanking() {
        renderJson(JsonResult.newJsonResult(extService.pointsRanking()));
    }

    public void pointsDeduction() {
        Map params = getRequestObject(getRequest(),HashMap.class);
        renderJson(JsonResult.newJsonResult(pointsService.pointsDeduction((Integer) params.get("point"))));
    }

    public void getPointTransList() {
        String conId = (String) Redis.use().hmget(getHeader("sk"), RedisKeyDetail.CON_ID).get(0);
        Map params = getRequestObject(getRequest(),HashMap.class);
        int pageNumber = (int) params.get("page_num");
        int pageSize = (int) params.get("page_size");
        renderJson(JsonResult.newJsonResult(pointsService.getPointTransList(conId, pageNumber, pageSize)));
    }

    public void myCustomer() {
        String conId = (String) Redis.use().hmget(getHeader("sk"), RedisKeyDetail.CON_ID).get(0);
        Map params = getRequestObject(getRequest(),HashMap.class);
        int pageNumber = (int) params.get("page_num");
        int pageSize = (int) params.get("page_size");
        renderJson(JsonResult.newJsonResult(extService.myCustomer(pageNumber, pageSize, conId)));
    }

    public void myBuyCustomer() {
        String conId = (String) Redis.use().hmget(getHeader("sk"), RedisKeyDetail.CON_ID).get(0);
        Map params = getRequestObject(getRequest(),HashMap.class);
        int pageNumber = (int) params.get("page_num");
        int pageSize = (int) params.get("page_size");
        renderJson(JsonResult.newJsonResult(extService.myBuyCustomer(pageNumber, pageSize, conId)));
    }

    public void myUnBuyCustomer() {
        String conId = (String) Redis.use().hmget(getHeader("sk"), RedisKeyDetail.CON_ID).get(0);
        Map params = getRequestObject(getRequest(),HashMap.class);
        int pageNumber = (int) params.get("page_num");
        int pageSize = (int) params.get("page_size");
        renderJson(JsonResult.newJsonResult(extService.myUnBuyCustomer(pageNumber, pageSize, conId)));
    }
}
