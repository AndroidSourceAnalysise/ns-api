/**
 * project name: ns-api
 * file name:HttpSender
 * package name:com.ns.common.sms
 * date:2018-04-14 18:24
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.common.sms;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLDecoder;

/**
 * description: //TODO <br>
 * date: 2018-04-14 18:24
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class HttpSender {
        public static final String url = "http://zapi.253.com/msg/HttpBatchSendSM";// 应用地址
        public static final String account = "V0467334";// 账号
        public static final String pswd = "zTyOKBQpJr88fe";// 密码
        public static final boolean needstatus = true;// 是否需要状态报告，需要true，不需要false
        public static final String extno = null;// 扩展码

        /**
         * 发送语音验证码
         *
         * @param code   验证码
         * @param mobile 手机号码，多个号码使用","分割
         * @return 返回值定义参见HTTP协议文档
         * @throws Exception
         */
        public static String batchSend(String mobile, String code) throws Exception {
            String msg = "您好，您的验证码是:".concat(code);// 短信内容
            HttpClient client = new HttpClient(new HttpClientParams(), new SimpleHttpConnectionManager(true));
            GetMethod method = new GetMethod();
            try {
                URI base = new URI(url, false);
                method.setURI(new URI(base, "HttpBatchSendSM", false));
                method.setQueryString(new NameValuePair[]{
                        new NameValuePair("account", account),
                        new NameValuePair("pswd", pswd),
                        new NameValuePair("mobile", mobile),
                        new NameValuePair("needstatus", String.valueOf(needstatus)),
                        new NameValuePair("msg", msg),
                        new NameValuePair("extno", extno),
                });
                int result = client.executeMethod(method);
                if (result == HttpStatus.SC_OK) {
                    InputStream in = method.getResponseBodyAsStream();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = in.read(buffer)) != -1) {
                        baos.write(buffer, 0, len);
                    }
                    return URLDecoder.decode(baos.toString(), "UTF-8");
                } else {
                    throw new Exception("HTTP ERROR Status: " + method.getStatusCode() + ":" + method.getStatusText());
                }
            } finally {
                method.releaseConnection();
            }
        }
}
