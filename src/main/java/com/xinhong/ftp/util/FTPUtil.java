package com.xinhong.ftp.util;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;

/**
 * Created by wingsby on 2017/8/11.
 */
public class FTPUtil {


    /** */
    /**
     * 连接到FTP服务器
     *
     * @param hostname 主机名
     * @param port     端口
     * @param username 用户名
     * @param password 密码
     * @return 是否连接成功
     * @throws IOException
     */
    public static FTPClient getConnectedFTPClient(String hostname, int port, String username, String password) {
        FTPClient client = new FTPClient();
        try {
            client.setConnectTimeout(3 * 60 * 1000);
            client.connect(hostname, port);
            //     ftpClient.setControlEncoding("GBK");
            if (FTPReply.isPositiveCompletion(client.getReplyCode())) {
                if (client.login(username, password)) {
                    return client;
                }
            }
//            client.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 判断远程目录是否存在
     *
     * @param remoteDir
     * @return
     */
    public static boolean isExistFolder(String remoteDir, FTPClient client) {
        try {
            boolean bl = client.changeWorkingDirectory(new String(remoteDir.getBytes("GBK"), "iso-8859-1"));
            return bl;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断远程文件是否存在
     *
     * @param remoteFile
     * @return
     */
    public static boolean isExistFile(String remoteFile, FTPClient client) {
        if (remoteFile == null || remoteFile.length() < 1) {
            return false;
        }
        try {
            client.enterLocalPassiveMode(); //设置被动模式
            client.setDefaultPort(168);
            client.setFileType(FTP.BINARY_FILE_TYPE);
            FTPFile[] files = client.listFiles(remoteFile);
            if (files.length != 1) {
                System.out.println("远程文件不存在:remoteFile:" + remoteFile);
                return false;
            } else {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


}
