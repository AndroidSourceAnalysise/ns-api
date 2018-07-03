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
    String POINTS_DISCOUNT_RATE = "points_discount_rate";
    /**
     * 关注成新会员上级获得积分
     */
    String SUBSCRIBE_POINTS_UP1 = "subscribe_points_up1";
    /**
     * 关注成新会员自己获得积分
     */
    String SUBSCRIBE_POINTS_SELF = "subscribe_points_self";
    /**
     * 重复消费折扣(百分制%)
     */
    String RE_CONSUME_RATE = "re_consume_rate";
    /**
     * 积分排行榜排名条数
     */
    String POINT_STATISTICS_NUM = "point_statistics_num";
    /**
     * 运费配置
     */
    String EXP_BASE_FEE = "exp_base_fee";
    /**
     * 验证码有效期 分钟
     */
    String VALID_TIME = "valid_time";

    String SESSION = "SESSION";

    String SESSION_KEY = "session_key";

    String CON_ID = "con_id";
}
