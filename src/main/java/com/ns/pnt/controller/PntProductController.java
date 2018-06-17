/**
 * project name: ns-crm
 * file name:PntProductController
 * package name:com.ns.pnt.controller
 * date:2018-02-08 15:09
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.pnt.controller;

import com.ns.common.base.BaseController;
import com.ns.common.constant.RedisKeyDetail;
import com.ns.common.json.JsonResult;
import com.ns.common.model.PntProduct;
import com.ns.common.utils.Util;
import com.ns.pnt.service.PntProductService;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;

import java.util.List;

/**
 * description: //TODO <br>
 * date: 2018-02-08 15:09
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class PntProductController extends BaseController {
    static PntProductService pntProductService = PntProductService.me;

    public void getProductList() {
        renderJson(JsonResult.newJsonResult(pntProductService.getProducts(getParaToInt("page_number", 1), getParaToInt("page_size", 100))));
    }
    public void getProductByName() {
        renderJson(JsonResult.newJsonResult(pntProductService.getProductByName(getPara("name"))));
    }

    public void getProductById() {
        renderJson(JsonResult.newJsonResult(pntProductService.getProductDetial(getPara("pnt_id"))));
    }

    public void getStock() {
        renderJson(JsonResult.newJsonResult(pntProductService.getStock(getPara("pnt_id"), getPara("sku_id"))));
    }

    public void getCategories() {
        Cache cache = Redis.use();
        if (cache.get(RedisKeyDetail.PNT_CATEGORIES) == null) {
            List<Record> record = Db.find("select * from pnt_categories where ENABLED = 1  order by DISPLAY_SEQ");
            cache.set(RedisKeyDetail.PNT_CATEGORIES, record);
        }
        renderJson(JsonResult.newJsonResult(cache.get(RedisKeyDetail.PNT_CATEGORIES)));
    }
}
