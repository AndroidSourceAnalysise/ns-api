/**
 * project name: ns-api
 * file name:NodeLikeController
 * package name:com.ns.node.controller
 * date:2018-03-29 16:34
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.node.controller;

import com.ns.common.base.BaseController;
import com.ns.common.json.JsonResult;
import com.ns.common.model.NodeLike;
import com.ns.common.utils.Util;
import com.ns.node.service.NodeLikeService;

/**
 * description: //TODO <br>
 * date: 2018-03-29 16:34
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class NodeLikeController extends BaseController {
    static NodeLikeService likeService = NodeLikeService.me;

    //{"SOURCE_ID":"笔记id","CON_ID":"1"}
    public void insert() {
        NodeLike nodeLike = Util.getRequestObject(getRequest(), NodeLike.class);
        renderJson(JsonResult.newJsonResult(likeService.insert(nodeLike)));
    }

    public void cancel() {
        renderJson(JsonResult.newJsonResult(likeService.cancel(getPara("contentId"), getPara("conId"))));
    }
}
