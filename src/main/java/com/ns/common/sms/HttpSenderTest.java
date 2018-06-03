package com.ns.common.sms;


public class HttpSenderTest {
    public static void main(String[] args) {
        String mobile = "18890363492";// 手机号码，多个号码使用","分割
        String msg = "123456";// 短信内容

        try {
            String returnString = HttpSender.batchSend(mobile, msg);
            int begin = returnString.indexOf(",")+1;
            int end = returnString.indexOf("\n");
            String result = returnString.substring(begin, end);
            String[] re = returnString.split("\n");
            System.out.println(returnString);
            System.out.println(re[2]);
            // TODO 处理返回值,参见HTTP协议文档
        } catch (Exception e) {
            // TODO 处理异常
            e.printStackTrace();
        }
    }
}
