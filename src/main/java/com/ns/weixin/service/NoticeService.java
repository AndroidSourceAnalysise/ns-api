/**
 * project name: ns-api
 * file name:NoticeService
 * package name:com.ns.weixin.service
 * date:2018-02-12 13:32
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.weixin.service;

import com.ns.common.model.BasCustomer;
import com.ns.common.model.TldSiteMsg;
import com.ns.tld.service.TldSiteMsgService;
import com.jfinal.weixin.sdk.api.TemplateData;
import com.jfinal.weixin.sdk.api.TemplateMsgApi;

/**
 * description: //TODO <br>
 * date: 2018-02-12 13:32
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class NoticeService {
    private static final String NEW_CUSTOMER_NOTICE_TEMPLATE_01 = "LpDY9HEfXkMMUvs39Z1dGe5_YRWG99vj4cirHvDg74E";
    public static final String TEMP_COLOR = "#173177";
    public static final NoticeService me = new NoticeService();
    static TldSiteMsgService tldSiteMsgService = TldSiteMsgService.me;

    /**
     * 新会员加入提醒
     */
    public String getNewCustomerNotice(String openId, String conName, String conNo, String createDt) {
        conName = conName == null ? "" : conName;
        String first = String.format("感谢您的关注,您的会员号是【%s】", conNo);
        TemplateData temp = TemplateData.New();
        temp.setTouser(openId);
        temp.setTemplate_id(NEW_CUSTOMER_NOTICE_TEMPLATE_01);
        temp.setUrl("");//详情链接
        temp.add("first", first, TEMP_COLOR);
        temp.add("keyword1", conName, TEMP_COLOR);
        temp.add("keyword2", conNo, TEMP_COLOR);
        temp.add("keyword3", createDt, TEMP_COLOR);
        return temp.build();
    }

    /**
     * 新会员加入提醒推荐人
     */
    public String getNewCustomerRefNotice(BasCustomer customer, BasCustomer refCustomer) {
        String first = String.format("新客户【%s】，于%s成为【弘德苑】的第%s位会员，会员号为【%s】", customer.getConName(), customer.getUpdateDt(), customer.getConNo(), customer.getConNo());
        TemplateData temp = TemplateData.New();
        temp.setTouser(refCustomer.getOPENID());
        temp.setTemplate_id(NEW_CUSTOMER_NOTICE_TEMPLATE_01);
        temp.setUrl("");//详情链接
        temp.add("first", first, TEMP_COLOR);
        temp.add("keyword1", customer.getConName(), TEMP_COLOR);
        temp.add("keyword2", customer.getConNo(), TEMP_COLOR);
        temp.add("keyword3", customer.getUpdateDt(), TEMP_COLOR);
//        temp.add("remark", "付出必有回报，亲，加油哦", TEMP_COLOR);
        return temp.build();
    }

    private static final String NEW_CUSTOMER_NOTICE_TEMPLATE_05 = "YBjUxAxkOVGgbziG-tpHOLnpXowbUYyaD70SKcfKvCU";

    /**
     * 订单付款消息模板
     */
    public void getOrderPaySuccessNotice(String conId, String openId, String orderNo, String payDate, String orderTotal, String paymentType) {
        String first = "您好，您的订单已付款成功";
        TemplateData temp = TemplateData.New();
        temp.setTouser(openId);
        temp.setTemplate_id(NEW_CUSTOMER_NOTICE_TEMPLATE_05);
        temp.setUrl("");//详情链接
        temp.add("first", first, TEMP_COLOR);
        temp.add("keyword1", orderNo, TEMP_COLOR);
        temp.add("keyword2", payDate, TEMP_COLOR);
        temp.add("keyword3", orderTotal, TEMP_COLOR);
        temp.add("keyword4", paymentType, TEMP_COLOR);
        temp.add("remark", "感谢您的惠顾", TEMP_COLOR);
        TemplateMsgApi.send(temp.build());
        tldSiteMsgService.addMsg("0", conId, "您好，您的订单已付款成功", 2);
    }

    private static final String NEW_CUSTOMER_NOTICE_TEMPLATE_04 = "8ACjQyFHW8YGxUu1a-rjhubJGTLceJP3SD1QD8hS7kI";

    /**
     * 积分模板消息
     */
    public void getPointsNotice(String conId, String openId, String conName, String payDate, String pointsNum, String pointsTotal) {
        Integer currentPoint = Integer.valueOf(pointsTotal) - Integer.valueOf(pointsNum);
        String first = String.format("亲爱的【%s】，您的积分账户有新的变动，具体内容如下", conName);
        TemplateData temp = TemplateData.New();
        temp.setTouser(openId);
        temp.setTemplate_id(NEW_CUSTOMER_NOTICE_TEMPLATE_04);
        temp.setUrl("");//详情链接
        temp.add("first", first, TEMP_COLOR);
        temp.add("keyword1", payDate, TEMP_COLOR);
        temp.add("keyword2", pointsNum, TEMP_COLOR);
        temp.add("keyword3", "付款成功", TEMP_COLOR);
        temp.add("keyword4", currentPoint + "", TEMP_COLOR);
        temp.add("remark", "感谢您的使用", TEMP_COLOR);
        TemplateMsgApi.send(temp.build());
        String msg = String.format("亲爱的【%s】，您的积分账户有新的变动，获得积分【%s】", conName, pointsNum);
        tldSiteMsgService.addMsg("0", conId, msg, 2);
    }

    public void getRpPointsNotice(String conId,String openId, String conName, String conNo, String payDate, String pointsNum, String pointsTotal) {
        Integer currentPoint = Integer.valueOf(pointsTotal) - Integer.valueOf(pointsNum);
        String first = String.format("亲爱的【%s】，您的积分账户有新的变动，具体内容如下", conName);
        TemplateData temp = TemplateData.New();
        temp.setTouser(openId);
        temp.setTemplate_id(NEW_CUSTOMER_NOTICE_TEMPLATE_04);
        temp.setUrl("");//详情链接
        temp.add("first", first, TEMP_COLOR);
        temp.add("keyword1", payDate, TEMP_COLOR);
        temp.add("keyword2", pointsNum, TEMP_COLOR);
        temp.add("keyword3", "您的下级会员【" + conNo + "】付款成功", TEMP_COLOR);
        temp.add("keyword4", currentPoint + "", TEMP_COLOR);
        temp.add("remark", "感谢您的使用", TEMP_COLOR);
        TemplateMsgApi.send(temp.build());
        String msg = String.format("亲爱的【%s】，您的积分账户有新的变动，下级【%s】付款成功,获得积分【%s】", conName,conNo, pointsNum);
        tldSiteMsgService.addMsg("0", conId, msg, 2);
    }

    private static final String NEW_CUSTOMER_NOTICE_TEMPLATE_02 = "CnPUnPIy5fK_ouKDkO-Y6WNUS1YeRf7SfIVIzeGRHdU";

    public void sendOrderReceivedNotice(String conId,String openId, String orderNo, String confirmDate, String orderTotal) {
        String first = "您好，您的一个订单已经确认收货了。";
        TemplateData temp = TemplateData.New();
        temp.setTouser(openId);
        temp.setTemplate_id(NEW_CUSTOMER_NOTICE_TEMPLATE_02);
        temp.setUrl("");//详情链接
        temp.add("first", first, TEMP_COLOR);
        temp.add("keyword1", orderNo, TEMP_COLOR);
        temp.add("keyword2", orderTotal, TEMP_COLOR);
        temp.add("keyword3", confirmDate, TEMP_COLOR);
        temp.add("remark", "感谢您在此购物成功，同时希望您的再次光临！", TEMP_COLOR);
        TemplateMsgApi.send(temp.build());
        String msg = String.format("您好，您的一个订单已经确认收货了,订单编号:【%s】", orderNo);
        tldSiteMsgService.addMsg("0", conId, msg, 2);
    }

    private static final String NEW_CUSTOMER_NOTICE_TEMPLATE_03 = "4FRn9FAmWBipi-jdX4_2U5Psd3TEscKNAwZTGFJH604";

    public void getRefundNotice(String conId,String openId, String orderNo) {
        String first = "您好，您的退货申请已通过";
        TemplateData temp = TemplateData.New();
        temp.setTouser(openId);
        temp.setTemplate_id(NEW_CUSTOMER_NOTICE_TEMPLATE_03);
        temp.setUrl("");//详情链接
        temp.add("first", first, TEMP_COLOR);
        temp.add("keyword1", orderNo, TEMP_COLOR);
        temp.add("remark", "我们在收到货后，会于2-3个工作日内给您打款，如有问题，可咨询客服哦！", TEMP_COLOR);
        TemplateMsgApi.send(temp.build());
        String msg =  "您好，您的退货申请已通过,我们在收到货后，会于2-3个工作日内给您打款，如有问题，可咨询客服哦！";
        tldSiteMsgService.addMsg("0", conId, msg, 2);
    }

    /**
     * 新会员加入提醒
     *
     * @param customer
     */
    public void sendNewCustomerNotice(BasCustomer customer, BasCustomer refCustomer) {
        String temp = getNewCustomerNotice(customer.getOPENID(), customer.getConName(), customer.getConNo(), customer.getUpdateDt());
        TemplateMsgApi.send(temp);
        /*站内信本人*/
        String msgTxt = String.format("您于%s成为【咪之猫】的第%s位会员，会员号是【%s】", customer.getUpdateDt(), customer.getConNo(), customer.getConNo());
        tldSiteMsgService.addMsg("0", customer.getID(), msgTxt, 3);
        //给上级发消息
        if (refCustomer != null) {
            String temp2 = getNewCustomerRefNotice(customer,refCustomer);
            TemplateMsgApi.send(temp2);
            msgTxt = String.format("新客户【%s】，于%s成为【咪之猫】的第%s位会员，会员号为【%s】", customer.getConName(), customer.getUpdateDt(), customer.getConNo(), customer.getConNo());
            tldSiteMsgService.addMsg("0", refCustomer.getID(), msgTxt, 3);
        }
    }
}
