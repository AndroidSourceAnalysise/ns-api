/**
 * project name: ns-crm
 * file name:TLdPhotosController
 * package name:com.ns.tld.controller
 * date:2018-02-09 17:08
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.tld.controller;

import com.ns.common.base.BaseController;
import com.ns.common.json.JsonResult;
import com.ns.common.model.TldPhotos;
import com.ns.common.utils.Util;
import com.ns.tld.service.TldPhotosService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description: //TODO <br>
 * date: 2018-02-09 17:08
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class TLdPhotosController extends BaseController {
    static TldPhotosService tldPhotosService = TldPhotosService.me;

    public void getPhotos() {
        Map resultMap = new HashMap();
        List<TldPhotos> list = tldPhotosService.getPhotos(1, 1);
        if (list != null) {
            list.addAll(tldPhotosService.getPhotos(1, 10));
        }
        resultMap.put("top", list);
        resultMap.put("bottom", tldPhotosService.getPhotos(1, 2));
        renderJson(JsonResult.newJsonResult(resultMap));
    }

}
