package com.ns.common.exception;

import com.ns.common.json.JsonError;

/**
 * Created by Administrator on 2016-03-01.
 */
public class CustException extends RuntimeException {
    public CustException(String message) {
        super(message);
        this.msg = message;
    }

    private int errCode;
    private String msg;

    public CustException(int errCode, String message) {
        super(message);
        this.errCode = errCode;
        this.msg = message;
    }

    public CustException(Throwable cause) {
        super(cause);
    }

    public CustException(String message, Throwable cause) {
        super(message, cause);
        this.msg = message;
    }

    public JsonError getJsonError() {

        return JsonError.newJsonError(errCode, msg);
    }
}
