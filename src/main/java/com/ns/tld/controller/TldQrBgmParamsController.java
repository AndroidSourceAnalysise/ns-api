/**
 * project name: ns-api
 * file name:TldQrBgmParamsController
 * package name:com.ns.tld.controller
 * date:2018-03-02 10:38
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.tld.controller;

import com.ns.common.base.BaseController;
import com.ns.common.json.JsonResult;
import com.ns.tld.service.TLdQrBgmParamsService;

/**
 * description: //TODO <br>
 * date: 2018-03-02 10:38
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class TldQrBgmParamsController extends BaseController {
    static TLdQrBgmParamsService service = TLdQrBgmParamsService.me;

    public void getQrBgmList() {
        renderJson(JsonResult.newJsonResult(service.getTldQrbgmParamsList()));
    }
}
