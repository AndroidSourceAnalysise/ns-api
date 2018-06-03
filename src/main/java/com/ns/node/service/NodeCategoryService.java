/**
 * project name: ns-api
 * file name:NodeCategoryService
 * package name:com.ns.node.service
 * date:2018-03-27 15:45
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.node.service;

import com.ns.common.model.NodeCategory;
import com.ns.common.utils.DateUtil;
import com.ns.common.utils.GUIDUtil;
import sun.misc.Cache;

import java.util.List;

/**
 * description: //TODO <br>
 * date: 2018-03-27 15:45
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class NodeCategoryService {
    private final NodeCategory dao = new NodeCategory();
    public static final NodeCategoryService me = new NodeCategoryService();
    private final String COLUMN = "ID,NAME,NODE_INDEX,DESCRIPTION,ENABLED,VERSION," +
            "STATUS,REMARK,CREATE_BY,CREATE_DT,UPDATE_DT ";


    public List<NodeCategory> getList() {
        return dao.find("select " + COLUMN + " from node_category where ENABLED = 1 order by NODE_INDEX desc");
    }
    public NodeCategory getById(String id) {
        return dao.findFirst("select " + COLUMN + " from node_category where id = ?", id);
    }
}
