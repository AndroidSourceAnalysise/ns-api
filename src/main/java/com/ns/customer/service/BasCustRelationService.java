/**
 * project name: ns-api
 * file name:BasCustRelationService
 * package name:com.ns.customer.service
 * date:2018-02-08 13:33
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.customer.service;

import com.ns.common.model.BasCustRelation;
import com.ns.common.model.BasCustomer;
import com.ns.common.utils.DateUtil;
import com.ns.common.utils.GUIDUtil;

/**
 * description: //TODO <br>
 * date: 2018-02-08 13:33
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class BasCustRelationService {
    public static final BasCustRelationService me = new BasCustRelationService();
    private final BasCustRelation dao = new BasCustRelation();
    static BasCustomerService basCustomerService = BasCustomerService.me;

    public void saveRelation(String rpNo, String openId, String nickName, int sex, String country, String province, String city, String pic) {
        BasCustomer customer = basCustomerService.getCustomerByOpenId(openId);
        if (customer == null || customer.getIsSubscribe() == 0) {
            BasCustRelation relation = getByOpenId(openId);
            if (relation == null) {
                relation = new BasCustRelation();
                relation.setID(GUIDUtil.getGUID());
                relation.setOpenId(openId);
                relation.setRpNo(rpNo);
                relation.setNickName(nickName);
                relation.setSEX(sex);
                relation.setCOUNTRY(country);
                relation.setPROVINCE(province);
                relation.setCITY(city);
                relation.setPIC(pic);
                relation.setUpdateDt(DateUtil.getNow());
                relation.setCreateDt(DateUtil.getNow());
                relation.save();
            } else {
                relation.setRpNo(rpNo);
                relation.setUpdateDt(DateUtil.getNow());
                relation.setVERSION(relation.getVERSION() + 1);
                relation.update();
            }
        }

    }

    public BasCustRelation getByOpenId(String openId) {
        return dao.findFirst("select * from bas_cust_relation where open_id = ?", openId);
    }
}
