package com.ns.common;

import com.ns.applet.controller.AppletController;
import com.ns.cart.controller.ShopCartController;
import com.ns.common.interceptor.GlobalInterceptor;
import com.ns.common.model._MappingKit;
import com.ns.common.quartzplugin.QuartzPlugin;
import com.ns.common.task.TaskQueuePlugin;
import com.ns.customer.controller.*;
import com.ns.file.controller.FileController;
import com.ns.index.IndexController;
import com.ns.node.controller.*;
import com.ns.pnt.controller.PntMenuController;
import com.ns.pnt.controller.PntProductCmtController;
import com.ns.pnt.controller.PntProductController;
import com.ns.sys.controller.SysDictController;
import com.ns.tld.controller.*;
import com.ns.tld.job.OrderCloseJob;
import com.ns.tld.job.OrderConfirmJob;
import com.ns.weixin.controller.WeixinApiController;
import com.ns.weixin.controller.WeixinMsgController;
import com.ns.weixin.controller.WeixinPayController;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.plugin.redis.RedisPlugin;
import com.jfinal.template.Engine;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiConfigKit;

/**
 * API引导式配置
 */
public class MyConfig extends JFinalConfig {
    // 本地开发模式
    private boolean isLocalDev = false;

    /**
     * 如果要支持多公众账号，只需要在此返回各个公众号对应的 ApiConfig 对象即可 可以通过在请求 url 中挂参数来动态从数据库中获取
     * ApiConfig 属性值
     */
    public static ApiConfig getApiConfig() {
        ApiConfig ac = new ApiConfig();

        // 配置微信 API 相关常量
        ac.setToken(PropKit.get("token"));
        ac.setAppId(PropKit.get("appId"));
        ac.setAppSecret(PropKit.get("appSecret"));

        /**
         * 是否对消息进行加密，对应于微信平台的消息加解密方式： 1：true进行加密且必须配置 encodingAesKey
         * 2：false采用明文模式，同时也支持混合模式
         */
        ac.setEncryptMessage(PropKit.getBoolean("encryptMessage", false));
        ac.setEncodingAesKey(PropKit.get("encodingAesKey",
                "setting it in config file"));
        return ac;
    }

    /**
     * 如果生产环境配置文件存在，则优先加载该配置，否则加载开发环境配置文件
     *
     * @param pro 生产环境配置文件
     * @param dev 开发环境配置文件
     */
    public void loadProp(String pro, String dev) {
        try {
            PropKit.use(pro);
        } catch (Exception e) {
            PropKit.use(dev);
            isLocalDev = true;
        }
    }

    /**
     * 配置常量
     */
    @Override
    public void configConstant(Constants me) {
        // 加载少量必要配置，随后可用PropKit.get(...)获取值
        loadProp("a_little_config_pro.txt", "a_little_config.txt");
        me.setDevMode(PropKit.getBoolean("devMode", false));
        // ApiConfigKit 设为开发模式可以在开发阶段输出请求交互的 xml 与 json 数据
        ApiConfigKit.setDevMode(me.getDevMode());
    }

    /**
     * 配置路由
     */
    @Override
    public void configRoute(Routes me) {
        me.add("/", IndexController.class, "/index");    // 第三个参数为该Controller的视图存放路径
        me.add("/api/msg", WeixinMsgController.class);// 第三个参数省略时默认与第一个参数值相同，在此即为 "/blog"
        me.add("/api/customer", BasCustomerController.class);
        me.add("/api/file", FileController.class);
        me.add("/api/ext", BasCustomerExtController.class);
        me.add("/api/address", BasCustAddressController.class);
        me.add("/api/qrcode", BasCustQrCodeController.class);
        me.add("/api/pntcmt", PntProductCmtController.class);
        me.add("/api/photos", TLdPhotosController.class);
        me.add("/api/pnt", PntProductController.class);
        me.add("/api/coupon", TldCouponController.class);
        me.add("/api/sys/redis", RedisController.class);
        me.add("/api/sitemsg", TldSiteMsgController.class);
        me.add("/api/order", TldOrdersController.class);
        me.add("/api/weixin/pay", WeixinPayController.class);
        me.add("/api/sys/dict", SysDictController.class);
        me.add("/api/request", BasCustomerRequestController.class);
        me.add("/api/node/category", NodeCategoryController.class);
        me.add("/api/node/cmt", NodeCmtController.class);
        me.add("/api/node/collection", NodeCollectionController.class);
        me.add("/api/node/content", NodeContentController.class);
        me.add("/api/node/focus", NodeFocusController.class);
        me.add("/api/node/like", NodeLikeController.class);
        me.add("/api/pnt/menu", PntMenuController.class);
        me.add("/api/identifycode", TldIdentifyCodeController.class);
        me.add("/api/api", WeixinApiController.class);
        me.add("/api/cart", ShopCartController.class);
        me.add("/api/applet", AppletController.class);


    }

    @Override
    public void configEngine(Engine me) {
    }

    /**
     * 配置插件
     */
    @Override
    public void configPlugin(Plugins me) {
        // 配置 druid 数据库连接池插件
        DruidPlugin druidPlugin = new DruidPlugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password").trim());
        druidPlugin.setConnectionInitSql("set names utf8mb4");
        me.add(druidPlugin);

        // 配置ActiveRecord插件
        ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
        // 所有映射在 MappingKit 中自动化搞定
        _MappingKit.mapping(arp);
        me.add(arp);
        // ehcahce插件配置
        me.add(new EhCachePlugin());

        RedisPlugin bbsRedis = new RedisPlugin("ns", "45.40.251.65", 7173, "h$n#s231");
        me.add(bbsRedis);
        //定时任务
        QuartzPlugin quartzPlugin = new QuartzPlugin(OrderCloseJob.class, OrderConfirmJob.class);
        me.add(quartzPlugin);

        //会员任务池
        int taskQueueSize = PropKit.getInt("taskQueueSize", 10000);
        int taskQueueThreads = PropKit.getInt("taskQueueThreads", 10);
        TaskQueuePlugin taskQueuePlugin = new TaskQueuePlugin("default", taskQueueSize, taskQueueThreads);
        me.add(taskQueuePlugin);
    }

    public static DruidPlugin createDruidPlugin() {
        return new DruidPlugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password").trim());
    }

    /**
     * 配置全局拦截器
     */
    @Override
    public void configInterceptor(Interceptors me) {
        me.add(new GlobalInterceptor());
    }

    /**
     * 配置处理器
     */
    @Override
    public void configHandler(Handlers me) {
        me.add(new ContextPathHandler());
    }

    /**
     * 系统启动完成后回调
     */
    @Override
    public void afterJFinalStart() {
        ApiConfigKit.putApiConfig(getApiConfig());
        //初始化fastdfs
        //FastDfsUtil util = FastDfsUtil.getInstance();
        //util.init();
    }

    /**
     * 系统关闭之前回调
     */
    @Override
    public void beforeJFinalStop() {
    }

    /**
     * 运行此 main 方法可以启动项目，此main方法可以放置在任意的Class类定义中，不一定要放于此
     * <p>
     * 使用本方法启动过第一次以后，会在开发工具的 debug、run config 中自动生成
     * 一条启动配置，可对该自动生成的配置再添加额外的配置项，例如 VM argument 可配置为：
     * -XX:PermSize=64M -XX:MaxPermSize=256M
     */
    public static void main(String[] args) {
        /**
         * 特别注意：Eclipse 之下建议的启动方式
         */
        JFinal.start("src/main/webapp", 80, "/", 5);

        /**
         * 特别注意：IDEA 之下建议的启动方式，仅比 eclipse 之下少了最后一个参数
         */
        // JFinal.start("src/main/webapp", 80, "/");
    }
}
