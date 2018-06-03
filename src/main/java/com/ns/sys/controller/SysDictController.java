/**
 * project name: ns-crm
 * file name:SysDictController
 * package name:com.ns.sys.controller
 * date:2018-02-12 16:12
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.sys.controller;

import com.ns.common.base.BaseController;
import com.ns.common.json.JsonResult;
import com.ns.common.model.SysDict;
import com.ns.common.utils.Util;
import com.ns.sys.service.SysDictService;

/**
 * description: //TODO <br>
 * date: 2018-02-12 16:12
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class SysDictController extends BaseController {
    static SysDictService sysDictService = SysDictService.me;


    public void getByParamKey() {
        String paramKey = getPara("paramKey");
        renderJson(JsonResult.newJsonResult(sysDictService.getByParamKey(paramKey)));
    }
}
