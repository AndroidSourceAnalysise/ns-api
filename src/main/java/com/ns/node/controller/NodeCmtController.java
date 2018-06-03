/**
 * project name: ns-api
 * file name:NodeCmtController
 * package name:com.ns.node.service
 * date:2018-03-29 19:16
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.node.controller;

import com.ns.common.base.BaseController;
import com.ns.common.json.JsonResult;
import com.ns.common.model.NodeCmt;
import com.ns.common.utils.Util;
import com.ns.node.service.NodeCmtService;
import com.ns.node.service.NodeContentService;
import com.jfinal.plugin.activerecord.Db;

import java.util.jar.JarOutputStream;

/**
 * description: //TODO <br>
 * date: 2018-03-29 19:16
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class NodeCmtController extends BaseController {
    static NodeCmtService nodeCmtService = NodeCmtService.me;

    //回复评论:{"SOURCE_ID":"笔记id","CON_ID":"会员ID","CONTENT":"内容","TO_CON_ID":"被评论者"}
    //评论笔记:{"SOURCE_ID":"笔记id","CON_ID":"会员ID","CONTENT":"内容"}
    public void comment() {
        NodeCmt cmt = Util.getRequestObject(getRequest(), NodeCmt.class);
        renderJson(JsonResult.newJsonResult(nodeCmtService.comment(cmt)));
    }

    public void delete() {
        String id = getPara("id");
        String conId = getPara("conId");
        renderJson(JsonResult.newJsonResult(nodeCmtService.delete(id, conId)));
    }

    public void getNodeCmt() {
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 10);
        String nodeContentId = getPara("nodeContentId");
        renderJson(JsonResult.newJsonResult(nodeCmtService.getNodeCmt(pageNumber, pageSize, nodeContentId)));
    }

    public void getUnreadList() {
        String conId = getPara("conId");
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 10);
        renderJson(JsonResult.newJsonResult(nodeCmtService.getUnreadList(conId, pageNumber, pageSize)));
    }

    public void updateStatus() {
        boolean result = Db.update("update node_cmt set status = 1 where  ENABLED = 1 and status = 0 and id = ? ", getPara("id")) > 0;
        renderJson(JsonResult.newJsonResult(result));
    }

    public void getUnreadNum() {
        String conId = getPara("conId");
        renderJson(JsonResult.newJsonResult(nodeCmtService.getUnreadNum(conId)));
    }
}
