/**
 * project name: ns-api
 * file name:NodeContentService
 * package name:com.ns.node.service
 * date:2018-03-27 17:16
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.node.service;

import com.ns.common.exception.CustException;
import com.ns.common.model.NodeContent;
import com.ns.common.utils.DateUtil;
import com.ns.common.utils.GUIDUtil;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import sun.misc.Cache;

import java.util.List;

/**
 * description: //TODO <br>
 * date: 2018-03-27 17:16
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class NodeContentService {
    private final NodeContent dao = new NodeContent();
    public static final NodeContentService me = new NodeContentService();
    private final String COLUMN = "ID,CATEGORY_ID,CONTENT,PIC1,PIC3,CON_ID,CON_NO,CON_NAME,CON_PIC,ENABLED,VERSION," +
            "STATUS,REMARK,CREATE_BY,CREATE_DT,UPDATE_DT ";

    public boolean insert(NodeContent content) {
        if (StrKit.isBlank(content.getCONTENT()) || content.getCONTENT().length() < 20) {
            throw new CustException("内容最少输入20个字!");
        } else {
            if (content.getCONTENT().length() > 500) {
                throw new CustException("内容超出最大限制长度:500字!");
            }
        }
        Record customer = Db.findFirst("select id,CON_NO,CON_NAME,PIC from bas_customer where ENABLED = 1 and ID = ?", content.getConId());
        if (customer == null) {
            throw new CustException("找不到会员信息!");
        }
        content.setID(GUIDUtil.getGUID());
        content.setConNo(customer.getStr("CON_NO"));
        content.setConName(customer.getStr("CON_NAME"));
        content.setConPic(customer.getStr("PIC"));
        content.setCreateDt(DateUtil.getNow());
        content.setUpdateDt(DateUtil.getNow());
        return content.save();
    }

    public boolean delete(String id, String conId) {
        NodeContent nodeContent = getById(id);
        if (nodeContent == null || !nodeContent.getConId().equals(conId)) {
            throw new CustException("只能删除自己的笔记!");
        }
        return dao.deleteById(id);
    }

    public NodeContent getById(String id) {
        return dao.findFirst("select " + COLUMN + " from node_content where ENABLED and id = ?", id);
    }

    public Page<Record> getByCategory(String conId, Integer pageNumber, Integer pageSize, String categoryId) {
        String select = "SELECT " + COLUMN;
        String sqlExceptSelect = " from node_content where ENABLED = 1 and CATEGORY_ID = ?";
        Page<Record> page = Db.paginate(pageNumber, pageSize, select, sqlExceptSelect, categoryId);
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
            //是否已关注
            boolean isFocus = Db.queryInt("select count(*) from node_focus where ENABLED = 1 and CON_ID = ? and FOCUS_CON_ID = ?", conId, focusConId) > 0;
            record.set("IS_FOCUS", isFocus);
            //评论
            List<Record> cmtList = Db.paginate(1, 3, "select ID,PARENT_ID,SOURCE_ID,CON_ID,CON_NO,CON_NAME,CON_PIC,CONTENT,TO_CON_ID,TO_CON_NO,TO_CON_NAME,TO_CON_PIC,ENABLED,VERSION,STATUS,REMARK,CREATE_BY,CREATE_DT,UPDATE_DT ", " from node_cmt where ENABLED = 1 and SOURCE_ID = ? order by CREATE_DT desc", id).getList();
            record.set("CMT_LIST", cmtList);
        }
        return page;
    }

    public Page<Record> getByConId(String conId, Integer pageNumber, Integer pageSize,String myConId) {
        String select = "SELECT " + COLUMN;
        String sqlExceptSelect = " from node_content where ENABLED = 1 and CON_ID = ?";
        Page<Record> page = Db.paginate(pageNumber, pageSize, select, sqlExceptSelect, conId);
        for (Record record : page.getList()) {
            String id = record.getStr("ID");
            String focusConId = record.getStr("CON_ID");

            //收藏数
            int collectionNum = Db.queryInt("select count(ID) from node_collection where ENABLED = 1 AND SOURCE_ID = ?", id);
            record.set("COLLECTION_NUM", collectionNum);
            //是否已收藏
            boolean isCollection = Db.queryInt("select count(ID) from node_collection where ENABLED = 1 AND SOURCE_ID = ? AND CON_ID = ?", id, myConId) > 0;
            record.set("IS_COLLECTION", isCollection);
            //是否点赞
            boolean isLike = Db.queryInt("select count(ID) from node_like where ENABLED = 1 AND SOURCE_ID = ? AND CON_ID = ?", id, myConId) > 0;
            record.set("IS_LIKE", isLike);
            //评论数
            int cmtNum = Db.queryInt("select count(ID) from node_cmt where ENABLED = 1 AND SOURCE_ID = ? ", id);
            record.set("CMT_NUM", cmtNum);
            //是否已关注
            boolean isFocus = Db.queryInt("select count(*) from node_focus where ENABLED = 1 and CON_ID = ? and FOCUS_CON_ID = ?", myConId, focusConId) > 0;
            record.set("IS_FOCUS", isFocus);
            //评论
            List<Record> cmtList = Db.paginate(1, 3, "select ID,PARENT_ID,SOURCE_ID,CON_ID,CON_NO,CON_NAME,CON_PIC,CONTENT,TO_CON_ID,TO_CON_NO,TO_CON_NAME,TO_CON_PIC,ENABLED,VERSION,STATUS,REMARK,CREATE_BY,CREATE_DT,UPDATE_DT ", " from node_cmt where ENABLED = 1 and SOURCE_ID = ? order by CREATE_DT desc", id).getList();
            record.set("CMT_LIST", cmtList);
        }
        return page;
    }

    public Record getById2(String id) {
        Record record = Db.findFirst("SELECT " + COLUMN + " from node_content where ENABLED = 1 and id = ? ", id);
        if (record != null) {
            String categoryName = Db.queryStr("select NAME from node_category where id = ?", record.getStr("CATEGORY_ID"));
            record.set("CATEGORY_NAME", categoryName);
            //收藏数
            int collectionNum = Db.queryInt("select count(ID) from node_collection where ENABLED = 1 AND SOURCE_ID = ?", id);
            record.set("COLLECTION_NUM", collectionNum);
            //评论数
            int cmtNum = Db.queryInt("select count(ID) from node_cmt where ENABLED = 1 AND SOURCE_ID = ? ", id);
            record.set("CMT_NUM", cmtNum);
            //评论
            List<Record> cmtList = Db.paginate(1, 3, "select ID,PARENT_ID,SOURCE_ID,CON_ID,CON_NO,CON_NAME,CON_PIC,CONTENT,TO_CON_ID,TO_CON_NO,TO_CON_NAME,TO_CON_PIC,ENABLED,VERSION,STATUS,REMARK,CREATE_BY,CREATE_DT,UPDATE_DT ", " from node_cmt where ENABLED = 1 and SOURCE_ID = ? order by CREATE_DT", id).getList();
            record.set("CMT_LIST", cmtList);
        }
        return record;
    }
}
