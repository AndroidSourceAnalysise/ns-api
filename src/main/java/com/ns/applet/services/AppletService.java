package com.ns.applet.services;

import com.alibaba.fastjson.JSON;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.ns.common.exception.CustException;
import com.ns.common.utils.Util;
import com.ns.customer.service.BasCustomerService;
import org.apache.commons.codec.binary.Base64;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AppletService {
    // https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code
    private static final AppletService INSTANCE = new AppletService();

    private static final String CODE = PropKit.get("code");
    private static final String APPID = PropKit.get("sappId");
    private static final String APPSECRET = PropKit.get("sappSecret");

    private AppletService() {

    }

    public static AppletService getInstance() {
        return INSTANCE;
    }

    public Object saveCustomerInfo(String sk, String body) {
        Map params = JSON.parseObject(body, HashMap.class);
        final String rawData = (String) params.get("rawData");
        final String signature = (String) params.get("signature");
        final String sessionKey = Redis.use().get(sk);
        final String sg = Util.sha1(rawData + sessionKey);
        if (!signature.equalsIgnoreCase(sg)) {
            throw new CustException("数据检验失败!");
        }
        final String encryptedData = (String) params.get("encryptedData");
        final String iv = (String) params.get("iv");
        try {
            final byte[] bed = Base64.decodeBase64(encryptedData.getBytes("UTF-8"));
            final byte[] biv = Base64.decodeBase64(iv.getBytes("UTF-8"));
            final byte[] bsessionKey = Base64.decodeBase64(sessionKey.getBytes("UTF-8"));
            final byte[] data = Util.AES_CBC_Decrypt(bed, bsessionKey, biv);
            final String decrtptedData = new String(data, "UTF-8");
            Map rs = JSON.parseObject(decrtptedData, HashMap.class);
            final String watermark = (String) rs.get("watermark");
            Map mwa = JSON.parseObject(watermark, HashMap.class);
            // wx083da9de81f15187
            final String appid = (String) mwa.get("appid");
            if (APPID.equalsIgnoreCase(appid)) {
                throw new CustException("数据水印检验失败!");
            }
            BasCustomerService bcs = BasCustomerService.me;
            final String unionId = (String) rs.get("unionId");
            String conId = bcs.isExistCustomerByUnionId(unionId);
            final String openId = (String) rs.get("OPENID");
            final String nickName = (String) rs.get("nickName");
            final int gender = (int) rs.get("gender");
            final String city = (String) rs.get("city");
            final String province = (String) rs.get("province");
            final String country = (String) rs.get("country");
            final String avatarUrl = (String) rs.get("avatarUrl");
            // 如果不存在就添加会员信息，存在就更新会员信息
            if (StrKit.isBlank(conId)) {
                conId = bcs.appletAddCustomer(nickName, avatarUrl, gender, "", country, province, city, unionId);
            } else {
                bcs.updateAppletCustomer(nickName, avatarUrl, gender, "", country, province, city, conId);
            }
            Map response = new HashMap();
            response.put("con_id", conId);
            return response;
        } catch (Throwable throwable) {
            throw new CustException("数据解析失败!");
        }
    }


    /**
     * 小程序登录
     *
     * @param code
     * @return
     */
    public Object login(String code, String oldSk) {
        try {
            String rs = HttpKit.get(CODE.replace("APPID", APPID).replace("SECRET", APPSECRET).replace("JSCODE", code));
            Map result = JSON.parseObject(rs, HashMap.class);
            if (rs.contains("errcode")) {
                throw new CustException((int) result.get("errcode"), (String) result.get("errmsg"));
            }
            String openId = (String) result.get("openid");
            String sessionKey = (String) result.get("session_key");
            String unionid = (String) result.get("unionid");
            // 如果开发者帐号下存在同主体的公众号，并且该用户已经关注了该公众号。开发者可以直接通过wx.login获取到该用户UnionID，无须用户再次授权。
            // 如果开发者帐号下存在同主体的公众号或移动应用，并且该用户已经授权登录过该公众号或移动应用。开发者也可以直接通过wx.login获取到该用户UnionID，无须用户再次授权。
            // 调用接口wx.getUserInfo，从解密数据中获取UnionID。注意本接口需要用户授权，请开发者妥善处理用户拒绝授权后的情况。
            String sk = generateSession(sessionKey, oldSk);
            HashMap<String, Object> response = new HashMap<>();
            response.put("sk", sk);
            String conId = BasCustomerService.me.isExistCustomerByUnionId(unionid);
            response.put("con_id", conId);
            return response;
        } catch (Throwable throwable) {
            throw new CustException(-1, "网络链接异常!");
        }

    }

    private String generateSession(String sessionKey, String oldSK) {
        String sk = UUID.randomUUID().toString();
        Cache cache = Redis.use();
        long i = cache.del(oldSK);
        String result = cache.set(sk, sessionKey);
        return sk;
    }
}
