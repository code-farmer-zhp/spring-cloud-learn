package com.feiniu.score.common;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Properties;

public class FtpUtil {


    private static final Logger LOG = LoggerFactory.getLogger(FtpUtil.class);

    public static void main(String[] args) {
        Channel channel = ftpConnect("ftp.idc1.fn", 2222, "code72_fcm_receive", "vhUmCYXW","sftp");
        uploadFile(channel, null);

    }

    public static Channel ftpConnect(String host, int port, String username, final String password,String ftpAgreement) {

        ChannelSftp sftp = null;
        Channel channel = null;
        Session sshSession = null;
        try {
            JSch jsch = new JSch();
            jsch.getSession(username, host, port);
            sshSession = jsch.getSession(username, host, port);
            sshSession.setPassword(password);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            LOG.info("step1");
            sshSession.connect();
            LOG.debug("Session connected!");
            channel = sshSession.openChannel(ftpAgreement);
            channel.connect();
            LOG.debug("Channel connected!");

        } catch (Exception e) {
            e.printStackTrace();
        }
       return channel;
    }

    //ftp上传文件
    public static void uploadFile(Channel channel, String fileName) {
        ChannelSftp sftp = null;
        if (channel != null && fileName != null) {
            sftp = (ChannelSftp) channel;

                try {
                    sftp.put("D:\\testdata\\"+fileName, "/FN020/" + fileName);
                } catch (SftpException e) {
                    LOG.error("文件上传失败:" + fileName);
                }


            sftp.quit();
            closeChannel(sftp);
            closeChannel(channel);
        }
    }

    //ftp上传文件
    public static void uploadFile(Channel channel, ArrayList<String> fileNameList,String dir,String year,String month,String day) {
        ChannelSftp sftp = null;
        if (channel != null && fileNameList != null) {
            sftp = (ChannelSftp) channel;
            for (int i = 0; i < fileNameList.size(); i++) {
                try {
                    sftp.put("/home/webdata/htdocs/attachment/"+dir+"/"+year+"/"+month+"/"+day+"/"+fileNameList.get(i), "/FN045/" +dir+"/" + fileNameList.get(i));
                } catch (SftpException e) {
                    LOG.error("文件上传失败:" + "/home/webdata/htdocs/attachment/"+dir+"/"+year+"/"+month+"/"+day+"/"+fileNameList.get(i)+"  上传至  "+"/FN045/" +dir+"/" + fileNameList.get(i));
                }
            }

            sftp.quit();
            closeChannel(sftp);
            closeChannel(channel);
        }
    }

    public static void closeChannel(Channel channel) {
        if (channel != null) {
            if (channel.isConnected()) {
                channel.disconnect();
            }
        }
    }

    public static void closeSession(Session session) {
        if (session != null) {
            if (session.isConnected()) {
                session.disconnect();
            }
        }
    }

    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);

        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
                flag = true;
            }
        }
        return flag;
    }


    //通过sftp创建目录
    public static void mkDir( Channel channel,String dirName)
    {
        ChannelSftp sftp = null;
        sftp = (ChannelSftp) channel;
        try
        {
            String[] dirs = dirName.split("/");
            String now = sftp.pwd();
            for (int i = 0; i < dirs.length; i++)
            {
                boolean dirExists = openDir(channel,dirs[i]);
                if (!dirExists)
                {
                    sftp.mkdir(dirs[i]);
                    sftp.cd(dirs[i]);

                }

            }
            sftp.cd(now);
        }
        catch (SftpException e)
        {
            LOG.error("mkDir Exception : " + e);
        }
    }

    public static boolean openDir(Channel channel,String directory)
    {
        ChannelSftp sftp = null;
        sftp = (ChannelSftp) channel;
        try
        {
            sftp.cd(directory);
            return true;
        }
        catch (SftpException e)
        {
            LOG.error("openDir Exception : " + e);
            return false;
        }
    }



}
