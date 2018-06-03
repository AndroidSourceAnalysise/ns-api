/**
 * project name: ns-crm
 * file name:TldCouponController
 * package name:com.ns.tld.controller
 * date:2018-03-06 13:52
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.tld.controller;

import com.ns.common.base.BaseController;
import com.ns.common.json.JsonResult;
import com.ns.common.model.TldCoupon;
import com.ns.common.model.TldCouponGrant;
import com.ns.common.utils.Util;
import com.ns.tld.service.TldCouponGrantService;
import com.ns.tld.service.TldCouponService;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;

/**
 * description: //TODO <br>
 * date: 2018-03-06 13:52
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class TldCouponController extends BaseController {
    static TldCouponService tldCouponService = TldCouponService.me;
    static TldCouponGrantService tldCouponGrantService = TldCouponGrantService.me;

    public void getTldCouponList() {
        renderJson(JsonResult.newJsonResult(tldCouponService.getTldCouponList(getPara("conId"))));
    }

    @Before({Tx.class})
    public void receiveCoupon() {
        String conId = getPara("conId");
        String couponId = getPara("couponId");
        tldCouponService.receiveCoupon(conId, couponId);
        renderJson(JsonResult.newJsonResult(true));
    }

    /**
     * 我的优惠券
     */
    public void getTldCouponGrantList() {
        renderJson(JsonResult.newJsonResult(tldCouponGrantService.getByConId(getPara("conId"))));
    }
    public void getUsableCoupon(){
        renderJson(JsonResult.newJsonResult(tldCouponGrantService.getUsableCoupon(getPara("conId"))));
    }
}
