/**
 * project name: ns-api
 * file name:RedisController
 * package name:com.ns.tld.controller
 * date:2018-03-08 18:20
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.tld.controller;

import com.ns.common.base.BaseController;
import com.ns.common.constant.RedisKeyDetail;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;

/**
 * description: //TODO <br>
 * date: 2018-03-08 18:20
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class RedisController extends BaseController {


    public void getKey() {
        Cache cache = Redis.use();
        renderJson(cache.get(getPara("key")));
    }

    public void getCounter() {
        Cache cache = Redis.use();
        renderJson(cache.getCounter(getPara("key")));
    }

    public void delKey() {
        Cache cache = Redis.use();
        renderJson(cache.del(getPara("key")));
    }

    public void aaa() {
        renderJson(Redis.use().incr(RedisKeyDetail.CON_NO_SEQ));
    }

    public void set() {
        renderJson(Redis.use().incrBy(RedisKeyDetail.CON_NO_SEQ, 98));
    }
}
