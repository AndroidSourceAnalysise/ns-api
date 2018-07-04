/**
 * project name: ns-crm
 * file name:TldPhotosService
 * package name:com.ns.tld.service
 * date:2018-02-09 16:31
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.tld.service;

import com.ns.common.constant.RedisKeyDetail;
import com.ns.common.model.TldPhotos;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description: //TODO <br>
 * date: 2018-02-09 16:31
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class TldPhotosService {
    public static final TldPhotosService me = new TldPhotosService();
    private final TldPhotos dao = new TldPhotos();
    private final String COLUMN = "ID,RELATION_ID,SYS_ID,TYPE,URL,HREF_URL,DISPLAY_SEQ,ENABLED,VERSION," +
            "STATUS,REMARK,CREATE_BY,CREATE_DT,UPDATE_DT ";

    public List<TldPhotos> getPhotos(int sysId, int type) {
        String key = RedisKeyDetail.PHOTOS_KEY + sysId + "-" + type;
        List<TldPhotos> photosList = new ArrayList<>();
        Cache cache = Redis.use();
        if (null == cache.get(key)) {
            photosList = dao.find("select " + COLUMN + " from tld_photos where SYS_ID = ? and TYPE = ? order by DISPLAY_SEQ desc", sysId, type);
            cache.set(key, photosList);
            cache.expire(key, 1800);//设置过期时间为30分钟
            return photosList;
        }
        return cache.get(key);
    }

    public Map getByProduct(String pntId) {
        // 1微信端 2 安卓APP  3苹果APP
        // 1系统首页轮播图, 2系统首页底部图片, 3 商品详情顶部图 , 4商品详细图片, 5商品视频 6 商品详情参数图 7,企业资质认证图片 8公司简介图片 9个人中心背景图片
        List<TldPhotos> top = dao.find("select " + COLUMN + " from tld_photos where ENABLED = 1 and RELATION_ID = ? and SYS_ID=1 and TYPE=3  order by DISPLAY_SEQ desc", pntId);
        List<TldPhotos> detail = dao.find("select " + COLUMN + " from tld_photos where ENABLED = 1 and RELATION_ID = ? and SYS_ID=1 and TYPE=4  order by DISPLAY_SEQ desc", pntId);
        List<TldPhotos> video = dao.find("select " + COLUMN + " from tld_photos where ENABLED = 1 and RELATION_ID = ? and SYS_ID=1 and TYPE=5  order by DISPLAY_SEQ desc", pntId);
        List<TldPhotos> params = dao.find("select " + COLUMN + " from tld_photos where ENABLED = 1 and RELATION_ID = ? and SYS_ID=1 and TYPE=6  order by DISPLAY_SEQ desc", pntId);
        HashMap map = new HashMap();
        map.put("top", top);
        map.put("detail", detail);
        map.put("params", params);
        map.put("video", video);
        return map;
    }
}
