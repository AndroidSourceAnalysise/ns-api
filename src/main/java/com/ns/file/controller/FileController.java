package com.ns.file.controller;

import com.jfinal.upload.UploadFile;
import com.ns.common.base.BaseController;
import com.ns.common.json.JsonResult;
import com.ns.file.cos.COSClientManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileController extends BaseController {
    public void upload() {
        List<HashMap<String, String>> list = new ArrayList<>();
        List<UploadFile> uploadFiles = getFiles();//文件上传
        String name;
        String fileAbsolutePath = "";
        for (UploadFile upFile : uploadFiles) {
            HashMap<String, String> map = new HashMap<>();
            name = upFile.getParameterName();
            final String type = getPara("type"); // 表单数据 上传文件的类别
            fileAbsolutePath = COSClientManager.getInstance().uploadFile2COS(upFile.getFile(), type);
            map.put("name", name);
            map.put("type", type);
            map.put("url", fileAbsolutePath);
            list.add(map);
        }
        this.getResponse().setHeader("Access-Control-Allow-Origin", "*");
        if (uploadFiles.size() > 1) {
            renderJson(JsonResult.newJsonResult(list));
        } else {
            renderJson(JsonResult.newJsonResult(fileAbsolutePath));//单张图片上传，bug修复
        }
    }

}
