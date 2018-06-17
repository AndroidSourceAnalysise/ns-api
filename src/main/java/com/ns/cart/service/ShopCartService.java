package com.ns.cart.service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.ns.common.model.BasCustomerCart;
import com.ns.common.utils.DateUtil;

import java.util.Map;
import java.util.UUID;

public class ShopCartService {
    private static final BasCustomerCart dao = BasCustomerCart.dao;
    private static final String LIST_SQL = "select t.cart_id,t.product_id,t.product_name,t.product_image_url,t.sku_id,t.sku_name,t.sku_image_url,t.product_num,t.product_price,t.re_product_price,t.sku_price,t.re_sku_price,t.is_selected from bas_customer_cart t where t.con_id=?";
    private static final String SELECT_ALL_SQL = "update bas_customer_cart set is_selected=? where con_id=?";
    private static final String SELECT_SQL = "update bas_customer_cart set is_selected=? where cart_id=?";
    private static final String QUERY_SQL = "select t.cart_id,t.product_id,t.product_name,t.product_image_url,t.sku_id,t.sku_name,t.sku_image_url,t.product_num,t.product_price,t.re_product_price,t.sku_price,t.re_sku_price,t.is_selected from bas_customer_cart t where t.product_id=? and t.sku_id=? and con_id=?";


    public static boolean add(Map<String, Object> params) {
        Record r = Db.findFirst(QUERY_SQL, params.get("product_id"), params.get("sku_id"));
        if (r == null) {
            Record record = new Record();
            params.put("cart_id", UUID.randomUUID().toString());
            params.put("creator", "ns");
            params.put("create_time", DateUtil.getNow());
            params.put("update_time", DateUtil.getNow());
            record.setColumns(params);
            return Db.save("bas_customer_cart", record);
        } else {
            r.set("product_num", r.getInt("product_num") + (int) params.get("product_num"));
            return Db.update("bas_customer_cart", r);
        }
    }

    public static Object list(String conId) {
        return dao.find(LIST_SQL, conId);
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

    public static boolean selectAll(int allSelect, String conId) {
        return Db.update(SELECT_ALL_SQL, allSelect, conId) > 0;
    }

    public static boolean select(int isSelect, String conId) {
        return Db.update(SELECT_SQL, isSelect, conId) > 0;
    }

}
