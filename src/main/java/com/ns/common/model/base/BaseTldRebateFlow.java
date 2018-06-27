package com.ns.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseTldRebateFlow<M extends BaseTldRebateFlow<M>> extends Model<M> implements IBean {

	public M setId(java.lang.String id) {
		set("id", id);
		return (M)this;
	}
	
	public java.lang.String getId() {
		return getStr("id");
	}

	public M setOrderNo(java.lang.String orderNo) {
		set("order_no", orderNo);
		return (M)this;
	}
	
	public java.lang.String getOrderNo() {
		return getStr("order_no");
	}

	public M setBuyerId(java.lang.String buyerId) {
		set("buyer_id", buyerId);
		return (M)this;
	}
	
	public java.lang.String getBuyerId() {
		return getStr("buyer_id");
	}

	public M setBuyerName(java.lang.String buyerName) {
		set("buyer_name", buyerName);
		return (M)this;
	}
	
	public java.lang.String getBuyerName() {
		return getStr("buyer_name");
	}

	public M setTwitterId(java.lang.String twitterId) {
		set("twitter_id", twitterId);
		return (M)this;
	}
	
	public java.lang.String getTwitterId() {
		return getStr("twitter_id");
	}

	public M setTwitterNo(java.lang.String twitterNo) {
		set("twitter_no", twitterNo);
		return (M)this;
	}
	
	public java.lang.String getTwitterNo() {
		return getStr("twitter_no");
	}

	public M setTwitterName(java.lang.String twitterName) {
		set("twitter_name", twitterName);
		return (M)this;
	}
	
	public java.lang.String getTwitterName() {
		return getStr("twitter_name");
	}

	public M setDirectRebateNo(java.lang.String directRebateNo) {
		set("direct_rebate_no", directRebateNo);
		return (M)this;
	}
	
	public java.lang.String getDirectRebateNo() {
		return getStr("direct_rebate_no");
	}

	public M setDirectRebateName(java.lang.String directRebateName) {
		set("direct_rebate_name", directRebateName);
		return (M)this;
	}
	
	public java.lang.String getDirectRebateName() {
		return getStr("direct_rebate_name");
	}

	public M setScale(java.math.BigDecimal scale) {
		set("scale", scale);
		return (M)this;
	}
	
	public java.math.BigDecimal getScale() {
		return get("scale");
	}

	public M setFee(java.math.BigDecimal fee) {
		set("fee", fee);
		return (M)this;
	}
	
	public java.math.BigDecimal getFee() {
		return get("fee");
	}

	public M setType(java.lang.Integer type) {
		set("type", type);
		return (M)this;
	}
	
	public java.lang.Integer getType() {
		return getInt("type");
	}

	public M setStatus(java.lang.Integer status) {
		set("status", status);
		return (M)this;
	}
	
	public java.lang.Integer getStatus() {
		return getInt("status");
	}

	public M setCreateDt(java.lang.String createDt) {
		set("create_dt", createDt);
		return (M)this;
	}
	
	public java.lang.String getCreateDt() {
		return getStr("create_dt");
	}

	public M setOperateDt(java.lang.String operateDt) {
		set("operate_dt", operateDt);
		return (M)this;
	}
	
	public java.lang.String getOperateDt() {
		return getStr("operate_dt");
	}

}
