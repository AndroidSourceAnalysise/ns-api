package com.ns.tld.service;

import java.util.List;

import com.ns.common.model.TldOrderItems;

public class TldOrderItemsSerivce {
	private final TldOrderItems dao = new TldOrderItems();
    public static final TldOrderItemsSerivce me = new TldOrderItemsSerivce();
    public static final String COLUMN = "ID,ORDER_ID,ORDER_NO,CON_ID,CON_NO,CON_NAME,PNT_ID,PNT_NAME,SKU_ID,SKU_NAME,QUANTITY,SALE_PRICE,AMOUNT,COMMENT_TAG,ENABLED,VERSION,STATUS,REMARK,CREATE_BY,CREATE_DT,UPDATE_DT";
	public List<TldOrderItems> findByOrderNo(String orderNo) {
		return dao.find("select " + COLUMN + " from tld_order_items where order_no =" + orderNo);
	}
    
}
