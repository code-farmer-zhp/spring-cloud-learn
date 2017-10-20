package com.feiniu.score.util;

import org.springframework.util.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.TreeMap;

public class SignUtil {
	
	public static char[] hexChar = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * 生成签名
	* @param params
	 * @param token 
	 * @return 签名
	 */
	public static String generate(TreeMap<String, String> params, String token) {
		String sign = md5(assemble(params));
		StringBuffer sb = new StringBuffer();
		if (sign != null && sign.length() > 0) {
			sb.append(sign.toUpperCase());
			sb.append(token);
			sign = md5(sb.toString());
		} else {
			sign = "";
		}
		return sign.toUpperCase();
	}

	/**
	 * 组装生成签名的字符
	 * 
	 * @param params
	 * @return
	 */
	private static String assemble(TreeMap<String, String> params) {
		String sign = null;
		if (CollectionUtils.isEmpty(params)) {
			return sign;
		}
		StringBuffer signBuffer = new StringBuffer();
		Set<String> keySet = params.keySet();
		for (String key : keySet) {
			String value = params.get(key);
			if (value == null || value.length() == 0) {
				continue;
			}
			signBuffer.append(key);
			signBuffer.append(value);
		}

		if (signBuffer.length() > 0) {
			sign = signBuffer.toString();
		}
//		sign = "keyFN0001timestamp1414491870";
//		System.out.println("assemble====="+sign);
		return sign;
	}
	
	public static String md5(
			String inputText) {
		String encryptText = null;
		if (inputText != null && inputText.length() > 0) {
			try {
				MessageDigest m = MessageDigest.getInstance("md5");
				m.update(inputText.getBytes("UTF8"));
				encryptText = toHexString(m.digest());
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return encryptText;
	}
	
	public static String toHexString(
			byte[] b) {
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(hexChar[(b[i] & 0xf0) >>> 4]);
			sb.append(hexChar[b[i] & 0x0f]);
		}
		return sb.toString();
	}
	
}
