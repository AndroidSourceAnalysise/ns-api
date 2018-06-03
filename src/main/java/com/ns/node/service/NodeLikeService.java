/**
 * project name: ns-api
 * file name:NodeLikeService
 * package name:com.ns.node.service
 * date:2018-03-27 19:51
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.node.service;

import com.ns.common.exception.CustException;
import com.ns.common.model.NodeLike;
import com.ns.common.utils.DateUtil;
import com.ns.common.utils.GUIDUtil;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

/**
 * description: //TODO <br>
 * date: 2018-03-27 19:51
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class NodeLikeService {
    private final NodeLike dao = new NodeLike();
    public static final NodeLikeService me = new NodeLikeService();
    private final String COLUMN = "ID,SOURCE_ID,CON_ID,CON_NO,CON_NAME,ENABLED,VERSION," +
            "STATUS,REMARK,CREATE_BY,CREATE_DT,UPDATE_DT ";

    //{"SOURCE_ID":"笔记id","CON_ID":"1"}
    public boolean insert(NodeLike nodeLike) {
        Record customer = Db.findFirst("select id,CON_NO,CON_NAME,PIC from bas_customer where ENABLED = 1 and ID = ?", nodeLike.getConId());
        if (customer == null) {
            throw new CustException("找不到会员信息!");
        }
        boolean isLike = Db.queryInt("select count(ID) from node_like where ENABLED = 1 AND SOURCE_ID = ? AND CON_ID = ?", nodeLike.getSourceId(), nodeLike.getConId()) > 0;
        if (isLike) {
            throw new CustException("不能重复点赞!");
        }
        nodeLike.setID(GUIDUtil.getGUID());
        nodeLike.setConNo(customer.getStr("CON_NO"));
        nodeLike.setConName(customer.getStr("CON_NAME"));
        nodeLike.setCreateDt(DateUtil.getNow());
        nodeLike.setUpdateDt(DateUtil.getNow());
        return nodeLike.save();
    }

    public boolean cancel(String contentId, String conId) {
        return Db.delete("delete from node_like where ENABLED = 1 AND SOURCE_ID = ? AND CON_ID = ?", contentId, conId) > 0;
    }
}
