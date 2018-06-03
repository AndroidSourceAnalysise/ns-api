package com.ns.customer.task;

import com.ns.common.constant.RedisKeyDetail;
import com.ns.common.model.BasCustomer;
import com.ns.common.task.Task;
import com.ns.common.utils.DateUtil;
import com.ns.common.utils.GUIDUtil;
import com.ns.customer.service.BasCustomerService;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.redis.Redis;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.CustomServiceApi;
import com.jfinal.weixin.sdk.api.UserApi;

/**
 * Created by Administrator on 2016-04-14.
 */
public class CreateCustomerTask extends Task {
    static BasCustomerService service = new BasCustomerService();
    static Log logger = Log.getLog(CreateCustomerTask.class);

    private String refNo;
    private String openId;


    public CreateCustomerTask(String refNo, String openId) {
        this.refNo = refNo;
        this.openId = openId;
    }

    /**
     * 任务执行
     */
    @Override
    public void execute() {
        try {
            service.addCustomer(refNo, openId);
        } catch (Exception e) {
            CustomServiceApi.sendText(openId, "温馨提醒：刚才可能由于网络延时，您扫描二维码没有成功，请取消关注公众号再扫描！");
            e.printStackTrace();
        }
    }
}
