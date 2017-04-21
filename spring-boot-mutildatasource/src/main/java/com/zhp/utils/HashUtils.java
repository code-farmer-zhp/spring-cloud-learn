
package com.zhp.utils;

import java.util.Date;


public final class HashUtils {
	
	private HashUtils() {
	}

	/**
	 * 计算String类型的hashcode
	 * */
	private static int getHashCode(String value) {
		int hash1 = 5381;
		int hash2 = hash1;
		int len = value.length();
		for (int i = 0; i < len; i++) {
			int c = value.charAt(i);
			hash1 = ((hash1 << 5) + hash1) ^ c;
			if (++i >= len) {
				break;
			}
			c = value.charAt(i);
			hash2 = ((hash2 << 5) + hash2) ^ c;
		}
		return hash1 + (hash2 * 1566083941);
	}

	private static int getHashCode(Long value) {
		value = (~value) + (value << 18); // key = (key << 18) - key - 1;
		value = value ^ (value >> 31);
		value = value * 21; // key = (key + (key << 2)) + (key << 4);
		value = value ^ (value >> 11);
		value = value + (value << 6);
		value = value ^ (value >> 22);
		return value.intValue();
	}

	private static int getHashCode(Date value) {

		return getHashCode(value.getTime());
	}

	private static int GetHashCode(Double value) {
		return getHashCode(Double.doubleToLongBits(value));
	}

	/**
	 * Bob Jenkins' 32 bit integer hash function 这六个数是随机数，
	 * 通过设置合理的6个数，你可以找到对应的perfect hash.
	 * */
	private static int getHashCode(Integer value) {
		value = (value + 0x7ed55d16) + (value << 12);
		value = (value ^ 0xc761c23c) ^ (value >> 19);
		value = (value + 0x165667b1) + (value << 5);
		value = (value + 0xd3a2646c) ^ (value << 9);
		value = (value + 0xfd7046c5) + (value << 3); // <<和 +的组合是可逆的
		value = (value ^ 0xb55a4f09) ^ (value >> 16);
		return value;
	}

	private static int getHashCode(Float value) {
		return getHashCode(Float.floatToIntBits(value));
	}

	private static int getHashCode(Boolean value) {
		return value ? 1231 : 1237;
	}

	private static int getHashCode(Byte value) {
		return getHashCode((int) value);
	}

	private static int getHashCode(Short value) {
		return getHashCode((int) value);
	}

	public static <T> int getHashCode(T value) {
		int result = 0;
		if (value instanceof Integer) {
			result = getHashCode((Integer) value);
		} else if (value instanceof Short) {
			result = getHashCode((Short) value);
		} else if (value instanceof Long) {
			result = getHashCode((Long) value);
		} else if (value instanceof Double) {
			result = getHashCode(value);
		} else if (value instanceof Float) {
			result = getHashCode((Float) value);
		} else if (value instanceof Byte) {
			result = getHashCode((Byte) value);
		} else if (value instanceof Date) {
			result = getHashCode((Date) value);
		} else if (value instanceof Boolean) {
			result = getHashCode((Boolean) value);
		} else if (value instanceof String) {
			result = getHashCode((String) value);
		} else {
			result = value.hashCode();
		}
		return result;
	}
}