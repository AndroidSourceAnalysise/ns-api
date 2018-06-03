/**
 * project name: ns-crm
 * file name:PntSkuService
 * package name:com.ns.pnt.service
 * date:2018-02-11 10:39
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.pnt.service;

import com.ns.common.constant.RedisKeyDetail;
import com.ns.common.exception.CustException;
import com.ns.common.model.PntSku;
import com.ns.common.utils.DateUtil;
import com.ns.common.utils.GUIDUtil;
import com.jfinal.plugin.redis.Redis;

import java.util.List;

/**
 * description: //TODO <br>
 * date: 2018-02-11 10:39
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class PntSkuService {
    public static final PntSkuService me = new PntSkuService();
    private final PntSku dao = PntSku.dao;
    private final String COLUMN = "ID,PRODUCT_ID,SKU,SAL_PRICE,STOCK,IMAGE_URL,THUMB_URL,BRIEF_DESCRIPTION,SEL_DEFAULT,INTEGRAL_TAG,INTEGRAL_SELF,INTEGRAL_SUP,WEIGHT,UNIT,DISPLAY_SEQ,ENABLED,VERSION," +
            "STATUS,REMARK,CREATE_BY,CREATE_DT,UPDATE_DT ";


    public List<PntSku> getByProduct(String pntId) {
        return dao.find("select " + COLUMN + " from pnt_sku where ENABLED = 1 and PRODUCT_ID = ? ", pntId);
    }

    public PntSku getById(String skuId) {
        PntSku sku = dao.findFirst("select " + COLUMN + " from pnt_sku where ENABLED = 1 and ID = ? ", skuId);
        if (sku == null) {
            throw new CustException("系统异常，请刷新后重试！");
        }
        return sku;
    }

}
