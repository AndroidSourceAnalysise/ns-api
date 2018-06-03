package com.ns.file.cos;

import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.AccessControlList;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.Owner;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;

import java.io.*;
import java.net.URL;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * 腾讯云 COS文件类
 *
 * @author longway
 */
public class COSClientManager {
    private static final Logger LOGGER = Logger.getLogger(COSClientManager.class.getName());
    // accessKey
    private static final String accessKeyId = PropKit.get("ossAccessKeyId");
    private static final String accessKeySecret = PropKit.get("ossAccessKeySecret");
    //空间
    private static final String BUCKET_NAME = "ns-1256668373";

    private static final Object LOCK = new Object();

    private volatile static COSClientManager sInstance;

    private COSClient mCOSClient;

    private COSClientManager() {
// 1 初始化用户身份信息(secretId, secretKey)
        COSCredentials cred = new BasicCOSCredentials(accessKeyId, accessKeySecret);
// 2 设置bucket的区域, COS地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
        ClientConfig clientConfig = new ClientConfig(new Region("ap-chengdu"));
// 3 生成cos客户端
        mCOSClient = new COSClient(cred, clientConfig);
        AccessControlList accessControlList = new AccessControlList();
        Owner owner = new Owner();
        owner.setId("qcs::cam::uin/100005491024:uin/100005491024");
        accessControlList.setOwner(owner);
        mCOSClient.setBucketAcl(BUCKET_NAME, accessControlList);
    }

    public static COSClientManager getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) {
                    sInstance = new COSClientManager();
                }
            }
        }
        return sInstance;
    }


    @Override
    protected void finalize() {
        try {
            super.finalize();
        } catch (Throwable throwable) {
            LOGGER.info(throwable.getLocalizedMessage());
        } finally {
            destroy();
        }
    }

    /**
     * 销毁
     */
    private void destroy() {
        mCOSClient.shutdown();
        sInstance = null;
    }

    private String getFileExtension(File file) {
        if (file == null) {
            return "";
        }
        String path = file.getAbsolutePath();
        if (path.contains(".")) {
            return path.substring(path.lastIndexOf("."));
        }
        return "";
    }

    private byte[] getBytes(File file) {
        if (file == null || file.isDirectory() || !file.exists()) {
            return new byte[0];
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            byte[] buff = new byte[8192];
            int len;
            while ((len = bis.read(buff)) != -1) {
                bos.write(buff, 0, len);
            }
            return bos.toByteArray();
        } catch (FileNotFoundException e) {
            LOGGER.info(e.getLocalizedMessage());
        } catch (IOException ioe) {
            LOGGER.info(ioe.getLocalizedMessage());
        } finally {
            try {
                bos.close();
                if (fis != null) {
                    fis.close();
                }
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException e) {
                LOGGER.info(e.getLocalizedMessage());
            }
        }
        return new byte[0];
    }

    public boolean deleteFile(String key, String dir) {
        try {
            mCOSClient.deleteObject(BUCKET_NAME, dir + "/" + key);
            return true;
        } catch (CosServiceException oe) {
            LOGGER.info(oe.getMessage());
        }
        return false;
    }

    /**
     * /**
     * 上传到OSS服务器  如果同名文件会覆盖服务器上的
     *
     * @param file 文件流
     * @return 出错返回"" ,唯一MD5数字签名
     */
    public String uploadFile2COS(File file, String dir) {
        //上传文件
        if (StrKit.isBlank(dir)) {
            dir = Dir.OTHER;
        }
        try {
            final String key = UUID.randomUUID().toString() + getFileExtension(file);
            // 创建上传Object的Metadata
            ObjectMetadata meta = new ObjectMetadata();
            // 设置上传文件长度
            final byte[] buff = getBytes(file);
            meta.setContentLength(buff.length);
            // 设置上传MD5校验
            String md5 = BinaryUtil.toBase64String(BinaryUtil.calculateMd5(buff));
            meta.setContentMD5(md5);
            // 设置上传内容类型
            meta.setContentType(Mimetypes.getInstance().getMimetype(file));
            FileInputStream fis = new FileInputStream(file);
            PutObjectResult putResult = mCOSClient.putObject(BUCKET_NAME, dir + "/" + key, fis,meta);
            LOGGER.info("file:" + file.getAbsolutePath() + ",key:" + key + ",md5:" + putResult);
            return getUrl(dir, key);
        } catch (CosServiceException oe) {
            LOGGER.info(oe.getMessage());
        } catch (CosClientException ce) {
            LOGGER.info(ce.getMessage());
        }catch (FileNotFoundException ex){
            LOGGER.info(ex.getMessage());
        }
        throw new RuntimeException("上传文件失败");
    }

    public String uploadFile2COS(String file, String dir) {
        //上传文件
        return uploadFile2COS(new File(file), dir);
    }

    /**
     * 获得url链接
     *
     * @param key
     * @return
     */
    public String getUrl(String dir, String key) {
        // 设置URL过期时间为100年  3600l* 1000*24*365*100
        Date expiration = new Date(new Date().getTime() + 3600l * 1000 * 24 * 365 * 100);
        // 生成URL
        try {
            URL url = mCOSClient.generatePresignedUrl(BUCKET_NAME, dir + "/" + key, expiration);
            LOGGER.info("url:" + url.toString());
            if (url != null) {
                return url.toString();
            }
        } catch (CosServiceException ce) {
            LOGGER.info(ce.getMessage());
        }
        throw new RuntimeException("获取文件链接失败");
    }
}
