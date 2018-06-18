/**
 * project name: hdy_project
 * file name:BasCustomerExtController
 * package name:com.ns.customer.controller
 * date:2018-02-01 21:49
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.customer.controller;

import com.ns.common.json.JsonResult;
import com.ns.customer.service.BasCustPointsService;
import com.ns.customer.service.BasCustomerExtService;
import com.jfinal.core.Controller;

/**
 * description: //TODO <br>
 * date: 2018-02-01 21:49
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class BasCustomerExtController extends Controller {
    static BasCustomerExtService extService = BasCustomerExtService.me;
    static BasCustPointsService pointsService = BasCustPointsService.me;

    public void getByConId() {
        renderJson(JsonResult.newJsonResult(extService.getByConId(getPara("con_id"))));
    }

    public void getMyPoints() {
        renderJson(JsonResult.newJsonResult(extService.getMyPoints(getPara("con_id"))));
    }

    public void pointsRanking() {
        renderJson(JsonResult.newJsonResult(extService.pointsRanking()));
    }

    public void pointsDeduction() {
        renderJson(JsonResult.newJsonResult(pointsService.pointsDeduction(getParaToInt("point"))));
    }

    public void getPointTransList() {
        String conId = getPara("con_id");
        int pageNumber = getParaToInt("page_number", 1);
        int pageSize = getParaToInt("page_size", 10);
        renderJson(JsonResult.newJsonResult(pointsService.getPointTransList(conId, pageNumber, pageSize)));
    }

    public void myCustomer() {
        String conId = getPara("con_id");
        int pageNumber = getParaToInt("page_number", 1);
        int pageSize = getParaToInt("page_size", 10);
        renderJson(JsonResult.newJsonResult(extService.myCustomer(pageNumber, pageSize, conId)));
    }

    public void myBuyCustomer() {
        String conId = getPara("con_id");
        int pageNumber = getParaToInt("page_number", 1);
        int pageSize = getParaToInt("page_size", 10);
        renderJson(JsonResult.newJsonResult(extService.myBuyCustomer(pageNumber, pageSize, conId)));
    }

    public void myUnBuyCustomer() {
        String conId = getPara("con_id");
        int pageNumber = getParaToInt("page_number", 1);
        int pageSize = getParaToInt("page_size", 10);
        renderJson(JsonResult.newJsonResult(extService.myUnBuyCustomer(pageNumber, pageSize, conId)));
    }
}
