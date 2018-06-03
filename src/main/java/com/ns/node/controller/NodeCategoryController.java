/**
 * project name: ns-api
 * file name:NodeCategoryController
 * package name:com.ns.node.controller
 * date:2018-03-29 16:12
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.node.controller;

import com.ns.common.base.BaseController;
import com.ns.common.json.JsonResult;
import com.ns.node.service.NodeCategoryService;

/**
 * description: //TODO <br>
 * date: 2018-03-29 16:12
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class NodeCategoryController extends BaseController {
    static NodeCategoryService nodeCategoryService = NodeCategoryService.me;

    /**
     * 所有分类
     */
    public void getList() {
        renderJson(JsonResult.newJsonResult(nodeCategoryService.getList()));
    }
}
