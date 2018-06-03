package com.ns.common.json;

/**
 * Created by Administrator on 2016-02-22.
 */
public class JsonResult {
    private final int result = 0;

    private Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getResult() {
        return result;
    }

    //public void setResult(int result) {
    //    this.result = result;
    //}

    public JsonResult(Object data) {
        this.data = data;
    }

    public static JsonResult newJsonResult(Object data){
        return new JsonResult(data);
    }
}
