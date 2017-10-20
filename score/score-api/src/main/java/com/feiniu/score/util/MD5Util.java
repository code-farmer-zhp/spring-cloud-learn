package com.feiniu.score.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
	// 全局数组
	private final static String[] strDigits = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	public MD5Util() {
	}

	// 返回形式为数字跟字符串
	private static String byteToArrayString(byte bByte) {
		int iRet = bByte;
		// System.out.println("iRet="+iRet);
		if (iRet < 0) {
			iRet += 256;
		}
		int iD1 = iRet / 16;
		int iD2 = iRet % 16;
		return strDigits[iD1] + strDigits[iD2];
	}

	// 返回形式只为数字
	private static String byteToNum(byte bByte) {
		int iRet = bByte;
		System.out.println("iRet1=" + iRet);
		if (iRet < 0) {
			iRet += 256;
		}
		return String.valueOf(iRet);
	}

	// 转换字节数组为16进制字串
	private static String byteToString(byte[] bByte) {
		StringBuilder sBuffer = new StringBuilder();
		for (int i = 0; i < bByte.length; i++) {
			sBuffer.append(byteToArrayString(bByte[i]));
		}
		return sBuffer.toString();
	}

	public static String getMD5Code(String strObj) {
		String resultString = null;
		try {
			resultString = strObj;
			MessageDigest md = MessageDigest.getInstance("MD5");
			// md.digest() 该函数返回值为存放哈希值结果的byte数组
			resultString = byteToString(md.digest(strObj.getBytes()));
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		}
		return resultString;
	}

	public static void main(String[] args) {
		//MD5Util getMD5 = new MD5Util();
		//System.out.println(getMD5.getMD5Code("aaas").substring(8, 8 + 16));
		//phone=18939560636&openId=openId&campaignId=20150109C0008&timestamp=1440639910817&ref=mk_FM&sign=ec1fb3d4f287eb613a73b8dfc51bde00
		StringBuilder sb = new StringBuilder();
		sb.append("openId=").append("openId").append("&")
				.append("campaignId=").append("20150109C0008").append("&")
				.append("timestamp=").append("1440639910817").append("&")
				.append("phone=").append("18939560636").append("&")
				.append("ref=").append("mk_FM").append("&")
				.append("key=").append("da0eec73a93194d0e5701042defe5f99");
		String signKey = MD5Util.getMD5Code(sb.toString());
		System.out.println(signKey);
	}
}
