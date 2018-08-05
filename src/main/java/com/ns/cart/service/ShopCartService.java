package com.ns.cart.service;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.ns.common.model.BasCustomerCart;
import com.ns.common.utils.DateUtil;

import java.util.*;

public class ShopCartService {
    private static final BasCustomerCart dao = BasCustomerCart.dao;
    private static final String COLUMN = "select t.cart_id,t.brief_description,t.product_id,t.product_name,t.image_url,t.sku_id,t.sku_name,t.sku_image_url,t.product_num,t.sal_price,t.re_sal_price,t.sku_sal_price,t.re_sku_sal_price,t.is_selected";

    private static final String SELECT_ALL_SQL = "update bas_customer_cart set is_selected=? where con_id=?";
    private static final String SELECT_SQL = "update bas_customer_cart set is_selected=? where cart_id=?";
    private static final String QUERY_SQL = "select t.cart_id,t.product_id,t.product_name,t.image_url,t.sku_id,t.sku_name,t.sku_image_url,t.product_num,t.sal_price,t.re_sal_price,t.sku_sal_price,t.re_sku_sal_price,t.is_selected from bas_customer_cart t";
    private static final String WHERE1 = " where t.product_id=? and con_id=?";
    private static final String WHERE2 = " where t.product_id=? and sku_id=? and con_id=?";


    public static boolean add(Map<String, Object> params, String conId) {
        final String productId = (String) params.get("product_id");
        final String skuId = (String) params.get("sku_id");
        Record r;
        if (StrKit.isBlank(skuId)) {
            r = Db.findFirst(QUERY_SQL + WHERE1, productId, conId);
        } else {
            r = Db.findFirst(QUERY_SQL + WHERE2, productId, skuId, conId);
        }
        if (r == null) {
            Record record = new Record();
            params.put("cart_id", UUID.randomUUID().toString());
            params.put("con_id", conId);
            params.put("creator", "ns");
            params.put("create_time", DateUtil.getNow());
            params.put("update_time", DateUtil.getNow());
            record.setColumns(params);
            return Db.save("bas_customer_cart", record);
        } else {
            r.set("cart_id", r.getStr("cart_id"));
            r.set("product_num", r.getInt("product_num") + (int) params.get("product_num"));
            return Db.update("bas_customer_cart", "cart_id", r);
        }
    }

    public static Object list(int start, int size, String conId) {
        return dao.paginate(start, size, COLUMN, "from bas_customer_cart t where t.con_id=?", conId);
    }

    public static boolean delete(String cartId) {
        return dao.deleteById(cartId);
    }

    /**
     * 删除购物车产品
     *
     * @param productId
     * @return
     */
    public static ArrayList deleteByProductId(ArrayList<String> productId) {
        ArrayList<String> rs = new ArrayList<>();
        for (String pd : productId) {
            final int c = Db.delete("delete from bas_customer_cart where product_id=? or sku_id=?", productId, productId);
            if (c <= 0) {
                rs.add(pd);
            }
        }
        return rs;
    }

    public static List<Map<String, Object>> update(List<HashMap> params) {
        ArrayList<Map<String, Object>> mapArrayList = new ArrayList<>();
        for (Map<String, Object> param : params) {
            Record record = new Record();
            param.put("update_time", DateUtil.getNow());
            record.setColumns(param);
            final boolean rs = Db.update("bas_customer_cart", "cart_id", record);
            if (!rs) {
                mapArrayList.add(param);
            }
        }
        return mapArrayList;
    }

    public static boolean selectAll(int allSelect, String conId) {
        return Db.update(SELECT_ALL_SQL, allSelect, conId) > 0;
    }

    public static boolean select(int isSelect, String cartId) {
        return Db.update(SELECT_SQL, isSelect, cartId) > 0;
    }

}
