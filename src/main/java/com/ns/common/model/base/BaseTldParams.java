package com.ns.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseTldParams<M extends BaseTldParams<M>> extends Model<M> implements IBean {

	public M setId(java.lang.String id) {
		set("id", id);
		return (M)this;
	}
	
	public java.lang.String getId() {
		return getStr("id");
	}

	public M setPostNo(java.lang.String postNo) {
		set("post_no", postNo);
		return (M)this;
	}
	
	public java.lang.String getPostNo() {
		return getStr("post_no");
	}

	public M setPostName(java.lang.String postName) {
		set("post_name", postName);
		return (M)this;
	}
	
	public java.lang.String getPostName() {
		return getStr("post_name");
	}

	public M setDirectPush(java.lang.Integer directPush) {
		set("direct_push", directPush);
		return (M)this;
	}
	
	public java.lang.Integer getDirectPush() {
		return getInt("direct_push");
	}

	public M setConTotal(java.lang.Integer conTotal) {
		set("con_total", conTotal);
		return (M)this;
	}
	
	public java.lang.Integer getConTotal() {
		return getInt("con_total");
	}

	public M setMonthSale(java.math.BigDecimal monthSale) {
		set("month_sale", monthSale);
		return (M)this;
	}
	
	public java.math.BigDecimal getMonthSale() {
		return get("month_sale");
	}

	public M setPercent(java.math.BigDecimal percent) {
		set("percent", percent);
		return (M)this;
	}
	
	public java.math.BigDecimal getPercent() {
		return get("percent");
	}

	public M setYear(java.lang.String year) {
		set("year", year);
		return (M)this;
	}
	
	public java.lang.String getYear() {
		return getStr("year");
	}

	public M setMonth(java.lang.String month) {
		set("month", month);
		return (M)this;
	}
	
	public java.lang.String getMonth() {
		return getStr("month");
	}

	public M setPerson(java.lang.Integer person) {
		set("person", person);
		return (M)this;
	}
	
	public java.lang.Integer getPerson() {
		return getInt("person");
	}

	public M setPersonActual(java.lang.Integer personActual) {
		set("person_actual", personActual);
		return (M)this;
	}
	
	public java.lang.Integer getPersonActual() {
		return getInt("person_actual");
	}

	public M setEnable(java.lang.Integer enable) {
		set("enable", enable);
		return (M)this;
	}
	
	public java.lang.Integer getEnable() {
		return getInt("enable");
	}

	public M setVersion(java.lang.Integer version) {
		set("version", version);
		return (M)this;
	}
	
	public java.lang.Integer getVersion() {
		return getInt("version");
	}

	public M setStatus(java.lang.Integer status) {
		set("status", status);
		return (M)this;
	}
	
	public java.lang.Integer getStatus() {
		return getInt("status");
	}

	public M setRemark(java.lang.String remark) {
		set("remark", remark);
		return (M)this;
	}
	
	public java.lang.String getRemark() {
		return getStr("remark");
	}

	public M setMemo(java.lang.String memo) {
		set("memo", memo);
		return (M)this;
	}
	
	public java.lang.String getMemo() {
		return getStr("memo");
	}

	public M setCreateBy(java.lang.String createBy) {
		set("create_by", createBy);
		return (M)this;
	}
	
	public java.lang.String getCreateBy() {
		return getStr("create_by");
	}

	public M setCreateDt(java.lang.String createDt) {
		set("create_dt", createDt);
		return (M)this;
	}
	
	public java.lang.String getCreateDt() {
		return getStr("create_dt");
	}

	public M setUpdateDt(java.lang.String updateDt) {
		set("update_dt", updateDt);
		return (M)this;
	}
	
	public java.lang.String getUpdateDt() {
		return getStr("update_dt");
	}

}