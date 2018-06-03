/**
 * project name: ns-api
 * file name:TldSiteMsgController
 * package name:com.ns.tld.controller
 * date:2018-02-12 16:22
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.tld.controller;

import com.ns.common.base.BaseController;
import com.ns.common.json.JsonResult;
import com.ns.common.model.TldSiteMsg;
import com.ns.common.utils.Util;
import com.ns.tld.service.TldSiteMsgService;

/**
 * description: //TODO <br>
 * date: 2018-02-12 16:22
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class TldSiteMsgController extends BaseController {
    static TldSiteMsgService msgService = TldSiteMsgService.me;

    //{"SENDID":"发送者id，0：表示系统发送","RECID":"接收者会员id","MSG_CONTENT":"内容","TYPE":"1会员通知,2订单通知,3系统通知"}
    public void addMsg() {
        TldSiteMsg msg = Util.getRequestObject(getRequest(), TldSiteMsg.class);
        renderJson(JsonResult.newJsonResult(msgService.addMsg(msg)));
    }

    public void getMsg() {
        String conId = getPara("conId");
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);
        String type = getPara("type");
        renderJson(JsonResult.newJsonResult(msgService.getMsg(pageNumber, pageSize, conId, type)));
    }
}
