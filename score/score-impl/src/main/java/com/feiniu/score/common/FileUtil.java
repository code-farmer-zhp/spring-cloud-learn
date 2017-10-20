package com.feiniu.score.common;

/*
*@author: Max
*@mail:1069905071@qq.com 
*@time:2017/2/9 17:53 
*/

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {
    private static final Logger LOG = LoggerFactory.getLogger(FileUtil.class);

    public static boolean writeDateToFileInBuf(String filePath, String data, boolean pend) {
        boolean ifSuccess=false;
        File file = new File(filePath);
        //if file doesnt exists, then create it
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                LOG.error("Exception occurs in createNewFile when file not exists ", e);
            }
        }
        if (file.exists()) {
            try (FileWriter fileWritter = new FileWriter(filePath, pend); BufferedWriter bufferWritter = new BufferedWriter(fileWritter)) {
                bufferWritter.write(data);
                bufferWritter.close();
                LOG.info("successfully write data to file :" + filePath);
                ifSuccess= true;
            } catch (IOException e) {
                LOG.error("Exception occurs when try to write data to file in buf.", e);
            }
        }
        return ifSuccess;
    }
}
