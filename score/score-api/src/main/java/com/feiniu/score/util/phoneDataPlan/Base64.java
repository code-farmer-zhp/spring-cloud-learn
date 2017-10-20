package com.feiniu.score.util.phoneDataPlan;

import java.io.UnsupportedEncodingException;


/**
 * <��׼Base64����>
 * <功能详细描述>
 * 
 * @author  nyl
 * @version  [版本号, Mar 8, 2011]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class Base64
{

    //private static final Log LOGGER = LogFactory.getLog(Base64.class);
    
    private static char base64Code[] = { 'A', 'B', 'C', 'D', 'E', 'F', 'G',
            'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
            'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', '+', '/', };

    private static byte base64Decode[] = { -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, // ע��}��63��Ϊ����SMP��
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, 63, -1, 63, // ��/���͡�-���������63��
            52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, 0, -1, -1, -1,
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, // ע��}��0��
            15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, // ��A���͡�=���������0��
            -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41,
            42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1, };
    
    /**
     * <默认构造函数>
     */
    private Base64()
    {
    }

    public static String encode(byte[] b)
    {
        int code = 0;

        StringBuffer sb = new StringBuffer(((b.length - 1) / 3) << 2 + 4);

        for (int i = 0; i < b.length; i++)
        {
            code |= (b[i] << (16 - i % 3 * 8)) & (0xff << (16 - i % 3 * 8));
            if (i % 3 == 2 || i == b.length - 1)
            {
                sb.append(base64Code[(code & 0xfc0000) >>> 18]);
                sb.append(base64Code[(code & 0x3f000) >>> 12]);
                sb.append(base64Code[(code & 0xfc0) >>> 6]);
                sb.append(base64Code[code & 0x3f]);
                code = 0;
            }
        }

        if (b.length % 3 > 0)
        {
            sb.setCharAt(sb.length() - 1, '=');
        }
        if (b.length % 3 == 1)
        {
            sb.setCharAt(sb.length() - 2, '=');
        }
        return sb.toString();
    }

    public static byte[] decode(String code)
    {
        if (code == null)
        {
            return new byte[0];
        }
        int len = code.length();
        if (len % 4 != 0)
        {
            throw new IllegalArgumentException(
                    "Base64 string length must be 4*n");
        }
        if (code.length() == 0)
        {
            return new byte[0];
        }

        int pad = 0;
        if (code.charAt(len - 1) == '=')
        {
            pad++;
        }
        if (code.charAt(len - 2) == '=')
        {
            pad++;
        }

        int retLen = len / 4 * 3 - pad;

        byte[] ret = new byte[retLen];

        char ch1, ch2, ch3, ch4;
        int i;
        for (i = 0; i < len; i += 4)
        {
            int j = i / 4 * 3;
            ch1 = code.charAt(i);
            ch2 = code.charAt(i + 1);
            ch3 = code.charAt(i + 2);
            ch4 = code.charAt(i + 3);
            int tmp = (base64Decode[ch1] << 18) | (base64Decode[ch2] << 12)
                    | (base64Decode[ch3] << 6) | (base64Decode[ch4]);
            ret[j] = (byte) ((tmp & 0xff0000) >> 16);
            if (i < len - 4)
            {
                ret[j + 1] = (byte) ((tmp & 0x00ff00) >> 8);
                ret[j + 2] = (byte) ((tmp & 0x0000ff));
            } else
            {
                if (j + 1 < retLen)
                {
                    ret[j + 1] = (byte) ((tmp & 0x00ff00) >> 8);
                }
                if (j + 2 < retLen)
                {
                    ret[j + 2] = (byte) ((tmp & 0x0000ff));
                }
            }
        }
        return ret;
    }
    
    public static String encode(String str)
    {
    	if (str == null || "".equals(str))
    		return "";
        try
        {
            byte[] bs = str.getBytes("UTF-8");
            return encode(bs);
        }
        catch (UnsupportedEncodingException e)
        {
            //LOGGER.error("UnsupportedEncodingException", e);
        }
        return null;
    }
    
    public static String decode2Str(String str)
    {
        byte[] bs = decode(str);
        try
        {
            return new String(bs, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            //LOGGER.error("UnsupportedEncodingException", e);
            return null;
        }
    }
    
    public static void main(String[] args) throws Exception
    {
        //String a = "=?UTF-8?B?6ZSA5ZSu6YOoY29kZTE=?=";
//        BCodec code = new BCodec();
       
        
//        String a1 = "销售部name1";
//        String c1 = code.encode(a1);
//        
//        System.out.println(c1);
//        
//        System.out.println(code.decode(c1));
        
        
//        byte[] a2 = a1.getBytes("UTF-8");
//        String b1 = encode(a2);
//        System.out.println(b1);
//        byte[] b = decode(b1);
//        System.out.println(new String(b));
    }
}