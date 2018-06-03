/**
 * project name: ns-api
 * file name:BasCustPointsService
 * package name:com.ns.customer.service
 * date:2018-03-09 17:22
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.customer.service;

import com.ns.common.constant.RedisKeyDetail;
import com.ns.common.model.BasCustPointTrans;
import com.ns.common.utils.DateUtil;
import com.ns.common.utils.GUIDUtil;
import com.ns.sys.service.SysDictService;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;

import java.math.BigDecimal;
import java.util.List;

/**
 * description: //TODO <br>
 * date: 2018-03-09 17:22
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class BasCustPointsService {
    public static BasCustPointsService me = new BasCustPointsService();
    private static final BasCustPointTrans dao = new BasCustPointTrans().dao();
    private static final String COLUMN = "ID,CON_ID,CON_NO,CON_NAME,FROM_CON_ID,FROM_CON_NO,FROM_CON_NAME,FROM_ORDER_ID,FROM_ORDER_NO,POINTS_TYPE,POINTS_QTY,ENABLED,VERSION,STATUS,REMARK,CREATE_BY,CREATE_DT,UPDATE_DT ";
    static SysDictService sysDictService = SysDictService.me;

    public boolean addPointTrans(BasCustPointTrans basCustPointTrans) {
        basCustPointTrans.setID(GUIDUtil.getGUID());
        basCustPointTrans.setCreateDt(DateUtil.getNow());
        basCustPointTrans.setUpdateDt(DateUtil.getNow());
        return basCustPointTrans.save();
    }

    public Page<BasCustPointTrans> getPointTransList(String conId, int pageNum, int pageSize) {
        return dao.paginate(pageNum, pageSize, "select " + COLUMN, " from bas_cust_point_trans where ENABLED  = 1  and con_id = ? order by CREATE_DT desc ", conId);
    }

    public List<BasCustPointTrans> getByOrderIdAndType(String orderId) {
        return dao.find("select " + COLUMN + " from bas_cust_point_trans   where ENABLED  = 1  and FROM_ORDER_ID = ?", orderId);
    }

    public BigDecimal pointsDeduction(int point) {
        BigDecimal amt = BigDecimal.ZERO;
        String rate = sysDictService.getByParamKey(RedisKeyDetail.POINTS_DISCOUNT_RATE);
        if (StrKit.notBlank(rate)) {
            amt = new BigDecimal(point).multiply(new BigDecimal(rate).divide(new BigDecimal(100)));
        }
        return amt;
    }

    public static void main(String[] args) {
        BigDecimal a = new BigDecimal(1).multiply(new BigDecimal(5).divide(new BigDecimal(100)));
        System.out.println(a);
    }
}
