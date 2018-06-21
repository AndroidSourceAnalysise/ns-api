/**
 * project name: ns-api
 * file name:TldOrdersService
 * package name:com.ns.tld.service
 * date:2018-03-10 14:55
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.tld.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cedarsoftware.util.io.JsonObject;
import com.ns.common.constant.RedisKeyDetail;
import com.ns.common.exception.CustException;
import com.ns.common.model.*;
import com.ns.common.utils.DateUtil;
import com.ns.common.utils.GUIDUtil;
import com.ns.customer.service.BasCustPointsService;
import com.ns.customer.service.BasCustomerExtService;
import com.ns.customer.service.BasCustomerService;
import com.ns.pnt.service.PntProductService;
import com.ns.pnt.service.PntSkuService;
import com.ns.sys.service.SysDictService;
import com.ns.weixin.service.NoticeService;
import com.ns.weixin.service.WeixinPayService;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.jfinal.weixin.sdk.api.PaymentApi;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.security.Guard;
import java.util.*;

/**
 * description: //TODO <br>
 * date: 2018-03-10 14:55
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class TldOrdersService {
    private final TldOrders dao = new TldOrders();
    public static final TldOrdersService me = new TldOrdersService();
    static BasCustomerService basCustomerService = BasCustomerService.me;
    static SysDictService sysDictService = SysDictService.me;
    static PntProductService pntProductService = PntProductService.me;
    static PntSkuService skuService = PntSkuService.me;
    static TldCouponGrantService couponService = TldCouponGrantService.me;
    static BasCustPointsService pointsService = BasCustPointsService.me;
    static BasCustomerExtService extService = BasCustomerExtService.me;
    static NoticeService noticeService = NoticeService.me;
    private final String COLUMN = "ID,ORDER_NO,CON_ID,CON_NO,CON_NAME,PIC,PAY_DT,SHIPPING_DT,CONFIRM_DT,COUNTRY,PROVINCE,CITY,DISTRICT,ADDRESS,POSTAL_CODE,MOBILE,RECIPIENTS,FREIGHT,WEIGHT,PAYMENT_TYPEID,PAYMENT_TYPE,ORDER_SOURCE,ORDER_TYPE,ORDER_TOTAL,COUPON_AMOUNT,INTEGRAL_AMOUNT,PNT_AMOUNT,IS_DISCOUNT,IS_REORDER,SELF_INTEGRAL,UP1_INTEGRAL,RP_ID,RP_NO,RP_NAME,ENABLED,VERSION," +
            "STATUS,REMARK,CREATE_BY,CREATE_DT,UPDATE_DT ";

    public static void main(String[] args) {
        BigDecimal a = new BigDecimal(799);
        int b = 5;
        System.out.println(a.multiply(new BigDecimal(b)).divide(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue());
        System.out.println(a.multiply(new BigDecimal(b)).divide(new BigDecimal(100)).intValue());
    }

    private int[] compute(boolean isTitter, PntProduct pntProduct, PntSku sku) {
        String re_consume_rate = sysDictService.getByParamKey(RedisKeyDetail.RE_CONSUME_RATE);
        int integralSelf = 0, integralSup = 0;
        if (isTitter) {
            BigDecimal disAmount;
            if (sku != null) {
                if (StrKit.notBlank(re_consume_rate)) {
                    disAmount = sku.getSalPrice().multiply(new BigDecimal(re_consume_rate)).divide(new BigDecimal(10));
                } else {
                    disAmount = sku.getSalPrice();
                }
                integralSelf += disAmount.multiply(new BigDecimal(sku.getIntegralSelf())).divide(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
                integralSup += disAmount.multiply(new BigDecimal(sku.getIntegralSup())).divide(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
            } else {
                if (StrKit.notBlank(re_consume_rate)) {
                    disAmount = pntProduct.getSalPrice().multiply(new BigDecimal(re_consume_rate)).divide(new BigDecimal(10));
                } else {
                    disAmount = pntProduct.getSalPrice();
                }
                integralSelf += disAmount.multiply(new BigDecimal(pntProduct.getIntegralSelf())).divide(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
                integralSup += disAmount.multiply(new BigDecimal(pntProduct.getIntegralSup())).divide(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
            }
        } else {
            if (sku != null) {
                integralSelf += sku.getSalPrice().multiply(new BigDecimal(sku.getIntegralSelf())).divide(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
                integralSup += sku.getSalPrice().multiply(new BigDecimal(sku.getIntegralSup())).divide(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
            } else {
                integralSelf += pntProduct.getSalPrice().multiply(new BigDecimal(pntProduct.getIntegralSelf())).divide(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
                integralSup += pntProduct.getSalPrice().multiply(new BigDecimal(pntProduct.getIntegralSup())).divide(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
            }
        }
        return new int[]{integralSelf, integralSup};
    }


    //{"COUNTRY":"中国","PROVINCE":"湖南省","CITY":"长沙市","DISTRICT":"岳麓区","ADDRESS":"雷锋大道","POSTAL_CODE":"412000","MOBILE":"13874133322","RECIPIENTS":"张三","FREIGHT":"0","PAYMENT_TYPEID":"0","PAYMENT_TYPE":"微信支付","ORDER_SOURCE":"1","ORDER_TYPE":"1"}
    public Map<String, String> newOrder(String sk,JSONObject jsonObject) {
        TldOrders orders = jsonObject.toJavaObject(TldOrders.class);
        orders.setConId((String) Redis.use().hmget(sk,RedisKeyDetail.CON_ID).get(0));
        BigDecimal couponAmount = BigDecimal.ZERO;
        BigDecimal pointAmount = BigDecimal.ZERO;
        BasCustomer customer = basCustomerService.getCustomerByIdNotNull(orders.getConId());
        String orderId = GUIDUtil.getGUID();
        String orderNo = getOrderNoSeq();

        //orders.setFREIGHT(computeFreight())
        boolean isTitter = customer.getConType() == 1;//是否是推客
        String[] items = jsonObject.getString("ITEMS").split("\\|");
        //自己获得的积分
        Integer integralSelf = 0;
        //上级获得的积分
        Integer integralSup = 0;
        BigDecimal pntAmountSum = BigDecimal.ZERO;
        List<TldOrderItems> itemsList = new ArrayList<>();
        int quantitySum = 0;
        for (String item : items) {
            String[] str = item.split("&");
            String pntId = str[0];
            String skuId = str[1];
            int quantity = Integer.valueOf(str[2]);
            PntProduct pntProduct = pntProductService.getById(pntId);
            PntSku sku = skuService.getById(skuId);
            BigDecimal amt;
            if (StrKit.notBlank(skuId)) {
                // sku 库存
                Long stock = Redis.use().getCounter(RedisKeyDetail.SKU_STOCK_ID + skuId);
                if (stock == null || stock < quantity) {
                    throw new CustException(pntProduct.getProductName() + "-" + sku.getSKU() + ":库存不足！");
                }
                amt = sku.getSalPrice().multiply(new BigDecimal(quantity));
                itemsList.add(setItems(customer, orderId, orderNo, sku, pntProduct, quantity, amt));
                int[] rs = compute(isTitter, pntProduct, sku);
                integralSelf = rs[0];
                integralSup = rs[1];
            } else {
                // 产品库存
                Long stock = Redis.use().getCounter(RedisKeyDetail.PRODUCT_STOCK_ID + pntId);
                if (stock == null || stock < quantity) {
                    throw new CustException(pntProduct.getProductName() + "-" + ":库存不足！");
                }
                amt = pntProduct.getSalPrice().multiply(new BigDecimal(quantity));
                itemsList.add(setItems(customer, orderId, orderNo, null, pntProduct, quantity, amt));
                int[] rs = compute(isTitter, pntProduct, sku);
                integralSelf = rs[0];
                integralSup = rs[1];
            }
//            integralSelf += sku.getIntegralSelf();
//            integralSup += sku.getIntegralSup();
            pntAmountSum = pntAmountSum.add(amt);
            quantitySum += quantity;
        }
        //判断是否使用优惠券
        if (jsonObject.containsKey("COUPON_GRANT_ID")) {
            couponAmount = computeCouponAmount(jsonObject.getString("COUPON_GRANT_ID"), pntAmountSum);
            //设置优惠券为已使用
            orders.setCouponGrantId(jsonObject.getString("COUPON_GRANT_ID"));

        }
        //判断是否使用积分

        if (jsonObject.containsKey("POINT")) {
            if (customer.getConType() == 0) {
                throw new CustException("推客才能享用积分兑换功能哦!");
            }
            int point = jsonObject.getInteger("POINT");
            pointAmount = pointsService.pointsDeduction(point);
            //扣积分
            Db.update("update bas_customer_ext set POINTS_ENABLED=POINTS_ENABLED-? where con_id = ?", point, customer.getID());
            //加积分流水
            BasCustPointTrans pointTrans = new BasCustPointTrans();
            pointTrans.setID(GUIDUtil.getGUID());
            pointTrans.setConId(customer.getID());
            pointTrans.setConNo(customer.getConNo());
            pointTrans.setConName(customer.getConName());
            pointTrans.setFromOrderId(orderId);
            pointTrans.setFromOrderNo(orderNo);
            pointTrans.setPointsType(3);
            pointTrans.setPointsQty(point);
            pointTrans.setCreateDt(DateUtil.getNow());
            pointTrans.setUpdateDt(DateUtil.getNow());
            pointTrans.save();

        }
        //计算运费
        orders.setFREIGHT(computeFreight(orders.getPROVINCE(), quantitySum));
        orders = setOrders(orders, orderId, orderNo, customer, integralSelf, integralSup, pntAmountSum, couponAmount, pointAmount);
        orders.save();
        for (TldOrderItems orderItems : itemsList) {
            orderItems.save();
        }
        subtractStock(items);
        Map<String, String> packageParams = new HashMap<>();
        if (orders.getOrderTotal().compareTo(BigDecimal.ZERO) > 0) {
            try {
                packageParams = WeixinPayService.prePay(orderId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            orderPay(orderId, 0);
        }
        packageParams.put("orderId", orderId);
        packageParams.put("orderNo", orderNo);
        packageParams.put("orderTotal", orders.getOrderTotal().toString());
        packageParams.put("point", integralSelf.toString());
        return packageParams;
    }

    private TldOrderItems setItems(BasCustomer customer, String orderId, String orderNo, PntSku sku, PntProduct
            pntProduct, int quantity, BigDecimal amt) {
        TldOrderItems orderItems = new TldOrderItems();
        orderItems.setID(GUIDUtil.getGUID());
        orderItems.setConId(customer.getID());
        orderItems.setConNo(customer.getConNo());
        orderItems.setConName(customer.getConName());
        orderItems.setCreateDt(DateUtil.getNow());
        orderItems.setUpdateDt(DateUtil.getNow());
        orderItems.setOrderId(orderId);
        orderItems.setOrderNo(orderNo);
        orderItems.setPntId(sku != null ? sku.getProductId() : pntProduct.getID());
        orderItems.setPntName(pntProduct.getProductName());
        orderItems.setSkuId(sku != null ? sku.getID() : null);
        orderItems.setSkuName(sku != null ? sku.getSKU() : null);
        orderItems.setSalePrice(sku != null ? sku.getSalPrice() : pntProduct.getSalPrice());
        orderItems.setQUANTITY(quantity);
        orderItems.setAMOUNT(amt);
        return orderItems;
    }

    private TldOrders setOrders(TldOrders orders, String orderId, String orderNo, BasCustomer customer,
                                int integralSelf, int integralSup, BigDecimal pntAmountSum, BigDecimal couponAmount, BigDecimal pointAmount) {
        //重消优惠
        BigDecimal disAmount = BigDecimal.ZERO;
        if (customer.getConType() == 1) {
            String re_consume_rate = sysDictService.getByParamKey(RedisKeyDetail.RE_CONSUME_RATE);
            if (StrKit.notBlank(re_consume_rate)) {
                disAmount = pntAmountSum.multiply(new BigDecimal(re_consume_rate)).divide(new BigDecimal(10));
            }
        } else {
            disAmount = pntAmountSum;
        }
        orders.setID(orderId);
        orders.setOrderNo(orderNo);
        orders.setConNo(customer.getConNo());
        orders.setConName(customer.getConName());
        orders.setPIC(customer.getPIC());
        orders.setRpId(customer.getRpId());
        orders.setRpNo(customer.getRpNo());
        orders.setRpName(customer.getRpName());
        orders.setIsReorder(customer.getConType());
        orders.setCreateDt(DateUtil.getNow());
        orders.setUpdateDt(DateUtil.getNow());
        orders.setSTATUS(1);
        orders.setSelfIntegral(integralSelf);
        orders.setUp1Integral(integralSup);
        orders.setIntegralAmount(pointAmount);
        orders.setCouponAmount(couponAmount);
        BigDecimal orderTotal = disAmount.subtract(couponAmount).subtract(pointAmount).add(orders.getFREIGHT());
        //防止出现负数
        if (orderTotal.compareTo(BigDecimal.ZERO) < 1) {
            orderTotal = BigDecimal.ZERO;
        }
        orders.setOrderTotal(orderTotal);
        orders.setPntAmount(pntAmountSum);
        return orders;
    }

    public void subtractStock(String[] items) {
        Jedis cache = Redis.use().getJedis();
        try {
            for (String item : items) {
                String[] str = item.split("&");
                String productId = str[0];
                String skuId = str[1];
                int quantity = Integer.valueOf(str[2]);
                long rs;
                if (StrKit.notBlank(skuId)) {
                    rs = cache.decrBy(RedisKeyDetail.SKU_STOCK_ID + skuId, Long.valueOf(quantity));
                } else {
                    rs = cache.decrBy(RedisKeyDetail.PRODUCT_STOCK_ID + productId, Long.valueOf(quantity));
                }
                if (rs < 0) {
                    if (StrKit.notBlank(skuId)) {
                        cache.incrBy(RedisKeyDetail.SKU_STOCK_ID + skuId, Long.valueOf(quantity));
                    } else {
                        cache.incrBy(RedisKeyDetail.PRODUCT_STOCK_ID + productId, Long.valueOf(quantity));
                    }
                    throw new CustException("库存不足！");
                }
            }
        } finally {
            cache.close();
        }
    }

    public BigDecimal computeCouponAmount(String couponId, BigDecimal amount) {
        BigDecimal couponAmount = BigDecimal.ZERO;
        TldCouponGrant coupon = couponService.getById(couponId);
        if (coupon != null && coupon.getSTATUS() == 0) {
            String dateTime = DateUtil.getNow(DateUtil.DEFAULT_DATE_TIME_RFGFX);
            if (DateUtil.isTween(dateTime, coupon.getStartDt(), coupon.getEndDt(), DateUtil.DEFAULT_DATE_TIME_RFGFX)) {
                //直减
                if (coupon.getCouponType() == 1 && amount.compareTo(coupon.getSafetyAmount()) == -1) {
                    throw new CustException("该优惠券异常!");
                }
                couponAmount = coupon.getDiscountAmount();
            } else {
                throw new CustException("优惠券已过期!");
            }
        } else {
            throw new CustException("优惠券不存在!");
        }
        //设置优惠券为已使用
        coupon.setSTATUS(1);
        coupon.setUpdateDt(DateUtil.getNow());
        coupon.update();
        return couponAmount;
    }

    public BigDecimal computeFreight(String province, int num) {
        BigDecimal expFee = BigDecimal.ZERO;
        String exp_base_fee = sysDictService.getByParamKey(RedisKeyDetail.EXP_BASE_FEE);
        if (StrKit.notBlank(exp_base_fee)) {
            List<JSONObject> jsonObjects = JSON.parseArray(exp_base_fee, JSONObject.class);
            for (JSONObject json : jsonObjects) {
                if (json.getString("disctChn").equals(province)) {
                    expFee = json.getBigDecimal("expFee").multiply(new BigDecimal(num));
                }
            }
        }
        return expFee;
    }

    public String getOrderNoSeq() {
        Jedis jedis = Redis.use().getJedis();
        String str;//时间戳+补齐6位=18位
        try {
            String no = jedis.get(RedisKeyDetail.ORDER_NO_SEQ);
            if (StrKit.isBlank(no)) {
                setExpireTime(jedis);
            }
            long orderSeq = jedis.incr(RedisKeyDetail.ORDER_NO_SEQ);
            String date = DateUtil.getNow("yyyyMMddHHmm");
            str = date + String.format("%06d", orderSeq);
        } finally {
            jedis.close();
        }
        return str;
    }

    //设置初始订单流水号,并设置过期时间
    private synchronized void setExpireTime(Jedis jedis) {
        if (StrKit.notBlank(jedis.get(RedisKeyDetail.ORDER_NO_SEQ))) {
            return;
        }
        Date start = new Date();
        Date end = DateUtil.getDateByDay(start, 1);
        Long between = (end.getTime() - start.getTime()) / 1000;// 除以1000是为了转换成秒
        jedis.setex(RedisKeyDetail.ORDER_NO_SEQ, between.intValue(), "0");
    }

    public TldOrders getOrderById(String id) {
        return dao.findFirst("select " + COLUMN + " from tld_orders where ENABLED = 1 and id = ?", id);
    }

    /**
     * 收到付款逻辑
     *
     * @param orderId
     * @param payType 0微信支付,1线下支付
     */
    public void orderPay(String orderId, int payType) {
        TldOrders orders = getOrderById(orderId);
        BasCustomer customer = basCustomerService.getCustomerById(orders.getConId());
        if (orders.getSTATUS() == 1) {
            orders.setPaymentTypeid(String.valueOf(payType));
            orders.setPaymentType(payType == 0 ? "微信支付" : "线下支付");
            BasCustomerExt selfExt = extService.getByConId(orders.getConId());
            BasCustomerExt up1Ext = extService.getByConId(orders.getRpId());
            orders.setPayDt(DateUtil.getNow());
            //自己加积分
            int selfIntegral = orders.getSelfIntegral();
            if (selfIntegral > 0) {
                selfExt.setPointsTotal(selfExt.getPointsTotal() + selfIntegral);
                selfExt.setPointsUncfmd(selfExt.getPointsUncfmd() + selfIntegral);
            }
            //先注释,付款只加积分,收货确认加这些数据
          /*  //重消加自己营业总额,重消总额
            if (orders.getIsReorder() == 1) {
                selfExt.setREVENUES(selfExt.getREVENUES().add(orders.getOrderTotal()));
                selfExt.setReConsumptions(selfExt.getReConsumptions().add(orders.getOrderTotal()));
            }
            //首单+上级已购买
            if (orders.getIsReorder() == 0) {
                up1Ext.setPuredCustQty(up1Ext.getPuredCustQty() + 1);
            }
            //自己消费总额
            selfExt.setCONSUMPTIONS(selfExt.getCONSUMPTIONS().add(orders.getOrderTotal()));
            //自己订单总数量
            selfExt.setOrdersTotal(selfExt.getOrdersTotal() + 1);*/
            //上级+积分
            int up1Integral = up1Ext == null ? 0 : orders.getUp1Integral();
            if (up1Integral > 0) {
                up1Ext.setPointsTotal(up1Ext.getPointsTotal() + up1Integral);
                up1Ext.setPointsUncfmd(up1Ext.getPointsUncfmd() + up1Integral);
            }
           /* //上级+营业总额
            up1Ext.setREVENUES(selfExt.getREVENUES().add(orders.getOrderTotal()));
            //上级+推广订单总数
            up1Ext.setOrdersProm(up1Ext.getOrdersProm() + 1);*/

            //变更订单状态  回调回来变更状态?
            orders.setSTATUS(2);
            orders.update();
            split(orders);
            //7:加积分流水
            if (selfIntegral > 0) {
                insertPointTrans(selfExt.getConId(), selfExt.getConNo(), selfExt.getConName(), selfExt.getConId(), selfExt.getConNo(), selfExt.getConName(), orderId, orders.getOrderNo(), selfIntegral, 2, "下单成功返积分");
            }
            if (up1Integral > 0) {
                insertPointTrans(up1Ext.getConId(), up1Ext.getConNo(), up1Ext.getConName(), selfExt.getConId(), selfExt.getConNo(), selfExt.getConName(), orderId, orders.getOrderNo(), up1Integral, 2, "下单成功返积分");
            }
            int subscribePointsUp1 = 0;
            int subscribePointsSelf = 0;
            //如果是首单
            if (orders.getIsReorder() == 0) {
                String subscribe_points_up1 = sysDictService.getByParamKey(RedisKeyDetail.SUBSCRIBE_POINTS_UP1);
                //上级加关注推荐积分
                if (StrKit.notBlank(subscribe_points_up1)) {
                    if (up1Ext != null) {
                        subscribePointsUp1 = Integer.valueOf(subscribe_points_up1);
                        if (subscribePointsUp1 > 0) {
                            up1Ext.setPointsTotal(up1Ext.getPointsTotal() + subscribePointsUp1);
                            up1Ext.setPointsUncfmd(up1Ext.getPointsUncfmd() + subscribePointsUp1);
                            insertPointTrans(up1Ext.getConId(), up1Ext.getConNo(), up1Ext.getConName(), selfExt.getConId(), selfExt.getConNo(), selfExt.getConName(), orderId, orders.getOrderNo(), Integer.valueOf(subscribe_points_up1), 1, "新增会员积分");
                        }
                    }
                }
                //自己加关注推荐积分
                String subscribe_points_self = sysDictService.getByParamKey(RedisKeyDetail.SUBSCRIBE_POINTS_SELF);
                if (StrKit.notBlank(subscribe_points_self)) {
                    subscribePointsSelf = Integer.valueOf(subscribe_points_self);
                    if (Integer.valueOf(subscribePointsSelf) > 0) {
                        selfExt.setPointsTotal(selfExt.getPointsTotal() + subscribePointsSelf);
                        selfExt.setPointsUncfmd(selfExt.getPointsUncfmd() + subscribePointsSelf);
                        insertPointTrans(selfExt.getConId(), selfExt.getConNo(), selfExt.getConName(), selfExt.getConId(), selfExt.getConNo(), selfExt.getConName(), orderId, orders.getOrderNo(), Integer.valueOf(subscribe_points_self), 1, "新增会员积分");
                    }
                }
                Db.update("update bas_customer set con_type = 1 where id = ? ", customer.getID());
            }
            boolean r = selfExt.update();
            if (up1Ext != null) {
                boolean r1 = up1Ext.update();
            }
            //发送模板消息
            noticeService.getOrderPaySuccessNotice(customer.getID(), customer.getOPENID(), orders.getOrderNo(), orders.getPayDt(), String.valueOf(orders.getOrderTotal()), orders.getPaymentType());
            //自己积分变动消息模板
            if (selfIntegral > 0 || subscribePointsSelf > 0) {
                noticeService.getPointsNotice(customer.getID(), customer.getOPENID(), customer.getConName(), orders.getPayDt(), String.valueOf(selfIntegral + subscribePointsSelf), String.valueOf(selfExt.getPointsTotal()));
            }
            //上级积分变动消息模板
            if (up1Integral > 0 || subscribePointsUp1 > 0) {
                String opneId = Db.queryStr("select OPENID from bas_customer where ID = ?", customer.getRpId());
                noticeService.getRpPointsNotice(customer.getRpId(), opneId, customer.getRpName(), customer.getConNo(), orders.getPayDt(), String.valueOf(up1Integral + subscribePointsUp1), String.valueOf(up1Ext.getPointsTotal()));
            }
        }

    }

    public TldOrders cancelFirstOrder(TldOrders order) {
        if (0 == order.getIsReorder()) {
            String sql = "select id from tld_orders t where t.status in (1,2,3,5,6,7,8) and t.is_reorder=1 and t.con_id= ?";
            List<TldOrders> orderList = dao.find(sql, order.getConId());
            if (orderList.size() > 0)
                throw new CustException("要对首单申请退款，必须先对其它[已付款]订单申请退款，并且要删除所有[新增订单]。", new Throwable("首单取消-存在其他正常重消订单！"));
        }

        return order;
    }

    /**
     * 分单
     *
     * @param orders
     */
    private void split(TldOrders orders) {
        List<Record> orderItems = Db.find("select QUANTITY,PNT_ID,PNT_NAME,SKU_ID,SKU_NAME,SALE_PRICE from tld_order_items where order_id = ?", orders.getID());
        int splitNum = 1;//分单数量
        for (Record items : orderItems) {
            Integer quantity = items.getInt("QUANTITY");
            Integer tempNum = 0;
            if (quantity < splitNum) {
                inertOrderSplit(orders, items.getStr("PNT_ID"), items.getStr("PNT_NAME"), items.getStr("SKU_ID"), items.getStr("SKU_NAME"), splitNum, items.getBigDecimal("SALE_PRICE"));
            } else {
                while (quantity > tempNum) {
                    if (quantity - tempNum > splitNum) {
                        inertOrderSplit(orders, items.getStr("PNT_ID"), items.getStr("PNT_NAME"), items.getStr("SKU_ID"), items.getStr("SKU_NAME"), splitNum, items.getBigDecimal("SALE_PRICE"));
                    } else {
                        inertOrderSplit(orders, items.getStr("PNT_ID"), items.getStr("PNT_NAME"), items.getStr("SKU_ID"), items.getStr("SKU_NAME"), splitNum, items.getBigDecimal("SALE_PRICE"));
                    }
                    tempNum += splitNum;
                }
            }
        }
    }

    private void inertOrderSplit(TldOrders orders, String pntId, String pntName, String skuId, String skuName,
                                 int splitNum, BigDecimal price) {
        TldOrderSplit split = new TldOrderSplit();
        split.setID(GUIDUtil.getGUID());
        split.setOrderId(orders.getID());
        split.setOrderNo(orders.getOrderNo());
        split.setConId(orders.getConId());
        split.setConNo(orders.getConNo());
        split.setConName(orders.getConName());
        split.setPntId(pntId);
        split.setPntName(pntName);
        split.setSkuId(skuId);
        split.setSkuName(skuName);
        split.setSplitNumber(splitNum);
        split.setCOUNTRY(orders.getCOUNTRY());
        split.setPROVINCE(orders.getPROVINCE());
        split.setCITY(orders.getCITY());
        split.setDISTRICT(orders.getDISTRICT());
        split.setADDRESS(orders.getADDRESS());
        split.setPostalCode(orders.getPostalCode());
        split.setMOBILE(orders.getMOBILE());
        split.setSalePrice(price);
        split.setRECIPIENTS(orders.getRECIPIENTS());
        split.setSTATUS(orders.getSTATUS());
        split.setREMARK(orders.getREMARK());
        split.setCreateDt(DateUtil.getNow());
        split.setUpdateDt(DateUtil.getNow());
        split.save();
    }

    /**
     * 新增积分流水
     *
     * @param conId
     * @param conNo
     * @param conName
     * @param fromConId
     * @param fromConNo
     * @param fromConName
     * @param orderId
     * @param orderNo
     * @param pointsQty
     * @param pointsType
     * @param remark
     */
    private void insertPointTrans(String conId, String conNo, String conName, String fromConId, String
            fromConNo, String fromConName, String orderId, String orderNo, Integer pointsQty, Integer pointsType, String
                                          remark) {
        BasCustPointTrans trans = new BasCustPointTrans();
        trans.setID(GUIDUtil.getGUID());
        trans.setConId(conId);
        trans.setConNo(conNo);
        trans.setConName(conName);
        trans.setFromConId(fromConId);
        trans.setFromConNo(fromConNo);
        trans.setFromConName(fromConName);
        trans.setFromOrderId(orderId);
        trans.setFromOrderNo(orderNo);
        trans.setPointsQty(pointsQty);
        trans.setPointsType(pointsType);
        trans.setREMARK(remark);
        trans.setCreateDt(DateUtil.getNow());
        trans.setUpdateDt(DateUtil.getNow());

        trans.save();
    }

    public List<Record> getOrderList(int pageNumber, int pageSize, String conId, Integer status) {
        StringBuffer sqlExceptSelect = new StringBuffer(" from tld_orders where ENABLED = 1 and status !=12 and con_id = '" + conId + "'");
        if (status != null) {
            //待收货应该包含 已打印和配送中
            if (status == 6) {
                sqlExceptSelect.append(" and status in(5,6) ");
            } else {
                sqlExceptSelect.append(" and status = " + status);
            }

        }
        sqlExceptSelect.append(" order by  CREATE_DT desc ");
        Page<Record> tldOrdersPage = Db.paginate(pageNumber, pageSize, "select " + COLUMN + "", sqlExceptSelect.toString());
        for (Record order : tldOrdersPage.getList()) {
            String orderId = order.get("ID");
            List<Record> items = Db.find("select t1.*,t2.THUMB_URL from tld_order_items t1 left join pnt_sku t2 on t1.sku_id = t2.id where t1.ENABLED = 1 and t1.order_id = ?", orderId);
            order.set("ITEMS", items);
        }
        return tldOrdersPage.getList();
    }

    /**
     * 将已评论的订单改为已评论
     */
    public void updateOrderStatus13(String conId) {
        List<Record> list = Db.find("select " + COLUMN + " from tld_orders where ENABLED = 1 and status=7 and con_id = ?", conId);
        for (Record order : list) {
            String orderId = order.get("ID");
            Integer count = Db.queryInt("select count(1) from tld_order_items where COMMENT_TAG = 0 and order_id = ? ", orderId);
            if (count == null || count == 0) {
                Db.update("update tld_orders set status = 13 where id = ?", orderId);
            }
        }
    }

    public Record getOrderStatusNum(String conId) {
        Record record = Db.findFirst("SELECT " +
                " sum(CASE STATUS WHEN 1 THEN 1 ELSE 0 END ) 'DFK'," +
                " sum(CASE WHEN STATUS IN (5, 6) THEN 1 ELSE 0 END ) 'DSH', " +
                " sum(CASE STATUS WHEN 7 THEN 1 ELSE 0 END ) 'DPJ' " +
                "FROM tld_orders where con_id = ?", conId);
        return record;
    }

    public Record getOrderItems(String orderId) {
        Record record = Db.findFirst("select " + COLUMN + " from tld_orders where ENABLED = 1 and id = ?", orderId);
        record.set("ITEMS", Db.find("select t1.*,t2.THUMB_URL from tld_order_items t1 left join pnt_sku t2 on t1.sku_id = t2.id where t1.ENABLED = 1 and t1.order_id = ?", orderId));
        return record;
    }

    public List<Record> getOrderSplit(String orderId) {
        return Db.find("select EXP_COMPANY_ID,EXP_COMPANY_NAME,WAYBILL from tld_order_split  where ENABLED = 1 and order_id = ?", orderId);
    }

    public boolean deleteOrder(String orderId) {
        TldOrders orders = getOrderById(orderId);
        //如果是已关闭的订单删除只需要改状态
        if (orders.getSTATUS() == 11) {
            Db.update("update tld_orders set STATUS = 12,VERSION = VERSION +1,UPDATE_DT = ?,REMARK = ? where id = ?", DateUtil.getNow(), "", orderId);
        } else {
            String couponGrantId = orders.getCouponGrantId();
            String conId = orders.getConId();
            if (StrKit.notBlank(couponGrantId)) {
                Db.update("update tld_coupon_grant set STATUS = 0 where id = ?", couponGrantId);
            }
            //查询该订单是否使用积分抵扣
            Record pointGrans = Db.findFirst("select ID,POINTS_QTY from bas_cust_point_trans where POINTS_TYPE = 3 and FROM_ORDER_ID = ?", orderId);
            if (pointGrans != null) {
                int point = pointGrans.getInt("POINTS_QTY");
                Db.update("update bas_customer_ext set POINTS_ENABLED=POINTS_ENABLED+? where con_id = ?", point, conId);
                Db.delete("delete FROM  bas_cust_point_trans where id = ?", pointGrans.getStr("ID"));
            }
            String remark = "";
            Db.update("update tld_orders set STATUS = 12,VERSION = VERSION +1,UPDATE_DT = ?,REMARK = ? where id = ?", DateUtil.getNow(), remark, orderId);
            //还原库存
            List<Record> itemsList = Db.find("select SKU_ID,PNT_ID,QUANTITY from tld_order_items where order_id = ?", orderId);
            Jedis cache = Redis.use().getJedis();
            try {
                for (Record items : itemsList) {
                    incrStock(cache, items);
                }
            } finally {
                cache.close();
            }
        }
        return true;
    }

    public boolean refund(String orderId) {
        TldOrders orders = getOrderById(orderId);
        cancelFirstOrder(orders);
        String couponGrantId = orders.getCouponGrantId();
        String conId = orders.getConId();
        if (StrKit.notBlank(couponGrantId)) {
            Db.update("update tld_coupon_grant set STATUS = 0 where id = ?", couponGrantId);
        }
        //查询该订单是否使用积分抵扣
        Record pointGrans = Db.findFirst("select ID,POINTS_QTY from bas_cust_point_trans where POINTS_TYPE = 3 and FROM_ORDER_ID = ?", orderId);
        if (pointGrans != null) {
            int point = pointGrans.getInt("POINTS_QTY");
            Db.update("update bas_customer_ext set POINTS_ENABLED=POINTS_ENABLED+? where con_id = ?", point, conId);
            Db.delete("delete FROM  bas_cust_point_trans where id = ?", pointGrans.getStr("ID"));
        }

        List<BasCustPointTrans> list = pointsService.getByOrderIdAndType(orderId);
        int selfIntegral = 0;
        int up1Integral = 0;
        for (BasCustPointTrans pointTrans : list) {
            if (pointTrans.getPointsType() == 1 || pointTrans.getPointsType() == 2) {
                if (pointTrans.getConId().equals(orders.getConId())) {
                    selfIntegral += pointTrans.getPointsQty();
                }
                if (pointTrans.getConId().equals(orders.getRpId())) {
                    up1Integral += pointTrans.getPointsQty();
                }
                insertPointTrans(pointTrans.getConId(), pointTrans.getConNo(), pointTrans.getConName(), pointTrans.getFromConId(), pointTrans.getFromConNo(), pointTrans.getFromConName(), orderId, orders.getOrderNo(), -pointTrans.getPointsQty(), pointTrans.getPointsType(), "订单退款,积分红冲");
            }
        }

        if (selfIntegral > 0) {
            //-总积分和未确认积分
            Db.update("update bas_customer_ext set POINTS_TOTAL=POINTS_TOTAL-?,POINTS_UNCFMD=POINTS_UNCFMD-? where con_id = ?", selfIntegral, selfIntegral, orders.getConId());
        }
        if (up1Integral > 0) {
            //-总积分和未确认积分
            Db.update("update bas_customer_ext set POINTS_TOTAL=POINTS_TOTAL-?,POINTS_UNCFMD=POINTS_UNCFMD-? where con_id = ?", up1Integral, up1Integral, orders.getRpId());
        }

        String remark = "未发货已退款";
        Db.update("update tld_orders set STATUS = 10,VERSION = VERSION +1,UPDATE_DT = ?,REMARK = ? where id = ?", DateUtil.getNow(), remark, orderId);


        String orderTotal = String.valueOf(orders.getOrderTotal().multiply(new BigDecimal(100)).intValue());
        if (orders.getPaymentTypeid().equals("0")) {
            WeixinPayService.refund(orderId, orderTotal, orderTotal);
        }
        if (orders.getIsReorder() == 0) {
            Db.update("update bas_customer set con_type = 0 where id = ?", orders.getConId());
        }
        //还原库存
        List<Record> itemsList = Db.find("select SKU_ID,PNT_ID,QUANTITY from tld_order_items where order_id = ?", orderId);
        Jedis cache = Redis.use().getJedis();
        try {
            for (Record items : itemsList) {
                incrStock(cache, items);
            }
        } finally {
            cache.close();
        }
        return true;
    }

    private void incrStock(Jedis cache, Record items) {
        final String skuId = items.getStr("SKU_ID");
        final String productId = items.getStr("PNT_ID");
        long quantity = Long.valueOf(items.getInt("QUANTITY"));
        // 有sku的回收sku库存，没有的回收产品库存
        if (StrKit.notBlank(skuId)) {
            cache.incrBy(RedisKeyDetail.SKU_STOCK_ID + skuId, quantity);
        } else {
            cache.incrBy(RedisKeyDetail.PRODUCT_STOCK_ID + productId, quantity);
        }
    }

    public boolean confirmOrder(String orderId) {
        String dateTime = DateUtil.getNow();
        TldOrders orders = getOrderById(orderId);
        if (orders == null) {
            throw new CustException("找不到订单信息");
        }
        if (orders.getSTATUS() == 5 || orders.getSTATUS() == 6) {
            BasCustomerExt selfExt = extService.getByConId(orders.getConId());
            BasCustomerExt up1Ext = extService.getByConId(orders.getRpId());
            //自己加积分
            int selfIntegral = 0;
            //上级+积分
            int up1Integral = 0;
            //查询订单流水
            List<BasCustPointTrans> list = pointsService.getByOrderIdAndType(orderId);
            for (BasCustPointTrans pointTrans : list) {
                if (pointTrans.getPointsType() == 1 || pointTrans.getPointsType() == 2) {
                    if (pointTrans.getConId().equals(orders.getConId())) {
                        selfIntegral += pointTrans.getPointsQty();
                    }
                    if (pointTrans.getConId().equals(orders.getRpId())) {
                        up1Integral += pointTrans.getPointsQty();
                    }
                }
            }
            //如果返了自身积分,确认收货之后,+已确认,-未确认,+可用
            if (selfIntegral > 0) {
                selfExt.setPointsUncfmd(selfExt.getPointsUncfmd() - selfIntegral);
                selfExt.setPointsCfmd(selfExt.getPointsCfmd() + selfIntegral);
                selfExt.setPointsEnabled(selfExt.getPointsEnabled() + selfIntegral);
            }
            //重消加自己营业总额,重消总额
            if (orders.getIsReorder() == 1) {
                selfExt.setREVENUES(selfExt.getREVENUES().add(orders.getOrderTotal()));
                selfExt.setReConsumptions(selfExt.getReConsumptions().add(orders.getOrderTotal()));
            }

            //自己消费总额
            selfExt.setCONSUMPTIONS(selfExt.getCONSUMPTIONS().add(orders.getOrderTotal()));
            //自己订单总数量
            selfExt.setOrdersTotal(selfExt.getOrdersTotal() + 1);

            //如果返了上级积分,确认收货之后,+已确认,-未确认,+可用
            if (up1Integral > 0) {
                if (up1Ext != null) {
                    up1Ext.setPointsUncfmd(up1Ext.getPointsUncfmd() - up1Integral);
                    up1Ext.setPointsCfmd(up1Ext.getPointsCfmd() + up1Integral);
                    up1Ext.setPointsEnabled(up1Ext.getPointsEnabled() + up1Integral);
                }
            }
            orders.setConfirmDt(dateTime);
            orders.setUpdateDt(dateTime);
            orders.setSTATUS(7);
            orders.update();
            Db.update("update tld_order_split set status = 7,VERSION=VERSION+1,UPDATE_DT = ? where order_id = ?", dateTime, orderId);
            selfExt.setUpdateDt(dateTime);
            selfExt.update();
            if (up1Ext != null) {
                //首单+上级已购买会员
                if (orders.getIsReorder() == 0) {
                    up1Ext.setPuredCustQty(up1Ext.getPuredCustQty() + 1);
                }
                //上级+营业总额
                up1Ext.setREVENUES(selfExt.getREVENUES().add(orders.getOrderTotal()));
                //上级+推广订单总数
                up1Ext.setOrdersProm(up1Ext.getOrdersProm() + 1);
                up1Ext.setUpdateDt(dateTime);
                up1Ext.update();
            }
            String opneId = Db.queryStr("select OPENID from bas_customer where ID = ?", orders.getConId());
            noticeService.sendOrderReceivedNotice(orders.getConId(), opneId, orders.getOrderNo(), orders.getConfirmDt(), String.valueOf(orders.getOrderTotal()));
        } else {
            throw new CustException("未出库订单不能确认收货");
        }
        return true;
    }
}
