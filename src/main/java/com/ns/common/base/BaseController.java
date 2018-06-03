/**
 * project name: hdy_project
 * file name:BaseController
 * package name:com.ns.common.base
 * date:2018-02-02 13:40
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.common.base;

import com.alibaba.fastjson.JSONObject;
import com.ns.common.exception.CustException;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * description: //TODO <br>
 * date: 2018-02-02 13:40
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class BaseController extends Controller {
    protected final Log logger = Log.getLog(this.getClass());

    /**
     * 取Request中的数据对象
     *
     * @param valueType
     * @return
     * @throws Exception
     */
    public static <T> T getRequestObject(HttpServletRequest request, Class<T> valueType) {
        int len = request.getContentLength();
        if (len > 0) {
            try (ServletInputStream inputStream = request.getInputStream();) {
                byte[] jsonBytes = getBytes(len, inputStream);
                inputStream.close();
                return JSONObject.parseObject(jsonBytes, valueType);
            } catch (Exception e) {
                e.printStackTrace();
                throw new CustException("输入参数解析失败", e);
            }
        } else {
            return JSONObject.parseObject("{}", valueType);
        }
    }

    private static byte[] getBytes(int len, ServletInputStream inputStream) throws IOException {
        byte[] jsonBytes = new byte[len];
        byte[] temp = new byte[1024];
        int readLen = 0;
        int destPos = 0;
        while ((readLen = inputStream.read(temp)) > 0) {
            System.arraycopy(temp, 0, jsonBytes, destPos, readLen);
            destPos += readLen;
        }
        inputStream.read(jsonBytes, 0, len);
        return jsonBytes;
    }
}
