package com.ns.weixin.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ns.common.model.BasCustomer;
import com.ns.customer.service.BasCustomerExtService;
import com.ns.customer.service.BasCustomerService;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.weixin.sdk.api.*;
import com.jfinal.weixin.sdk.jfinal.ApiController;

import java.util.List;

public class WeixinApiController extends ApiController {
    static BasCustomerService customerService = BasCustomerService.me;
    static BasCustomerExtService extService = BasCustomerExtService.me;

    /**
     * 为WeixinConfig onLineTokenUrl处提供AccessToken
     * <p>
     * 此处是为了开发测试和生产环境同时使用一套appId时为开发测试环境提供AccessToken
     * <p>
     * 设计初衷：https://www.oschina.net/question/2702126_2237352
     */
    public void getToken() {
        String key = getPara("key");
        String json = ApiConfigKit.getAccessTokenCache().get(key);
        renderText(json);
    }

    /**
     * 获取公众号菜单
     */
    public void getMenu() {
        ApiResult apiResult = MenuApi.getMenu();
        if (apiResult.isSucceed())
            renderText(apiResult.getJson());
        else
            renderText(apiResult.getErrorMsg());
    }

    /**
     * 创建菜单
     */
    public void createMenu() {
        String str = "{\n" +
                "    \"button\": [\n" +
                "        {\n" +
                "            \"name\": \"进入理财\",\n" +
                "            \"url\": \"http://m.bajie8.com/bajie/enter\",\n" +
                "            \"type\": \"view\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"安全保障\",\n" +
                "            \"key\": \"112\",\n" +
                "\t    \"type\": \"click\"\n" +
                "        },\n" +
                "        {\n" +
                "\t    \"name\": \"使用帮助\",\n" +
                "\t    \"url\": \"http://m.bajie8.com/footer/cjwt\",\n" +
                "\t    \"type\": \"view\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        ApiResult apiResult = MenuApi.createMenu(str);
        if (apiResult.isSucceed())
            renderText(apiResult.getJson());
        else
            renderText(apiResult.getErrorMsg());
    }

    /**
     * 获取公众号关注用户
     */
    public void getFollowers() {
        ApiResult apiResult = UserApi.getFollows();
        JSONObject jsonObject = apiResult.get("data");
        jsonObject.getJSONArray("openid");
        List stringList = jsonObject.getJSONArray("openid");
        for (int i = 0; i < stringList.size(); i++) {
            String openId = "";

            try {
                openId = String.valueOf(stringList.get(i));
                System.out.println("++++++++++++++++++++++++++++++++" + i + ":openId" + openId);
                String customer = Db.queryStr("select id from bas_customer where OPENID = ?", openId);
                if (StrKit.notBlank(customer)) {
                    continue;
                }
                System.out.println("*******************第" + i + "个会员start*******************");
                ApiResult result = getUserInfo(openId);
                BasCustomer customer1 = customerService.setCustomerAttr(result.getStr("nickname"), result.getStr("headimgurl"), result.getInt("sex"), 0, "", result.getStr("country"), result.getStr("province"),
                        result.getStr("city"), "", "", result.getStr("unionid"), openId, "", "", "");
                customer1.save();
                extService.addBasCustomerExt(customer1.getID(), customer1.getConNo(), customer1.getConName());
                System.out.println("*******************第" + i + "个会员end*******************");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("=========================================openId异常=========================================:" + openId);
            }
        }


        renderText(apiResult.getJson());
    }

    public void getFollowers2() {
        List<Record> recordList = Db.find("select ID,CON_NO,CON_NAME,CREATE_DT from bas_customer");
        for (Record user : recordList) {
            String customer = Db.queryStr("select id from bas_customer_ext where CON_ID = ?", user.getStr("ID"));
            try {
                if (StrKit.notBlank(customer)) {
                    continue;
                }
                extService.addBasCustomerExt(user.getStr("ID"), user.getStr("CON_NO"), user.getStr("CON_NAME"));
                System.out.println("会员================================================成功:"+user.getStr("CON_NO"));
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("=========================================openId异常=========================================:" + user.getStr("CON_NO"));
            }
        }
        renderText("OK");
    }

    /**
     * 获取用户信息
     */
    public void getUserInfo() {
        ApiResult apiResult = UserApi.getUserInfo("ohbweuNYB_heu_buiBWZtwgi4xzU");
        renderText(apiResult.getJson());
    }

    private ApiResult getUserInfo(String openId) {
        return UserApi.getUserInfo(openId);
    }

    /**
     * 发送模板消息
     */
    public void sendMsg() {
        String str = " {\n" +
                "           \"touser\":\"ohbweuNYB_heu_buiBWZtwgi4xzU\",\n" +
                "           \"template_id\":\"9SIa8ph1403NEM3qk3z9-go-p4kBMeh-HGepQZVdA7w\",\n" +
                "           \"url\":\"http://www.sina.com\",\n" +
                "           \"topcolor\":\"#FF0000\",\n" +
                "           \"data\":{\n" +
                "                   \"first\": {\n" +
                "                       \"value\":\"恭喜你购买成功！\",\n" +
                "                       \"color\":\"#173177\"\n" +
                "                   },\n" +
                "                   \"keyword1\":{\n" +
                "                       \"value\":\"去哪儿网发的酒店红包（1个）\",\n" +
                "                       \"color\":\"#173177\"\n" +
                "                   },\n" +
                "                   \"keyword2\":{\n" +
                "                       \"value\":\"1元\",\n" +
                "                       \"color\":\"#173177\"\n" +
                "                   },\n" +
                "                   \"remark\":{\n" +
                "                       \"value\":\"欢迎再次购买！\",\n" +
                "                       \"color\":\"#173177\"\n" +
                "                   }\n" +
                "           }\n" +
                "       }";
        ApiResult apiResult = TemplateMsgApi.send(str);
        renderText(apiResult.getJson());
    }

    /**
     * 获取参数二维码
     */
    public void getQrcode() {
        String str = "{\"expire_seconds\": 604800, \"action_name\": \"QR_SCENE\", \"action_info\": {\"scene\": {\"scene_id\": 123}}}";
        ApiResult apiResult = QrcodeApi.create(str);
        renderText(apiResult.getJson());

//        String str = "{\"action_name\": \"QR_LIMIT_STR_SCENE\", \"action_info\": {\"scene\": {\"scene_str\": \"123\"}}}";
//        ApiResult apiResult = QrcodeApi.create(str);
//        renderText(apiResult.getJson());
    }

    /**
     * 长链接转成短链接
     */
    public void getShorturl() {
        String str = "{\"action\":\"long2short\"," +
                "\"long_url\":\"http://wap.koudaitong.com/v2/showcase/goods?alias=128wi9shh&spm=h56083&redirect_count=1\"}";
        ApiResult apiResult = ShorturlApi.getShorturl(str);
        renderText(apiResult.getJson());
    }

    /**
     * 获取客服聊天记录
     */
    public void getRecord() {
        String str = "{\n" +
                "    \"endtime\" : 987654321,\n" +
                "    \"pageindex\" : 1,\n" +
                "    \"pagesize\" : 10,\n" +
                "    \"starttime\" : 123456789\n" +
                " }";
        ApiResult apiResult = CustomServiceApi.getRecord(str);
        renderText(apiResult.getJson());
    }

    /**
     * 获取微信服务器IP地址
     */
    public void getCallbackIp() {
        ApiResult apiResult = CallbackIpApi.getCallbackIp();
        renderText(apiResult.getJson());
    }
}

