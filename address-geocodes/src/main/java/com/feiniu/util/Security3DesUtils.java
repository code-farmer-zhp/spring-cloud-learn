package com.feiniu.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Security3DesUtils {

    private static Cipher decryptCipher = null;

    // 密钥
    private static final String keyString = "123456";

    private static Log log = LogFactory.getLog(Security3DesUtils.class);

    static {
        try {
            // 获得密钥
            SecretKey secretKey = new SecretKeySpec(keyString.getBytes(), "DESede");

            /* 获得一个私鈅加密类Cipher，DESede是算法，ECB是加密模式，PKCS5Padding是填充方式 */
            Cipher encryptCipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            // 设置工作模式为加密模式，给出密钥
            encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey);

            /* 获得一个私鈅加密类Cipher，DESede是算法，ECB是加密模式，PKCS5Padding是填充方式 */
            decryptCipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            // 设置工作模式为解密模式，给出密钥
            decryptCipher.init(Cipher.DECRYPT_MODE, secretKey);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 解密
     */
    public static String decrypt(String message) {
        String result = null;
        if (StringUtils.isNotBlank(message)) {
            try {
                // 进行BASE64编码
                byte[] messageBytes = Base64.decodeBase64(message);
                byte[] resultBytes;
                synchronized (Security3DesUtils.class) {
                    // 正式执行解密操作
                    resultBytes = decryptCipher.doFinal(messageBytes);
                }
                result = new String(resultBytes, "UTF-8");
            } catch (Exception e) {
                log.error("解密" + message + "失败, " + e.getMessage());
            }
        }
        return result;
    }
}
