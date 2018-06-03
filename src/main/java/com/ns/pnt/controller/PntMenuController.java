/**
 * project name: ns-api
 * file name:PntMenuController
 * package name:com.ns.pnt.controller
 * date:2018-04-03 10:36
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.pnt.controller;

import com.ns.common.base.BaseController;
import com.ns.common.json.JsonResult;
import com.ns.pnt.service.PntMenuService;

/**
 * description: //TODO <br>
 * date: 2018-04-03 10:36
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class PntMenuController extends BaseController {
    static PntMenuService service = PntMenuService.me;

    public void getList() {
        renderJson(JsonResult.newJsonResult(service.getMenu()));
    }
}
