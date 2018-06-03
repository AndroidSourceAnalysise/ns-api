package com.ns.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseNodeFocus<M extends BaseNodeFocus<M>> extends Model<M> implements IBean {

	public M setID(java.lang.String ID) {
		set("ID", ID);
		return (M)this;
	}
	
	public java.lang.String getID() {
		return getStr("ID");
	}

	public M setConPic(java.lang.String conPic) {
		set("CON_PIC", conPic);
		return (M)this;
	}
	
	public java.lang.String getConPic() {
		return getStr("CON_PIC");
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

	public M setFocusConId(java.lang.String focusConId) {
		set("FOCUS_CON_ID", focusConId);
		return (M)this;
	}
	
	public java.lang.String getFocusConId() {
		return getStr("FOCUS_CON_ID");
	}

	public M setFocusConNo(java.lang.String focusConNo) {
		set("FOCUS_CON_NO", focusConNo);
		return (M)this;
	}
	
	public java.lang.String getFocusConNo() {
		return getStr("FOCUS_CON_NO");
	}

	public M setFocusConName(java.lang.String focusConName) {
		set("FOCUS_CON_NAME", focusConName);
		return (M)this;
	}
	
	public java.lang.String getFocusConName() {
		return getStr("FOCUS_CON_NAME");
	}

	public M setFocusConPic(java.lang.String focusConPic) {
		set("FOCUS_CON_PIC", focusConPic);
		return (M)this;
	}
	
	public java.lang.String getFocusConPic() {
		return getStr("FOCUS_CON_PIC");
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
