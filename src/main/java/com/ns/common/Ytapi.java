package com.ns.common;

import com.ns.common.exception.CustException;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Ytapi {

    public static final String GBK = "GBK";
    public static final String UTF8 = "UTF-8";

    public static void main(String[] args) {
        System.out.println(ytPost("807116837376"));
    }

    public static String ytPost(String no) {
        String date = nowDateTimeToString();
        String str = "lvyCJPapp_keyJyzyWtformatJSONmethodyto.Marketing.WaybillTracetimestamp" + date + "user_idhongdeyuanv1.01";
        String sign = "sign=" + getMD5(str)
                + "&app_key=JyzyWt&format=JSON&method=yto.Marketing.WaybillTrace&timestamp=" + date
                + "&user_id=hongdeyuan&v=1.01&param=[{\"Number\":\"" + no + "\"}]";
        try {
            return post("http://MarketingInterface.yto.net.cn", UTF8, sign);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 发送POST 请求
     *
     * @param url     请求地址
     * @param charset 编码格式
     * @param params  请求参数
     * @return 响应
     * @throws IOException
     */
    public static String post(String url, String charset, String params) throws IOException {
        HttpURLConnection conn = null;
        OutputStreamWriter out = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer result = new StringBuffer();
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setConnectTimeout(19 * 1000);
            conn.setReadTimeout(19 * 1000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("accept", "*/*");
//            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Accept-Charset", charset);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            out = new OutputStreamWriter(conn.getOutputStream(), charset);
            out.write(params);
            out.flush();
            inputStream = conn.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream, charset);
            reader = new BufferedReader(inputStreamReader);
            String tempLine = null;
            while ((tempLine = reader.readLine()) != null) {
                result.append(tempLine);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (null != conn)
                conn.disconnect();
        }
        return result.toString();
    }

    /**
     * MD5加密
     *
     * @param message 要进行MD5加密的字符串
     * @return 加密结果为32位字符串
     */
    public static String getMD5(String message) {
        MessageDigest messageDigest = null;
        StringBuffer md5StrBuff = new StringBuffer();
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(message.getBytes("UTF-8"));

            byte[] byteArray = messageDigest.digest();
            for (int i = 0; i < byteArray.length; i++) {
                if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                    md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
                else
                    md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return md5StrBuff.toString().toUpperCase();// 字母大写
    }

    public static String nowDateTimeToString() {
        return new SimpleDateFormat("yyyy-M-d HH:mm:ss").format(new Date());
    }


    /**
     * 通过圆通APi，获取圆通电子面单号
     * @param clientId 圆通分配的客户id
     * @param parternId
     * @param actionUrl
     * @param xmlParams
     * @return
     */
    public static String getYTOMailNo(String clientId, String parternId, String actionUrl, String xmlParams) {
        String rtn = "";
        try {
            // 加密
            MessageDigest messagedigest = MessageDigest.getInstance("MD5");
            messagedigest.update((xmlParams + parternId).getBytes("UTF-8"));
            byte[] abyte0 = messagedigest.digest();
            String data_digest = new String(Base64.encodeBase64(abyte0));

            // url 参数拼接
            String params = "logistics_interface=" + URLEncoder.encode(xmlParams, "UTF-8")
                    + "&data_digest=" + URLEncoder.encode(data_digest, "UTF-8") + "&clientId="
                    + URLEncoder.encode(clientId, "UTF-8");

            rtn = post(actionUrl, "UTF-8", params);
        } catch (Exception e) {
            throw new CustException(e.getMessage());
        }

        return rtn;
    }
}
