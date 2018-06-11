/**
 * project name: ns-api
 * file name:NodeFocusService
 * package name:com.ns.node.service
 * date:2018-03-29 15:25
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.node.service;

import com.ns.common.exception.CustException;
import com.ns.common.model.NodeFocus;
import com.ns.common.utils.DateUtil;
import com.ns.common.utils.GUIDUtil;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;


/**
 * description: //TODO <br>
 * date: 2018-03-29 15:25
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class NodeFocusService {
    private final NodeFocus dao = new NodeFocus();
    public static final NodeFocusService me = new NodeFocusService();
    private final String COLUMN = "ID,CON_ID,CON_NO,CON_NAME,CON_PIC,FOCUS_CON_ID,FOCUS_CON_NO,FOCUS_CON_NAME,FOCUS_CON_PIC,ENABLED,VERSION," +
            "STATUS,REMARK,CREATE_BY,CREATE_DT,UPDATE_DT ";

    public boolean insert(String conId, String focusConId) {
        boolean isFocus = Db.queryInt("select count(*) from node_focus where ENABLED = 1 and CON_ID = ? and FOCUS_CON_ID = ?", conId, focusConId) > 0;
        if (isFocus) {
            throw new CustException("重复关注!");
        }
        Record customer = Db.findFirst("select id,CON_NO,CON_NAME,PIC from bas_customer where ENABLED = 1 and ID = ?", conId);
        if (customer == null) {
            throw new CustException("找不到会员信息!");
        }
        Record customer2 = Db.findFirst("select id,CON_NO,CON_NAME,PIC from bas_customer where ENABLED = 1 and ID = ?", focusConId);
        if (customer2 == null) {
            throw new CustException("找不到被关注着会员信息!");
        }
        NodeFocus nodeFocus = new NodeFocus();
        nodeFocus.setID(GUIDUtil.getGUID());
        nodeFocus.setConId(conId);
        nodeFocus.setConNo(customer.getStr("CON_NO"));
        nodeFocus.setConName(customer.getStr("CON_NAME"));
        nodeFocus.setConPic(customer.getStr("PIC"));
        nodeFocus.setFocusConId(focusConId);
        nodeFocus.setFocusConNo(customer2.getStr("CON_NO"));
        nodeFocus.setFocusConName(customer2.getStr("CON_NAME"));
        nodeFocus.setFocusConPic(customer2.getStr("PIC"));
        nodeFocus.setCreateDt(DateUtil.getNow());
        nodeFocus.setUpdateDt(DateUtil.getNow());
        return nodeFocus.save();
    }

    //我的关注列表
    public Page<NodeFocus> getByConId(int pageNumber, int pageSize, String conId) {
        return  dao.paginate(pageNumber,pageSize,"select " + COLUMN ," from node_focus where ENABLED = 1 and CON_ID = ?",conId);
    }

    //{"ID":"","ENABLED":"0"}
    public boolean update(NodeFocus nodeFocus) {
        nodeFocus.setUpdateDt(DateUtil.getNow());
        return nodeFocus.update();
    }
    public boolean cancel(String conId, String focusConId) {
        return Db.delete("delete from node_focus where FOCUS_CON_ID = ? AND CON_ID = ?", focusConId, conId) > 0;
    }
}
