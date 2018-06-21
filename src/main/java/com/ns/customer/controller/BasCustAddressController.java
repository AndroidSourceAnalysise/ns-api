package com.ns.customer.controller;

import com.jfinal.plugin.redis.Redis;
import com.ns.common.base.BaseController;
import com.ns.common.constant.RedisKeyDetail;
import com.ns.common.json.JsonResult;
import com.ns.common.model.BasCustAddress;
import com.ns.common.utils.Util;
import com.ns.customer.service.BasCustAddressService;

import java.util.HashMap;
import java.util.Map;

public class BasCustAddressController extends BaseController {
    static BasCustAddressService service = BasCustAddressService.me;

    //{"CON_ID":"1","TEL":"","IS_DEFAULT":"是否默认收货地址【0：非默认  1：默认】","MOBILE":"13684654466","RECIPIENTS":"王强","COUNTRY":"中国","PROVINCE":"湖南省","CITY":"长沙市","DISTRICT":"岳麓区","ADDRESS":"岳麓大道"}
    public void createAddress() {
        BasCustAddress address = Util.getRequestObject(getRequest(), BasCustAddress.class);
        address.setConId((String) Redis.use().hmget(getHeader("sk"), RedisKeyDetail.CON_ID).get(0));
        renderJson(JsonResult.newJsonResult(service.createAddress(address)));
    }

    public void getAddressList() {
        String conId = (String) Redis.use().hmget(getHeader("sk"), RedisKeyDetail.CON_ID).get(0);
        renderJson(JsonResult.newJsonResult(service.getAddressList(conId)));
    }

    public void getDefault() {
        String conId = (String) Redis.use().hmget(getHeader("sk"), RedisKeyDetail.CON_ID).get(0);
        renderJson(JsonResult.newJsonResult(service.getDefault(conId)));
    }

    //{"ID":"1","CON_ID":"1","TEL":"","IS_DEFAULT":"是否默认收货地址【0：非默认  1：默认】","MOBILE":"13684654466","RECIPIENTS":"王强","COUNTRY":"中国","PROVINCE":"湖南省","CITY":"长沙市","DISTRICT":"岳麓区","ADDRESS":"岳麓大道"}
    public void updateAddress() {
        BasCustAddress address = Util.getRequestObject(getRequest(), BasCustAddress.class);
        address.setConId((String) Redis.use().hmget(getHeader("sk"), RedisKeyDetail.CON_ID).get(0));
        renderJson(JsonResult.newJsonResult(service.updateAddress(address)));
    }

    public void deleteAddress() {
        Map params = getRequestObject(getRequest(), HashMap.class);
        renderJson(JsonResult.newJsonResult(service.deleteAddress((String) params.get("id"))));
    }

    public void getById() {
        Map params = getRequestObject(getRequest(), HashMap.class);
        renderJson(JsonResult.newJsonResult(service.getById((String) params.get("id"))));
    }
}
