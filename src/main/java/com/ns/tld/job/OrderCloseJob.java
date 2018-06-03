/**
 * project name: ns-api
 * file name:OrderCloseJob
 * package name:com.ns.tld.job
 * date:2018-03-16 15:40
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.tld.job;

import com.ns.common.constant.RedisKeyDetail;
import com.ns.common.quartzplugin.Scheduled;
import com.ns.common.utils.DateUtil;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.redis.Redis;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * description: 关闭未付款订单定时任务//TODO <br>
 * date: 2018-03-16 15:40
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
//执行间隔  每5分钟执行一次
@Scheduled(fixedDelay = 1000 * 60 * 5)
//不允许job 并行处理
@DisallowConcurrentExecution
public class OrderCloseJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        List<Record> orders = Db.find("select ID,CON_ID,CREATE_DT,COUPON_GRANT_ID from tld_orders where STATUS = 1");
        for (Record order : orders) {
            try {
                closeOrder(order);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void closeOrder(Record order) {
        String orderId = order.getStr("ID");
        String conId = order.getStr("CON_ID");
        String dateTime = DateUtil.getNow();
        String createDt = order.getStr("CREATE_DT");
        String couponGrantId = order.getStr("COUPON_GRANT_ID");
        //获取两个时间的时间差(分钟)
        long diff = DateUtil.getTimeDiff(dateTime, createDt, 3);
        if (diff > 30) {
            //判断该订单是否使用优惠券
            if (StrKit.notBlank(couponGrantId)) {
                Db.update("update tld_coupon_grant set STATUS = 0 where id = ?", couponGrantId);
            }
            //查询该订单是否使用积分抵扣
            Record pointGrans = Db.findFirst("select ID,POINTS_QTY from bas_cust_point_trans where POINTS_TYPE = 3 and FROM_ORDER_ID = ?", orderId);
            if (pointGrans != null) {
                int point = pointGrans.getInt("POINTS_QTY");
                Db.update("update bas_customer_ext set POINTS_ENABLED=POINTS_ENABLED+? where con_id = ?", point, conId);
                Db.delete("delete FROM  bas_cust_point_trans where id = ?", pointGrans.getStr("ID"));
            }

            String remark = "未付款自动关闭";
            Db.update("update tld_orders set STATUS = 11,VERSION = VERSION +1,UPDATE_DT = ?,REMARK = ? where id = ?", dateTime, remark, orderId);
            //还原库存
            List<Record> itemsList = Db.find("select SKU_ID,QUANTITY from tld_order_items where order_id = ?", orderId);
            Jedis cache = Redis.use().getJedis();
            try {
                for (Record items : itemsList) {
                    cache.incrBy(RedisKeyDetail.SKU_STOCK_ID + items.getStr("SKU_ID"), Long.valueOf(items.getInt("QUANTITY")));
                }
            } finally {
                cache.close();
            }
        }
    }
}
