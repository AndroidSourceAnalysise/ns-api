/**
 * project name: ns-api
 * file name:PntProductCmtService
 * package name:com.ns.pnt.service
 * date:2018-02-08 16:26
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.pnt.service;

import com.ns.common.exception.CustException;
import com.ns.common.model.BasCustLike;
import com.ns.common.model.BasCustomer;
import com.ns.common.model.PntProductCmt;
import com.ns.common.utils.DateUtil;
import com.ns.common.utils.GUIDUtil;
import com.ns.customer.service.BasCustomerService;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

/**
 * description: //TODO <br>
 * date: 2018-02-08 16:26
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class PntProductCmtService {
    public static final PntProductCmtService me = new PntProductCmtService();
    private PntProductCmt dao = new PntProductCmt();
    static BasCustomerService basCustomerService = BasCustomerService.me;
    private static final String COLUMN = "ID,PARENT_ID,SOURCE_ID,CON_ID,CON_NO,CON_NAME,PIC,PHOTO_URL1,PHOTO_URL2,PHOTO_URL3,CONTENT,LIKE_QTY,TO_CON_ID,TO_CON_NO,TO_CON_NAME,ENABLED,VERSION,STATUS,REMARK,CREATE_BY,CREATE_DT,UPDATE_DT";

    /**
     * 插入主评论
     *
     * @param pntProductCmt
     * @return
     */
    public boolean inertCMT(PntProductCmt pntProductCmt, String itemID) {
        pntProductCmt.setID(GUIDUtil.getGUID());
        pntProductCmt.setConId(pntProductCmt.getConId());
        pntProductCmt.setConName("系统回复");
        pntProductCmt.setConNo("0");
        pntProductCmt.setPIC("");
        //0是系统回复
        if (!"0".equals(pntProductCmt.getConId())) {
            BasCustomer customer = basCustomerService.getCustomerByIdNotNull(pntProductCmt.getConId());
            pntProductCmt.setConId(pntProductCmt.getConId());
            pntProductCmt.setConName(customer.getConName());
            pntProductCmt.setConNo(customer.getConNo());
            pntProductCmt.setPIC(customer.getPIC());
        }
        pntProductCmt.setUpdateDt(DateUtil.getNow());
        pntProductCmt.setCreateDt(DateUtil.getNow());

        if (pntProductCmt.save()) {
            if (pntProductCmt.getParentId().equals("0")) {
                Db.update("update tld_order_items set COMMENT_TAG = 1 where id = ?", itemID);
            }
        }
        return true;
    }

    /**
     * 商品评论点赞
     *
     * @param cmtId 评论主键
     * @param conId
     */
    public boolean pntCmtLike(String cmtId, String conId) {
        int likeNum = Db.queryInt("select count(*) from bas_cust_like where source_id = ? and con_id = ?", cmtId, conId);
        if (likeNum > 0) {
            throw new CustException("重复点赞!");
        }
        BasCustLike basCustLike = new BasCustLike();
        basCustLike.setID(GUIDUtil.getGUID());
        basCustLike.setSourceId(cmtId);
        basCustLike.setConId(conId);
        basCustLike.setUpdateDt(DateUtil.getNow());
        basCustLike.setCreateDt(DateUtil.getNow());
        basCustLike.save();
        //评论点赞数+
        boolean rs = Db.update("update pnt_product_cmt set like_qty = like_qty+1 where id = ?", cmtId) > 0;
        return rs;
    }

    public Page<Record> getPntCmtList(int pageNumber, int pageSize, String pntId, String conId) {
        StringBuffer sqlExceptSelect = new StringBuffer(" from pnt_product_cmt where enabled = 1 and SOURCE_ID = ? order by CREATE_DT");
        Page<Record> page = Db.paginate(pageNumber, pageSize, "select " + COLUMN + " ", sqlExceptSelect.toString(), pntId);
        for (Record r : page.getList()) {
            Page<Record> page2 = Db.paginate(1, 3, "select " + COLUMN + " ", "from pnt_product_cmt where enabled = 1 and CON_ID != '0' and SOURCE_ID = ? order by CREATE_DT", r.getStr("ID"));
            for (Record record : page2.getList()) {
                record.set("IS_LIKE", Db.queryInt("select count(*) from bas_cust_like where source_id = ? and con_id = ?", record.getStr("ID"), conId) > 0);
            }
            //系统回复
            List<Record> SYS_REVERT = Db.find("select " + COLUMN + " from pnt_product_cmt where enabled = 1 and CON_ID = '0' and to_con_id = ? and SOURCE_ID = ?", r.getStr("CON_ID"), r.getStr("ID"));
            r.set("REPLY_NUM", page2.getTotalRow());
            r.set("SYS_REVERT", SYS_REVERT);
            r.set("CHILDREN_CMT", page2.getList());
            r.set("IS_LIKE", Db.queryInt("select count(*) from bas_cust_like where source_id = ? and con_id = ?", r.getStr("ID"), conId) > 0);
        }
        return page;
    }

    public Page<Record> getPntCmtChildren(int pageNumber, int pageSize, String id, String conId) {
        Page<Record> page2 = Db.paginate(pageNumber, pageSize, "select " + COLUMN + " ", "from pnt_product_cmt where enabled = 1 and SOURCE_ID = ? order by CREATE_DT desc", id);
        for (Record record : page2.getList()) {
            record.set("IS_LIKE", Db.queryInt("select count(*) from bas_cust_like where source_id = ? and con_id = ?", record.getStr("ID"), conId) > 0);
        }
        return page2;
    }
}
