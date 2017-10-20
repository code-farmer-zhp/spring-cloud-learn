package com.feiniu.member.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/*
*a efficient tool to read file content to String.
* read a file to a stringbuilder then convert to String ,make sure file's content is under the capability of String size.
*@author: Max
*@mail:1069905071@qq.com 
*@time:2017/2/23 16:51 
*/
public class CustomFileUtil {
    /**
     *
     * @param file
     * @param readEncoding  UTF-8
     * @return
     * @throws IOException
     */
    public static String readFile(String file, String readEncoding) throws IOException {
        BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream(file), readEncoding));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");

        try {
            while((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }

            return stringBuilder.toString();
        } finally {
            reader.close();
        }
    }
}
