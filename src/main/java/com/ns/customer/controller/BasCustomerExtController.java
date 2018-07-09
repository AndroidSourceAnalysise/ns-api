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
        final String conId = (String) Redis.use().hmget(getHeader("sk"), RedisKeyDetail.CON_ID).get(0);
        renderJson(JsonResult.newJsonResult(extService.getByConId(conId)));
    }

    public void getMyPoints() {
        final String conId = (String) Redis.use().hmget(getHeader("sk"), RedisKeyDetail.CON_ID).get(0);
        renderJson(JsonResult.newJsonResult(extService.getMyPoints(conId)));
    }

    public void pointsRanking() {
        renderJson(JsonResult.newJsonResult(extService.pointsRanking()));
    }

    public void promotionRanking() {
        Map params = getRequestObject(getRequest(), HashMap.class);
        final int start = (int) params.get("page_num");
        final int end = (int) params.get("page_size");
        final String y = (String) params.get("year");
        final String m = (String) params.get("month");
        renderJson(JsonResult.newJsonResult(extService.promotionRanking(start,end,y,m)));
    }


    public void pointsDeduction() {
        Map params = getRequestObject(getRequest(), HashMap.class);
        renderJson(JsonResult.newJsonResult(pointsService.pointsDeduction((Integer) params.get("point"))));
    }

    public void getPointTransList() {
        String conId = (String) Redis.use().hmget(getHeader("sk"), RedisKeyDetail.CON_ID).get(0);
        Map params = getRequestObject(getRequest(), HashMap.class);
        int pageNumber = (int) params.get("page_num");
        int pageSize = (int) params.get("page_size");
        renderJson(JsonResult.newJsonResult(pointsService.getPointTransList(conId, pageNumber, pageSize)));
    }

    public void myCustomer() {
        String conId = (String) Redis.use().hmget(getHeader("sk"), RedisKeyDetail.CON_ID).get(0);
        final Map params = getRequestObject(getRequest(), HashMap.class);
        final int pageNumber = (int) params.get("page_num");
        final int pageSize = (int) params.get("page_size");
        final int type = (int) params.get("type");
        if (type == 0) {
            renderJson(JsonResult.newJsonResult(extService.myCustomer(pageNumber, pageSize, conId)));
        } else if (type == 1) {
            renderJson(JsonResult.newJsonResult(extService.myBuyCustomer(pageNumber, pageSize, conId)));
        } else if (type == 2) {
            renderJson(JsonResult.newJsonResult(extService.myUnBuyCustomer(pageNumber, pageSize, conId)));
        }
    }

}
