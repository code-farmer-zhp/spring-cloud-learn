package com.feiniu.member.util;

import org.apache.log4j.Logger;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * Java版3DES加密解密，适用于PHP版3DES加密解密(PHP语言开发的MCRYPT_3DES算法、MCRYPT_MODE_ECB模式、PKCS7填充方式)
 * 
 */
@SuppressWarnings("restriction")
public class Mcrypt3Des {
	// key对象
	private static SecretKey secretKey = null;
	// 私鈅加密对象Cipher
	private static Cipher cipher = null;
	// 密钥
	private static String keyString = "111111111122222222223333";
	
	private static Logger log = Logger.getRootLogger();
	
	static {
		try {
			// 获得密钥
			secretKey = new SecretKeySpec(keyString.getBytes(), "DESede");
			/* 获得一个私鈅加密类Cipher，DESede是算法，ECB是加密模式，PKCS5Padding是填充方式 */
			cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 加密
	 * 
	 * @param message
	 * @return
	 */
	public static String desEncrypt(String message) {
		// DES加密字符串
		String result = "";
		// 去掉换行符后的加密字符串
		String newResult = "";
		try {
			// 设置工作模式为加密模式，给出密钥
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			// 正式执行加密操作
			byte[] resultBytes = cipher.doFinal(message.getBytes("UTF-8")); 
			BASE64Encoder enc = new BASE64Encoder();
			// 进行BASE64编码
			result = enc.encode(resultBytes);
			// 去掉加密串中的换行符
			newResult = filter(result); 
		} catch (Exception e) {
			//log.error(e.getMessage(), e);
		}
		return newResult;
	}

	/**
	 * 解密
	 * 
	 * @param message
	 * @return
	 * @throws Exception
	 */
	public static String desDecrypt(String message) {
		String result = "";
		try {
			BASE64Decoder dec = new BASE64Decoder();
			// 进行BASE64编码
			byte[] messageBytes = dec.decodeBuffer(message); 
			// 设置工作模式为解密模式，给出密钥
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			// 正式执行解密操作
			byte[] resultBytes = cipher.doFinal(messageBytes);
			result = new String(resultBytes, "UTF-8");
		} catch (Exception e) {
			// 什么都不干， 抛出异常说明没有加密
			//log.error(e.getMessage(), e);
		}
		return result;
	}

	/**
	 * 去掉加密字符串换行符
	 * 
	 * @param str
	 * @return
	 */
	public static String filter(String str) {
		String output = "";
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			int asc = str.charAt(i);
			if (asc != 10 && asc != 13) {
				sb.append(str.subSequence(i, i + 1));
			}
		}
		output = new String(sb);
		return output;
	}

	/**
	 * 加密解密测试
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			// String strText = "Hello world!";
			// String strText = "中文ABc123";
			// String strText = "abcd";
			String strText = "13122759151";
			String deseResult = desEncrypt(strText);// 加密
			System.out.println("加密结果：" + deseResult);

			String desdResult = desDecrypt("uciNnmMgaJo=");// 解密
			System.out.println("解密结果：" + desdResult);
			
			Map<String, String> map = new HashMap<String, String>();
					
			for(int i = 0; i < 3000; i++){
				map.put(i+"", desEncrypt(i+""));
			}
			
			System.out.println("加密完毕");
			long totalStart = System.currentTimeMillis();
			for(int i = 0; i < 3000; i++){
				desDecrypt(map.get("" + i));
			}
			long totalEnd = System.currentTimeMillis();
			System.out.println("解密总耗时：" + ((double)(totalEnd - totalStart)/1000) + "秒");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
