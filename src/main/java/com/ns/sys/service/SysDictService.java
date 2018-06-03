/**
 * project name: ns-crm
 * file name:SysDictService
 * package name:com.ns.sys.service
 * date:2018-02-12 16:04
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.sys.service;

import com.ns.common.model.SysDict;
import com.ns.common.utils.DateUtil;
import com.ns.common.utils.GUIDUtil;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;

/**
 * description: //TODO <br>
 * date: 2018-02-12 16:04
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class SysDictService {
    public static final SysDictService me = new SysDictService();
    private final SysDict dao = SysDict.dao;
    private static final String COLUMN = "ID,GROUP_NAME,GROUP_CODE,PARAM_KEY,PARAM_VALUE,ENABLED,VERSION,STATUS,REMARK,CREATE_BY,CREATE_DT,UPDATE_DT ";

    public boolean addSysDict(SysDict sysDict) {
        sysDict.setID(GUIDUtil.getGUID());
        sysDict.setUpdateDt(DateUtil.getNow());
        sysDict.setCreateDt(DateUtil.getNow());
        return sysDict.save();
    }


    public boolean addSysDict(String groupName, String groupCode, String paramKey, String paramValue) {
        SysDict sysDict = new SysDict();
        sysDict.setID(GUIDUtil.getGUID());
        sysDict.setGroupName(groupName);
        sysDict.setGroupCode(groupCode);
        sysDict.setParamKey(paramKey);
        sysDict.setParamValue(paramValue);
        sysDict.setUpdateDt(DateUtil.getNow());
        sysDict.setCreateDt(DateUtil.getNow());
        return sysDict.save();
    }


    public String getByParamKey(String paramKey) {
        Cache cache = Redis.use();
        String paramValue = cache.get(paramKey);
        if (StrKit.isBlank(paramValue)) {
            paramValue = Db.queryStr("select PARAM_VALUE from sys_dict where ENABLED = 1 and PARAM_KEY = ?", paramKey);
        }
        return paramValue;

    }
}
