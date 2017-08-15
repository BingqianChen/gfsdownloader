package com.xinhong.sftp.util;

import com.jcraft.jsch.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SFtpUploadUtil {

    static String host="123.59.100.107";
    static int port=22;
    static String username="app";
    static String password="app82193302";
    private ChannelSftp sftp = null;
//    private String localPath = "/var/test";
//    private String remotePath = "/var/tset";
//    private String fileListPath = "/var/test/java/test.txt";
    private final String seperator = "/";

    /**
     * connect server via sftp
     */
    public void connect() {
        try {
            if(sftp != null){
                System.out.println("sftp is not null");
            }
            JSch jsch = new JSch();
            jsch.getSession(username, host, port);
            Session sshSession = jsch.getSession(username, host, port);
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
            System.out.println("Connected to " + host + ".");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Disconnect with server
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

    public void download() {
        // TODO Auto-generated method stub


    }


    private void download(String directory, String downloadFile,String saveFile, ChannelSftp sftp) {
        try {
            sftp.cd(directory);
            File file = new File(saveFile);
            sftp.get(downloadFile, new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * upload all the files to the server
     */
    public void  upload(String localFile,String remoteFile) {
       /* FileOutputStream os = null;
//        if(OSinfoUtils.isWindows()){
//            saveFile = "E:" + saveFile;
//        }
        File file = new File(remoteFile);
        try {
            if (!file.exists()) {
                File parentFile = file.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                file.createNewFile();
            }
            SftpProgressMonitor monitor = new SFtpdownUtil.MyProgressMonitor();
            sftp.put(localFile, remoteFile, monitor, ChannelSftp.OVERWRITE);
        } catch (Exception e) {
            exit(sftp);
            e.getMessage();
        } finally {
            try {
                os.close();
                System.out.println("上传成功...");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("上传失败...");
            }
        }*/
        try {
                    File file = new File(localFile);
                    if(file.isFile()){
                        File rfile = new File(remoteFile);
                        String rpath = rfile.getParent();
                        try {
                            createDir(rpath, sftp);
                        } catch (Exception e) {
                            System.out.println("创建目录：" + rpath);
                        }
                        try {
                            this.sftp.cd(rpath);
                        }catch (Exception e){
                            System.out.println("进入"+rpath+"目录失败...");
                        }
                        this.sftp.put(new FileInputStream(file), file.getName());
                        System.out.println("已经上传到： " + localFile);
                    }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SftpException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    public static void exit(final ChannelSftp sftp) {
        sftp.exit();
    }
    /**
     * create Directory
     * @param filepath
     * @param sftp
     */
    private void createDir(String filepath, ChannelSftp sftp){
        boolean bcreated = false;
        boolean bparent = false;
        File file = new File(filepath);
        String ppath = file.getParent();
        try {
            this.sftp.cd(ppath);
            System.out.println("进入"+ppath+"目录成功...");
            bparent = true;
        } catch (SftpException e1) {
            System.out.println("进入"+ppath+"目录失败...");
            bparent = false;
        }
        try {
            if(bparent){
                try {
                    this.sftp.cd(filepath);
                    System.out.println("进入"+filepath+"目录成功...");
                    bcreated = true;
                } catch (Exception e) {
                    System.out.println("进入"+filepath+"目录失败...");
                    bcreated = false;
                }
                if(!bcreated){
                    System.out.println("创建"+filepath+"：目录...");
                    this.sftp.mkdir(filepath);
                    bcreated = true;
                }
                return;
            }else{
                createDir(ppath,sftp);
                this.sftp.cd(ppath);
                this.sftp.mkdir(filepath);
            }
        } catch (SftpException e) {
            System.out.println("创建目录 :" + filepath+"失败");
            e.printStackTrace();
        }

        try {
            System.out.println("进入该"+filepath+"目录...");
            this.sftp.cd(filepath);
        } catch (SftpException e) {
            e.printStackTrace();
            System.out.println("进入 :" + filepath+"失败");
        }

    }
    /**
     * get all the files need to be upload or download
     * @param file
     * @return
     */
    private List<String> getFileEntryList(String file){
        ArrayList<String> fileList = new ArrayList<String>();
        InputStream in = null;
        try {

            in = new FileInputStream(file);
            InputStreamReader inreader = new InputStreamReader(in);

            LineNumberReader linreader = new LineNumberReader(inreader);
            String filepath = linreader.readLine();
            while(filepath != null){
                fileList.add(filepath);
                filepath = linreader.readLine();
            }
            in.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            if(in != null){
                in = null;
            }
        }

        return fileList;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return the sftp
     */
    public ChannelSftp getSftp() {
        return sftp;
    }

    /**
     * @param sftp the sftp to set
     */
    public void setSftp(ChannelSftp sftp) {
        this.sftp = sftp;
    }

    /**
     * @return the localPath
     */



}
