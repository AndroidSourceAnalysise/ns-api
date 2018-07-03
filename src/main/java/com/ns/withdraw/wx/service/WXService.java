//package com.ns.withdraw.wx.service;
//
//import com.jfinal.plugin.activerecord.Db;
//import com.jfinal.weixin.sdk.api.ApiConfigKit;
//import com.jfinal.weixin.sdk.api.ApiResult;
//import com.jfinal.weixin.sdk.api.TemplateMsgApi;
//import com.jfinal.weixin.sdk.utils.JsonUtils;
//import org.apache.http.util.TextUtils;
//import redis.clients.jedis.Jedis;
//
//import java.math.BigDecimal;
//import java.util.HashMap;
//import java.util.Map;
//
//public class WXService {
//    /**
//     * App提现
//     *
//     * @param param
//     * @return
//     */
//    public Map<String, String> withdrawApp(WithdrawAppParam param) {
//        // 添加提现次数验证
//        Map<String, String> isAllowResult = Customer.isAllowGetMoney(param.getCON_ID(), "CD100");
//        if (isAllowResult != null) {
//            final String isAllow = isAllowResult.get("IS_ALLOW");
//            if ("N".equalsIgnoreCase(isAllow)) {
//                final String maxAllowCount = isAllowResult.get("MAX_ALLOW_COUNT");
//                throw new CustException("本月提现次数不足,本月提现最多" + maxAllowCount + "次");
//            }
//        }
//
//        String SERIAL_ID = RandomGUID.genRandomGUID(false);
//        String conId = param.getCON_ID();
//        BigDecimal amount = param.getAMOUNT();//wo
//        //触发器逻辑接入
//        Customer customer = Customer.getById(conId);
//
//
//        checkAmountOfMoneyApp(customer, param.getAMOUNT());
//
//        BigDecimal TAX = BigDecimal.ZERO;
//        String TAXSwitch = "0";
//        Jedis jedis = RedisUtil.getJedis();
//        try {
//            TAXSwitch = jedis.get("TAXSwitch"); // 是否开启 1 表示开启
//            if (TextUtils.isEmpty(TAXSwitch)) {
//                TAXSwitch = "1";
//            }
//            if (TAXSwitch.equals("1")) {
//                String TAXType = jedis.get("TAXType");
//                // 如果redis没有对应的值，使用默认税收
//                if (TextUtils.isEmpty(TAXType)) {
//                    TAXType = "4.46";
//                }
//                TAX = new BigDecimal(TAXType).divide(new BigDecimal("100"));  //税率
//            }
//
//        } finally {
//            RedisUtil.close(jedis);
//        }
//
//        BigDecimal TAXAMOUNT = amount.multiply(TAX).setScale(2, BigDecimal.ROUND_HALF_UP);
//
//        //产生提现申请
//        Request request = new Request();
//        request.setCON_ID(param.getCON_ID());
//        request.setCON_NAME(param.getCON_NAME());
//        request.setREFUND_TYPE(param.getREFUND_TYPE());
//        request.setAMOUNT(param.getAMOUNT());
//        request.setTAX(TAXAMOUNT);
//        request.setACTUAL_AMOUNT(amount.subtract(TAXAMOUNT));
//        //  request.setACTUAL_AMOUNT(amount.subtract(null == param.getTAX() ? BigDecimal.ZERO : param.getTAX()));
//
//        request.setCELL_PHONE(param.getCELL_PHONE());
//        request.setREAL_NAME(param.getREAL_NAME());
//        request.setALIPAY_ACCOUNT(param.getALIPAY_ACCOUNT());
//        request.setBANK_CARD(param.getBANK_CARD());
//        request.setBANK_NAME(param.getBANK_NAME());
//        request.setREFUND_TYPE(param.getREFUND_TYPE());
//        request.setSYS_ACCOUNT(param.getSYS_ACCOUNT());
//
//        request.setREQUEST_TYPE(BigDecimal.ZERO);
//        request.setSTATUS(BigDecimal.ZERO);
//        request.setCREATE_DT(Util.nowDateTimeToString());
//        request.setUPDATETIME(Util.nowDateTimeToString());
//
//
//        //禁止提现
//        BigDecimal isAllowWithDraw = null == customer.getIS_WITHDRAW() ? BigDecimal.ZERO : customer.getIS_WITHDRAW();
//        if (isAllowWithDraw.compareTo(BigDecimal.ZERO) == 0)
//            throw new CustException("您的帐号暂时被锁定，请联系客服！");
//
//        BigDecimal isAuto = getIsAutoWithDraw(amount);
//        request.setORDER_ID(SERIAL_ID);
//        request.setORDER_NO(SERIAL_ID);
//        request.setCON_NO(customer.getCON_NO());
//        request.setCON_NAME(customer.getCON_NAME());
//        request.setWECHAT_ACCOUNT(customer.getOPENID_WECHAT());
//        request.setAUTO_WITHDRAW(isAuto);
//
//        //更改用户信息 -- 增加 已申请，减少 可提现、津贴、奖励
//        BigDecimal commission = param.getCOMMISSION();//佣金
//        BigDecimal subsidy = param.getSUBSIDY();//津贴
//        BigDecimal reward = param.getREWARD();//奖励
//        BigDecimal shop = param.getSHOP();//店铺
//        BigDecimal reqBalance = customer.getREQUEST_BALANCE();//可提现
//        BigDecimal totalAllowance = customer.getTOTAL_ALLOWANCE();//总津贴
//        totalAllowance = totalAllowance == null ? BigDecimal.ZERO : totalAllowance;
//        BigDecimal totalAward = customer.getTOTAL_AWARD();//奖励总金额
//        totalAward = totalAward == null ? BigDecimal.ZERO : totalAward;
//        /*佣金*/
//        if (commission.compareTo(BigDecimal.ZERO) == 1) {//提现佣金大于0
//            if (reqBalance.compareTo(commission) >= 0) {
//                customer.setREQUEST_AMOUNT(checkNullBigDecimal(customer.getREQUEST_AMOUNT()).add(commission));//已申请 增加
//                customer.setREQUEST_BALANCE(reqBalance.subtract(commission));//减少 可提现
//            } else
//                throw new CustException("可提现金额不足！");
//        }
//        /*津贴*/
//        if (subsidy.compareTo(BigDecimal.ZERO) == 1) {//提现津贴大于0
//            if (totalAllowance.compareTo(subsidy) >= 0) {
//                customer.setTOTAL_ALLOWANCE_REQUEST(checkNullBigDecimal(customer.getTOTAL_ALLOWANCE_REQUEST()).add(subsidy));//已申请+
//                customer.setTOTAL_ALLOWANCE(totalAllowance.subtract(subsidy));//减少 津贴 可提现
//            } else
//                throw new CustException("可提现津贴金额不足！");
//        }
//        /*奖励*/
//        if (reward.compareTo(BigDecimal.ZERO) == 1) {//提现奖励大于0
//            if (totalAward.compareTo(reward) >= 0) {
//                customer.setTOTAL_AWARD_REQUEST(checkNullBigDecimal(customer.getTOTAL_AWARD_REQUEST()).add(reward));//已申请+
//                customer.setTOTAL_AWARD(totalAward.subtract(reward));//减少 奖励 可提现
//            } else
//                throw new CustException("可提现奖励金额不足！");
//        }
//        /*店铺返利*/
//        if (shop.compareTo(BigDecimal.ZERO) == 1) {//提现店铺大于0
//            if (customer.getSHOP_AWARD_AVAILABLE().compareTo(shop) >= 0) {
//                customer.setSHOP_AWARD_REQUEST(checkNullBigDecimal(customer.getSHOP_AWARD_REQUEST()).add(shop));//已申请+
//                customer.setSHOP_AWARD_AVAILABLE(customer.getSHOP_AWARD_AVAILABLE().subtract(shop));//减少 店铺 可提现
//            } else
//                throw new CustException("可提现奖励金额不足！");
//        }
//        updateCustomer(customer);
//        /*更改用户信息  --END*/
//
//        //保存提现记录
//        request.setSERIAL_ID(SERIAL_ID);
//        request.save();
//        //提现明细
//        Db.update("insert into BAS_CUST_WITHDRAW_ITEM(ID,SERIAL_ID,CON_ID,DRAW_TYPE,AMOUNT,SYS_ACCOUNT) values(?, ?, ?, ?, ?, ?)",
//                RandomGUID.genRandomGUID(false), SERIAL_ID, conId, "1", commission, param.getSYS_ACCOUNT());//佣金
//        Db.update("insert into BAS_CUST_WITHDRAW_ITEM(ID,SERIAL_ID,CON_ID,DRAW_TYPE,AMOUNT,SYS_ACCOUNT) values(?, ?, ?, ?, ?, ?)",
//                RandomGUID.genRandomGUID(false), SERIAL_ID, conId, "2", subsidy, param.getSYS_ACCOUNT());//津贴
//        Db.update("insert into BAS_CUST_WITHDRAW_ITEM(ID,SERIAL_ID,CON_ID,DRAW_TYPE,AMOUNT,SYS_ACCOUNT) values(?, ?, ?, ?, ?, ?)",
//                RandomGUID.genRandomGUID(false), SERIAL_ID, conId, "3", reward, param.getSYS_ACCOUNT());//奖励
//        Db.update("insert into BAS_CUST_WITHDRAW_ITEM(ID,SERIAL_ID,CON_ID,DRAW_TYPE,AMOUNT,SYS_ACCOUNT) values(?, ?, ?, ?, ?, ?)",
//                RandomGUID.genRandomGUID(false), SERIAL_ID, conId, "4", shop, param.getSYS_ACCOUNT());//店铺返利
//
//        /**/
//        Map<String, String> rtnMap = null;//给前端返回结果值
//        if (BigDecimal.ONE.equals(isAuto) && "0".equals(param.getREFUND_TYPE())) {//如果自动提现-,且为微信提现方式
//            //做提现处理
//            rtnMap = WeiXinMchPayService.mchPayNew(SERIAL_ID, isAuto);
//        }
//        rtnMap = rtnMap == null ? new HashMap<String, String>() : rtnMap;
//        rtnMap.put("SERIAL_ID", SERIAL_ID);
//
//        return rtnMap;
//    }
//
//    /**
//     * 企业支付
//     *
//     * @param SERIAL_ID 申请单编号
//     * @return Map<String               ,                               String> map
//     */
//    public static Map<String, String> mchPayNew(String SERIAL_ID, BigDecimal isAuto) {
//        Request request = Request.getWithdrawByID(SERIAL_ID);
//        BigDecimal WITHDRAW_OUTER_STATUS = request.getWITHDRAW_OUTER_STATUS();
//        String CON_ID = request.getCON_ID();
//        String REAL_NAME = request.getREAL_NAME();
//        //用户信息
//        Customer customer = Customer.getById(CON_ID);
//        String OPEN_ID = customer.getOPENID_WECHAT();
//        BigDecimal ACTUAL_AMOUNT = request.getACTUAL_AMOUNT();
//        BigDecimal TAX = request.getTAX() == null ? BigDecimal.ZERO : request.getTAX();
//        BigDecimal status = request.getSTATUS();
//        String sys_account = request.getSYS_ACCOUNT();
//        String refundeType = request.getREFUND_TYPE();
//
//        if (WITHDRAW_OUTER_STATUS.equals(BigDecimal.ONE) || !status.equals(BigDecimal.ZERO)) {
//            throw new CustException("此单号已提交微信提现申请" + ",或系统单据状态是非申请中");
//        }
//        if (!"0".equals(refundeType))//0 -代表微信钱包，非微信钱包提现方式，不走以下流程
//            throw new CustException("非微信提现方式，不能发起提现！");
//
//        Map<String, String> responseMsg = null;
//        ApiConfigKit.setThreadLocalApiConfig(MyConfig.getApiConfig());//加载公众号信息
//        if (BigDecimal.ONE.equals(isAuto)) {//自动提现
//            WithdrawNoCheckCall callBack = new WithdrawNoCheckCall(SERIAL_ID, CON_ID, ACTUAL_AMOUNT.toString(), TAX.toString(), REAL_NAME, "", sys_account);
//            Map<String, Object> map = (Map<String, Object>) Db.execute(callBack);
//            String result = (String) map.get("RESULT");
//            //先处理存储过程，在处理微信企业支付
//            responseMsg = afterProExecute(CON_ID, result, SERIAL_ID, OPEN_ID, REAL_NAME, ACTUAL_AMOUNT);
//        } else {//人工审核
//            //手动提现流程
//            WithdrawCall callBack2 = new WithdrawCall(SERIAL_ID, CON_ID, "");
//            Map<String, Object> map = (Map<String, Object>) Db.execute(callBack2);
//            String result = (String) map.get("RESULT");
//            //先处理存储过程，在处理微信企业支付
//            responseMsg = afterProExecute(CON_ID, result, SERIAL_ID, OPEN_ID, REAL_NAME, ACTUAL_AMOUNT);
//        }
//
//        return responseMsg;
//    }
//
//    private static Map<String, String> afterProExecute(String conId, String result, String SERIAL_ID, String OPEN_ID, String REAL_NAME, BigDecimal ACTUAL_AMOUNT) {
//        /*20161124 起，对于微信是否真实提现成功，交由WithDrawConfirmJob 去处理   原处理逻辑见：afterProExecute_EX20161124() */
//        Map<String, String> rsMap = null;
//        String confirmTag = "1";
//        String qryWechatMsg = "";
//        String updateDt = "";
//        //存储过程处理成功
//        if ("1".equals(result)) {
//            rsMap = getWxMchPayResult(SERIAL_ID, OPEN_ID, REAL_NAME, ACTUAL_AMOUNT, "OPTION_CHECK");
//            /*调起企业支付api失败*/
//            if (null == rsMap) {
//                rsMap = new HashMap<>();
//                rsMap.put("result_code", "FAIL");
//                rsMap.put("return_msg", "企业支付失败，请联系客服！");
//                qryWechatMsg = "企业支付失败，请联系客服!";
//                withDrawFailedAndNotice(conId, SERIAL_ID, OPEN_ID, ACTUAL_AMOUNT, qryWechatMsg);
//            }
//            /*处理微信返回结果*/
//            if ("SUCCESS".equals(rsMap.get("result_code"))) {//return_code
//                Db.update("update bas_customer_request set FINANCE_CHECKTIME = ?, CASHIER_CHECKTIME = ?  where  SERIAL_ID=? ", rsMap.get("payment_time"), rsMap.get("payment_time"), SERIAL_ID);
//                NoticeService.sendMchPayNotice(conId, OPEN_ID, ACTUAL_AMOUNT.toString(), Util.nowDateTimeToString(), "1", "");//成功消息
//            } else if ("SYSTEMERROR".equals(rsMap.get("err_code"))) {
//
//                confirmTag = "0";
//                qryWechatMsg = rsMap.get("err_code_des");
//            } else {
//                // String errMsg = rsMap.get("return_msg");
//                String errMsg = rsMap.get("return_msg") + rsMap.get("err_code_des"); //洪武修改 2017-03-04
//                rsMap.put("return_msg", errMsg);
//                //微信企业支付失败
//                qryWechatMsg = errMsg;
//                withDrawFailedAndNotice(conId, SERIAL_ID, OPEN_ID, ACTUAL_AMOUNT, errMsg);
//
//            }
//        } else {//存储过程处理失败
//            rsMap = new HashMap<>();
//            rsMap.put("result_code", "FAIL");
//            rsMap.put("return_msg", "系统数据处理异常:" + result);
//            qryWechatMsg = "系统数据处理异常,请联系客服!";
//            withDrawFailedAndNotice(conId, SERIAL_ID, OPEN_ID, ACTUAL_AMOUNT, qryWechatMsg);
//        }
//
//        /*增加微信处理结果日志*/
//        WechatWithdrawLog.insertLog(RandomGUID.genRandomGUID(false), SERIAL_ID, OPEN_ID, JsonUtils.toJson(rsMap)
//                , result, "system", Util.nowDateTimeToString(), confirmTag, qryWechatMsg, updateDt);
//        /*============*/
//
//        return rsMap;
//    }
//
//    public static void withDrawFailedAndNotice(String conId, String serial_id, String open_id, BigDecimal actual_amount, String errMsg) {
//        Request.wechatRefundFail(serial_id);
//        NoticeService.sendMchPayNotice(conId, open_id, actual_amount.toString(), Util.nowDateTimeToString(), "0", errMsg);
//    }
//
//    /**
//     * 余额提现申请结果通知
//     */
//    public static ApiResult sendMchPayNotice(String conId, String OPENID_WECHAT, String ACTUAL_AMOUNT, String CREATE_DT, String CHECK_RESULT, String CHECK_REMARK) {
//        LOGGER.info("余额提现申请结果通知：" + OPENID_WECHAT);
//        String temp = NoticeTemplate.getMchPayNotice(OPENID_WECHAT, ACTUAL_AMOUNT, CREATE_DT, CHECK_RESULT, CHECK_REMARK);
//        System.out.println(temp);
//        ApiResult apiResult = TemplateMsgApi.send(temp);
//        LOGGER.info("NoticeService.sendMchPayNotice...............apiResult=>" + apiResult);
//
//        /*添加站内信*/
//        String msg = String.format("您的款项由于【%s】，不能通过审核，请重新申请。", CHECK_REMARK);
//        if (CHECK_RESULT.equals("1")) {
//            msg = String.format("您的款项已于%s汇出，大约【%d天】内到帐，请注意查收。", Util.nowDateTimeToString(), 3);
//        }
//        SitMsgModel.sendSysMsg(conId, msg, arr[4]);
//
//        return apiResult;
//    }
//}
