package com.ns.cart.controller;

import com.ns.cart.service.ShopCartService;
import com.ns.common.base.BaseController;
import com.ns.common.json.JsonResult;

import java.util.HashMap;
import java.util.Map;


public class ShopCartController extends BaseController {
    /**
     * 添加商品到购物车
     */
    public void add() {
        Map<String, Object> params = getRequestObject(getRequest(), HashMap.class);
        renderJson(JsonResult.newJsonResult(ShopCartService.add(params)));
    }

    /**
     * 删除商品
     */
    public void delete() {
        final String cartId = (String) getRequestObject(getRequest(), HashMap.class).get("cartId");
        renderJson(JsonResult.newJsonResult(ShopCartService.delete(cartId)));
    }

    /**
     * 购物车列表
     */
    public void list() {
        String conId = (String) getRequestObject(getRequest(), HashMap.class).get("conId");
        renderJson(JsonResult.newJsonResult(ShopCartService.list(conId)));
    }


    /**
     * 更新购物车商品
     */
    public void update() {
        Map<String, Object> params = getRequestObject(getRequest(), HashMap.class);
        renderJson(JsonResult.newJsonResult(ShopCartService.update(params)));
    }

    /**
     * 选正或反选所有商品
     */
    public void selectAll() {
        Map params = getRequestObject(getRequest(), HashMap.class);
        final int allSelect = (int) params.get("allSelect");
        final String conId = (String) params.get("conId");
        renderJson(JsonResult.newJsonResult(ShopCartService.selectAll(allSelect, conId)));
    }

    /**
     * 选正单个或反选单个商品
     */
    public void select() {
        Map params = getRequestObject(getRequest(), HashMap.class);
        final int isSelect = (int) params.get("isSelect");
        final String conId = (String) params.get("conId");
        renderJson(JsonResult.newJsonResult(ShopCartService.select(isSelect, conId)));
    }
}
