/**
 * project name: ns-api
 * file name:TldSiteMsgService
 * package name:com.ns.tld.service
 * date:2018-02-12 13:49
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.tld.service;

import com.ns.common.model.TldSiteMsg;
import com.ns.common.utils.DateUtil;
import com.ns.common.utils.GUIDUtil;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;

import java.util.List;

/**
 * description: //TODO <br>
 * date: 2018-02-12 13:49
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class TldSiteMsgService {
    public static final TldSiteMsgService me = new TldSiteMsgService();
    private TldSiteMsg dao = TldSiteMsg.dao;
    private static final String COLUMN = "ID,SENDID,RECID,MSG_CONTENT,TYPE,ENABLED,VERSION,STATUS,REMARK,CREATE_BY,CREATE_DT,UPDATE_DT";

    public boolean addMsg(TldSiteMsg msg) {
        msg.setID(GUIDUtil.getGUID());
        msg.setCreateDt(DateUtil.getNow());
        msg.setUpdateDt(DateUtil.getNow());
        return msg.save();
    }

    /**
     * 发送站内消息
     *
     * @param sendId     发送者
     * @param recId      接收者
     * @param msgContent 发送内容
     * @param type       1会员通知,2订单通知,3系统通知
     * @return
     */
    public boolean addMsg(String sendId, String recId, String msgContent, int type) {
        TldSiteMsg msg = new TldSiteMsg();
        msg.setID(GUIDUtil.getGUID());
        msg.setSENDID(sendId);
        msg.setRECID(recId);
        msg.setMsgContent(msgContent);
        msg.setTYPE(type);
        msg.setCreateDt(DateUtil.getNow());
        msg.setUpdateDt(DateUtil.getNow());
        return msg.save();
    }

    public Page<TldSiteMsg> getMsg(int pageNumber, int pageSize, String conId, String type) {
        StringBuffer sqlExceptSelect = new StringBuffer(" from tld_site_msg where enabled = 1 and RECID = ? ");
        if (StrKit.notBlank(type)) {
            sqlExceptSelect.append("and type = " + type);
        }
        sqlExceptSelect.append(" order by CREATE_DT desc");
        return dao.paginate(pageNumber, pageSize, "select " + COLUMN + " ", sqlExceptSelect.toString(), conId);
    }
}
