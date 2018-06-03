package com.ns.common.interceptor;

import com.ns.common.exception.CustException;
import com.ns.common.json.JsonError;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.log.Log;
import org.apache.log4j.Logger;


/**
 * description: 全局拦截器//TODO <br>
 * date: 2018-02-01 17:22
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class GlobalInterceptor implements Interceptor {
    protected final Log LOGGER = Log.getLog(this.getClass());
    @Override
    public void intercept(Invocation inv) {
        try {
            inv.getController().getResponse().setHeader("Access-Control-Allow-Origin", "*");
            inv.invoke();
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error Controller:" + e);
            if (e instanceof CustException) {
                CustException custException = (CustException) e;
                inv.getController().renderJson(custException.getJsonError());
            } else {
                inv.getController().renderJson(JsonError.newJsonError(500, e.getMessage()));
                LOGGER.error("ERROR: " + e);
            }
        }
        LOGGER.info("Global After invoking " + inv.getActionKey());
    }
}
