/**
 * project name: ns-api
 * file name:OrderConfirmJob
 * package name:com.ns.tld.job
 * date:2018-04-27 14:53
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.tld.job;

import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.jfinal.plugin.activerecord.Db;
import com.ns.common.quartzplugin.Scheduled;
import com.ns.common.utils.DateUtil;
import com.ns.tld.service.TldTwitterService;

/**
 * 推客信息表月度定时任务.
 * 1. 备份表
 * 2. 清空月度销售额
 * **/
@Scheduled(cron = "0 5 0 1 * ?")
public class TwitterMonthJob implements Job {
    static TldTwitterService twitterService = TldTwitterService.me;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        backUpTwitterTable();
        cleanMonthScale();
    }

    private void backUpTwitterTable() {
    	String lastMonthStr = DateUtil.getMonthStr(-1,0,"yyyy_MM");
    	String bak_table_name = "tld_twitter".concat("_").concat(lastMonthStr); 
    	Db.update("create table " + bak_table_name + " as select * from tld_twitter");
    }
    
    private void cleanMonthScale() {
		Db.update("update tld_twitter set month_sale = 0 ");
	}

}
