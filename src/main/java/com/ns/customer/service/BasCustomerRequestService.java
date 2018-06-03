/**
 * project name: ns-api
 * file name:BasCustomerRequestService
 * package name:com.ns.customer.service
 * date:2018-03-20 15:19
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.customer.service;

import com.ns.common.exception.CustException;
import com.ns.common.model.BasCustomerRequest;
import com.ns.common.model.TldOrders;
import com.ns.common.utils.DateUtil;
import com.ns.common.utils.GUIDUtil;
import com.ns.tld.service.TldOrdersService;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

/**
 * description: //TODO <br>
 * date: 2018-03-20 15:19
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class BasCustomerRequestService {
    public static BasCustomerRequestService me = new BasCustomerRequestService();
    private static final BasCustomerRequest dao = new BasCustomerRequest().dao();
    private static final String COLUMN = "ID,CON_ID,CON_NO,CON_NAME,ORDER_ID,ORDER_NO,REQUEST_TYPE,REQUEST_STATUS,REFUND_TYPE," +
            "CUST_SERVICE_CHECKTIME,CUST_SERVICE_CHECKRESULT," +
            "CUST_SERVICE_CHECK_REMARK,FIN_CHECK_DT,FIN_CHECK_RESULT,FIN_CHECK_REMARK,RETURN_REASON,PNT_PHOTO_URL1,PNT_PHOTO_URL2," +
            "PNT_PHOTO_URL3,EXP_COMPANY_ID,EXP_WAYBILL,IS_RETURN,REFUND_STATUS,REFUND_TRANSFER,ENABLED,VERSION,STATUS,REMARK," +
            "CREATE_BY,CREATE_DT,UPDATE_DT ";
    static TldOrdersService ordersService = TldOrdersService.me;

    public boolean applyForRefund(String orderId, Integer returnReason, String pic1, String pic2, String pic3, String remark, String expWaybill, String expCompanyId) {
        TldOrders orders = ordersService.getOrderById(orderId);
        ordersService.cancelFirstOrder(orders);
        if (orders.getSTATUS() == 5 || orders.getSTATUS() == 6) {
            BasCustomerRequest request = setRequestAttr(orders, returnReason, pic1, pic2, pic3, remark, expWaybill, expCompanyId);
            request.save();
            Db.update("update tld_orders set status = 8,VERSION=VERSION+1,UPDATE_DT = ? where id = ?", DateUtil.getNow(), orderId);
            Db.update("update tld_order_split set status = 8,VERSION=VERSION+1,UPDATE_DT = ? where order_id = ?", DateUtil.getNow(), orderId);
        } else {
            throw new CustException("未发货不能申请退货!");
        }
        return true;
    }

    public List<Record> getByConId(String conId) {
        List<Record> list = Db.find("select " + COLUMN + " from bas_customer_request where ENABLED = 1 and con_id = ?", conId);
        for (Record request : list) {
            request.set("ORDER", ordersService.getOrderItems(request.getStr("ORDER_ID")));
        }
        return list;
    }

    public BasCustomerRequest getById(String conId) {
        return dao.findFirst("select " + COLUMN + " from bas_customer_request where ENABLED = 1 and id = ?", conId);
    }

    public BasCustomerRequest getByOrderId(String id) {
        return dao.findFirst("select " + COLUMN + " from bas_customer_request where ENABLED = 1 and ORDER_ID = ?", id);
    }

    public boolean updateRequest(BasCustomerRequest request) {
        request.setUpdateDt(DateUtil.getNow());
        return request.update();
    }

    private BasCustomerRequest setRequestAttr(TldOrders orders, Integer returnReason, String pic1, String pic2, String pic3, String remark, String expWaybill, String expCompanyId) {
        BasCustomerRequest request = new BasCustomerRequest();
        request.setID(GUIDUtil.getGUID());
        request.setConId(orders.getConId());
        request.setConNo(orders.getConNo());
        request.setConName(orders.getConName());
        request.setOrderId(orders.getID());
        request.setOrderNo(orders.getOrderNo());
        request.setRequestType(2);
        request.setRefundType(Integer.valueOf(orders.getPaymentTypeid()));
        request.setAMOUNT(orders.getOrderTotal());
        request.setRequestStatus(0);
        request.setReturnReason(returnReason);
        request.setPntPhotoUrl1(pic1);
        request.setPntPhotoUrl2(pic2);
        request.setPntPhotoUrl3(pic3);
        request.setREMARK(remark);
        request.setExpCompanyId(expCompanyId);
        request.setExpWaybill(expWaybill);
        request.setCreateDt(DateUtil.getNow());
        request.setUpdateDt(DateUtil.getNow());
        return request;
    }
}
