/**
 * project name: ns-api
 * file name:PntMenuService
 * package name:com.ns.pnt.service
 * date:2018-04-03 10:37
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.pnt.service;

import com.ns.common.constant.RedisKeyDetail;
import com.ns.common.model.PntMenu;
import com.ns.common.model.PntProductCmt;
import com.ns.customer.service.BasCustomerService;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;

import java.util.List;

/**
 * description: //TODO <br>
 * date: 2018-04-03 10:37
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class PntMenuService {
    public static final PntMenuService me = new PntMenuService();
    private PntMenu dao = new PntMenu();
    private static final String COLUMN = "ID,MENU_NAME,MENU_DESC,MENU_TYPE,MENU_VALUE,ICON_URL,DISPLAY_SEQ,ENABLED,VERSION,STATUS,REMARK,CREATE_BY,CREATE_DT,UPDATE_DT";

    public List<PntMenu> getMenu() {
        Cache cache = Redis.use();
        if (cache.get(RedisKeyDetail.PNT_MENU) == null) {
            cache.setex(RedisKeyDetail.PNT_MENU, 1800, dao.find("select " + COLUMN + " from pnt_menu where ENABLED = 1 order by DISPLAY_SEQ"));
        }
        return cache.get(RedisKeyDetail.PNT_MENU);
    }
}
