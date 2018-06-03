/**
 * project name: hdy_project
 * file name:BasCustQrCodeService
 * package name:com.ns.customer.service
 * date:2018-02-02 14:36
 * author: wq
 * Copyright (c) CD Technology Co.,Ltd. All rights reserved.
 */
package com.ns.customer.service;

import com.ns.common.exception.CustException;
import com.ns.common.model.BasCustQrcode;
import com.ns.common.model.BasCustomer;
import com.ns.common.model.TldQrbgmParams;
import com.ns.common.qrcode.ImageKit;
import com.ns.common.qrcode.QrCodeUtil;
import com.ns.common.utils.DateUtil;
import com.ns.common.utils.GUIDUtil;
import com.ns.common.utils.Util;
import com.ns.file.cos.COSClientManager;
import com.ns.file.cos.Dir;
import com.ns.tld.service.TLdQrBgmParamsService;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;

import com.jfinal.weixin.sdk.api.SnsAccessToken;
import com.jfinal.weixin.sdk.api.SnsAccessTokenApi;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Objects;

/**
 * description: //TODO <br>
 * date: 2018-02-02 14:36
 *
 * @author wq
 * @version 1.0
 * @since JDK 1.8
 */
public class BasCustQrCodeService {
    private static final String COLUMN = "ID,CON_ID,CON_NO,CON_NAME,OPEN_ID,CODE_URL,FILE_PATH,BGM_ID,ENABLED,VERSION,STATUS,REMARK,CREATE_BY,CREATE_DT,UPDATE_DT";
    public static final String fileDir = PropKit.get("baseUploadPath") + "/qrcode/";
    public static final String imageUrl = PropKit.get("imageUrl");
    public static final BasCustQrCodeService me = new BasCustQrCodeService();
    private final BasCustQrcode dao = new BasCustQrcode().dao();
    static BasCustomerService basCustomerService = BasCustomerService.me;
    static TLdQrBgmParamsService bgmParamsService = TLdQrBgmParamsService.me;
    static BasCustRelationService relationService = BasCustRelationService.me;

    /**
     * 获取二维码
     *
     * @param conNo
     * @param type          0=刷新
     * @param bgmTemplateId
     * @return
     * @throws Exception
     */
    public BasCustQrcode getConQrcode(String conNo, int type, String bgmTemplateId) throws Exception {
        BasCustomer customer = basCustomerService.getCustomerByConNoNotNull(conNo);
        BasCustQrcode basCustQrcode = getDefaultQrcodeByConNo(conNo);
        //如果这个会员没有二维码
        if (basCustQrcode == null) {
            return createQrdode(customer, basCustQrcode, bgmParamsService.getDefault());
        } else if (basCustQrcode != null && type == 0) {
            return createQrdode(customer, basCustQrcode, bgmParamsService.getById(bgmTemplateId));
        } else {
            return basCustQrcode;
        }
    }

    /**
     * 创建二维码
     *
     * @param customer
     * @param basCustQrcode
     * @param tldQrbgmParams
     * @return
     * @throws Exception
     */
    private BasCustQrcode createQrdode(BasCustomer customer, BasCustQrcode basCustQrcode, TldQrbgmParams tldQrbgmParams) throws Exception {
        String qrCodeFilePath = genLocalQrCode(customer.getConNo());
        String relativePath;//图片合成
        String codeUrl;
        //如果这个会员没有产生过二维码.则新增
        if (basCustQrcode == null) {
            relativePath = mergeImg(tldQrbgmParams, qrCodeFilePath, customer);
            codeUrl = savePicToFileServer(relativePath);
            basCustQrcode = setQrcodeAttr(customer, tldQrbgmParams.getID(), codeUrl, relativePath);
            basCustQrcode.save();
            return basCustQrcode;
        } else {
            //会员已经有二维码,换模板
            BasCustQrcode basCustQrcode2 = getQrcodeByConNoAndBgmId(customer.getConNo(), tldQrbgmParams.getID());
            //新模板
            if (basCustQrcode2 == null) {
                relativePath = mergeImg(tldQrbgmParams, qrCodeFilePath, customer);
                codeUrl = savePicToFileServer(relativePath);
                basCustQrcode2 = setQrcodeAttr(customer, tldQrbgmParams.getID(), codeUrl, relativePath);
                basCustQrcode2.save();
                //默认设为非默认
                basCustQrcode.setSTATUS(0);
                basCustQrcode.update();
            } else {
                if (Objects.equals(basCustQrcode.getID(), basCustQrcode2.getID())) {
                    relativePath = mergeImg(tldQrbgmParams, qrCodeFilePath, customer);
                    codeUrl = savePicToFileServer(relativePath);
                    basCustQrcode2.setCodeUrl(codeUrl);
                    basCustQrcode2.setFilePath(relativePath);
                    basCustQrcode2.setBgmId(tldQrbgmParams.getID());
                    basCustQrcode2.setVERSION(basCustQrcode2.getVERSION() + 1);
                    basCustQrcode2.update();
                } else {
                    relativePath = mergeImg(tldQrbgmParams, qrCodeFilePath, customer);
                    codeUrl = savePicToFileServer(relativePath);
                    basCustQrcode2.setCodeUrl(codeUrl);
                    basCustQrcode2.setFilePath(relativePath);
                    basCustQrcode2.setBgmId(tldQrbgmParams.getID());
                    basCustQrcode2.setVERSION(basCustQrcode2.getVERSION() + 1);
                    basCustQrcode2.setSTATUS(1);
                    basCustQrcode2.update();
                    //默认设为非默认
                    basCustQrcode.setSTATUS(0);
                    basCustQrcode.update();
                }
            }
            return basCustQrcode2;
        }
    }

    public BasCustQrcode getQrcodeByConNoAndBgmId(String conNo, String bgmId) {
        return dao.findFirst("select " + COLUMN + " from bas_cust_qrcode where con_no = ? and bgm_id = ?", conNo, bgmId);
    }

    private BasCustQrcode setQrcodeAttr(BasCustomer customer, String bgmId, String codeUrl, String filePath) {
        BasCustQrcode basCustQrcode = new BasCustQrcode();
        basCustQrcode.setID(GUIDUtil.getGUID());
        basCustQrcode.setConId(customer.getID());
        basCustQrcode.setConName(customer.getConName());
        basCustQrcode.setConNo(customer.getConNo());
        basCustQrcode.setBgmId(bgmId);
        basCustQrcode.setOpenId(customer.getOPENID());
        basCustQrcode.setCodeUrl(codeUrl);
        basCustQrcode.setFilePath(filePath);
        basCustQrcode.setSTATUS(1);//设为默认二维码
        basCustQrcode.setCreateDt(DateUtil.getNow());
        basCustQrcode.setUpdateDt(DateUtil.getNow());
        return basCustQrcode;
    }

    /**
     * 合成二维码
     *
     * @param baseFile
     * @param qrCodeFilePath
     * @param customer
     * @return
     */
    public String mergeImg(TldQrbgmParams baseFile, String qrCodeFilePath, BasCustomer customer) {
        String saveFileName = "";
        String pic = customer.getPIC();
        String conName = customer.getConName();
        String con_no = customer.getConNo();
        try {
            InputStream imageIn2 = new FileInputStream(qrCodeFilePath);
            BufferedImage imageBase = ImageKit.getImageURL(baseFile.getCodeUrl());//背景图
            BufferedImage imageTwo = ImageIO.read(imageIn2);
            Graphics g = imageBase.getGraphics();
            //给二维码绘画头像
            try {
                if (StrKit.notBlank(pic) && baseFile.getShowIcon() == 1) {
                    if (!pic.startsWith("http")) {
                        pic = imageUrl + pic;
                    }
                    HttpClient httpclient = HttpClients.createDefault();
                    HttpGet httpget = new HttpGet(pic);
                    HttpResponse response = httpclient.execute(httpget);
                    HttpEntity entity = response.getEntity();
                    InputStream avatarIn = entity.getContent();
                    BufferedImage imageAvatar = ImageIO.read(avatarIn);
                    g.drawImage(imageAvatar, baseFile.getIconX(), baseFile.getIconY(), baseFile.getIconW(), baseFile.getIconH(), null);
                }
            } catch (Exception e) {
                e.getMessage();
            }
            g.drawImage(imageTwo, baseFile.getQrX(), baseFile.getQrY(), baseFile.getQrW(), baseFile.getQrH(), null);//x , y , w , h
            g.setColor(Color.BLACK);
            if (baseFile.getShowConName() == 1) {
                g.setFont(new Font(baseFile.getFontCharset(), Font.PLAIN, baseFile.getFontSize()));
                g.drawString(conName == null ? con_no : conName, baseFile.getConNameX(), baseFile.getConNameY());
            }
            saveFileName = fileDir + con_no + "/" + con_no + ".jpg";
            Util.mkDir(saveFileName);
            OutputStream outImage = new FileOutputStream(saveFileName);
            ImageIO.write(imageBase, "jpeg", outImage);
            imageIn2.close();
            outImage.close();
        } catch (Exception e) {
            throw new CustException("图片合成失败!{}" + e);
        }

        return saveFileName;
    }

    /**
     * 调用文件上传，把文件上传到文件服务器
     *
     * @param filePath 文件绝对路径
     * @return 文件在服务器上的访问地址
     */
    public String savePicToFileServer(String filePath) {
        String serverUrl = "";
        try {
//            serverUrl = FastDfsService.upFile(filePath, "qrcode", "jpg");//老式
            //serverUrl = FastDfsService.upFileWithGroupName(filePath, "group1", "jpg");//新款
            serverUrl = COSClientManager.getInstance().uploadFile2COS(filePath, Dir.DIMEN);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*删除临时文件*/
        File file = new File(filePath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
        }
        return serverUrl;
    }

    /**
     * 生成本地二维码
     *
     * @param con_no
     * @return
     * @throws Exception
     */
    private String genLocalQrCode(String con_no) throws Exception {

        //String qrCode_exInfo = PropKit.get("serverUrlQrCode") + "?CON_NO=" + con_no + "&SYS_ACCOUNT=" + sysacct;
        String qrCode_exInfo = PropKit.get("serverUrl") + "api/qrcode/sanQrCode?conNo=" + con_no;

        String filePath = fileDir + con_no;
        String fileName = QrCodeUtil.encode(qrCode_exInfo, "", filePath, con_no, "png", true);
        return filePath + "/" + fileName;

    }

    /**
     * 获取用户默认二维码
     *
     * @param conNo
     * @return
     */
    public BasCustQrcode getDefaultQrcodeByConNo(String conNo) {
        return dao.findFirst("select " + COLUMN + " from bas_cust_qrcode where con_no = ? and status = 1", conNo);
    }

    public String sanQrCode(String conNo) {
        String redirect_uri = PropKit.get("serverUrl") + "api/qrcode/dealSanQrCode";
        return SnsAccessTokenApi.getAuthorizeURL(PropKit.get("appId"), redirect_uri, conNo, true);
    }

    public String dealSanQrCode(String code, String state) {
        SnsAccessToken snsAccessToken = SnsAccessTokenApi.getSnsAccessToken(PropKit.get("appId"), PropKit.get("appSecret"), code);
        String openid = snsAccessToken.getOpenid();
        String conNo = state;
        relationService.saveRelation(conNo, openid, "", 0, "", "", "", "");
        return "http://xhd777.com.cn/ns-wechat/html/focusJump.html";
    }
}
