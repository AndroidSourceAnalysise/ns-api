/**
 * project name: ns-api
 * file name:TldOrdersController
 * package name:com.ns.tld.controller
 * date:2018-03-10 14:46
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.tld.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.redis.Redis;
import com.ns.common.Ytapi;
import com.ns.common.base.BaseController;
import com.ns.common.constant.RedisKeyDetail;
import com.ns.common.json.JsonResult;
import com.ns.common.model.BasCustomerExt;
import com.ns.common.model.TldOrders;
import com.ns.common.utils.Util;
import com.ns.customer.service.BasCustomerExtService;
import com.ns.tld.domain.YTDO;
import com.ns.tld.service.TldOrdersService;
import com.jfinal.aop.Before;
import com.jfinal.kit.HttpKit;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.util.*;

/**
 * description: //TODO <br>
 * date: 2018-03-10 14:46
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class TldOrdersController extends BaseController {
    static TldOrdersService ordersService = TldOrdersService.me;
    static BasCustomerExtService extService = BasCustomerExtService.me;
    @Before(Tx.class)
    public void newOrder() {
        JSONObject jsonObject = Util.getRequestObject(getRequest(), JSONObject.class);
        renderJson(JsonResult.newJsonResult(ordersService.newOrder(getHeader("sk"), jsonObject)));
    }


    /**
     * 获取运费
     */
    public void getFreight() {
        Map params = getRequestObject(getRequest(), HashMap.class);
        final String province = (String) params.get("province");
        final int num = (int) params.get("num");
        renderJson(JsonResult.newJsonResult(TldOrdersService.me.computeFreight(province, num)));
    }

    public void getOrderList() {
        Map params = getRequestObject(getRequest(), HashMap.class);
        int pageNumber = (int) params.get("page_number");
        int pageSize = (int) params.get("page_size");
        final String rConId = (String) Redis.use().hmget(getHeader("sk"), RedisKeyDetail.CON_ID).get(0);
        Integer status = (Integer) params.get("status");
        renderJson(JsonResult.newJsonResult(ordersService.getOrderList(pageNumber, pageSize, rConId, status)));
    }

    public void orderStatusNum() {
        final String conId = (String) Redis.use().hmget(getHeader("sk"), RedisKeyDetail.CON_ID).get(0);
        renderJson(JsonResult.newJsonResult(ordersService.getOrderStatusNum(conId)));
    }
    
    public void rebate() {
    	String orderId = getPara("orderId");
    	String conId = getPara("conId");
    	int status = getParaToInt("status");
    	BasCustomerExt selfExt = extService.getByConId(conId);
    	TldOrders orders = ordersService.getOrderById(orderId);
    	ordersService.rebate(selfExt, orders, status);
    }

    @Before(Tx.class)
    public void updateOrderStatus13() {
        final String conId = (String) Redis.use().hmget(getHeader("sk"), RedisKeyDetail.CON_ID).get(0);
        ordersService.updateOrderStatus13(conId);
        renderJson(JsonResult.newJsonResult(true));
    }

    public void getOrderItems() {
        Map params = getRequestObject(getRequest(), HashMap.class);
        String orderId = (String) params.get("order_id");
        renderJson(JsonResult.newJsonResult(ordersService.getOrderItems(orderId)));
    }

    @Before(Tx.class)
    public void offlinePay() {
        Map params = getRequestObject(getRequest(), HashMap.class);
        String conId = (String) params.get("orderId");
        ordersService.orderPay(conId, 1);
        renderJson(JsonResult.newJsonResult(true));
    }

    @Before(Tx.class)
    public void deleteOrder() {
        Map params = getRequestObject(getRequest(), HashMap.class);
        renderJson(JsonResult.newJsonResult(ordersService.deleteOrder((String) params.get("order_id"))));
    }

    @Before(Tx.class)
    public void refund() {
        Map params = getRequestObject(getRequest(), HashMap.class);
        renderJson(JsonResult.newJsonResult(ordersService.refund((String) params.get("order_id"))));
    }

    @Before(Tx.class)
    public void confirmOrder() {
        Map params = getRequestObject(getRequest(), HashMap.class);
        renderJson(JsonResult.newJsonResult(ordersService.confirmOrder((String) params.get("order_id"))));
    }

    public void getWaybill() {
        Map params = getRequestObject(getRequest(), HashMap.class);
        String json = Ytapi.ytPost((String) params.get("billNo"));
        List<YTDO> ytdoList = JSON.parseArray(json, YTDO.class);
        Collections.sort(ytdoList, new Comparator<YTDO>() {
            @Override
            public int compare(YTDO o1, YTDO o2) {
                final int rs = o1.getUpload_Time().compareToIgnoreCase(o2.getUpload_Time());
                if (rs > 0) {
                    return -1;
                } else if (rs < 0) {
                    return 1;
                }
                return 0;
            }
        });
        renderJson(JsonResult.newJsonResult(ytdoList));
    }

    public void getOrderSplit() {
        Map params = getRequestObject(getRequest(), HashMap.class);
        renderJson(JsonResult.newJsonResult(ordersService.getOrderSplit((String) params.get("order_id"))));
    }

    public void getOrderById() {
        Map params = getRequestObject(getRequest(), HashMap.class);
        final String orderId = (String) params.get("order_id");
        renderJson(JsonResult.newJsonResult(ordersService.getOrderById(orderId)));
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
