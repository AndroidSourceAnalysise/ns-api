package com.ns.customer.controller;

import com.ns.common.base.BaseController;
import com.ns.common.json.JsonResult;
import com.ns.common.utils.GUIDUtil;
import com.ns.customer.service.BasCustQrCodeService;
import com.ns.tld.service.TLdQrBgmParamsService;
import com.jfinal.kit.PropKit;

import java.util.HashMap;
import java.util.Map;

public class BasCustQrCodeController extends BaseController {
    static BasCustQrCodeService service = BasCustQrCodeService.me;
    static TLdQrBgmParamsService qrBgmParamsService = TLdQrBgmParamsService.me;

    public void getQrBgmList() {
        renderJson(JsonResult.newJsonResult(qrBgmParamsService.getTldQrbgmParamsList()));
    }

    public void getQrcode() throws Exception {
        String conNo = getPara("conNo");
        int type = getParaToInt("type", 1);
        String bgmTemplateId = getPara("bgmTemplateId", "89DF5B0AA1984C55938065215F69CCE1");
        Map map = new HashMap<>();
        String qrCode_exInfo = PropKit.get("serverUrl") + "api/qrcode/sanQrCode?conNo=" + conNo;
        map.put("qrcode", service.getConQrcode(conNo, type, bgmTemplateId));
        map.put("url", qrCode_exInfo);
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
