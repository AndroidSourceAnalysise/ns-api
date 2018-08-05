package com.ns.tld.service;

import java.math.BigDecimal;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.ns.common.model.BasCustomerExt;
import com.ns.common.model.TldOrders;
import com.ns.common.model.TldRebateFlow;
import com.ns.common.model.TldTwitter;
import com.ns.common.utils.DateUtil;
import com.ns.common.utils.GUIDUtil;

public class TldRebateFlowService {
    private final TldRebateFlow dao = new TldRebateFlow();
    public static final TldRebateFlowService me = new TldRebateFlowService();
    //1.直推首单, 2.直推复购, 3.间接首单 4.间接复购',
    public final int TYPE_DIR_FIR = 1;
    public final int TYPE_DIR_RE = 2;
    public final int TYPE_UN_FIR = 3;
    public final int TYPE_UN_RE = 4;
    public final int STATUS_WAITING = 0;
    public final int STATUS_FINISH = 1;
    public final int STATUS_DISABLE = -1;

    private static final String COLUMN = "id,order_no,buyer_id,buyer_name,twitter_id,twitter_no,twitter_name,direct_rebate_no,direct_rebate_name,scale,fee,type,status,create_dt,operate_dt";

    public List<TldRebateFlow> getByOrderNo(String orderNo) {
        return dao.find("select " + COLUMN + " from tld_rebate_flow where order_no=?", orderNo);
    }

    public void addBuyRebate(TldTwitter t, TldTwitter downT, BasCustomerExt buyerCus, TldOrders orders, BigDecimal totalFee, int type) {
        TldRebateFlow flow = new TldRebateFlow();
        flow.setId(GUIDUtil.getGUID());
        flow.setBuyerId(buyerCus.getConId());
        flow.setBuyerName(buyerCus.getConName());
        flow.setScale(orders.getCouponAmount());
        flow.setFee(totalFee);
        flow.setTwitterId(t.getConId());
        flow.setTwitterNo(t.getConNo());
        flow.setTwitterName(t.getConName());
        flow.setDirectRebateNo(downT.getConNo());
        flow.setDirectRebateName(downT.getConName());
        flow.setStatus(STATUS_WAITING);
        flow.setCreateDt(DateUtil.getNow());
        flow.setType(type);
        flow.save();
    }

    public TldRebateFlow findTwitterOrder(String conNo, String orderNo) {
        return dao.findFirst("select " + COLUMN + " from tld_rebate_flow where order_no=? and twitter_no=?", orderNo, conNo);
    }

    public void confirmRebate(String orderNo, String tId) {
        Db.update("update tld_rebate_flow set status = " + STATUS_FINISH + ",operate_dt = now() where orderNo=? and twitter_id=?", orderNo, tId);
    }

    public void disableRebate(String orderNo, String tId) {
        Db.update("update tld_rebate_flow set status = " + STATUS_DISABLE + ",operate_dt = now() where orderNo=? and twitter_id=?", orderNo, tId);
    }

    public Object getTwitterFlowList(String conNo, Integer status, String type, int pageNumber, int pageSize) {
        StringBuffer sb = new StringBuffer("from tld_rebate_flow where twitter_no = '" + conNo + "'");
        if (status != null) {
            sb.append(" and status = " + status);
        }
        if (type != null) {
            sb.append(" and type in ( " + type + ")");
        }
        sb.append(" order by create_dt desc");
        Page<Record> flowList = Db.paginate(pageNumber, pageSize, "select " + COLUMN + "",
                sb.toString());

        return flowList;
    }

}
