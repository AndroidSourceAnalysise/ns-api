/**
 * project name: hdy_project
 * file name:BasCustomerService
 * package name:com.ns.customer.service
 * date:2018-02-01 17:22
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.customer.service;

import com.ns.common.constant.RedisKeyDetail;
import com.ns.common.exception.CustException;
import com.ns.common.model.BasCustomer;
import com.ns.common.utils.DateUtil;
import com.ns.common.utils.GUIDUtil;
import com.ns.sys.service.SysDictService;
import com.ns.tld.service.TldIdentifyCodeService;
import com.ns.weixin.service.NoticeService;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.redis.Redis;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.CustomServiceApi;
import com.jfinal.weixin.sdk.api.UserApi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description: //TODO <br>
 * date: 2018-02-01 17:22
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class BasCustomerService {
    public static final BasCustomerService me = new BasCustomerService();
    private static final String COLUMN = "ID,CON_NO,CON_NAME,REAL_NAME,CON_TYPE,PIC,SEX,BIRTHDAY,COUNTRY,PROVINCE,CITY,DISTRICT,ADDRESS,MOBILE,UNION_ID,OPENID," +
            "IS_LOCKOUT,RP_ID,RP_NO,RP_NAME,IS_SUBSCRIBE,ENABLED,VERSION,STATUS,REMARK,CREATE_BY,CREATE_DT,UPDATE_DT ";
    private static final String BY_UNIONID_SQL = "select t.ID from bas_customer t where UNION_ID=?";
    private static final String BY_ID_SQL = "select t.CON_NAME,t.PIC,t.SEX,t.BIRTHDAY,t.COUNTRY,t.PROVINCE,t.CITY,t.DISTRICT,t.ADDRESS from bas_customer t where t.ID=?";
    private static final String QUERY_APPLET_OPENID_BY_ID = "select t.APPLET_OPENID from bas_customer t where t.ID=?";
    private static final String QUERY_MOBILE_BIND_SQL = "select t.mobile from bas_customer t where t.ID=?";
    private static final String QUERY_REFEREE_SQL = "select t.RP_ID from bas_customer t where t.ID=?";
    private static final String QUERY_REFEREE_COUNT_SQL = "select count(*) from bas_customer t where t.RP_ID=?";
    private static final String QUERY_MEMBER_SQL = "select t.ORDERS_TOTAL from bas_customer_ext t where t.ID=?";
    private static final String UPDATE_REFEREE_SQL = "update bas_customer t set t.RP_ID=?,t.RP_NO,t.RP_NAME where t.ID=?";


    /**
     * 检测推荐人
     */
    public boolean checkReferee(String conId) {
        // 1. 上级为空 2. 未购买 3. 没有下级
        Record record = Db.findFirst(QUERY_REFEREE_SQL, conId);
        if (record != null) {
            throw new CustException(100, "您已经有推荐人了!");
        }
        int count = Db.queryInt(QUERY_MEMBER_SQL, conId);
        if (count > 0) {
            throw new CustException(101, "您已经购买了产品!");
        }
        count = Db.queryInt(QUERY_REFEREE_COUNT_SQL, conId);
        if (count > 0) {
            throw new CustException(102, "您已经有会员!");
        }
        return true;
    }

    /**
     * 更新推荐人
     */
    public boolean updateReferee(String conId, String refereeNo) {
        checkReferee(conId);
        BasCustomer basCustomer = getCustomerByConNo(refereeNo);
        if (basCustomer.getConType() == 0) {
            throw new CustException(103, "会员" + refereeNo + "还没有购买过产品哦!");
        }
        return Db.update(UPDATE_REFEREE_SQL, basCustomer.getID(), refereeNo, basCustomer.getConName(), conId) > 0;
    }

    /**
     * 获取推荐人基础信息
     */
    public Object getRefereeBaseInfo(String refereeNo) {
        BasCustomer basCustomer = getCustomerByConNo(refereeNo);
        Map rs = new HashMap();
        rs.put("avatar", basCustomer.getPIC());
        rs.put("con_name", basCustomer.getConName());
        return rs;
    }

    /**
     * 自动推荐人
     */
    public boolean autoReferee(String conId) {
        String sysRefereeNo = SysDictService.me.getByParamKey("auto_referee_no");
        if (StrKit.isBlank(sysRefereeNo)) {
            sysRefereeNo = "1";
        }
        return updateReferee(conId, sysRefereeNo);
    }

    public boolean isMobileBind(String conId) {
        return StrKit.notBlank(Db.findFirst(QUERY_MOBILE_BIND_SQL, conId).getStr("mobile"));
    }


    private final BasCustomer dao = new BasCustomer().dao();
    static BasCustomerExtService extService = BasCustomerExtService.me;
    static NoticeService noticeService = NoticeService.me;
    static TldIdentifyCodeService identifyCodeService = TldIdentifyCodeService.me;

    public String isExistCustomerByUnionId(String unionId) {
        if (StrKit.isBlank(unionId)) {
            return null;
        }
        return Db.queryStr(BY_UNIONID_SQL, unionId);
    }


    /**
     * 关注新增会员
     *
     * @param refNo
     * @param openId
     */
    public void addCustomer(String refNo, String openId) {
        String str = "hi，咪之猫终于等到你啦！\n" +
                "吃货的世界，咪之猫最懂你；\n" +
                "咪之猫不仅仅能提供最美，最实惠，最有价值的产品，同时能够给予最美好的精神滋养！\n" +
                "<a href=\"http://www.nashengbuy.com\">了解详情，请点击进入>></a>";
        if (StrKit.isBlank(refNo)) {
            refNo = Db.queryStr("select rp_no from bas_cust_relation where open_id = ?", openId);
        }
        BasCustomer customer = getCustomerByOpenId(openId);
        BasCustomer refCustomer = getCustomerByConNo(refNo);
        String rpId = refCustomer == null ? "" : refCustomer.getID();
        String rpNo = refCustomer == null ? "" : refCustomer.getConNo();
        String rpName = refCustomer == null ? "" : refCustomer.getConName();
        if (customer != null) {
            //判断是否已经关注公众号
            if (customer.getIsSubscribe() == 1) {
                //发送重复关注消息
                CustomServiceApi.sendText(openId, "请不要重复关注");
            } else {
                //如果之前取消关注,现在重新关注.
                if (refCustomer != null) {
                    //自己扫自己
                    if (openId.equals(refCustomer.getOPENID())) {
                        CustomServiceApi.sendText(openId, "亲，不要自己扫自己的二维码关注哦^ ^！");
                        return;
                    }
                }
                customer.setIsSubscribe(1);
                //重新关注后,如果之前上级为空，则可以改关系
                if (StrKit.isBlank(customer.getRpId())) {
                    customer.setRpId(rpId);
                    customer.setRpName(rpNo);
                    customer.setRpNo(rpName);
                }
                customer.setUpdateDt(DateUtil.getNow());
                customer.update();
                noticeService.sendNewCustomerNotice(customer, refCustomer);
                CustomServiceApi.sendText(openId, str);
            }
        } else {
            ApiResult result = UserApi.getUserInfo(openId);
            customer = setCustomerAttr(result.getStr("nickname"), result.getStr("headimgurl"), result.getInt("sex"), 0, "", result.getStr("country"), result.getStr("province"),
                    result.getStr("city"), "", "", result.getStr("unionid"), openId, rpId, rpNo, rpName);
            if (customer.save() && extService.addBasCustomerExt(customer.getID(), customer.getConNo(), customer.getConName())) {
                noticeService.sendNewCustomerNotice(customer, refCustomer);
                CustomServiceApi.sendText(openId, str);
            } else {
                throw new CustException("新增会员异常!");
            }

        }


        //setCustomerAttr();

        //return customer.save() && extService.addBasCustomerExt(customer.getID(), customer.getConNo(), customer.getConName());
    }

    public BasCustomer setCustomerAttr(String conName, String pic, int sex, int conType, String birthDay, String country, String province, String city, String district,
                                       String address, String unionId, String openId, String rpId, String rpNo, String rpName) {
        BasCustomer customer = new BasCustomer();
        customer.setID(GUIDUtil.getGUID());
        customer.setConNo(String.valueOf(Redis.use().incr(RedisKeyDetail.CON_NO_SEQ)));
        customer.setConName(conName);
        customer.setConType(conType);
        customer.setPIC(pic);
        customer.setSEX(sex);
        customer.setBIRTHDAY(birthDay);
        customer.setCOUNTRY(country);
        customer.setPROVINCE(province);
        customer.setCITY(city);
        customer.setDISTRICT(district);
        customer.setADDRESS(address);
        customer.setUnionId(unionId);
        customer.setOPENID(openId);
        customer.setRpId(rpId);
        customer.setRpNo(rpNo);
        customer.setRpName(rpName);
        customer.setIsSubscribe(1);
        customer.setCreateDt(DateUtil.getNow());
        customer.setUpdateDt(DateUtil.getNow());
        return customer;
    }


    public String appletAddCustomer(String conName, String pic, int sex, String birthDay, String country, String province, String city, String unionId, String openId) {
        BasCustomer customer = new BasCustomer();
        String conId = GUIDUtil.getGUID();
        customer.setID(conId);
        customer.setConNo(String.valueOf(Redis.use().incr(RedisKeyDetail.CON_NO_SEQ)));
        customer.setConName(conName);
        customer.setConType(0);
        customer.setPIC(pic);
        customer.setSEX(sex);
        customer.setBIRTHDAY(birthDay);
        customer.setCOUNTRY(country);
        customer.setPROVINCE(province);
        customer.setCITY(city);
        customer.setUnionId(unionId);
        customer.setAppletOpenid(openId);
        customer.setIsSubscribe(0);
        customer.setCreateDt(DateUtil.getNow());
        customer.setUpdateDt(DateUtil.getNow());
        if (!customer.save()) {
            throw new CustException("添加会员信息失败!");
        }
        return conId;
    }

    public boolean updateAppletCustomer(String conName, String pic, int sex, String birthDay, String country, String province, String city, String conId) {
        Record record = Db.findFirst(BY_ID_SQL, conId);
        BasCustomer customer = new BasCustomer();
        customer.setID(conId);
        if (StrKit.isBlank(record.getStr("CON_NAME"))) {
            customer.setConName(conName);
        }
        if (StrKit.isBlank(record.getStr("PIC"))) {
            customer.setPIC(pic);
        }
        if (StrKit.isBlank(record.getStr("SEX"))) {
            customer.setSEX(sex);
        }
        if (StrKit.isBlank(record.getStr("BIRTHDAY"))) {
            customer.setBIRTHDAY(birthDay);
        }
        if (StrKit.isBlank(record.getStr("COUNTRY"))) {
            customer.setCOUNTRY(country);
        }
        if (StrKit.isBlank(record.getStr("PROVINCE"))) {
            customer.setPROVINCE(province);
        }
        if (StrKit.isBlank(record.getStr("CITY"))) {
            customer.setCITY(city);
        }
        customer.setUpdateDt(DateUtil.getNow());
        return customer.update();
    }

    public boolean updateAppletCustomer(String openId, String conId) {
        Record record = Db.findFirst(QUERY_APPLET_OPENID_BY_ID, conId);
        if (StrKit.isBlank(record.getStr("APPLET_OPENID"))) {
            BasCustomer customer = new BasCustomer();
            customer.setID(conId);
            customer.setAppletOpenid(openId);
            return customer.update();
        }
        return true;
    }

    public boolean bindMobile(String mobile, String conId, String code) {
        boolean result = identifyCodeService.checkIdentifyCode(mobile, code);
        if (result) {
            Record record2 = Db.findFirst("select ID,MOBILE from bas_customer where MOBILE = ?", mobile);
            if (record2 != null) {
                throw new CustException("<" + mobile + ">该手机号码已存在绑定关系，请更换手机号码！");
            }
            return Db.update("update bas_customer set MOBILE = ? where id = ?", mobile, conId) > 0;
        } else {
            throw new CustException("验证码已失效,请重新获取!");
        }
    }

    /**
     * 根据Id找会员信息
     *
     * @param id
     * @return
     */
    public BasCustomer getCustomerById(String id) {
        return dao.findById(id);
    }

    public boolean update(BasCustomer customer, String code) {
        if (StrKit.notBlank(customer.getMOBILE())) {
            if (StrKit.isBlank(code)) {
                throw new CustException("验证码不能为空!");
            }
            boolean result = identifyCodeService.checkIdentifyCode(customer.getMOBILE(), code);
            if (!result) {
                throw new CustException("验证码已失效,请重新获取!");
            }
            Record record2 = Db.findFirst("select ID,MOBILE from bas_customer where MOBILE = ?", customer.getMOBILE());
            if (record2 != null) {
                throw new CustException("<" + customer.getMOBILE() + ">该手机号码已存在绑定关系，请更换手机号码！");
            }
        }
        customer.setUpdateDt(DateUtil.getNow());
        return customer.update();
    }

    /**
     * 根据Id找会员信息
     *
     * @param id
     * @return
     */
    public BasCustomer getCustomerByIdNotNull(String id) {
        BasCustomer customer = dao.findById(id);
        if (customer == null) {
            throw new CustException("找不到会员信息");
        }
        return customer;
    }

    private static final String CUSTOMER_BASE_INFO_SQL = "select c.CON_NAME,c.PIC,c.CON_NO,c.RP_NAME,b.REVENUES,b.POINTS_ENABLED from (select t.ID,t.CON_NAME,t.PIC,t.CON_NO,t.RP_NAME from bas_customer t where t.id=?) c left join bas_customer_ext b on c.ID=b.CON_ID";


    public Record getBaseCustomerByIdNotNull(String conId) {
        List<Record> customer = Db.find(CUSTOMER_BASE_INFO_SQL, conId);
        if (customer == null || customer.isEmpty()) {
            throw new CustException("找不到会员信息");
        }
        return customer.get(0);
    }

    public BasCustomer getCustomerByOpenIdNotNull(String openId) {
        BasCustomer customer = dao.findFirst("select " + COLUMN + " from bas_customer where openid = ? and enabled = 1", openId);
        if (customer == null) {
            throw new CustException("找不到会员信息");
        }
        return customer;
    }

    /**
     * 根据会员号查找
     *
     * @param conNo
     * @param enabled 是否有效
     * @return
     */
    public BasCustomer getCustomerByConNo(String conNo, int enabled) {
        return dao.findFirst("select " + COLUMN + " from bas_customer where con_no = ? and enabled = ?", conNo, enabled);
    }

    public BasCustomer getCustomerByConNo(String conNo) {
        return dao.findFirst("select " + COLUMN + " from bas_customer where con_no = ? and enabled = 1", conNo);
    }


    public BasCustomer getCustomerByConNoNotNull(String conNo) {
        BasCustomer customer = dao.findFirst("select " + COLUMN + " from bas_customer where con_no = ? and enabled = 1", conNo);
        if (customer == null) {
            throw new CustException("找不到会员信息");
        }
        return customer;
    }

    /**
     * 取消关注
     *
     * @param openId
     */
    public void cancelSubscribe(String openId) {
        //变成未关注
        Db.update("update bas_customer set IS_SUBSCRIBE = 0,UPDATE_DT= ? where openid = ?", DateUtil.getNow(), openId);
        //删除关系
        if (StrKit.notBlank(Db.queryStr("select open_id from bas_cust_relation where open_id = ?", openId))) {
            Db.delete("delete from bas_cust_relation where OPEN_ID = ?", openId);
        }
    }

    /**
     * 根据openId查询会员信息
     *
     * @param openId
     */
    public BasCustomer getCustomerByOpenId(String openId) {
        return dao.findFirst("select " + COLUMN + " from bas_customer where openid = ? and enabled = 1", openId);
    }

    /**
     * 分页查询会员数据
     *
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public Page<BasCustomer> paginate(int pageNumber, int pageSize) {
        return dao.paginate(pageNumber, pageSize, "select " + COLUMN + "", "from bas_customer order by create_dt desc");
    }

    int s = 0;

    public int a(String conId) {
        s = 0;
        List<Record> list = Db.find("select * from treenodes where pid = ?", conId);

        int v = b(list, 0);
        return v;
    }

    public int b(List<Record> list, int count) {
        for (Record r : list) {
            String id = r.getStr("id");
            String name = r.getStr("nodename");
            System.out.println(name);
            List<Record> list2 = Db.find("select * from treenodes where pid = ?", id);
            b(list2, count);
            s++;
        }
        return s;
    }

    public int dg(String conId) {
        List<Record> list = Db.find("select * from treenodes where pid = ?", conId);
        for (Record record : list) {
            dg(record.getStr("id"));
            s += 1;
        }
        return s;
    }

    public void dg2(List<Record> list, String conId) {
        List<Record> list2 = Db.find("select * from treenodes where pid = ?", conId);
        for (Record record : list) {
            record.getStr("RP_ID").equals(conId);
        }
    }
}
