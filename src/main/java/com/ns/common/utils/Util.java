package com.ns.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.ns.common.exception.CustException;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016-02-24.
 */
public class Util {
    private static final Log LOGGER = Log.getLog(Util.class.getName());

    /**
     * 取Request中的数据对象
     *
     * @param valueType
     * @return
     * @throws Exception
     */
    public static <T> T getRequestObject2(HttpServletRequest request, Class<T> valueType) {
        String json = HttpKit.readData(request);
        if (StrKit.notBlank(json)) {
            try {
                LOGGER.info("PostParam: " + json);
                return JSONObject.parseObject(json, valueType);
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("输入参数解析失败:" + json);
                LOGGER.error("Error:" + e.getMessage());
                throw new CustException("输入参数解析失败", e);
            }
        } else {
            LOGGER.info("PostParam: " + "{}");
            return JSONObject.parseObject("{}", valueType);
        }
    }

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
            String json = "";
            try (ServletInputStream inputStream = request.getInputStream();) {
                byte[] jsonBytes = getBytes(len, inputStream);
                inputStream.close();
                json = new String(jsonBytes, Charset.forName("UTF-8"));
                LOGGER.info("PostParam: " + json);
                return JSONObject.parseObject(jsonBytes, valueType);
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("Error:" + e.toString());
                throw new CustException("输入参数解析失败", e);
            }
        } else {
            LOGGER.info("PostParam: " + "{}");
            return JSONObject.parseObject("{}", valueType);
        }
    }

    /**
     * 手机号正则验证
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();

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

    public static Date getDateByDay(Date start, int end) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        calendar.add(5, end);
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        Date date = calendar.getTime();
        return date;
    }

    public static String getRequestBytes(HttpServletRequest request) {
        int len = request.getContentLength();
        if (len > 0) {
            String json = "";
            try (ServletInputStream inputStream = request.getInputStream();) {
                byte[] jsonBytes = getBytes(len, inputStream);
                inputStream.close();
                json = new String(jsonBytes, Charset.forName("UTF-8"));
                LOGGER.info("PostContent: " + json);
                return json;
            } catch (Exception e) {
                LOGGER.error("Error:" + e.toString());
                throw new CustException("读取数据失败", e);
            }
        } else {
            LOGGER.info("PostContent: " + "");
            return "";
        }
    }

    public static String nowDateToString() {
        Date time = new Date();
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        String ctime = formatter.format(time);

        return ctime;
    }

    public static String nowDateTimeToString() {
        Date time = new Date();
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String ctime = formatter.format(time);

        return ctime;
    }

    public static String nowAfterSecDateTimeToString(int sec) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, +sec);
        Date time = cal.getTime();
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String ctime = formatter.format(time);

        return ctime;
    }

    /**
     * 比较时间
     *
     * @param DATE1
     * @param DATE2
     * @return
     */
    public static int compare_date(String DATE1, String DATE2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.getTime() > dt2.getTime()) {
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    public static String getDateAsNumberStr(String date) {
        return date.replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "");
    }

    public static String accuracy(int num1, int num2) {

        // 创建一个数值格式化对象

        NumberFormat numberFormat = NumberFormat.getInstance();

        // 设置精确到小数点后2位

        numberFormat.setMaximumFractionDigits(2);

        String result = numberFormat.format((float) num1 / (float) num2 * 100);

        return result;
    }

    /**
     * 将一个 JavaBean 对象转化为一个 Map
     *
     * @param bean 要转化的JavaBean 对象
     * @return 转化出来的 Map 对象
     * @throws IntrospectionException    如果分析类属性失败
     * @throws IllegalAccessException    如果实例化 JavaBean 失败
     * @throws InvocationTargetException 如果调用属性的 setter 方法失败
     */
    @SuppressWarnings("rawtypes")
    public static Map toMap(Object bean) {
        Class<? extends Object> clazz = bean.getClass();
        Map<Object, Object> returnMap = new HashMap<Object, Object>();
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(clazz);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (int i = 0; i < propertyDescriptors.length; i++) {
                PropertyDescriptor descriptor = propertyDescriptors[i];
                String propertyName = descriptor.getName();
                if (!propertyName.equals("class")) {
                    Method readMethod = descriptor.getReadMethod();
                    Object result = null;
                    result = readMethod.invoke(bean, new Object[0]);
                    if (null != propertyName) {
                        propertyName = propertyName.toString();
                    }
                    if (null != result) {
                        result = result.toString();
                    }
                    returnMap.put(propertyName, result);
                }
            }
        } catch (IntrospectionException e) {
            System.out.println("分析类属性失败");
        } catch (IllegalAccessException e) {
            System.out.println("实例化 JavaBean 失败");
        } catch (IllegalArgumentException e) {
            System.out.println("映射错误");
        } catch (InvocationTargetException e) {
            System.out.println("调用属性的 setter 方法失败");
        }
        return returnMap;
    }

    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    public static String getGUID() {
        return getUUID().toUpperCase().replaceAll("-", "");
    }

    /**
     * Convert byte[] to hex string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
     *
     * @param src byte[] data
     * @return hex string
     */
    public static String bytesToHexString(byte[] src) {
        return bytesToHexString(src, "");
    }

    /**
     * Convert byte[] to hex string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
     *
     * @param src byte[] data
     * @return hex string
     */
    public static String bytesToHexString(byte[] src, String splitChar) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv + splitChar);
        }
        return stringBuilder.toString().toUpperCase();
    }

    /**
     * Convert hex string to byte[]
     *
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * Convert char to byte
     *
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static boolean isNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            System.out.println(str.charAt(i));
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static BigDecimal BigDecimalMultiply(BigDecimal a, BigDecimal b) {
        if (a == null) {
            a = BigDecimal.ZERO;
        }
        if (b == null) {
            b = BigDecimal.ZERO;
        }
        return a.multiply(b);
    }

    public static BigDecimal BigDecimalDivide(BigDecimal a, BigDecimal b) {
        if (a == null) {
            a = BigDecimal.ZERO;
        }
        if (b == null) {
            b = BigDecimal.ZERO;
        }
        return a.divide(b);
    }

    public static BigDecimal BigDecimalSubtract(BigDecimal a, BigDecimal b) {
        if (a == null) {
            a = BigDecimal.ZERO;
        }
        if (b == null) {
            b = BigDecimal.ZERO;
        }
        return a.subtract(b);
    }

    public static BigDecimal BigDecimalAdd(BigDecimal a, BigDecimal b) {
        if (a == null) {
            a = BigDecimal.ZERO;
        }
        if (b == null) {
            b = BigDecimal.ZERO;
        }
        return a.add(b);
    }

    /**
     * md5加密
     *
     * @param value String
     * @return String
     */
    public static String md5(String value) {
        String md5Str = "";
        try {
            //String pwd = "123";
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            //加密后的字符串
            md5Str = Util.bytesToHexString(md5.digest(value.getBytes("utf-8")));
            System.out.println(md5Str);
        } catch (NoSuchAlgorithmException e) {
            throw new CustException("没有MD5加密算法", e);
        } catch (UnsupportedEncodingException e) {
            throw new CustException("不支特utf-8编码", e);
        }
        return md5Str;
    }

    public static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static String getIp2(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StrKit.notBlank(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (StrKit.notBlank(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }

    /**
     * 生成md5文件path
     *
     * @param dirPath  String
     * @param fileName String
     * @param fileExt  String
     * @return String
     */
    public static String genMd5FilePath(String dirPath, String fileName, String fileExt) {
        String md5Str = md5(fileName);
        String fullFileName = md5Str + "." + fileExt;
        byte[] md5StrBytes = md5Str.getBytes();
        byte[] a = Arrays.copyOfRange(md5StrBytes, 0, 2);
        byte[] b = Arrays.copyOfRange(md5StrBytes, 2, 4);
        byte[] c = Arrays.copyOfRange(md5StrBytes, 4, 6);
        String path_a = new String(a);
        String path_b = new String(b);
        String path_c = new String(c);

        Path path = Paths.get(dirPath, path_a, path_b, path_c, fullFileName);
        return path.toString();
    }

    /**
     * 产生文件对应的文件夹
     *
     * @param filePath String
     * @return String
     */
    public static boolean mkDir(String filePath) {
        boolean result = false;
        Path path = Paths.get(filePath);
        Path parendPath = path.getParent();
        File dir = new File(parendPath.toString());
        if (dir == null || !dir.exists()) {
            result = dir.mkdirs();
        }
        return result;
    }

    /**
     * 构造where in 的语句条件
     *
     * @param org
     * @param regex
     * @return
     */
    public static String getInSql(String org, String regex) {
        String[] orgArray = org.replace(regex, ",").split(",");
        StringBuilder sb = new StringBuilder();
        for (String temp : orgArray) {
            sb.append("'" + temp + "',");
        }

        return sb.toString().substring(0, sb.toString().length() - 1);
    }

}
