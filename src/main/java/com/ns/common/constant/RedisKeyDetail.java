/**
 * project name: hdy_project
 * file name:RedisKeyDetail
 * package name:com.ns.common.constant
 * date:2018-02-02 9:54
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.common.constant;

/**
 * description: //TODO <br>
 * date: 2018-02-02 9:54
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public interface RedisKeyDetail {
    /**
     * 会员号自增规则
     */
    String CON_NO_SEQ = "con_no_seq";

    String ORDER_NO_SEQ = "ORDER_NO_SEQ";

    String PHOTOS_KEY = "PHOTOS-";
    /**
     * 所有商品缓存
     */
    String PRODUCT_ALL = "PRODUCT-ID-ALL";
    /**
     * 单个商品缓存
     */
    String PRODUCT_ID = "PRODUCT-ID-";
    String PNT_CATEGORIES = "PNT_CATEGORIES";
    String PNT_MENU = "PNT_MENU";
    /**
     * 商品总库存
     */
    String PRODUCT_STOCK_ID = "PRODUCT-STOCK-ID-";
    /**
     * SKU库存
     */
    String SKU_STOCK_ID = "SKU-STOCK-ID-";
    /**
     * 积分折扣率(百分制%)
     */
    String POINTS_DISCOUNT_RATE = "POINTS_DISCOUNT_RATE";
    /**
     * 关注成新会员上级获得积分
     */
    String SUBSCRIBE_POINTS_UP1 = "SUBSCRIBE_POINTS_UP1";
    /**
     * 关注成新会员自己获得积分
     */
    String SUBSCRIBE_POINTS_SELF = "SUBSCRIBE_POINTS_SELF";
    /**
     * 重复消费折扣(百分制%)
     */
    String RE_CONSUME_RATE = "RE_CONSUME_RATE";
    /**
     * 积分排行榜排名条数
     */
    String POINT_STATISTICS_NUM = "POINT_STATISTICS_NUM";
    /**
     * 运费配置
     */
    String EXP_BASE_FEE = "EXP_BASE_FEE";
    /**
     * 验证码有效期 分钟
     */
    String VALID_TIME = "VALID_TIME";
}
