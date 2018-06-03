/**
 * project name: ns-crm
 * file name:TldCouponService
 * package name:com.ns.tld.service
 * date:2018-03-06 10:11
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.tld.service;

import com.ns.common.exception.CustException;
import com.ns.common.model.BasCustomer;
import com.ns.common.model.TldCoupon;
import com.ns.common.model.TldCouponGrant;
import com.ns.common.utils.DateUtil;
import com.ns.common.utils.GUIDUtil;
import com.ns.customer.service.BasCustomerService;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

/**
 * description: //TODO <br>
 * date: 2018-03-06 10:11
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class TldCouponService {
    private final TldCoupon dao = new TldCoupon();
    public static final TldCouponService me = new TldCouponService();
    private final String COLUMN = "ID,NAME,DESCRIPTION,SAFETY_AMOUNT,DISCOUNT_AMOUNT,TOTAL_NUMBER,REMAIN_NUMBER,COUPON_TYPE,IMAGE_URL,START_DT,END_DT,ENABLED,VERSION," +
            "STATUS,REMARK,CREATE_BY,CREATE_DT,UPDATE_DT ";
    static TldCouponGrantService tldCouponGrantService = TldCouponGrantService.me;
    static BasCustomerService customerService = BasCustomerService.me;

    public boolean addTldCoupon(TldCoupon coupon) {
        coupon.setID(GUIDUtil.getGUID());
        coupon.setCreateDt(DateUtil.getNow());
        coupon.setUpdateDt(DateUtil.getNow());
        return coupon.save();
    }

    public boolean updateTldCoupon(TldCoupon coupon) {
        coupon.setUpdateDt(DateUtil.getNow());
        return coupon.update();
    }

    public boolean deleteTldCoupon(String id) {
        return dao.deleteById(id);
    }

    public TldCoupon getById(String id) {
        return dao.findFirst("select " + COLUMN + " from tld_coupon where id = ?", id);
    }

    public List<Record> getTldCouponList(String conId) {
        List<Record> couponList = Db.find("select " + COLUMN + " from tld_coupon where STATUS = 1 and ENABLED = 1");
        for (Record coupon : couponList) {
            TldCouponGrant tldCouponGrant = tldCouponGrantService.getByIdAndConId(coupon.getStr("ID"), conId);
            coupon.set("IS_RECEIVE", tldCouponGrant != null);
        }
        return couponList;
    }

    public List<TldCouponGrant> getTldCouponGrantList(String conId) {
        List<TldCouponGrant> grantsList = tldCouponGrantService.getByConId(conId);

        return grantsList;
    }


    public void receiveCoupon(String conId, String couponId) {
        BasCustomer customer = customerService.getCustomerByIdNotNull(conId);
        TldCoupon coupon = getById(couponId);
        //判断剩余数量
        if (coupon.getRemainNumber() <= 0) {
            throw new CustException("该优惠券已被领取完!");
        }
        TldCouponGrant tldCouponGrant = tldCouponGrantService.getByIdAndConId(couponId, conId);
        if (tldCouponGrant != null) {
            throw new CustException("您已经领取过该券,不能重复领取!");
        }
        //产生流水记录
        TldCouponGrant grant = new TldCouponGrant();
        grant.setConId(conId);
        grant.setConName(customer.getConName());
        grant.setConNo(customer.getConNo());
        grant.setCouponId(couponId);
        grant.setCouponName(coupon.getNAME());
        grant.setCouponType(coupon.getCouponType());
        grant.setDESCRIPTION(coupon.getDESCRIPTION());
        grant.setDiscountAmount(coupon.getDiscountAmount());
        grant.setImageUrl(coupon.getImageUrl());
        grant.setSafetyAmount(coupon.getSafetyAmount());
        grant.setStartDt(coupon.getStartDt());
        grant.setEndDt(coupon.getEndDt());
        if (tldCouponGrantService.addTldCouponGrant(grant)) {
            //剩余数量-1
            int result = Db.update("update tld_coupon set VERSION=VERSION+1,REMAIN_NUMBER=REMAIN_NUMBER-1 where id = ? and VERSION = ?", couponId, coupon.getVERSION());
            if (result == 0) {
                throw new CustException("网络错误,请稍后再试!");
            }
        }
    }
}
