package com.ns.applet.services;

import com.alibaba.fastjson.JSON;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.ns.common.constant.RedisKeyDetail;
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

    public boolean saveCustomerInfo(String sk, String body) {
        Map params = JSON.parseObject(body, HashMap.class);
        final String rawData = (String) params.get("rawData");
        final String signature = (String) params.get("signature");
        final String sessionKey = (String) Redis.use().hmget(sk, RedisKeyDetail.SESSION_KEY).get(0);
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
            final com.alibaba.fastjson.JSONObject watermark = (com.alibaba.fastjson.JSONObject) rs.get("watermark");
            // wx083da9de81f15187
            final String appid = (String) watermark.get("appid");
            if (!(APPID.equalsIgnoreCase(appid))) {
                throw new CustException("数据水印检验失败!");
            }
            BasCustomerService bcs = BasCustomerService.me;
            // o6Xwh1gCKLQFarU2mBrr6gcscrUs
            final String unionId = (String) rs.get("unionId");
            String conId = bcs.isExistCustomerByUnionId(unionId);
            final String openId = (String) rs.get("openId");
            final String nickName = (String) rs.get("nickName");
            final int gender = (int) rs.get("gender");
            final String city = (String) rs.get("city");
            final String province = (String) rs.get("province");
            final String country = (String) rs.get("country");
            final String avatarUrl = (String) rs.get("avatarUrl");
            // 如果不存在就添加会员信息，存在就更新会员信息
            if (StrKit.isBlank(conId)) {
                conId = bcs.appletAddCustomer(nickName, avatarUrl, gender, "", country, province, city, unionId, openId);
            } else {
                bcs.updateAppletCustomer(nickName, avatarUrl, gender, "", country, province, city, conId);
            }
            final String rConId = (String) Redis.use().hmget(sk, RedisKeyDetail.CON_ID).get(0);
            if (StrKit.isBlank(rConId) || !(rConId.equalsIgnoreCase(conId))) {
                Redis.use().hset(sk, RedisKeyDetail.CON_ID, conId);
            }
            return true;
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
        String rs;
        try {
            rs = HttpKit.get(CODE.replace("APPID", APPID).replace("SECRET", APPSECRET).replace("JSCODE", code));
        } catch (Throwable throwable) {
            throw new CustException("网络连接异常!");
        }
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
        HashMap<String, Object> response = new HashMap<>();
        String conId = BasCustomerService.me.isExistCustomerByUnionId(unionid);
        String sk = generateSession(sessionKey, oldSk, conId);
        response.put("sk", sk);
        boolean isRegistered = StrKit.notBlank(conId);
        // 如果会员已经注册过了，则更新会员的小程序openId
        if (isRegistered) {
            BasCustomerService.me.updateAppletCustomer(openId, conId);
        }
        response.put("isRegistered", isRegistered);
        return response;


    }

    private String generateSession(String sessionKey, String oldSK, String conId) {
        String sk = UUID.randomUUID().toString();
        Cache cache = Redis.use();
        if (StrKit.notBlank(oldSK)) {
            long i = cache.del(oldSK);
            System.out.println(i);
        }
        Map rm = new HashMap();
        rm.put(RedisKeyDetail.SESSION_KEY, sessionKey);
        if (StrKit.notBlank(conId)) {
            rm.put(RedisKeyDetail.CON_ID, conId);
        }
        String result = cache.hmset(sk, rm);
        return sk;
    }
}
