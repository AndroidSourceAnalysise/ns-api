/**
 * project name: ns-crm
 * file name:PntProductService
 * package name:com.ns.pnt.service
 * date:2018-02-08 15:11
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.pnt.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.ns.common.constant.RedisKeyDetail;
import com.ns.common.exception.CustException;
import com.ns.common.model.PntProduct;
import com.ns.common.model.PntSku;
import com.ns.common.model.TldPhotos;
import com.ns.tld.service.TldPhotosService;

/**
 * description: //TODO <br>
 * date: 2018-02-08 15:11
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class PntProductService {
    public static final PntProductService me = new PntProductService();
    private final PntProduct dao = new PntProduct();
    private static final String COLUMN = "ID,PRODUCT_NAME,PY_CODE,PRODUCT_CODE,SAL_PRICE,ABB_NAME,TITLE,IMAGE_URL,THUMB_URL,BRIEF_DESCRIPTION,INTEGRAL_TAG,INTEGRAL_SELF,INTEGRAL_SUP,DISPLAY_SEQ,ENABLED,VERSION," +
            "STATUS,REMARK,CREATE_BY,CREATE_DT,UPDATE_DT ";
    static PntSkuService pntSkuService = PntSkuService.me;
    static TldPhotosService tldPhotosService = TldPhotosService.me;

    public List<PntProduct> getProducts(int pageNumber, int pageSize) {
        String key = RedisKeyDetail.PRODUCT_ALL;
        Cache cache = Redis.use();
        if (null == cache.get(key)) {
            List<PntProduct> pntProducts = dao.find("select " + COLUMN + " from pnt_product where  STATUS = 1 and enabled = 1 order by DISPLAY_SEQ desc");
            cache.set(key, pntProducts);
            cache.expire(key, 1800);//设置过期时间为30分钟
        }
        return cache.get(key);
    }

    public List<PntProduct> getProductByName(String name) {
        String key = RedisKeyDetail.PRODUCT_ALL;
        Cache cache = Redis.use();
        if (null == cache.get(key)) {
            List<PntProduct> pntProducts = dao.find("select " + COLUMN + " from pnt_product where  STATUS = 1 and enabled = 1 order by DISPLAY_SEQ desc");
            cache.set(key, pntProducts);
            cache.expire(key, 1800);//设置过期时间为30分钟
        }
        List<PntProduct> pntProducts = cache.get(key);
        Iterator<PntProduct> i = pntProducts.iterator();
        while (i.hasNext()) {
            PntProduct pnt = i.next();
            if (pnt.getProductName().indexOf(name) == -1) {
                i.remove();
            }
        }
        return pntProducts;
    }

    public Map<String, Object> getProductDetial(String pntId) {
        Map<String, Object> resultMap = new HashMap<>();
        String key = RedisKeyDetail.PRODUCT_ID + pntId;
        Cache cache = Redis.use();
        if (null == cache.get(key)) {
            PntProduct pntProduct = getById(pntId);
            List<PntSku> skus = pntSkuService.getByProduct(pntId);
            List<TldPhotos> photosList = tldPhotosService.getByProduct(pntId);
            resultMap.put("product", pntProduct);
            resultMap.put("skuList", skus);
            resultMap.put("photosList", photosList);
            cache.set(key, resultMap);
            cache.expire(key, 1800);//设置过期时间为30分钟
        }
        return cache.get(key);
    }

    /**
     * 获取product，sku 库存集合
     *
     * @param pntId
     * @param skuId
     * @return
     */
    public List<Map<String, Object>> getStock(String pntId, String skuId) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        Map<String, Object> map = getProductDetial(pntId);
        List<PntSku> skus = (List<PntSku>) map.get("skuList");
        Cache cache = Redis.use();
        // 如果传进来是sku，则返回sku库存，如果不是，则判断是否是sku，如果不是返回商品库存列表
        if (StrKit.isBlank(skuId)) {
            for (PntSku s : skus) {
                Map<String, Object> stockMap = new HashMap<>();
                String sd = s.getID();
                if (StrKit.isBlank(sd)) {
                    stockMap.put(pntId, cache.getCounter(RedisKeyDetail.PRODUCT_STOCK_ID + s.getID()));
                } else {
                    stockMap.put(s.getID(), cache.getCounter(RedisKeyDetail.SKU_STOCK_ID + s.getID()));
                }
                resultList.add(stockMap);
            }
        } else {
            Map<String, Object> stockMap = new HashMap<>();
            stockMap.put(skuId, cache.getCounter(RedisKeyDetail.SKU_STOCK_ID + skuId));
            resultList.add(stockMap);
        }
        return resultList;
    }

    public PntProduct getById(String pntId) {
        return dao.findFirst("select " + COLUMN + " from pnt_product where  STATUS = 1 and enabled = 1 and id = ? ", pntId);
    }

    public PntProduct getByIdNotNull(String pntId) {
        PntProduct pntProduct = dao.findFirst("select " + COLUMN + " from pnt_product where  STATUS = 1 and enabled = 1 and id = ? ", pntId);
        if (pntProduct == null) {
            throw new CustException("您购买的商品异常！请刷新后重试！");
        }
        return pntProduct;
    }
}
