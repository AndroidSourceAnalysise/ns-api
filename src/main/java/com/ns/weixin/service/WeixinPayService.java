/**
 * project name: ns-api
 * file name:WeixinPayService
 * package name:com.ns.weixin.service
 * date:2018-03-19 9:55
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.weixin.service;

import com.ns.common.exception.CustException;
import com.ns.common.utils.GUIDUtil;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.weixin.sdk.api.PaymentApi;
import com.jfinal.weixin.sdk.kit.IpKit;
import com.jfinal.weixin.sdk.kit.PaymentKit;
import com.jfinal.weixin.sdk.utils.JsonUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * description: //TODO <br>
 * date: 2018-03-19 9:55
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class WeixinPayService {

    //商户相关资料
    private static String appid = PropKit.get("appId");
    private static String partner = PropKit.get("partner");
    private static String paternerKey = PropKit.get("paternerKey");
    private static String notify_url = PropKit.get("notify_url");
    private static String cert_path = PropKit.get("certPath");

    /**
     * 公众号支付js-sdk
     */
    public static Map<String, String> prePay(String orderId) {
        // openId，采用 网页授权获取 access_token API：SnsAccessTokenApi获取
        Record record = Db.findFirst("select ID,CON_ID,ORDER_NO,ORDER_TOTAL,STATUS from tld_orders where id = ?", orderId);
        if (record == null || record.getInt("STATUS") != 1) {
            throw new CustException("订单数据异常或订单不是未付款状态!");
        }

        String openId = Db.queryStr("select OPENID from bas_customer where id = ?", record.getStr("CON_ID"));

        BigDecimal totalFee = record.getBigDecimal("ORDER_TOTAL").multiply(new BigDecimal(100));

        // 统一下单文档地址：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_1

        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", appid);
        params.put("mch_id", partner);
        params.put("body", "订单编号:" + record.getStr("ORDER_NO"));
        params.put("out_trade_no", record.getStr("ID"));
        params.put("total_fee", totalFee.intValue() + "");

        String ip = "";//IpKit.getRealIp(request);
        if (StrKit.isBlank(ip)) {
            ip = "127.0.0.1";
        }

        params.put("spbill_create_ip", ip);
        params.put("trade_type", PaymentApi.TradeType.JSAPI.name());
        params.put("nonce_str", System.currentTimeMillis() / 1000 + "");
        params.put("notify_url", notify_url);
        params.put("openid", openId);

        String sign = PaymentKit.createSign(params, paternerKey);
        params.put("sign", sign);
        String xmlResult = PaymentApi.pushOrder(params);

        System.out.println(xmlResult);
        Map<String, String> result = PaymentKit.xmlToMap(xmlResult);

        String return_code = result.get("return_code");
        String return_msg = result.get("return_msg");
        if (StrKit.isBlank(return_code) || !"SUCCESS".equals(return_code)) {
            throw new CustException(return_msg);
        }
        String result_code = result.get("result_code");
        if (StrKit.isBlank(result_code) || !"SUCCESS".equals(result_code)) {
            throw new CustException(return_msg);
        }
        // 以下字段在return_code 和result_code都为SUCCESS的时候有返回
        String prepay_id = result.get("prepay_id");

        Map<String, String> packageParams = new HashMap<String, String>();
        packageParams.put("appId", appid);
        packageParams.put("timeStamp", System.currentTimeMillis() / 1000 + "");
        packageParams.put("nonceStr", System.currentTimeMillis() + "");
        packageParams.put("package", "prepay_id=" + prepay_id);
        packageParams.put("signType", "MD5");
        String packageSign = PaymentKit.createSign(packageParams, paternerKey);
        packageParams.put("paySign", packageSign);
        //String jsonStr = JsonUtils.toJson(packageParams);
        return packageParams;


    }

    public static void refund(String orderId, String totalFee, String refundFee) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", appid);
        params.put("mch_id", partner);
        params.put("out_trade_no", orderId);
        params.put("out_refund_no", orderId);
        params.put("total_fee", totalFee);
        params.put("refund_fee", refundFee);
        Map<String, String> result = PaymentApi.refund(params, paternerKey, cert_path);
        if ("SUCCESS".equals(result.get("return_code"))) {
            if (!"SUCCESS".equals(result.get("result_code"))) {
                throw new CustException(result.get("err_code_des"));
            }
        } else {
            throw new CustException(result.get("return_msg"));
        }
    }
}
