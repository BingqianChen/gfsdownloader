package com.xinhong.sftp.util;

import com.jcraft.jsch.*;
import com.xinhong.ftp.util.DownloadStatus;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by shijunna on 2016/8/8.
 */
public class SFtpdownUtil {
    private final Logger logger = Logger.getLogger(SFtpdownUtil.class);
    private ChannelSftp sftp = null;
    private String ftpURL, username, pwd, remotefile, localfile;
    private int ftpport;

    public SFtpdownUtil(String remotePath, String localPath) {
        remotefile = remotePath;
        localfile = localPath;
        logger.info("remotepath = " + remotePath);
        logger.info("localPath = " + localPath);
    }

    String lastLocalFile = "";
    String strYMDH = "";

    public void setDownloadInfo(String lastLocalFile, String strYMDH) {
        this.lastLocalFile = lastLocalFile;
        this.strYMDH = strYMDH;
    }

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

    public boolean connect(String hostname, int port, String username, String password) throws IOException {
        try {
            if (sftp != null) {
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
     *
     * @param downloadFile 下载的文件
     * @param saveFile     存在本地的路径
     * @return 文件
     * @throws Exception 异常
     */
    public DownloadStatus download(final String downloadFile, String saveFile) {
        FileOutputStream os = null;
//        if(OSinfoUtils.isWindows()){
//            saveFile = "E:" + saveFile;
//        }
        File file = new File(saveFile);
        try {
            if (!file.exists()) {
                File parentFile = file.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                file.createNewFile();
            }
            SftpProgressMonitor monitor = new MyProgressMonitor();
            sftp.get(downloadFile, saveFile, monitor, ChannelSftp.RESUME);
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
     * 上传文件
     *
     * @param localFile  上传的文件
     * @param remoteFile 目标文件路径
     * @return
     */
    public void upload(String localFile, String remoteFile) {
        FileOutputStream os = null;
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
            SftpProgressMonitor monitor = new MyProgressMonitor();
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
        }


/*        FileOutputStream os = null;
//        if(OSinfoUtils.isWindows()){
//            saveFile = "E:" + saveFile;
//        }
        File file = new File(remoteFile);//目标文件夹
        try {
            if (!file.exists()) {
                File parentFile = file.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                file.createNewFile();
            }
            File localfile = new File(localFile);
            //File[] files = localfile.listFiles();
            //System.out.println(files);
            SftpProgressMonitor monitor = new MyProgressMonitor();
            this.sftp.put(new FileInputStream(file), file.getName());
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
        }*/
    }


//    public DownloadStatus download1(){
//
//        Map<String, String> sftpDetails = new HashMap<String, String>();
//        // 设置主机ip，端口，用户名，密码
//        sftpDetails.put(SFTPConstants.SFTP_REQ_HOST, "10.9.167.55");
//        sftpDetails.put(SFTPConstants.SFTP_REQ_USERNAME, "root");
//        sftpDetails.put(SFTPConstants.SFTP_REQ_PASSWORD, "arthur");
//        sftpDetails.put(SFTPConstants.SFTP_REQ_PORT, "22");
//
//        SFTPChannel channel = test.getSFTPChannel();
//        ChannelSftp chSftp = channel.getChannel(sftpDetails, 60000);
//
//        String filename = "/home/omc/ylong/sftp/INTPahcfg.tar.gz";
//        SftpATTRS attr = chSftp.stat(filename);
//        long fileSize = attr.getSize();
//
//        String dst = "D:\\INTPahcfg.tar.gz";
//        OutputStream out = new FileOutputStream(dst);
//        try {
//
//            chSftp.get(filename, dst, new FileProgressMonitor(fileSize)); // 代码段1
//
//            // chSftp.get(filename, out, new FileProgressMonitor(fileSize)); // 代码段2
//
//            /**
//             * 代码段3
//             *
//             InputStream is = chSftp.get(filename, new MyProgressMonitor());
//             byte[] buff = new byte[1024 * 2];
//             int read;
//             if (is != null) {
//             System.out.println("Start to read input stream");
//             do {
//             read = is.read(buff, 0, buff.length);
//             if (read > 0) {
//             out.write(buff, 0, read);
//             }
//             out.flush();
//             } while (read >= 0);
//             System.out.println("input stream read done.");
//             }
//             */
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            chSftp.quit();
//            channel.closeChannel();
//        }
//    }
//    /**
//     * 下载文件-sftp协议.
//     * @param downloadFile 下载的文件
//     * @param saveFile 存在本地的路径
//     * @return 文件
//     * @throws Exception 异常
//     */
//    public DownloadStatus downloadLsEntry(final String downloadFile, final String saveFile){
//        boolean isDownload = false;
//        DownloadStatus result = DownloadStatus.Download_New_Failed;
//        List<ChannelSftp.LsEntry> files = getRemoteFilesLsEntry(downloadFile);
//        if (files.size() < 1) {
//            logger.info("远程文件不存在:list命令执行失败！remote = " + downloadFile);
//            return DownloadStatus.Remote_File_Noexist;
//        }else if (files.size() == 1 ) {
//            logger.info("Vector<ChannelSftp.LsEntry> vector = sftp.ls(remoteFolder);;该命令获取的文件个数=1！remote = " + remote);
//        }else{
//            logger.info("Vector<ChannelSftp.LsEntry> vector = sftp.ls(remoteFolder);;该命令获取的文件个数>1！remote = " + remote);
//            if(files.get(0) != null){
//                logger.info("files.get(0).getFilename() = " + files.get(0).getFilename());
//            }else{
//                logger.info("files.get(0).getFilename() = null");
//            }
//        }
//        long lRemoteSize = files.get(0).getAttrs().getSize();
//        File f = new File(saveFile);
//        //本地存在文件，进行断点下载
//        if (f.exists()) {
//            long localSize = f.length();
//            //判断本地文件大小是否大于远程文件大小
//            if (localSize >= lRemoteSize) {
//                logger.info("本地文件大于等于远程文件，下载中止,local="+local);
//                isDownload = true;
//                result =  DownloadStatus.Local_Bigger_Remote;
//            }
//
//            //进行断点续传，并记录状态
//            FileOutputStream out = new FileOutputStream(f, true);
//            sftp.setRestartOffset(localSize);
//            //    InputStream in = ftpClient.retrieveFileStream(new String(remote.getBytes("GBK"),"iso-8859-1"));
//            InputStream in = ftpClient.retrieveFileStream(new String(remote));
//            byte[] bytes = new byte[1024];
//            long step = lRemoteSize / 100;
//            long process = localSize / step;
//            int c;
//            while ((c = in.read(bytes)) != -1) {
//                out.write(bytes, 0, c);
//                localSize += c;
//                long nowProcess = localSize / step;
//                if (nowProcess > process) {
//                    process = nowProcess;
//                    if (process % 5 == 0) {
//                        logger.info("文件=" + local + "下载进度：" + process + "%");
//                        if(process == 100){
//                            isDownload = true;
//                        }
//                    }
//                }
//            }
//            in.close();
//            out.close();
//            result = DownloadStatus.Download_From_Break_Success;
//        }
//
//        FileOutputStream os = null;
//        File file = new File(saveFile);
//        try {
//            if (!file.exists()) {
//                File parentFile = file.getParentFile();
//                if (!parentFile.exists()) {
//                    parentFile.mkdirs();
//                }
//                file.createNewFile();
//            }
//            os = new FileOutputStream(file);
////            List<String> list = formatPath(downloadFile);
//            sftp.get(downloadFile, os);
//        } catch (Exception e) {
//            exit(sftp);
//            e.getMessage();
//        } finally {
//            try {
//                os.close();
//                return DownloadStatus.Download_New_Success;
//            } catch (IOException e) {
//                e.printStackTrace();
//                return DownloadStatus.Download_New_Failed;
//            }
//        }
//    }

    /**
     * 关闭协议-sftp协议.
     *
     * @param sftp sftp连接
     */
    public static void exit(final ChannelSftp sftp) {
        sftp.exit();
    }
    /** */
    /**
     * 断开与远程服务器的连接
     *
     * @throws IOException
     */
    public void disconnect() {
        if (sftp.isConnected()) {
            sftp.disconnect();
            logger.info("SFTP断开连接");
        }

    }

    /**
     * 文件路径前缀. /ddit-remote
     */
    private static final String PRE_FIX = "/test-noryar";

    /**
     * 格式化路径.
     *
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

    /**
     * 获取远程文件列表
     *
     * @param remoteFolder
     * @return
     */
    public List<ChannelSftp.LsEntry> getRemoteFilesLsEntry(String remoteFolder) {
        if (remoteFolder == null || remoteFolder.length() < 1) {
            return null;
        }
        try {
            List<ChannelSftp.LsEntry> list = new ArrayList<>();
            Vector<ChannelSftp.LsEntry> vector = sftp.ls(remoteFolder);
            if (vector == null || vector.size() < 1) {
                System.out.println("远程文件不存在:remoteFolder:" + remoteFolder);
                return null;
            } else {
                Iterator<ChannelSftp.LsEntry> iterator = vector.iterator();
                while (iterator.hasNext()) {
                    list.add(iterator.next());
                }
                return list;
            }
        } catch (SftpException e) {
            e.printStackTrace();
            logger.error("判断远程文件是否存在失败，远程文件 = " + remoteFolder + ",\r\n错误信息：" + e.getMessage());
            return null;
        }
    }


    /**
     * 进度监控器-JSch每次传输一个数据块，就会调用count方法来实现主动进度通知
     */
    public static class MyProgressMonitor implements SftpProgressMonitor {
        private long count = 0;     //当前接收的总字节数
        private long max = 0;       //最终文件大小
        private long percent = -1;  //进度
        private String path = null; //当前接受的文件路径

        /**
         * 当每次传输了一个数据块后，调用count方法，count方法的参数为这一次传输的数据块大小
         */
        @Override
        public boolean count(long count) {
            this.count += count;
            if (percent >= this.count * 100 / max) {
                return true;
            }
            percent = this.count * 100 / max;
            System.out.println(path + " Completed " + this.count + "(" + percent
                    + "%) out of " + max + ".");
            return true;
        }

        /**
         * 当传输结束时，调用end方法
         */
        @Override
        public void end() {
            System.out.println("Transferring done.");
        }

        /**
         * 当文件开始传输时，调用init方法
         */
        @Override
        public void init(int op, String src, String dest, long max) {
            System.out.println("Transferring begin.");
            this.path = dest;
            this.max = max;
            this.count = 0;
            this.percent = -1;
        }
    }
}
