package com.xiaozhao.servicenetwork.framework.util;


import java.security.MessageDigest;
/**
 * 
 * [概 要] <MD5字符串加密><br/>
 * [环 境] J2EE 1.7
 * 创建时间：2017年6月20日 上午9:41:52 <br>
 * @author he
 * @version 1.0
 */
public class MD5Encryption {
	public static String MD5(String str){
		char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};       
        try {
            byte[] btInput = str.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char md5Str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                md5Str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                md5Str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(md5Str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
	}
	public static void main(String[] args) {
		System.out.println(MD5Encryption.MD5("admin123"));
	}
}
