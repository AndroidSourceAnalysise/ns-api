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
import java.util.List;

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

    public List<TldPhotos> getByProduct(String pntId) {
        return dao.find("select " + COLUMN + " from tld_photos where ENABLED = 1 and RELATION_ID = ? order by DISPLAY_SEQ desc", pntId);
    }
}
