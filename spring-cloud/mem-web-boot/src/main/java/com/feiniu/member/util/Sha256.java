package com.feiniu.member.util;

import org.apache.log4j.Logger;

import java.nio.charset.Charset;
import java.security.MessageDigest;

public class Sha256 {
	private static Logger log = Logger.getRootLogger();
	public static String encrypt(String message) {
		String result = "";
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(message.getBytes(Charset.forName("UTF-8")));
			byte[] hashed = md.digest();
			result= Sha256.getHex(hashed).toLowerCase();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return result;
	}
	
	
	static final String HEXES = "0123456789ABCDEF";
	public static String getHex(byte [] raw ) {
	    if ( raw == null ) {
	      return null;
	    }
	    final StringBuilder hex = new StringBuilder( 2 * raw.length );
	    for ( final byte b : raw ) {
	      hex.append(HEXES.charAt((b & 0xF0) >> 4))
	         .append(HEXES.charAt((b & 0x0F)));
	    }
	    return hex.toString();
	}
	
	
	public static void main(String[] args) {
		try {
			String s = "带汉字~!@#$%^&*()_+[]{};,./<>?;HelloWorld-test";
			System.out.println(encrypt(s));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}