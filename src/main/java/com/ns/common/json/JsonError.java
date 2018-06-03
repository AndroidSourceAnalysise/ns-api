package com.ns.common.json;

/**
 * Created by Administrator on 2016-02-22.
 */
public class JsonError {
    public JsonError(int error, Object errorData) {
        //this.result = -1;
        this.error = error;
        this.errorData = errorData;
    }

    public int getResult() {
        return result;
    }

    //public void setResult(int result) {
    //    this.result = result;
    //}

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public Object getErrorData() {
        return errorData;
    }

    public void setErrorData(Object errorData) {
        this.errorData = errorData;
    }

    private final int result = -1;
    private int error;
    private Object errorData;

    public static JsonError newJsonError(int error, Object errorData){
        return new JsonError(error, errorData);
    }

    public static JsonError newJsonErrorParam(Object errorData){
        return new JsonError(1001, errorData);
    }
}
