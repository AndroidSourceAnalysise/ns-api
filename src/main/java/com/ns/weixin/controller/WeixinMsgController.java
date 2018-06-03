package com.ns.weixin.controller;

import com.ns.common.task.Task;
import com.ns.common.task.TaskQueuePlugin;
import com.ns.customer.service.BasCustomerService;
import com.ns.customer.task.CreateCustomerTask;
import com.jfinal.log.Log;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.CustomServiceApi;
import com.jfinal.weixin.sdk.api.UserApi;
import com.jfinal.weixin.sdk.jfinal.MsgControllerAdapter;
import com.jfinal.weixin.sdk.msg.in.*;
import com.jfinal.weixin.sdk.msg.in.event.*;
import com.jfinal.weixin.sdk.msg.in.speech_recognition.InSpeechRecognitionResults;
import com.jfinal.weixin.sdk.msg.out.OutCustomMsg;
import com.jfinal.weixin.sdk.msg.out.OutTextMsg;
import com.jfinal.weixin.sdk.msg.out.OutVoiceMsg;

public class WeixinMsgController extends MsgControllerAdapter {
    static Log logger = Log.getLog(WeixinMsgController.class);
    private static final String helpStr = "\t你的品位不错哦  么么哒。";

    @Override
    protected void processInTextMsg(InTextMsg inTextMsg) {
        OutTextMsg outMsg = new OutTextMsg(inTextMsg);
        outMsg.setContent("文本消息~");
        render(outMsg);
    }

    @Override
    protected void processInVoiceMsg(InVoiceMsg inVoiceMsg) {
        //转发给多客服PC客户端
//		OutCustomMsg outCustomMsg = new OutCustomMsg(inVoiceMsg);
//		render(outCustomMsg);
        OutVoiceMsg outMsg = new OutVoiceMsg(inVoiceMsg);
        // 将刚发过来的语音再发回去
        outMsg.setMediaId(inVoiceMsg.getMediaId());
        render(outMsg);
    }

    @Override
    protected void processInVideoMsg(InVideoMsg inVideoMsg) {
        /*
         * 腾讯 api 有 bug，无法回复视频消息，暂时回复文本消息代码测试 OutVideoMsg outMsg = new
         * OutVideoMsg(inVideoMsg); outMsg.setTitle("OutVideoMsg 发送");
         * outMsg.setDescription("刚刚发来的视频再发回去"); // 将刚发过来的视频再发回去，经测试证明是腾讯官方的 api
         * 有 bug，待 api bug 却除后再试 outMsg.setMediaId(inVideoMsg.getMediaId());
         * render(outMsg);
         */
        OutTextMsg outMsg = new OutTextMsg(inVideoMsg);
        outMsg.setContent("\t视频消息已成功接收，该视频的 mediaId 为: " + inVideoMsg.getMediaId());
        render(outMsg);
    }

    @Override
    protected void processInShortVideoMsg(InShortVideoMsg inShortVideoMsg) {
        OutTextMsg outMsg = new OutTextMsg(inShortVideoMsg);
        outMsg.setContent("\t视频消息已成功接收，该视频的 mediaId 为: " + inShortVideoMsg.getMediaId());
        render(outMsg);
    }

    @Override
    protected void processInLocationMsg(InLocationMsg inLocationMsg) {
        OutTextMsg outMsg = new OutTextMsg(inLocationMsg);
        outMsg.setContent("已收到地理位置消息:" + "\nlocation_X = " + inLocationMsg.getLocation_X() + "\nlocation_Y = "
                + inLocationMsg.getLocation_Y() + "\nscale = " + inLocationMsg.getScale() + "\nlabel = "
                + inLocationMsg.getLabel());
        render(outMsg);
    }

    @Override
    protected void processInLinkMsg(InLinkMsg inLinkMsg) {
        //转发给多客服PC客户端
        OutCustomMsg outCustomMsg = new OutCustomMsg(inLinkMsg);
        render(outCustomMsg);
    }

    @Override
    protected void processInCustomEvent(InCustomEvent inCustomEvent) {
        logger.debug("测试方法：processInCustomEvent()");
        renderNull();
    }

    @Override
    protected void processInImageMsg(InImageMsg inImageMsg) {
        //转发给多客服PC客户端
        OutCustomMsg outCustomMsg = new OutCustomMsg(inImageMsg);
        render(outCustomMsg);

    }

    /**
     * 实现父类抽方法，处理关注/取消关注消息
     */
    @Override
    protected void processInFollowEvent(InFollowEvent inFollowEvent) {
        String openId = inFollowEvent.getFromUserName();
        if (InFollowEvent.EVENT_INFOLLOW_SUBSCRIBE.equals(inFollowEvent.getEvent())) {
            logger.debug("关注：" + inFollowEvent.getFromUserName());
            Task task = new CreateCustomerTask("", openId);
            TaskQueuePlugin.use("default").putTask(task);
        }
        // 如果为取消关注事件，将无法接收到传回的信息
        if (InFollowEvent.EVENT_INFOLLOW_UNSUBSCRIBE.equals(inFollowEvent.getEvent())) {
            BasCustomerService service = new BasCustomerService();
            service.cancelSubscribe(openId);
            logger.debug("取消关注：" + inFollowEvent.getFromUserName());
        }
        renderNull();
    }

    @Override
    protected void processInQrCodeEvent(InQrCodeEvent inQrCodeEvent) {
        String openId = inQrCodeEvent.getFromUserName();
        if (InQrCodeEvent.EVENT_INQRCODE_SUBSCRIBE.equals(inQrCodeEvent.getEvent())) {
            String refNo = getSceneNo(inQrCodeEvent.getEventKey());
            logger.debug("扫码未关注：" + inQrCodeEvent.getFromUserName());
            Task task = new CreateCustomerTask(refNo, openId);
            TaskQueuePlugin.use("default").putTask(task);
           /* OutTextMsg outMsg = new OutTextMsg(inQrCodeEvent);
            outMsg.setContent("感谢您的关注，二维码内容：" + inQrCodeEvent.getEventKey());*/
        }
        if (InQrCodeEvent.EVENT_INQRCODE_SCAN.equals(inQrCodeEvent.getEvent())) {
            logger.debug("扫码已关注：" + inQrCodeEvent.getFromUserName());
            renderOutTextMsg("扫码已关注,二维码内容：" + inQrCodeEvent.getEventKey());
            CustomServiceApi.sendText(openId, "请不要重复关注");
        }
        renderNull();
    }

    private static String getSceneNo(String qrscene) {
        String[] data = qrscene.split("_");
        return data[1];
    }

    @Override
    protected void processInLocationEvent(InLocationEvent inLocationEvent) {
        logger.debug("发送地理位置事件：" + inLocationEvent.getFromUserName());
        OutTextMsg outMsg = new OutTextMsg(inLocationEvent);
        outMsg.setContent("地理位置是：" + inLocationEvent.getLatitude());
        render(outMsg);
    }

    @Override
    protected void processInMassEvent(InMassEvent inMassEvent) {
        logger.debug("测试方法：processInMassEvent()");
        renderNull();
    }

    /**
     * 实现父类抽方法，处理自定义菜单事件
     */
    @Override
    protected void processInMenuEvent(InMenuEvent inMenuEvent) {
        logger.debug("菜单事件：" + inMenuEvent.getFromUserName());
        OutTextMsg outMsg = new OutTextMsg(inMenuEvent);
        outMsg.setContent("菜单事件内容是：" + inMenuEvent.getEventKey());
        render(outMsg);
    }

    @Override
    protected void processInSpeechRecognitionResults(InSpeechRecognitionResults inSpeechRecognitionResults) {
        logger.debug("语音识别事件：" + inSpeechRecognitionResults.getFromUserName());
        OutTextMsg outMsg = new OutTextMsg(inSpeechRecognitionResults);
        outMsg.setContent("语音识别内容是：" + inSpeechRecognitionResults.getRecognition());
        render(outMsg);
    }

    @Override
    protected void processInTemplateMsgEvent(InTemplateMsgEvent inTemplateMsgEvent) {
        logger.debug("测试方法：processInTemplateMsgEvent()");
        renderNull();
    }


}
