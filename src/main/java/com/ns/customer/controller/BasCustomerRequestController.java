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

import java.util.HashMap;
import java.util.Map;

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
        Map params = getRequestObject(getRequest(), HashMap.class);
        String orderId = (String) params.get("order_id");
        Integer returnReason = (Integer) params.get("return_reason");
        String pic1 = (String) params.get("pic1");
        String pic2 = (String) params.get("pic2");
        String pic3 = (String) params.get("pic3");
        String remark = (String) params.get("remark");
        String expWaybill = (String) params.get("expWaybill");
        String expCompanyId = (String) params.get("expCompanyId");
        renderJson(JsonResult.newJsonResult(basCustomerRequestService.applyForRefund(orderId, returnReason, pic1, pic2, pic3, remark, expWaybill, expCompanyId)));
    }

    //{"ID","1","EXP_COMPANY_ID":"","EXP_WAYBILL":""}
    public void updateRequest() {
        BasCustomerRequest request = Util.getRequestObject(getRequest(), BasCustomerRequest.class);
        renderJson(JsonResult.newJsonResult(basCustomerRequestService.updateRequest(request)));
    }

    public void getById() {
        Map params = getRequestObject(getRequest(), HashMap.class);
        String id = (String) params.get("id");
        renderJson(JsonResult.newJsonResult(basCustomerRequestService.getById(id)));
    }

    public void getByConId() {
        Map params = getRequestObject(getRequest(), HashMap.class);
        String conId = (String) params.get("conId");
        renderJson(JsonResult.newJsonResult(basCustomerRequestService.getByConId(conId)));
    }

    public void getByOrderId() {
        Map params = getRequestObject(getRequest(), HashMap.class);
        String orderId = (String) params.get("orderId");
        renderJson(JsonResult.newJsonResult(basCustomerRequestService.getByOrderId(orderId)));
    }
}
