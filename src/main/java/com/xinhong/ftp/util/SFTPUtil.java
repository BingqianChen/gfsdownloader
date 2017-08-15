package com.xinhong.ftp.util;

import com.jcraft.jsch.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;

/**
 * Created by shijunna on 2017/2/21.
 */
public class SFTPUtil {
    private final Logger logger = Logger.getLogger(SFTPUtil.class);
    private ChannelSftp sftp = null;
    /** *//**
     * 连接到FTP服务器
     * @param hostname 主机名
     * @param port 端口
     * @param username 用户名
     * @param password 密码
     * @return 是否连接成功
     * @throws IOException
     */
    public boolean connect(String hostname,int port,String username,String password){
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
     * 判断远程目录是否存在
     * @param remoteDir
     * @return
     */
    public boolean isExistFolder(String remoteDir){
        try {
            Vector<?> vector = sftp.ls(remoteDir);
            if (null == vector)
                return false;
            else
                return true;
        } catch (SftpException e) {
            return false;
        }
    }
    /**
     * 判断远程文件是否存在
     * @param remoteFile
     * @return
     */
    public boolean isExistFile(String remoteFile){
        if(remoteFile == null || remoteFile.length() < 1){
            return false;
        }
        try {
            Vector<?> vector = sftp.ls(remoteFile);
            if (vector == null || vector.size() != 1) {
                System.out.println("远程文件不存在:remoteFile:" + remoteFile);
                return false;
            }else {
                return true;
            }
        } catch (SftpException e) {
            e.printStackTrace();
            logger.error("判断远程文件是否存在失败，远程文件 = " + remoteFile + ",\r\n错误信息：" + e.getMessage());
            return false;
        }
    }

    /**
     * 获取远程文件列表
     * @param remoteFolder
     * @return
     */
    public List<String> getRemoteFiles(String remoteFolder){
        if(remoteFolder == null || remoteFolder.length() < 1){
            return null;
        }
        try {
            List<String> list = new ArrayList<>();
            Vector<ChannelSftp.LsEntry> vector = sftp.ls(remoteFolder);
            if (vector == null || vector.size() < 1) {
                System.out.println("远程文件不存在:remoteFolder:" + remoteFolder);
                return null;
            }else {
                Iterator<ChannelSftp.LsEntry> iterator = vector.iterator();
                while(iterator.hasNext()){
                    ChannelSftp.LsEntry entry = iterator.next();
                    list.add(entry.getFilename());

                }
                return list;
            }
        } catch (SftpException e) {
            e.printStackTrace();
            logger.error("判断远程文件是否存在失败，远程文件 = " + remoteFolder + ",\r\n错误信息：" + e.getMessage());
            return null;
        }
    }

    /** *//**
     * 断开与远程服务器的连接
     * @throws IOException
     */
    public void disconnect() {
        if(this.sftp != null){
            if(this.sftp.isConnected()){
                this.sftp.disconnect();
            }else if(this.sftp.isClosed()){
                System.out.println("sftp is closed already");
            }
        }
    }

}
