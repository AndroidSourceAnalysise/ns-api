package com.ns.cart.service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.ns.common.model.BasCustomerCart;
import com.ns.common.utils.DateUtil;

import java.util.Map;

public class ShopCartService {
    private static final BasCustomerCart dao = BasCustomerCart.dao;
    private static final String LIST_SQL = "select t.cart_id,t.product_id,t.product_name,t.product_image_url,t.sku_id,t.sku_name,t.sku_image_url,t.product_num,t.product_price,t.re_product_price,t.sku_price,t.re_sku_price,t.is_selected from bas_customer_cart t";
    private static final String SELECT_ALL_SQL = "update bas_customer_cart set is_selected=?";
    private static final String SELECT_SQL = "update bas_customer_cart set is_selected=? where cart_id=?";


    public static boolean add(Map<String, Object> params) {
        Record record = new Record();
        params.put("creator", "ns");
        params.put("create_time", DateUtil.getNow());
        params.put("update_time", DateUtil.getNow());
        record.setColumns(params);
        return Db.save("bas_customer_cart", record);
    }

    public static Object list() {
        return dao.find(LIST_SQL);
    }

    public static boolean delete(String cartId) {
        return dao.deleteById(cartId);
    }

    public static boolean update(Map<String, Object> params) {
        Record record = new Record();
        params.put("update_time", DateUtil.getNow());
        record.setColumns(params);
        return Db.update("bas_customer_cart", record);
    }

    public static boolean selectAll(int allSelect) {
        return Db.update(SELECT_ALL_SQL, allSelect) > 0;
    }

    public static boolean select(int isSelect) {
        return Db.update(SELECT_SQL, isSelect) > 0;
    }

}
