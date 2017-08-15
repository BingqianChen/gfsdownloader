package com.xinhong.ftp.util;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by shijunna on 2016/8/8.
 */
public class SFtpdownUtil {
    private final Logger logger = Logger.getLogger(SFtpdownUtil.class);
    private ChannelSftp sftp = null;
    private String ftpURL,username,pwd,remotefile, localfile;
    private int ftpport;
    public SFtpdownUtil(String remotePath, String localPath ){
        remotefile = remotePath;
        localfile = localPath;
        logger.info("remotepath = " + remotePath);
        logger.info("localPath = " +  localPath);
    }

    String lastLocalFile = "";
    String strYMDH = "";
    public void setDownloadInfo(String lastLocalFile, String strYMDH){
        this.lastLocalFile = lastLocalFile;
        this.strYMDH = strYMDH;
    }

    /** *//**
     * 连接到FTP服务器
     * @param hostname 主机名
     * @param port 端口
     * @param username 用户名
     * @param password 密码
     * @return 是否连接成功
     * @throws IOException
     */
    public boolean connect(String hostname,int port,String username,String password) throws IOException{
        try {
            if(sftp != null){
                System.out.println("sftp is not null");
            }
            JSch jsch = new JSch();
            jsch.getSession(username, hostname, port);
            Session sshSession = jsch.getSession(username, hostname, port);
            System.out.println("Session created.");
            sshSession.setPassword(password);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            sshSession.connect();
            System.out.println("Session connected.");
            System.out.println("Opening Channel.");
            Channel channel = sshSession.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;
            System.out.println("Connected to " + hostname + ".");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    /**
     * 下载文件-sftp协议.
     * @param downloadFile 下载的文件
     * @param saveFile 存在本地的路径
     * @return 文件
     * @throws Exception 异常
     */
    public DownloadStatus download(final String downloadFile, final String saveFile){
        FileOutputStream os = null;
        File file = new File(saveFile);
        try {
            if (!file.exists()) {
                File parentFile = file.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                file.createNewFile();
            }
            os = new FileOutputStream(file);
//            List<String> list = formatPath(downloadFile);
            sftp.get(downloadFile, os);
            System.out.println(os);

        } catch (Exception e) {
            exit(sftp);
            e.getMessage();
        } finally {
            try {
                os.close();
                return DownloadStatus.Download_New_Success;
            } catch (IOException e) {
                e.printStackTrace();
                return DownloadStatus.Download_New_Failed;
            }
        }
    }
    /**
     * 关闭协议-sftp协议.
     * @param sftp sftp连接
     */
    public static void exit(final ChannelSftp sftp) {
        sftp.exit();
    }
    /** *//**
     * 断开与远程服务器的连接
     * @throws IOException
     */
    public void disconnect() {
        if(sftp.isConnected()){
            sftp.disconnect();
            logger.info("SFTP断开连接" );
        }

    }
    /**
     * 文件路径前缀. /ddit-remote
     */
    private static final String PRE_FIX = "/test-noryar";
    /**
     * 格式化路径.
     * @param srcPath 原路径. /xxx/xxx/xxx.yyy 或 X:/xxx/xxx/xxx.yy
     * @return list, 第一个是路径（/xxx/xxx/）,第二个是文件名（xxx.yy）
     */
    public static List<String> formatPath(final String srcPath) {
        List<String> list = new ArrayList<String>(2);
        String dir = "";
        String fileName = "";
        String repSrc = srcPath.replaceAll("\\\\", "/");
        int firstP = repSrc.indexOf("/");
        int lastP = repSrc.lastIndexOf("/");
        fileName = repSrc.substring(lastP + 1);
        dir = repSrc.substring(firstP, lastP);
        dir = PRE_FIX + (dir.length() == 1 ? dir : (dir + "/"));
        list.add(dir);
        list.add(fileName);
        return list;
    }

}
