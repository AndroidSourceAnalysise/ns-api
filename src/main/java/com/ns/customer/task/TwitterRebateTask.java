package com.ns.customer.task;

import com.jfinal.log.Log;
import com.jfinal.weixin.sdk.api.CustomServiceApi;
import com.ns.common.model.BasCustomerExt;
import com.ns.common.model.TldOrders;
import com.ns.common.task.Task;
import com.ns.tld.service.TldOrdersService;

/**
 * Created by Administrator on 2016-04-14.
 */
public class TwitterRebateTask extends Task {
    static TldOrdersService tldOrderService = TldOrdersService.me;
    static Log logger = Log.getLog(TwitterRebateTask.class);

    private BasCustomerExt selfExt;
    private TldOrders orders;
    private int status;

    public TwitterRebateTask(BasCustomerExt selfExt, TldOrders orders, int status) {
        this.selfExt = selfExt;
        this.orders = orders;
        this.status = status;
    }

    /**
     * 任务执行
     */
    @Override
    public void execute() {
        try {
        	tldOrderService.rebate(selfExt, orders, status);
        } catch (Exception e) {
        	logger.error("rebate thread errors,order:" + orders.getOrderNo(), e);
            e.printStackTrace();
        }
    }
}
