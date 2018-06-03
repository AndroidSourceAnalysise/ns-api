/**
 * project name: ns-api
 * file name:NodeFocusController
 * package name:com.ns.node.controller
 * date:2018-03-30 11:52
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.node.controller;

import com.ns.common.base.BaseController;
import com.ns.common.json.JsonResult;
import com.ns.common.model.NodeFocus;
import com.ns.common.utils.Util;
import com.ns.node.service.NodeFocusService;

/**
 * description: //TODO <br>
 * date: 2018-03-30 11:52
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class NodeFocusController extends BaseController {
    static NodeFocusService nodeFocusService = NodeFocusService.me;

    public void insert() {
        String conId = getPara("conId");
        String focusConId = getPara("focusConId");
        renderJson(JsonResult.newJsonResult(nodeFocusService.insert(conId, focusConId)));
    }

    /**
     * 我的关注
     */
    public void getMyFocus() {
        String conId = getPara("conId");
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 10);
        renderJson(JsonResult.newJsonResult(nodeFocusService.getByConId(pageNumber, pageSize, conId)));
    }

    public void cancel() {
        String conId = getPara("conId");
        String focusConId = getPara("focusConId");
        renderJson(JsonResult.newJsonResult(nodeFocusService.cancel(conId, focusConId)));
    }
}
