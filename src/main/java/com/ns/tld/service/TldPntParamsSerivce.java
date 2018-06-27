package com.ns.tld.service;

import java.util.List;

import com.ns.common.model.TldPntParams;

public class TldPntParamsSerivce {
    private final TldPntParams dao = new TldPntParams();
    public static final TldPntParamsSerivce me = new TldPntParamsSerivce();
    private final String COLUMN = "id,product_id,product_name,sku_id,sku_name,post_no,post_name,percent,repercent,enable,version,status,remark,memo,create_by,create_dt,update_dt";
    public TldPntParams getByPId(String pId){
    	return dao.findFirst("select " + COLUMN + " from tld_pnt_params where product_id =?", pId);
    }
	public List<TldPntParams> queryByPntIds(List<String> prodcutIds) {
		StringBuffer sb =  new StringBuffer("select" + COLUMN + " from tld_pnt_params where productId in(");
		int len = prodcutIds.size();
		for(int i=0; i<len; i++){
			if(i==len-1){
				sb.append("?)");
			}else{
				sb.append("?,");
			}
		}
		for(String str : prodcutIds){
			sb.append(str + ",");
		}
		String sql = sb.toString();
		sql = sql.substring(0, sql.length()-2);
		return dao.find(sql);
	}

}
