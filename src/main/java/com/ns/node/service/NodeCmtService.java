/**
 * project name: ns-api
 * file name:NodeCmtService
 * package name:com.ns.node.service
 * date:2018-03-29 11:24
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.node.service;

import com.ns.common.exception.CustException;
import com.ns.common.model.NodeCmt;
import com.ns.common.utils.DateUtil;
import com.ns.common.utils.GUIDUtil;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;


/**
 * description: //TODO <br>
 * date: 2018-03-29 11:24
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class NodeCmtService {
    private final NodeCmt dao = new NodeCmt();
    public static final NodeCmtService me = new NodeCmtService();
    private final String COLUMN = "ID,PARENT_ID,SOURCE_ID,CON_ID,CON_NO,CON_NAME,CON_PIC,CONTENT,TO_CON_ID,TO_CON_NO,TO_CON_NAME,TO_CON_PIC,BY_CON_ID,ENABLED,VERSION," +
            "STATUS,REMARK,CREATE_BY,CREATE_DT,UPDATE_DT ";

    //回复:{"SOURCE_ID":"笔记id","CON_ID":"会员ID","CONTENT":"内容","TO_CON_ID":"被评论者"}
    //评论:{"SOURCE_ID":"笔记id","CON_ID":"会员ID","CONTENT":"内容"}
    public boolean comment(NodeCmt cmt) {
        cmt.setID(GUIDUtil.getGUID());
        Record customer = Db.findFirst("select id,CON_NO,CON_NAME,PIC from bas_customer where ENABLED = 1 and ID = ?", cmt.getConId());
        if (customer == null) {
            throw new CustException("找不到会员信息!");
        }
        cmt.setConNo(customer.getStr("CON_NO"));
        cmt.setConName(customer.getStr("CON_NAME"));
        cmt.setConPic(customer.getStr("PIC"));
        if (StrKit.notBlank(cmt.getToConId())) {
            Record customer2 = Db.findFirst("select id,CON_NO,CON_NAME,PIC from bas_customer where ENABLED = 1 and ID = ?", cmt.getToConId());
            if (customer2 == null) {
                throw new CustException("找不到会员信息!");
            }
            cmt.setToConNo(customer2.getStr("CON_NO"));
            cmt.setToConName(customer2.getStr("CON_NAME"));
            cmt.setToConPic(customer2.getStr("PIC"));
            cmt.setByConId(cmt.getToConId());
        } else {
            //否则被评论者就是笔记作者
            String conId = Db.queryStr("select CON_ID from node_content where id = ?", cmt.getSourceId());
            cmt.setByConId(conId);
        }

        cmt.setCreateDt(DateUtil.getNow());
        cmt.setUpdateDt(DateUtil.getNow());
        return cmt.save();
    }

    public Page<Record> getUnreadList(String conId, Integer pageNumber, Integer pageSize) {
        Page<Record> record = Db.paginate(pageNumber, pageSize, "select " + COLUMN,
                " from node_cmt where ENABLED = 1 and status = 0 and BY_CON_ID = ? order by CREATE_DT desc", conId);
        //Db.update("update node_cmt set status = 1 where  ENABLED = 1 and status = 0 and BY_CON_ID = ? ", conId);
        return record;
    }

    public Integer getUnreadNum(String conId) {
        return Db.queryInt("select count(ID) from node_cmt where ENABLED = 1 and status = 0 and BY_CON_ID = ? ", conId);
    }

    public boolean delete(String id, String conId) {
        NodeCmt cmt = getById(id);
        if (cmt == null || !cmt.getConId().equals(conId)) {
            throw new CustException("只能删除自己的评论!");
        }
        return dao.deleteById(id);
    }

    public NodeCmt getById(String id) {
        return dao.findFirst("select " + COLUMN + " from node_cmt where ENABLED = 1 and id = ?", id);
    }

    public Page<NodeCmt> getNodeCmt(Integer pageNumber, Integer pageSize, String nodeContentId) {
        return dao.paginate(pageNumber, pageSize, "select " + COLUMN,
                " from node_cmt where ENABLED = 1 and SOURCE_ID = ? order by CREATE_DT desc", nodeContentId);
    }

}
