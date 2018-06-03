/**
 * project name: ns-api
 * file name:TldIdentifyCodeService
 * package name:com.ns.tld.service
 * date:2018-04-14 16:50
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.tld.service;

import com.ns.common.constant.RedisKeyDetail;
import com.ns.common.exception.CustException;
import com.ns.common.model.TldIdentifyCode;
import com.ns.common.sms.HttpSender;
import com.ns.common.sms.SmsSend;
import com.ns.common.sms.SmsSendResponse;
import com.ns.common.utils.DateUtil;
import com.ns.common.utils.GUIDUtil;
import com.ns.sys.service.SysDictService;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;
import java.util.Random;

/**
 * description: //TODO <br>
 * date: 2018-04-14 16:50
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class TldIdentifyCodeService {
    private final TldIdentifyCode dao = new TldIdentifyCode();
    public static final TldIdentifyCodeService me = new TldIdentifyCodeService();
    private final String COLUMN = "ID,CON_ID,MOBILE,IDENTIFY_CODE,ENABLED,VERSION," +
            "STATUS,REMARK,CREATE_BY,CREATE_DT,UPDATE_DT ";
    static SysDictService sysDictService = SysDictService.me;

    public boolean getCode(String mobile, String conId, int type) throws Exception {
        Integer validTime = 20;//默认20分钟有效时间
        String valid = sysDictService.getByParamKey(RedisKeyDetail.VALID_TIME);
        if (StrKit.notBlank(valid)) {
            validTime = Integer.valueOf(valid);
        }
        Record record = Db.findFirst("select ID,MOBILE from bas_customer where id = ?", conId);
        if (record == null) {
            throw new CustException("找不到会员信息!");
        }
        if (mobile.equals(record.getStr("MOBILE"))) {
            throw new CustException("<" + mobile + ">该手机号码已存在绑定关系，请更换手机号码！");
        }
        TldIdentifyCode tldIdentifyCode = dao.findFirst("select " + COLUMN + " from tld_identify_code where ENABLED = 1 and mobile = ?", mobile);
        if (tldIdentifyCode != null) {
            long timeDiff = DateUtil.getTimeDiff(DateUtil.getNow(), tldIdentifyCode.getCreateDt(), DateUtil.DIFF_UNIT_MIN);
            if (timeDiff > validTime) {
                //如果过期了,则变更为失效
                Db.delete("delete from tld_identify_code where id = ?", tldIdentifyCode.getID());
            } else {
                throw new CustException("验证码有效期为:" + validTime + "分钟,不能重复发送验证码!");
            }
        }
        String code = genRandomCode();
        isnert(code, conId, mobile);
        if (type == 0) {
            SmsSendResponse response = SmsSend.sendSms(mobile, code,validTime);
            if ("0".equals(response.getCode())) {
                return true;
            }
        } else {
            //api返回无规则,傻逼人员设计
            String returnString = HttpSender.batchSend(mobile, code);
            int begin = returnString.indexOf(",") + 1;
            int end = returnString.indexOf("\n");
            String result = returnString.substring(begin, end);
            if ("0".equals(result)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkIdentifyCode(String mobile, String code) {
        Integer validTime = 20;//默认20分钟有效时间
        String valid = sysDictService.getByParamKey(RedisKeyDetail.VALID_TIME);
        if (StrKit.notBlank(valid)) {
            validTime = Integer.valueOf(valid);
        }
        List<TldIdentifyCode> tldIdentifyCodes = dao.find("select " + COLUMN + " from tld_identify_code where ENABLED = 1 and mobile = ? and IDENTIFY_CODE = ?", mobile, code);
        for (TldIdentifyCode t : tldIdentifyCodes) {
            long timeDiff = DateUtil.getTimeDiff(DateUtil.getNow(), t.getCreateDt(), DateUtil.DIFF_UNIT_MIN);
            if (timeDiff > validTime) {
                //如果过期了,则变更为失效
                Db.delete("delete from tld_identify_code where id = ?", t.getID());
            } else {
                return true;
            }
        }
        return false;
    }

    public boolean isnert(String code, String conId, String mobile) {
        TldIdentifyCode tldIdentifyCode = new TldIdentifyCode();
        tldIdentifyCode.setID(GUIDUtil.getGUID());
        tldIdentifyCode.setConId(conId);
        tldIdentifyCode.setMOBILE(mobile);
        tldIdentifyCode.setIdentifyCode(code);
        tldIdentifyCode.setCreateDt(DateUtil.getNow());
        tldIdentifyCode.setUpdateDt(DateUtil.getNow());
        return tldIdentifyCode.save();
    }

    /**
     * 生成6位随机码
     *
     * @return
     */
    private static String genRandomCode() {
        String code = "";
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            code += random.nextInt(10);
        }

        return code;
    }
}
