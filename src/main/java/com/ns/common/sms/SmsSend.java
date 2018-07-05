/**
 * project name: ns-api
 * file name:SmsSend
 * package name:com.ns.common.sms
 * date:2018-04-14 16:39
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.common.sms;

import com.alibaba.fastjson.JSON;

import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
 * description: //TODO <br>
 * date: 2018-04-14 16:39
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class SmsSend {
    public static final String charset = "utf-8";
    // 用户平台API账号(非登录账号,示例:N1234567)
    public static String account = "N8437461";
    // 用户平台API密码(非登录密码)
    public static String pswd = "l3vFn9jKz";

    public static void main(String[] args) throws UnsupportedEncodingException {

        //请求地址请登录253云通讯自助通平台查看或者询问您的商务负责人获取
        String smsSingleRequestServerUrl = "http://smssh1.253.com/msg/send/json";
        // 短信内容
        String msg = "【弘德苑】你好,你的验证码是159357";
        //手机号码
        String phone = "18890363492";
        //状态报告
        String report = "true";

        SmsSendRequest smsSingleRequest = new SmsSendRequest(account, pswd, msg, phone, report);

        String requestJson = JSON.toJSONString(smsSingleRequest);

        System.out.println("before request string is: " + requestJson);

        String response = SmsUtil.sendSmsByPost(smsSingleRequestServerUrl, requestJson);

        System.out.println("response after request result is :" + response);

        SmsSendResponse smsSingleResponse = JSON.parseObject(response, SmsSendResponse.class);

        System.out.println("response  toString is :" + smsSingleResponse);

    }

    public static SmsSendResponse sendSms(String phone, String code, Integer validTime) {
        //请求地址请登录253云通讯自助通平台查看或者询问您的商务负责人获取
        String smsSingleRequestServerUrl = "http://smssh1.253.com/msg/send/json";
        // 短信内容
        String msg = "【咪之猫】您好,您的验证码是".concat(code) + "有效期为" + validTime + "分钟!";
        //手机号码

        //状态报告
        String report = "true";

        SmsSendRequest smsSingleRequest = new SmsSendRequest(account, pswd, msg, phone, report);

        String requestJson = JSON.toJSONString(smsSingleRequest);

        System.out.println("before request string is: " + requestJson);

        String response = SmsUtil.sendSmsByPost(smsSingleRequestServerUrl, requestJson);

        System.out.println("response after request result is :" + response);

        SmsSendResponse smsSingleResponse = JSON.parseObject(response, SmsSendResponse.class);
        return smsSingleResponse;

    }

}
