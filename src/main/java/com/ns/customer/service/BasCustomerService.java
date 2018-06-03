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
import com.ns.common.model.Blog;
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

import java.util.List;
import java.util.Objects;

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

    private final BasCustomer dao = new BasCustomer().dao();
    static BasCustomerExtService extService = BasCustomerExtService.me;
    static NoticeService noticeService = NoticeService.me;
    static TldIdentifyCodeService identifyCodeService = TldIdentifyCodeService.me;

    /**
     * 关注新增会员
     *
     * @param refNo
     * @param openId
     */
    public void addCustomer(String refNo, String openId) {
        String str = "hi，你终于找到我了！\n" +
                "\n" +
                "教育孩子，如此简单；\n" +
                "在孩子最美好的年华 ，给予最美好的精神滋养！\n" +
                " \n" +
                "0-7岁，经典陪你长大\n" +
                "\n" +
                "\n" +
                "http://mp.weixin.qq.com/s/DgllyrRCI9eyI-ShdSw3cA";
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
