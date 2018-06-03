/**
 * project name: ns-api
 * file name:TldOrdersController
 * package name:com.ns.tld.controller
 * date:2018-03-10 14:46
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.tld.controller;

import com.alibaba.fastjson.JSONObject;
import com.ns.common.Ytapi;
import com.ns.common.base.BaseController;
import com.ns.common.json.JsonResult;
import com.ns.common.utils.Util;
import com.ns.tld.service.TldOrdersService;
import com.jfinal.aop.Before;
import com.jfinal.kit.HttpKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.util.HashMap;
import java.util.Map;

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

    @Before(Tx.class)
    public void newOrder() {
        JSONObject jsonObject = Util.getRequestObject(getRequest(), JSONObject.class);
        renderJson(JsonResult.newJsonResult(ordersService.newOrder(jsonObject)));
    }

    public void getOrderList() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);
        String conId = getPara("conId");
        Integer status = getParaToInt("status");
        renderJson(JsonResult.newJsonResult(ordersService.getOrderList(pageNumber, pageSize, conId, status)));
    }

    public void orderStatusNum() {
        String conId = getPara("conId");
        renderJson(JsonResult.newJsonResult(ordersService.getOrderStatusNum(conId)));
    }
    @Before(Tx.class)
    public void updateOrderStatus13() {
        String conId = getPara("conId");
        ordersService.updateOrderStatus13(conId);
        renderJson(JsonResult.newJsonResult(true));
    }

    public void getOrderItems() {
        String orderId = getPara("orderId");
        renderJson(JsonResult.newJsonResult(ordersService.getOrderItems(orderId)));
    }

    @Before(Tx.class)
    public void offlinePay() {
        String conId = getPara("orderId");
        ordersService.orderPay(conId, 1);
        renderJson(JsonResult.newJsonResult(true));
    }

    @Before(Tx.class)
    public void deleteOrder() {
        renderJson(JsonResult.newJsonResult(ordersService.deleteOrder(getPara("orderId"))));
    }

    @Before(Tx.class)
    public void refund() {
        renderJson(JsonResult.newJsonResult(ordersService.refund(getPara("orderId"))));
    }

    @Before(Tx.class)
    public void confirmOrder() {
        renderJson(JsonResult.newJsonResult(ordersService.confirmOrder(getPara("orderId"))));
    }

    public void getWaybill() {
        renderText(Ytapi.ytPost(getPara("billNo")));
    }

    public void getOrderSplit() {
        renderJson(JsonResult.newJsonResult(ordersService.getOrderSplit(getPara("orderId"))));
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
