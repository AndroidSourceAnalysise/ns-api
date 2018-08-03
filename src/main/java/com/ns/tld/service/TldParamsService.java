package com.ns.tld.service;

import com.jfinal.kit.StrKit;
import com.ns.common.model.TldParams;
import com.ns.common.model.TldTwitter;

/**
 * @author qgn
 * 推客升级service
 * 暂定：001合伙人  002经理  003高级总监   004总监  005主管  009普通推客. 
 */
public class TldParamsService {
	private final TldParams dao = new TldParams();
	public static final TldParamsService me = new TldParamsService();
	private final String COLUMN = "id,post_no,post_name,direct_push,con_total,month_sale,percent,year,month,person,person_actual,enabled,version,status,remark,memo,create_by,create_dt,update_dt";
	private final String defaultNo = "009";//初始会员级别
	public final String maxNo = "001";//最高级.
	private final String levelStr = "009,005,004,005,003,002,001"; 
	public TldParams getDefult(){
		return getByPostNo(defaultNo);
	}
	public TldParams getByPostNo(String no){
		return dao.findFirst("select " + COLUMN + " from tld_params where enabled = 1 and post_no = ?", no);
	}
	
	/**
	 * 判断no1 级别是否比no2高.
	 * */
	public boolean compare(String no1, String no2) {
		return levelStr.indexOf(no1) > levelStr.indexOf(no2);
	}

	/**
	 * 判断no1 级别是否比no2高.
	 * */
	public String getNextPostNo(String no){
		if(maxNo.equals(no)){
			return null;
		}
		return levelStr.substring(levelStr.indexOf(no) + 4, levelStr.indexOf(no) + 7);
	};
	
	/**
	 * 是否可以升级.
	 *   参数中不为null,0的全部需要对比
	 *   否则返回 null;
	 *   是则返回新级别参数.
	 * */
	public TldParams isCanUpLevel(TldTwitter t){
		String nextPost = this.getNextPostNo(t.getPostNo());
		if(StrKit.isBlank(nextPost)){
			return null;
		}
		TldParams newParams = this.getByPostNo(nextPost);
		if(isFit(newParams, t)){
			return newParams;
		}
		return null;
	}
	
	private boolean isFit(TldParams p, TldTwitter t) {
		return (p.getDirectPush()==null?true:p.getDirectPush()<=t.getConfirmedDirectPush())
				&&(p.getConTotal()==null?true:p.getConTotal()<=t.getConfirmedConTotal())
				&&(p.getMonthSale()==null?true:p.getMonthSale().compareTo(t.getMonthSale())==-1)
				&&p.getPersonActual() <= p.getPerson();
	}
	public static void main(String[] args) {
		TldParamsService s = new TldParamsService();
		System.out.println(s.getNextPostNo("001"));
	}
	public void appendOne(TldParams tldparams) {
		tldparams.setPersonActual(tldparams.getPersonActual()+1).update();
	}
}
