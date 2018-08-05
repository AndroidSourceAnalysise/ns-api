package com.ns.tld.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.ns.common.model.BasCustomer;
import com.ns.common.model.BasCustomerExt;
import com.ns.common.model.TldOrderItems;
import com.ns.common.model.TldOrders;
import com.ns.common.model.TldParams;
import com.ns.common.model.TldPntParams;
import com.ns.common.model.TldRebateFlow;
import com.ns.common.model.TldTwitter;
import com.ns.common.utils.DateUtil;
import com.ns.common.utils.GUIDUtil;
import com.ns.customer.service.BasCustomerService;
import com.ns.pnt.service.PntProductService;

/**
 * @author qgn twitter 相关.
 */
public class TldTwitterService {
	private final TldTwitter dao = new TldTwitter();
	public static final TldTwitterService me = new TldTwitterService();
	static TldParamsService paramsService = TldParamsService.me;
	static BasCustomerService basCustomerService = BasCustomerService.me;
	static TldPntParamsSerivce tldPntParamsService = TldPntParamsSerivce.me;
	static PntProductService pntProductService = PntProductService.me;
	static TldOrderItemsSerivce tldOrderItemsSerivce = TldOrderItemsSerivce.me;
	static TldRebateFlowService tldRebateFlowService = TldRebateFlowService.me;
	private final String COLUMN = "id,post_no,con_id,con_no,con_name,mobile, enabled,version, status,remark, memo,min_date, year,month,  day, percent, is_first_month, up_no,up_name,rebate_up_no,rebate_up_name,referee_id,referee_name, month_sale,unconfirm_direct_push,unconfirm_con_total,   confirmed_direct_push, confirmed_con_total,unconfirm_promotion_order,confirmed_promotion_order,  unconfirm_promotion_fee,confirmed_promotion_fee,unconfirm_order,confirmed_order,unconfirm_scale,confirmed_scale,request_amount,requested_total,reserve_available,balance_amount,create_by,create_dt,update_dt";

	public TldTwitter getByNo(String conNo) {
		return dao.findFirst("select " + COLUMN + " from tld_twitter where enabled = 1 and con_no = ?", conNo);
	}

	public TldTwitter create(BasCustomer bc) {
		String upName = "";
		String upNo = "";
		String rebateUpName = "";
		String rebateUpNo = "";
		TldParams defaultParam = paramsService.getDefult();
		TldTwitter upTwitter = this.getUpTwitter(bc.getRpNo());
		if (null != upTwitter) {
			upNo = upTwitter.getConNo();
			upName = upTwitter.getConName();
			String upPostNo = upTwitter.getPostNo();
			if (paramsService.compare(upPostNo, defaultParam.getPostNo())) {
				rebateUpNo = upNo;
				rebateUpName = upName;
			} else {
				rebateUpNo = upTwitter.getRebateUpNo();
				rebateUpName = upTwitter.getRebateUpName();
			}
		}
		TldTwitter t = new TldTwitter();
		t.setId(GUIDUtil.getGUID());
		t.setConId(bc.getID());// TODO.是否需要单独的规则编码
		t.setConName(bc.getConName());
		t.setConNo(bc.getConNo());
		t.setMobile(bc.getMOBILE());
		t.setRefereeId(bc.getRpId());
		t.setRefereeName(bc.getRpName());// TODO.是否补充推荐人编码,自身真实姓名等
		t.setCreateDt(DateUtil.getNow());
		t.setPostNo(defaultParam.getPostNo());
		t.setPercent(defaultParam.getPercent());
		t.setRebateUpNo(rebateUpNo);
		t.setRebateUpName(rebateUpName);
		t.setUpName(upName);
		t.setUpNo(upNo);
		t.save();
		return t;
	}

	/**
	 * 推客返利确认到账后操作.
	 */
	public void tryUpGrade(TldTwitter t) {
		TldParams params = paramsService.isCanUpLevel(t);
		if (null == params) {
			return;
		}
		String rebateUpName = t.getRebateUpName();
		String rebateUpNo = t.getRebateUpNo();
		if (StrKit.notBlank(rebateUpNo)) {
			TldTwitter rT = this.getByNo(t.getRebateUpName());
			if (!paramsService.compare(rT.getPostNo(), t.getPostNo())) {// 与上级脱离返利关系.
				rebateUpName = rT.getRebateUpName();
				rebateUpNo = rT.getRebateUpNo();
				updateRebateDownTwitter(t);// 将当前推客下级的返利上级改为当前推客.
			}
		}
		t.setRebateUpName(rebateUpName);
		t.setRebateUpNo(rebateUpNo);
		t.setPostNo(params.getPostNo());
		t.setPercent(params.getPercent());
		t.setMinDate(DateUtil.getNow());
		params.setPersonActual(params.getPersonActual() + 1);
		params.update();
	}

	/**
	 * 将当前推客a 下级的返利上级改为a. 遍历所有返利上级是a返利上级的推客,修改属于a分支.
	 **/
	private void updateRebateDownTwitter(TldTwitter t) {
		List<TldTwitter> list = dao.find(
				"select " + COLUMN + " from tld_twitter where enabled = 1 and rebate_up_no = ?", t.getRebateUpNo());
		for (TldTwitter tt : list) {
			if (isChild(tt, t.getConNo(), t.getRebateUpNo())) {
				tt.setRebateUpName(t.getConName());
				tt.setRebateUpNo(t.getConNo());
				tt.setCreateDt(DateUtil.getNow());
				tt.update();
			}
		}
	}

	private boolean isChild(TldTwitter tt, String t1, String upT) {
		while (!upT.equals(tt.getUpNo())) {
			if (t1.equals(tt.getUpNo())) {
				return true;
			}
			tt = this.getByNo(tt.getUpNo());
		}
		return false;
	}

	private TldTwitter getUpTwitter(String rpNo) {
		TldTwitter up = this.getByNo(rpNo);
		if (null != up)
			return up;
		String upTwitterNo = basCustomerService.findUpTwitterNo(rpNo);
		if (StrKit.isBlank(upTwitterNo)) {
			return null;
		}
		return this.getByNo(upTwitterNo);
	}

	/**
	 * 查看上级是不是推客，如果不是就把上级设置为推客并返回.
	 */
	public TldTwitter findDirectTwitter(String selfNo, String upNo) {
		TldTwitter t = this.getByNo(upNo);
		if (null != t) {
			return t;
		}
		return create(basCustomerService.getCustomerByConNo(upNo));
	}

	public void addPayDirect(TldTwitter t, BasCustomerExt buyerCus, TldOrders orders) {
		int orderType = 2;// TODO.常量在哪
		int type = tldRebateFlowService.TYPE_DIR_RE;
		List<TldOrderItems> itemlist = tldOrderItemsSerivce.findByOrderNo(orders.getOrderNo());
		List prodcutIds = new ArrayList();
		Map<String, Integer> m = new HashMap<String, Integer>();
		for (TldOrderItems i : itemlist) {
			prodcutIds.add(i.getPntId());
			m.put(i.getPntId(), i.getQUANTITY());
		}
		List<TldPntParams> pnts = tldPntParamsService.queryByPntIds(prodcutIds);
		BigDecimal totalFee = new BigDecimal(0);
		for (TldPntParams pnt : pnts) {// 对每件产品进行返利.
			BigDecimal rate = null;
			if (orders.getIsReorder() == 0) {
				rate = pnt.getPercent();
				type = tldRebateFlowService.TYPE_DIR_FIR;
			} else {
				rate = pnt.getRepercent();
			}
			int quanlity = m.get(pnt.getProductId());
			BigDecimal price = pntProductService.getById(pnt.getProductId()).getSalPrice();
			BigDecimal fee = rate.multiply(new BigDecimal(quanlity)).multiply(price);
			totalFee = totalFee.add(fee);
		}
		tldRebateFlowService.addBuyRebate(t, null, buyerCus, orders,totalFee, type);
		updateTwInfo(t, type, orderType, totalFee, orders.getPntAmount());
	}

	public void addPayNormal(TldTwitter downT, BasCustomerExt buyerCus, TldOrders orders, BigDecimal rebated) {
		int type = tldRebateFlowService.TYPE_UN_RE;
		int orderType = 2;// TODO.常量在哪
		if (orders.getIsReorder() == 0) {
			type = tldRebateFlowService.TYPE_DIR_FIR;
		}
		BigDecimal amount = orders.getPntAmount();
		TldTwitter upT = this.getByNo(downT.getRebateUpNo());
		if (upT != null) {
			TldParams p = paramsService.getByPostNo(upT.getPostNo());
			BigDecimal rate = p.getPercent().subtract(rebated);
			BigDecimal totalFee = rate.multiply(amount);
			tldRebateFlowService.addBuyRebate(upT, downT, buyerCus, orders, totalFee, type);
			updateTwInfo(upT, type, orderType, totalFee, orders.getPntAmount());
			if (rate.doubleValue() > 0) {
				addPayNormal(upT, buyerCus, orders, rebated);
			}
		}
	}

	public void confirmDirectOrder(TldTwitter t, BasCustomerExt selfExt, TldOrders orders) {
		int orderType = 7;// TODO.常量在哪
		int type = tldRebateFlowService.TYPE_DIR_RE;
		TldRebateFlow flow = tldRebateFlowService.findTwitterOrder(t.getConNo(), orders.getOrderNo());
		if(flow == null || tldRebateFlowService.STATUS_WAITING != flow.getStatus()){
			return ;//订单已处理.
		}
		if (orders.getIsReorder() == 0) {
			type = tldRebateFlowService.TYPE_DIR_FIR;
		}
		BigDecimal fee= flow.getFee();
		tldRebateFlowService.confirmRebate(orders.getOrderNo(), t.getConId());
		updateTwInfo(t, type, orderType, fee, orders.getPntAmount());
		TldParams tldparams = paramsService.isCanUpLevel(t);
		if(tldparams != null){//推客升级
			t.setPostNo(tldparams.getPostNo());
			t.setPostName(tldparams.getPostName());
			t.setPercent(tldparams.getPercent());
			paramsService.appendOne(tldparams);
			this.updateRebateDownTwitter(t);//将自己的下级返利推客设置为他.
		}
	}

	public void confirmNormalOrder(TldTwitter t, TldOrders orders) {
		int type = tldRebateFlowService.TYPE_UN_RE;
		int orderType = 7;// TODO.常量在哪
		if (orders.getIsReorder() == 0) {
			type = tldRebateFlowService.TYPE_DIR_FIR;
		}
		BigDecimal amount = orders.getPntAmount();
		TldTwitter upT = this.getByNo(t.getRebateUpNo());
		if (upT != null) {
			TldRebateFlow flow = tldRebateFlowService.findTwitterOrder(t.getConNo(), orders.getOrderNo());
			if(flow == null || tldRebateFlowService.STATUS_WAITING != flow.getStatus()){
				return ;//订单已处理.
			}
			BigDecimal fee = flow.getFee();
			tldRebateFlowService.confirmRebate(orders.getOrderNo(), t.getConId());
			updateTwInfo(upT, type, orderType, fee, orders.getPntAmount());
			confirmNormalOrder(upT, orders);
		}
	}

	public void disabledDirectOrder(TldTwitter t, TldOrders orders) {
		int orderType = 7;// TODO.常量在哪
		int type = tldRebateFlowService.TYPE_DIR_RE;
		TldRebateFlow flow = tldRebateFlowService.findTwitterOrder(t.getConNo(), orders.getOrderNo());
		if(flow == null || tldRebateFlowService.STATUS_WAITING != flow.getStatus()){
			return ;//订单已处理.
		}
		if (orders.getIsReorder() == 0) {
			type = tldRebateFlowService.TYPE_DIR_FIR;
		}
		BigDecimal fee= flow.getFee();
		tldRebateFlowService.disableRebate(orders.getOrderNo(), t.getConId());
		updateTwInfo(t, type, orderType, fee, orders.getPntAmount());
	}

	public void disabledNormalOrder(TldTwitter t, TldOrders orders) {
		int orderType = 10;// TODO.常量在哪
		int type = tldRebateFlowService.TYPE_DIR_RE;
		TldRebateFlow flow = tldRebateFlowService.findTwitterOrder(t.getConNo(), orders.getOrderNo());
		if(flow == null || tldRebateFlowService.STATUS_WAITING != flow.getStatus()){
			return ;//订单已处理.
		}
		if (orders.getIsReorder() == 0) {
			type = tldRebateFlowService.TYPE_DIR_FIR;
		}
		BigDecimal fee= flow.getFee();
		tldRebateFlowService.disableRebate(orders.getOrderNo(), t.getConId());
		updateTwInfo(t, type, orderType, fee, orders.getPntAmount());
	}

	
	/**
	 * 下级会员相关信息修改 如果是首单,修改 会员数量与金额: 首单，总会员数+1; 直推 非首单，仅修改金额 tradeType 2已付款 7已收货
	 * 10已退款
	 */
	public void updateTwInfo(TldTwitter t, int rebateType, int orderType, BigDecimal income, BigDecimal orderAmount) {
		if (orderType == 2) {// 支付.
			if (rebateType == tldRebateFlowService.TYPE_DIR_FIR) {
				t.setUnconfirmDirectPush(t.getUnconfirmDirectPush() + 1);
				t.setUnconfirmConTotal(t.getUnconfirmConTotal() + 1);
			} else if (rebateType == tldRebateFlowService.TYPE_UN_FIR) {
				t.setUnconfirmConTotal(t.getUnconfirmConTotal() + 1);
			}
			t.setUnconfirmOrder(t.getUnconfirmOrder() + 1);
			t.setUnconfirmScale(t.getUnconfirmScale().add(orderAmount));
		} else if (orderType == 7) {// 完成订单.
			if (rebateType == tldRebateFlowService.TYPE_DIR_FIR) {
				t.setConfirmedDirectPush(t.getConfirmedDirectPush() + 1);
				t.setUnconfirmDirectPush(t.getUnconfirmDirectPush() - 1);
				t.setConfirmedConTotal(t.getConfirmedConTotal() + 1);
				t.setUnconfirmConTotal(t.getUnconfirmConTotal() - 1);
			} else if (rebateType == tldRebateFlowService.TYPE_UN_FIR) {
				t.setUnconfirmConTotal(t.getUnconfirmConTotal() - 1);
				t.setConfirmedConTotal(t.getConfirmedConTotal() + 1);
			}
			t.setConfirmedOrder(t.getConfirmedOrder() + 1);
			t.setUnconfirmOrder(t.getUnconfirmOrder() - 1);
			t.setConfirmedScale(t.getConfirmedScale().add(orderAmount));
			t.setUnconfirmScale(t.getUnconfirmScale().multiply(orderAmount));
		}
		if (orderType == 10) {// 撤单
			if (rebateType == tldRebateFlowService.TYPE_DIR_FIR) {
				t.setUnconfirmDirectPush(t.getUnconfirmDirectPush() - 1);
				t.setUnconfirmConTotal(t.getUnconfirmConTotal() - 1);
			} else if (rebateType == tldRebateFlowService.TYPE_UN_FIR) {
				t.setUnconfirmConTotal(t.getUnconfirmConTotal() - 1);
			}
			t.setUnconfirmOrder(t.getUnconfirmOrder() - 1);
			t.setUnconfirmScale(t.getUnconfirmScale().multiply(orderAmount));
		}
	}

	public Object getMonthScaleList(int pageNumber, int pageSize) {
        String sql = " from tld_twitter where enabled = 1 and month_sale>0 order by month_sale desc";
        Page<Record> scaleList = Db.paginate(pageNumber, pageSize, "select " + COLUMN + "", sql);
		return scaleList;
	}

	
}