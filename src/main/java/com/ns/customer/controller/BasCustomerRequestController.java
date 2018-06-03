/**
 * project name: ns-api
 * file name:BasCustomerRequestController
 * package name:com.ns.customer.controller
 * date:2018-03-22 9:55
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.customer.controller;

import com.ns.common.base.BaseController;
import com.ns.common.json.JsonResult;
import com.ns.common.model.BasCustomerRequest;
import com.ns.common.utils.Util;
import com.ns.customer.service.BasCustomerRequestService;

/**
 * description: //TODO <br>
 * date: 2018-03-22 9:55
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class BasCustomerRequestController extends BaseController {
    static BasCustomerRequestService basCustomerRequestService = BasCustomerRequestService.me;

    public void applyForRefund() {
        String orderId = getPara("orderId");
        Integer returnReason = getParaToInt("returnReason");
        String pic1 = getPara("pic1");
        String pic2 = getPara("pic2");
        String pic3 = getPara("pic3");
        String remark = getPara("remark");
        String expWaybill = getPara("expWaybill");
        String expCompanyId = getPara("expCompanyId");
        renderJson(JsonResult.newJsonResult(basCustomerRequestService.applyForRefund(orderId, returnReason, pic1, pic2, pic3, remark, expWaybill, expCompanyId)));
    }

    //{"ID","1","EXP_COMPANY_ID":"","EXP_WAYBILL":""}
    public void updateRequest() {
        BasCustomerRequest request = Util.getRequestObject(getRequest(), BasCustomerRequest.class);
        renderJson(JsonResult.newJsonResult(basCustomerRequestService.updateRequest(request)));
    }

    public void getById() {
        String id = getPara("id");
        renderJson(JsonResult.newJsonResult(basCustomerRequestService.getById(id)));
    }

    public void getByConId() {
        String conId = getPara("conId");
        renderJson(JsonResult.newJsonResult(basCustomerRequestService.getByConId(conId)));
    }

    public void getByOrderId() {
        String orderId = getPara("orderId");
        renderJson(JsonResult.newJsonResult(basCustomerRequestService.getByOrderId(orderId)));
    }
}
