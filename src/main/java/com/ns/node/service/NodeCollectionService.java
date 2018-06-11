/**
 * project name: ns-api
 * file name:NodeCollectionService
 * package name:com.ns.node.service
 * date:2018-03-29 14:12
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.node.service;

import com.ns.common.exception.CustException;
import com.ns.common.model.NodeCollection;
import com.ns.common.utils.DateUtil;
import com.ns.common.utils.GUIDUtil;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

/**
 * description: //TODO <br>
 * date: 2018-03-29 14:12
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class NodeCollectionService {
    private final NodeCollection dao = new NodeCollection();
    public static final NodeCollectionService me = new NodeCollectionService();
    private final String COLUMN = "ID,SOURCE_ID,CON_ID,CON_NO,CON_NAME,ENABLED,VERSION," +
            "STATUS,REMARK,CREATE_BY,CREATE_DT,UPDATE_DT ";

    //{"SOURCE_ID":"笔记id","CON_ID","会员id"}
    public boolean insert(NodeCollection nodeCollection) {
        Record customer = Db.findFirst("select id,CON_NO,CON_NAME,PIC from bas_customer where ENABLED = 1 and ID = ?", nodeCollection.getConId());
        if (customer == null) {
            throw new CustException("找不到会员信息!");
        }
        boolean isCollection = Db.queryInt("select count(ID) from node_collection where ENABLED = 1 AND SOURCE_ID = ? AND CON_ID = ?", nodeCollection.getSourceId(), nodeCollection.getConId()) > 0;
        if (isCollection) {
            throw new CustException("不能重复收藏!");
        }
        nodeCollection.setID(GUIDUtil.getGUID());
        nodeCollection.setConNo(customer.getStr("CON_NO"));
        nodeCollection.setConName(customer.getStr("CON_NAME"));
        nodeCollection.setCreateDt(DateUtil.getNow());
        nodeCollection.setUpdateDt(DateUtil.getNow());
        return nodeCollection.save();
    }

    public boolean cancel(String contentId, String conId) {
        return Db.delete("delete from node_collection where SOURCE_ID = ? AND CON_ID = ?", contentId, conId) > 0;
    }


    public Page<Record> getByConId(String conId, Integer pageNumber, Integer pageSize) {
        String select = "SELECT T2.ID,T2.CONTENT,T2.PIC1,T2.PIC2,T2.PIC3,T2.CON_ID,T2.CON_NO,T2.CON_NAME,T2.CON_PIC ";
        String sqlExceptSelect = "FROM node_collection T1 INNER JOIN node_content T2 ON T1.SOURCE_ID = T2.ID WHERE T1.ENABLED = 1 AND T2.ENABLED = 1 AND  T1.CON_ID = ?";
        Page<Record> page = Db.paginate(pageNumber, pageSize, select, sqlExceptSelect, conId);
        for (Record record : page.getList()) {
            String id = record.getStr("ID");
            String focusConId = record.getStr("CON_ID");
            //收藏数
            int collectionNum = Db.queryInt("select count(ID) from node_collection where ENABLED = 1 AND SOURCE_ID = ?", id);
            record.set("COLLECTION_NUM", collectionNum);
            //是否已收藏
            boolean isCollection = Db.queryInt("select count(ID) from node_collection where ENABLED = 1 AND SOURCE_ID = ? AND CON_ID = ?", id, conId) > 0;
            record.set("IS_COLLECTION", isCollection);
            //是否点赞
            boolean isLike = Db.queryInt("select count(ID) from node_like where ENABLED = 1 AND SOURCE_ID = ? AND CON_ID = ?", id, conId) > 0;
            record.set("IS_LIKE", isLike);
            //评论数
            int cmtNum = Db.queryInt("select count(ID) from node_cmt where ENABLED = 1 AND SOURCE_ID = ? ", id);
            record.set("CMT_NUM", cmtNum);
            //评论
            List<Record> cmtList = Db.paginate(1, 3, "select ID,PARENT_ID,SOURCE_ID,CON_ID,CON_NO,CON_NAME,CON_PIC,CONTENT,TO_CON_ID,TO_CON_NO,TO_CON_NAME,TO_CON_PIC,ENABLED,VERSION,STATUS,REMARK,CREATE_BY,CREATE_DT,UPDATE_DT ", "from node_cmt where ENABLED = 1 and SOURCE_ID = ? order by CREATE_DT desc", id).getList();
            record.set("CMT_LIST", cmtList);
            //是否已关注
            boolean isFocus = Db.queryInt("select count(*) from node_focus where ENABLED = 1 and CON_ID = ? and FOCUS_CON_ID = ?", conId, focusConId) > 0;
            record.set("IS_FOCUS", isFocus);
        }
        return page;
    }
}
