/**
 * project name: ns-api
 * file name:NodeContentController
 * package name:com.ns.node.controller
 * date:2018-03-29 16:14
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.node.controller;

import com.ns.common.base.BaseController;
import com.ns.common.json.JsonResult;
import com.ns.common.model.NodeContent;
import com.ns.common.utils.Util;
import com.ns.node.service.NodeContentService;

/**
 * description: //TODO <br>
 * date: 2018-03-29 16:14
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class NodeContentController extends BaseController {
    static NodeContentService nodeContentService = NodeContentService.me;

    /**
     * 根据分类查笔记
     */
    public void getListByCategory() {
        String conId = getPara("conId");
        Integer pageNumber = getParaToInt("pageNumber");
        Integer pageSize = getParaToInt("pageSize");
        String categoryId = getPara("categoryId");
        renderJson(JsonResult.newJsonResult(nodeContentService.getByCategory(conId, pageNumber, pageSize, categoryId)));
    }
    public void getByConId() {
        String conId = getPara("conId");
        String myConId = getPara("myConId");
        Integer pageNumber = getParaToInt("pageNumber");
        Integer pageSize = getParaToInt("pageSize");
        renderJson(JsonResult.newJsonResult(nodeContentService.getByConId(conId, pageNumber, pageSize,myConId)));
    }

    public void getById() {
        String id = getPara("id");
        renderJson(JsonResult.newJsonResult(nodeContentService.getById2(id)));
    }

    /**
     * 写笔记 {"CATEGORY_ID":"分类ID","CONTENT":"内容","PIC1":"图片","CON_ID":"1"}
     */
    public void insert() {
        NodeContent nodeContent = Util.getRequestObject(getRequest(), NodeContent.class);
        renderJson(JsonResult.newJsonResult(nodeContentService.insert(nodeContent)));
    }

    /**
     * 删除笔记
     */
    public void delete() {
        String id = getPara("id");
        String conId = getPara("conId");
        renderJson(JsonResult.newJsonResult(nodeContentService.delete(id, conId)));
    }
}
