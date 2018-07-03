/**
 * project name: ns-crm
 * file name:TldCouponGrantService
 * package name:com.ns.tld.service
 * date:2018-03-06 10:55
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.tld.service;

import com.ns.common.model.TldCouponGrant;
import com.ns.common.utils.DateUtil;
import com.ns.common.utils.GUIDUtil;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * description: //TODO <br>
 * date: 2018-03-06 10:55
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class TldCouponGrantService {
    private final TldCouponGrant dao = new TldCouponGrant();
    public static final TldCouponGrantService me = new TldCouponGrantService();
    private final String COLUMN = "ID,COUPON_ID,COUPON_NAME,CON_ID,CON_NO,CON_NAME,DESCRIPTION,SAFETY_AMOUNT,DISCOUNT_AMOUNT,COUPON_TYPE,IMAGE_URL,START_DT,END_DT,ENABLED,VERSION," +
            "STATUS,REMARK,CREATE_BY,CREATE_DT,UPDATE_DT ";

    public boolean addTldCouponGrant(TldCouponGrant couponGrant) {
        couponGrant.setID(GUIDUtil.getGUID());
        couponGrant.setCreateDt(DateUtil.getNow());
        couponGrant.setUpdateDt(DateUtil.getNow());
        return couponGrant.save();
    }

    public boolean updateTldCouponGrant(TldCouponGrant couponGrant) {
        couponGrant.setUpdateDt(DateUtil.getNow());
        return couponGrant.update();
    }

    public TldCouponGrant getById(String id) {
        return dao.findFirst("select " + COLUMN + " from tld_coupon_grant where enabled = 1 and id = ?", id);
    }

    public TldCouponGrant getByIdAndConId(String id, String conId) {
        return dao.findFirst("select " + COLUMN + " from tld_coupon_grant where enabled = 1 and coupon_id = ? and con_Id = ?", id, conId);
    }

    public List<TldCouponGrant> getByConId(String conId) {
        List<TldCouponGrant> grantsList = dao.find("select " + COLUMN + " from tld_coupon_grant where enabled = 1 and con_Id = ? ORDER BY UPDATE_DT desc", conId);
        return pastDue(grantsList, false);
    }

    public List<TldCouponGrant> getUsableCoupon(String conId) {
        List<TldCouponGrant> grantsList = dao.find("select " + COLUMN + " from tld_coupon_grant where enabled = 1 and status = 0 and con_Id = ? ORDER BY UPDATE_DT desc", conId);
        return pastDue(grantsList, true);
    }

    public List<TldCouponGrant> getCoupon(int status,String conId) {
        List<TldCouponGrant> grantsList = dao.find("select " + COLUMN + " from tld_coupon_grant where enabled = 1 and status = ? and con_Id = ? ORDER BY UPDATE_DT desc", status,conId);
        return grantsList;
    }

    /**
     * 过期
     *
     * @param grantsList
     * @return
     */
    public List<TldCouponGrant> pastDue(List<TldCouponGrant> grantsList, boolean remove) {
        Iterator<TldCouponGrant> i = grantsList.iterator();
        while (i.hasNext()) {
            TldCouponGrant grant = i.next();
            if (grant.getSTATUS() == 0) {
                String dateTime = DateUtil.getNow(DateUtil.DEFAULT_DATE_TIME_RFGFX);
                if (!DateUtil.isTween(dateTime, grant.getStartDt(), grant.getEndDt(), DateUtil.DEFAULT_DATE_TIME_RFGFX)) {
                    if (grant.update()) {
                        grant.setSTATUS(2);
                        grant.setUpdateDt(DateUtil.getNow());
                        if (remove) {
                            i.remove();
                        }
                    }

                }
            }
        }
        return grantsList;
    }


    public static void main(String[] args) {
        //2018-03-20 15:47:11
        String dateTime = DateUtil.getNow(DateUtil.DEFAULT_DATE_TIME_RFGFX);
        System.out.println(dateTime);
        if (!DateUtil.isTween(dateTime, "2018-03-06 21:47:00", "2018-03-30 21:45:00", DateUtil.DEFAULT_DATE_TIME_RFGFX)) {
            System.out.println(1);
        }
    }
}
