package com.ns.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseTldOrderItems<M extends BaseTldOrderItems<M>> extends Model<M> implements IBean {

	public M setID(java.lang.String ID) {
		set("ID", ID);
		return (M)this;
	}
	
	public java.lang.String getID() {
		return getStr("ID");
	}

	public M setOrderId(java.lang.String orderId) {
		set("ORDER_ID", orderId);
		return (M)this;
	}
	
	public java.lang.String getOrderId() {
		return getStr("ORDER_ID");
	}

	public M setOrderNo(java.lang.String orderNo) {
		set("ORDER_NO", orderNo);
		return (M)this;
	}
	
	public java.lang.String getOrderNo() {
		return getStr("ORDER_NO");
	}

	public M setConId(java.lang.String conId) {
		set("CON_ID", conId);
		return (M)this;
	}
	
	public java.lang.String getConId() {
		return getStr("CON_ID");
	}

	public M setConNo(java.lang.String conNo) {
		set("CON_NO", conNo);
		return (M)this;
	}
	
	public java.lang.String getConNo() {
		return getStr("CON_NO");
	}

	public M setConName(java.lang.String conName) {
		set("CON_NAME", conName);
		return (M)this;
	}
	
	public java.lang.String getConName() {
		return getStr("CON_NAME");
	}

	public M setPntId(java.lang.String pntId) {
		set("PNT_ID", pntId);
		return (M)this;
	}
	
	public java.lang.String getPntId() {
		return getStr("PNT_ID");
	}

	public M setPntName(java.lang.String pntName) {
		set("PNT_NAME", pntName);
		return (M)this;
	}
	
	public java.lang.String getPntName() {
		return getStr("PNT_NAME");
	}

	public M setSkuId(java.lang.String skuId) {
		set("SKU_ID", skuId);
		return (M)this;
	}
	
	public java.lang.String getSkuId() {
		return getStr("SKU_ID");
	}

	public M setSkuName(java.lang.String skuName) {
		set("SKU_NAME", skuName);
		return (M)this;
	}
	
	public java.lang.String getSkuName() {
		return getStr("SKU_NAME");
	}

	public M setQUANTITY(java.lang.Integer QUANTITY) {
		set("QUANTITY", QUANTITY);
		return (M)this;
	}
	
	public java.lang.Integer getQUANTITY() {
		return getInt("QUANTITY");
	}

	public M setSalePrice(java.math.BigDecimal salePrice) {
		set("SALE_PRICE", salePrice);
		return (M)this;
	}
	
	public java.math.BigDecimal getSalePrice() {
		return get("SALE_PRICE");
	}

	public M setAMOUNT(java.math.BigDecimal AMOUNT) {
		set("AMOUNT", AMOUNT);
		return (M)this;
	}
	
	public java.math.BigDecimal getAMOUNT() {
		return get("AMOUNT");
	}

	public M setCommentTag(java.lang.Integer commentTag) {
		set("COMMENT_TAG", commentTag);
		return (M)this;
	}
	
	public java.lang.Integer getCommentTag() {
		return getInt("COMMENT_TAG");
	}

	public M setENABLED(java.lang.Integer ENABLED) {
		set("ENABLED", ENABLED);
		return (M)this;
	}
	
	public java.lang.Integer getENABLED() {
		return getInt("ENABLED");
	}

	public M setVERSION(java.lang.Integer VERSION) {
		set("VERSION", VERSION);
		return (M)this;
	}
	
	public java.lang.Integer getVERSION() {
		return getInt("VERSION");
	}

	public M setSTATUS(java.lang.Integer STATUS) {
		set("STATUS", STATUS);
		return (M)this;
	}
	
	public java.lang.Integer getSTATUS() {
		return getInt("STATUS");
	}

	public M setREMARK(java.lang.String REMARK) {
		set("REMARK", REMARK);
		return (M)this;
	}
	
	public java.lang.String getREMARK() {
		return getStr("REMARK");
	}

	public M setCreateBy(java.lang.String createBy) {
		set("CREATE_BY", createBy);
		return (M)this;
	}
	
	public java.lang.String getCreateBy() {
		return getStr("CREATE_BY");
	}

	public M setCreateDt(java.lang.String createDt) {
		set("CREATE_DT", createDt);
		return (M)this;
	}
	
	public java.lang.String getCreateDt() {
		return getStr("CREATE_DT");
	}

	public M setUpdateDt(java.lang.String updateDt) {
		set("UPDATE_DT", updateDt);
		return (M)this;
	}
	
	public java.lang.String getUpdateDt() {
		return getStr("UPDATE_DT");
	}

}
