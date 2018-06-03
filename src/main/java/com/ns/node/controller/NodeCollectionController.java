/**
 * project name: ns-api
 * file name:NodeCollectionController
 * package name:com.ns.node.controller
 * date:2018-03-30 11:47
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.node.controller;

import com.ns.common.base.BaseController;
import com.ns.common.json.JsonResult;
import com.ns.common.model.NodeCollection;
import com.ns.common.utils.Util;
import com.ns.node.service.NodeCollectionService;

/**
 * description: //TODO <br>
 * date: 2018-03-30 11:47
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class NodeCollectionController extends BaseController {
    static NodeCollectionService nodeCollectionService = NodeCollectionService.me;

    //{"SOURCE_ID":"笔记id","CON_ID","会员id"}
    public void insert() {
        NodeCollection nodeCollection = Util.getRequestObject(getRequest(), NodeCollection.class);
        renderJson(JsonResult.newJsonResult(nodeCollectionService.insert(nodeCollection)));
    }

    //{"ID":"笔记id","CON_ID","1"}
    public void cancel() {
        NodeCollection nodeCollection = Util.getRequestObject(getRequest(), NodeCollection.class);
        renderJson(JsonResult.newJsonResult(nodeCollectionService.cancel(nodeCollection.getSourceId(), nodeCollection.getConId())));
    }

    public void getByConId() {
        String conId = getPara("conId");
        Integer pageNumber = getParaToInt("pageNumber");
        Integer pageSize = getParaToInt("pageSize");
        renderJson(JsonResult.newJsonResult(nodeCollectionService.getByConId(conId, pageNumber, pageSize)));
    }
}
