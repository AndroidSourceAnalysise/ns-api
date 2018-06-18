/**
 * project name: ns-api
 * file name:PntProductCmtController
 * package name:com.ns.pnt.controller
 * date:2018-02-08 16:40
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.pnt.controller;

import com.alibaba.fastjson.JSONObject;
import com.ns.common.base.BaseController;
import com.ns.common.json.JsonResult;
import com.ns.common.model.PntProductCmt;
import com.ns.common.utils.Util;
import com.ns.pnt.service.PntProductCmtService;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.tx.Tx;

/**
 * description: //TODO <br>
 * date: 2018-02-08 16:40
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class PntProductCmtController extends BaseController {
    static PntProductCmtService cmtService = PntProductCmtService.me;

    //{"CON_ID":"E03FCBB50C3C4C04B2980FCAFE14E62A","PARENT_ID":"主评论是0","SOURCE_ID":"评论时填商品ID","PHOTO_URL1":"图片1","PHOTO_URL2":"图片2","PHOTO_URL3":"图片3","CONTENT":"内容!"}
    //{"CON_ID":"E03FCBB50C3C4C04B2980FCAFE14E62A","PARENT_ID":"主评论ID","SOURCE_ID":"主评论ID","CONTENT":"内容!","TO_CON_ID":"被评论用户ID","TO_CON_NO":"1","TO_CON_NAME":""}
    @Before(Tx.class)
    public void inertCMT() {
        JSONObject json = Util.getRequestObject(getRequest(), JSONObject.class);
        PntProductCmt cmt = JSONObject.toJavaObject(json, PntProductCmt.class);
        renderJson(JsonResult.newJsonResult(cmtService.inertCMT(cmt, json.getString("ITEM_ID"))));
    }

    public void pntCmtLike() {
        String cmtId = getPara("cmtId");
        String conId = getPara("conId");
        renderJson(JsonResult.newJsonResult(cmtService.pntCmtLike(cmtId, conId)));
    }

    public void cancelLike() {
        String cmtId = getPara("cmtId");
        String conId = getPara("conId");
        renderJson(JsonResult.newJsonResult(Db.delete("delete from bas_cust_like where ENABLED = 1 AND SOURCE_ID = ? AND CON_ID = ?", cmtId, conId) > 0));
    }

    public void getPntCmtList() {
        int pageNumber = getParaToInt("page_number");
        int pageSize = getParaToInt("page_size");
        String pntId = getPara("pnt_id");
        String conId = getPara("con_id");
        renderJson(JsonResult.newJsonResult(cmtService.getPntCmtList(pageNumber, pageSize, pntId, conId)));
    }

    public void getPntCmtChildren() {
        int pageNumber = getParaToInt("pageNumber");
        int pageSize = getParaToInt("pageSize");
        String id = getPara("id");
        String conId = getPara("conId");
        renderJson(JsonResult.newJsonResult(cmtService.getPntCmtChildren(pageNumber, pageSize, id, conId)));
    }
}
