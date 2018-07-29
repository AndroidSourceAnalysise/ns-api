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

import com.jfinal.plugin.redis.Redis;
import com.ns.common.base.BaseController;
import com.ns.common.constant.RedisKeyDetail;
import com.ns.common.exception.CustException;
import com.ns.common.json.JsonResult;
import com.ns.common.model.BasCustomer;
import com.ns.customer.service.BasCustomerService;
import com.ns.tld.service.TldRebateFlowService;
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
    static TldRebateFlowService rebateflowService = TldRebateFlowService.me;


    public void getCurrMonthSacleList(){
    	int pageNumber = 1;
        int pageSize = 20;
        renderJson(JsonResult.newJsonResult(tldTwitterService.getMonthScaleList(pageNumber, pageSize)));

    }
    public void getTwitterFlowList() throws Exception {
        final String conId = (String) Redis.use().hmget(getHeader("sk"), RedisKeyDetail.CON_ID).get(0);
        BasCustomer basCustomer = BasCustomerService.me.getCustomerById(conId);
        if (basCustomer == null) {
            throw new CustException("会员信息异常!");
        }
        final String conNo = basCustomer.getConNo();
        
    	Map params = getRequestObject(getRequest(), HashMap.class);
        int pageNumber = (int) params.get("pageNumber");
        int pageSize = (int) params.get("pageSize");
        Integer status = (Integer) params.get("status");
        String type = (String)params.get("type");
        renderJson(JsonResult.newJsonResult(rebateflowService.getTwitterFlowList(conNo, status, type, pageNumber, pageSize)));
    }
   

}
