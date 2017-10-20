package com.feiniu.score.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Created by peng.zhou on 2015/7/1.
 */
public class ExceptionMsgUtil {

    public static String getMsg(Exception e) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(byteArrayOutputStream);
        e.printStackTrace(stream);
        String errorMsg = byteArrayOutputStream.toString();
        return errorMsg;
    }
}
