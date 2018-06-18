/**
 * project name: hdy_project
 * file name:BasCustomerExtService
 * package name:com.ns.customer.service
 * date:2018-02-01 21:48
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.customer.service;

import com.ns.common.constant.RedisKeyDetail;
import com.ns.common.model.BasCustomerExt;
import com.ns.common.utils.DateUtil;
import com.ns.common.utils.GUIDUtil;
import com.ns.sys.service.SysDictService;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description: //TODO <br>
 * date: 2018-02-01 21:48
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class BasCustomerExtService {
    public static BasCustomerExtService me = new BasCustomerExtService();
    private static final BasCustomerExt dao = new BasCustomerExt().dao();
    static BasCustPointsService basCustPointsService = BasCustPointsService.me;
    private static final String COLUMN = "ID,CON_ID,CON_NO,CON_NAME,REVENUES,CONSUMPTIONS,RE_CONSUMPTIONS,PURED_CUST_QTY,UNPURED_CUST_QTY,ORDERS_TOTAL,ORDERS_PROM," +
            "POINTS_ENABLED,POINTS_TOTAL,POINTS_CFMD,POINTS_UNCFMD,ENABLED,VERSION,STATUS,REMARK,CREATE_BY,CREATE_DT,UPDATE_DT ";
    static SysDictService sysDictService = SysDictService.me;

    /**
     * 创建会员拓展信息
     *
     * @param conId
     * @param conNo
     * @param conName
     * @return
     */
    public boolean addBasCustomerExt(String conId, String conNo, String conName) {
        BasCustomerExt ext = new BasCustomerExt();
        ext.setID(GUIDUtil.getGUID());
        ext.setConId(conId);
        ext.setConNo(conNo);
        ext.setConName(conName);
        ext.setCreateDt(DateUtil.getNow());
        ext.setUpdateDt(DateUtil.getNow());
        return ext.save();
    }

    /**
     * 根据会员ID查询扩展数据
     *
     * @param conId
     * @return
     */
    public BasCustomerExt getByConId(String conId) {
        return dao.findFirst("select " + COLUMN + " from bas_customer_ext where enabled = 1 and con_id = ?", conId);
    }

    public Map<String, Object> getMyPoints(String conId) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("POINTS_ENABLED", Db.findFirst("SELECT POINTS_TOTAL,POINTS_ENABLED FROM BAS_CUSTOMER_EXT  WHERE ENABLED = 1 AND CON_ID = ? ", conId));
        resultMap.put("POINTTRANS", basCustPointsService.getPointTransList(conId, 1, 5));
        // 积分抵扣比例 例如100 也就是1积分抵扣1块钱
        resultMap.put("POINT_CREDIT", SysDictService.me.getByParamKey("points_discount_rate"));
        return resultMap;
    }

    public List<Record> pointsRanking() {
        String pointStatisticsNum = sysDictService.getByParamKey(RedisKeyDetail.POINT_STATISTICS_NUM);
        int num = 10;
        if (StrKit.notBlank(pointStatisticsNum)) {
            num = Integer.valueOf(pointStatisticsNum);
        }
        return Db.find("SELECT T.ID,T.PIC,T.CON_NAME,T.CON_NO,T2.POINTS_CFMD FROM BAS_CUSTOMER T INNER JOIN BAS_CUSTOMER_EXT T2 ON T.ID = T2.CON_ID ORDER BY T2.POINTS_CFMD DESC LIMIT " + num);
    }

    public Page<Record> myCustomer(int pageNumber, int pageSize, String conId) {
        String select = "SELECT  T1.ID,T1.CON_NO,T1.CON_NAME,T1.PIC,T1.CON_TYPE,T1.CREATE_DT AS REGISTER_DT,T2.ORDER_TOTAL,T2.UP1_INTEGRAL,T2.CREATE_DT AS ORDER_DT,T2.ID AS ORDER_ID ";
        String sqlExceptSelect = "FROM BAS_CUSTOMER T1 LEFT JOIN TLD_ORDERS T2 ON T1.ID = T2.CON_ID AND T2.STATUS = 7 WHERE T1.RP_ID = ? order by T1.CREATE_DT desc";
        return Db.paginate(pageNumber, pageSize, select, sqlExceptSelect, conId);
    }

    public Page<Record> myBuyCustomer(int pageNumber, int pageSize, String conId) {
        //return Db.paginate(pageNumber, pageSize, "SELECT CON_ID,CON_NO,CON_NAME,PIC,CREATE_DT AS ORDER_DT,UP1_INTEGRAL,ORDER_TOTAL ", " FROM TLD_ORDERS WHERE STATUS = 7 AND RP_ID = ? GROUP BY CON_ID ORDER BY UPDATE_DT ", conId);
        return Db.paginate(pageNumber, pageSize, "SELECT ID,CON_NO,CON_NAME,PIC,CREATE_DT AS REGISTER_DT ", " FROM BAS_CUSTOMER WHERE CON_TYPE = 1 AND RP_ID = ? ORDER BY CREATE_DT desc", conId);
    }

    public Page<Record> myUnBuyCustomer(int pageNumber, int pageSize, String conId) {
        return Db.paginate(pageNumber, pageSize, "SELECT ID,CON_NO,CON_NAME,PIC,CREATE_DT AS REGISTER_DT ", " FROM BAS_CUSTOMER WHERE CON_TYPE = 0 AND RP_ID = ? ORDER BY CREATE_DT desc", conId);
    }
}
