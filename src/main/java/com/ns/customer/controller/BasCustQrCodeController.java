package com.ns.customer.controller;

import com.jfinal.plugin.redis.Redis;
import com.ns.common.base.BaseController;
import com.ns.common.constant.RedisKeyDetail;
import com.ns.common.exception.CustException;
import com.ns.common.json.JsonResult;
import com.ns.common.model.BasCustQrcode;
import com.ns.common.model.BasCustomer;
import com.ns.common.model.TldQrbgmParams;
import com.ns.common.utils.GUIDUtil;
import com.ns.customer.service.BasCustQrCodeService;
import com.ns.customer.service.BasCustomerService;
import com.ns.tld.service.TLdQrBgmParamsService;
import com.jfinal.kit.PropKit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasCustQrCodeController extends BaseController {
    static BasCustQrCodeService service = BasCustQrCodeService.me;
    static TLdQrBgmParamsService qrBgmParamsService = TLdQrBgmParamsService.me;

    public void getQrBgmList() {
        List<TldQrbgmParams> tldQrbgmParams = qrBgmParamsService.getTldQrbgmParamsList();
        List<Map> result = new ArrayList<>();
        for (TldQrbgmParams t : tldQrbgmParams) {
            Map r = new HashMap();
            r.put("url", t.getCodeUrl());
            r.put("id", t.getID());
            result.add(r);
        }
        renderJson(JsonResult.newJsonResult(result));
    }

    public void getQrcode() throws Exception {
        Map params = getRequestObject(getRequest(), HashMap.class);
        final String conId = (String) Redis.use().hmget(getHeader("sk"), RedisKeyDetail.CON_ID).get(0);
        BasCustomer basCustomer = BasCustomerService.me.getCustomerById(conId);
        if (basCustomer == null) {
            throw new CustException("会员信息异常!");
        }
        final String conNo = basCustomer.getConNo();
        int type = (int) params.get("type");
        String bgmTemplateId = (String) params.get("bgmTemplateId");
        Map map = new HashMap<>();
        //String qrCode_exInfo = PropKit.get("serverUrl") + "api/qrcode/sanQrCode?conNo=" + conNo;
        BasCustQrcode basCustQrcode = service.getConQrcode(conNo, type, bgmTemplateId);
        final String tid = basCustQrcode.getBgmId();
        final String codeUrl = basCustQrcode.getCodeUrl();
        map.put("bgmId", tid);
        map.put("codeImageUrl", codeUrl);
        //map.put("codeContentUrl", qrCode_exInfo);
        renderJson(JsonResult.newJsonResult(map));
    }

    public void sanQrCode() {
        String conNo = getPara("conNo");
        redirect(service.sanQrCode(conNo));
    }

    public void dealSanQrCode() {
        String code = getPara("code");
        String state = getPara("state");
        redirect(service.dealSanQrCode(code, state));
        //forwardAction(service.dealSanQrCode(code, state));
    }


}
