package com.rj.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class Md5Util {
    public static final String SIGN_KEY = "sign_key";//签名key

    public static final String PARAMETER_KEY = "parameter_key";//拼装的参数key

    /**
     * 新的md5签名，首尾放secret。
     *
     * @param key 商家key
     *            tradeNo  流水号
     *            secret
     * @param key 分配给您的APP_SECRET
     */
    public static String md5Signature(long id, String tradeNo,
                                      String key) {

        StringBuffer sb = new StringBuffer();
        sb.append(key);
        sb.append(id);
        sb.append(tradeNo);
        sb.append(key);

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String result = null;
        try {
            result = byte2hex(md.digest(sb.toString().getBytes("utf-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 二行制转字符串
     */
    private static String byte2hex(byte[] b) {
        StringBuffer hs = new StringBuffer();
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1)
                hs.append("0").append(stmp);
            else
                hs.append(stmp);
        }
        return hs.toString().toUpperCase();
    }

    /***
     * 对请求的参数排序，生成定长的签名
     * @param  paramsMap  排序后的字符串
     * @param secret 密钥
     * */
    public static String md5Signature(Map<String, String> paramsMap, String secret) {
        Map<String, String> resultMap = new HashMap<String, String>();//用于记录返回的签名和将参数按照签名拼装的url
        String result = "";
        StringBuilder sb = new StringBuilder();
        Map<String, String> treeMap = new TreeMap<String, String>();
        treeMap.putAll(paramsMap);
        sb.append(secret);
        Iterator<String> iterator = treeMap.keySet().iterator();
        while (iterator.hasNext()) {
            String name = (String) iterator.next();
            sb.append(name).append(treeMap.get(name));
        }
        sb.append(secret);
        resultMap.put(PARAMETER_KEY, sb.toString());
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");             /**MD5加密，输出一个定长信息摘要*/
            result = byte2hex(md.digest(sb.toString().getBytes("utf-8")));

        } catch (Exception e) {
            throw new RuntimeException("sign error !");
        }
        resultMap.put(SIGN_KEY, result);
        return result;
    }

    /**
     * Calculates the MD5 digest and returns the value as a 16 element
     * <code>byte[]</code>.
     *
     * @param data Data to digest
     * @return MD5 digest
     */
    public static byte[] md5(String data) {
        return md5(data.getBytes());
    }

    /**
     * Calculates the MD5 digest and returns the value as a 16 element
     * <code>byte[]</code>.
     *
     * @param data Data to digest
     * @return MD5 digest
     */
    public static byte[] md5(byte[] data) {
        return getDigest().digest(data);
    }

    /**
     * Returns a MessageDigest for the given <code>algorithm</code>.
     *
     * @param
     * @return An MD5 digest instance.
     * @throws RuntimeException when a {@link NoSuchAlgorithmException} is
     *                          caught
     */

    static MessageDigest getDigest() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
