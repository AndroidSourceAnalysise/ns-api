/**
 * project name: ns-api
 * file name:OrderConfirmJob
 * package name:com.ns.tld.job
 * date:2018-04-27 14:53
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.tld.job;

import com.ns.common.quartzplugin.Scheduled;
import com.ns.common.utils.DateUtil;
import com.ns.tld.service.TldOrdersService;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;

/**
 * description: //TODO <br>
 * date: 2018-04-27 14:53
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
//每天0点执行一次
@Scheduled(cron = "0 0 0 * * ?")
//不允许job 并行处理
@DisallowConcurrentExecution
public class OrderConfirmJob implements Job {
    static TldOrdersService ordersService = TldOrdersService.me;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String dateTime = DateUtil.addDate(DateUtil.getNow(), -15, 0);
        List<Record> orders = Db.find("select ID,CON_ID,CREATE_DT,COUPON_GRANT_ID from tld_orders where STATUS = 6 and pay_dt <=" + "'" + dateTime + "'");
        for (Record order : orders) {
            try {
                ordersService.confirmOrder(order.getStr("ID"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
