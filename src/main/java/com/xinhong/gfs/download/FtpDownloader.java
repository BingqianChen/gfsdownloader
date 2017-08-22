package com.xinhong.gfs.download;

import com.xinhong.ftp.util.DownloadStatus;
import com.xinhong.util.ConfigCommon;
import com.xinhong.util.ConfigUtil;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by wingsby on 2017/8/11.
 */
public abstract class FtpDownloader implements Runnable {
    private final Logger logger = Logger.getLogger(PoolFTPDownloader.class);

    public FTPClient ftpClient = new FTPClient();
    protected String ftpURL, username, pwd;
    protected DownloadTask task;
    protected int ftpport;

    int defaultTimeoutSecond = 3 * 60;
    int connectTimeoutSecond = 3 * 60;
    int dataTimeoutSecond = 3 * 60;
    int controlKeepAliveReplyTimeoutSecond = 3 * 60;
    int blocksize = Integer.valueOf(ConfigUtil.getProperty("dwon.blocksize"));
    int piecesize = Integer.valueOf(ConfigUtil.getProperty("down.piecesize"));
    int checksize = Integer.valueOf(ConfigUtil.getProperty("down.checkbytes"));
    int rollsize = Integer.valueOf(ConfigUtil.getProperty("down.rollsize"));


    public boolean connect()  {
        try {
            ftpClient.connect(this.ftpURL, this.ftpport);
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("FTP服务器无法连接");
            return false;
        }
        ftpClient.setControlEncoding("GBK");
        if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
            try {
                if (ftpClient.login(this.username, this.pwd)) {
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
                logger.info("FTP服务器无法登陆");
            }
        }
        disconnect();
        return false;
    }

    /** */
    /**
     * 断开与远程服务器的连接
     *
     * @throws IOException
     */
    public void disconnect() {
        try {
            if (ftpClient.isConnected()) {
                ftpClient.disconnect();
//                logger.info("FTP断开连接");
            }
        } catch (IOException E) {
            E.printStackTrace();
            logger.info("FTP断开连接失败");
        }
    }


    /**
     * 获取远程文件列表
     *
     * @param remoteFolder
     * @return
     */
    public FTPFile[] getRemoteFiles(String remoteFolder) {
        if (remoteFolder == null || remoteFolder.length() < 1) {
            return null;
        }
        try {
            ftpClient.enterLocalPassiveMode(); //设置被动模式
            ftpClient.setDefaultPort(168);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            FTPFile[] files = ftpClient.listFiles(remoteFolder);
            if (files == null || files.length < 1) {
                System.out.println("远程文件不存在:remoteFolder:" + remoteFolder);
                return null;
            } else {
                return files;
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("判断远程文件是否存在失败，远程文件 = " + remoteFolder + ",\r\n错误信息：" + e.getMessage());
            return null;
        }
    }

    @Override
    public void run() {
    }

    public DownloadStatus download(DownloadTask task) throws Exception {
        boolean isDownload = false;
        ftpClient.enterLocalPassiveMode(); //设置被动模式
        //ftpClient.enterLocalActiveMode();  //设置主动模式
        ftpClient.setDefaultPort(ftpport); //gfs 168
        //设置以二进制方式传输
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.setControlKeepAliveReplyTimeout(controlKeepAliveReplyTimeoutSecond * 1000);
        ftpClient.setDefaultTimeout(defaultTimeoutSecond * 1000);
        ftpClient.setConnectTimeout(connectTimeoutSecond * 1000);
        ftpClient.setDataTimeout(dataTimeoutSecond * 1000);
        DownloadStatus result = DownloadStatus.FAILURE;
        FTPFile[] files = ftpClient.listFiles(task.getRemote());
        if (files.length < 1) {
            logger.info("远程文件不存在:list命令执行失败！remote = " + task.getRemote());
            return DownloadStatus.Remote_File_Noexist;
        }
        long lRemoteSize = files[0].getSize();
        File f = new File(task.getLocal());
        //本地存在文件，进行断点下载
        if (f.exists() && f.length() >= checksize) {
            long localSize = f.length();
            if(localSize == lRemoteSize){
                //说明代码出错，返回正常下载即可
                return DownloadStatus.Downloaded;
            }
            //判断本地文件大小是否大于远程文件大小
            if (localSize >lRemoteSize) {
                logger.info("本地文件大于等于远程文件，开始删除部分数据,并重新下载文件，" +
                        "local=" + task.getLocal());
                RandomAccessFile reader = new RandomAccessFile(f, "r");
                int saveSize=0;
                while(saveSize>100000000){
                    saveSize/=2;
                }
                byte[]tmp=new byte[(int) (saveSize)];
                int c=reader.read(tmp,0,tmp.length);
                reader.close();
                while(f.exists()){
                    f.delete();
                    Thread.sleep(1000);
                }
                RandomAccessFile writer = new RandomAccessFile(f, "rw");
                writer.write(tmp);
                writer.close();
                result = DownloadStatus.FAILURE;
                return result;
            }
            //进行断点续传，并记录状态
            isDownload = checkBreakLocalDown(task, localSize, lRemoteSize);
            if (isDownload) result = DownloadStatus.Download_From_Break_Success;
        } else {
            if (!f.getParentFile().exists()) {
                Thread.sleep(500);
                f.getParentFile().mkdirs();
            }
            f.createNewFile();
            RandomAccessFile out = new RandomAccessFile(f, "rw");

            InputStream in = ftpClient.retrieveFileStream(new String(task.getRemote()));
            isDownload = download2File(out, in, 0, lRemoteSize, task);
            logger.info("文件下载完毕");
        }
        if (isDownload) {
            result = DownloadStatus.Download_New_Success;
            logger.info("文件=" + task.getRemote() + "下载完毕,写入txt");
            DownloadTaskCenter.notifyDownloadStatus(task,TaskOperation.RemoveDowning);
            postProcess(task);
            logger.info("写入成功");
        }
        this.disconnect();
        return result;
    }

    protected abstract void postProcess(DownloadTask task);

    //@ offset 本地文件写数据
    //断点续传时in 也到offset字节了
    public boolean download2File(RandomAccessFile out, InputStream in, int offset, long lRemoteSize, DownloadTask task) {
        try {
            out.seek(offset);
            long stime = System.currentTimeMillis();
            byte[] bytes = new byte[piecesize];
            long step = lRemoteSize / 100;
            long process = 0;
            long tlocalSize = offset ;
            int c;
            long pretime = System.currentTimeMillis();
            ByteBuffer byteBuffer = ByteBuffer.allocate(blocksize);
            int bufferCnt = 0;
            while ((c = in.read(bytes)) != -1) {
                    byteBuffer.put(bytes,0,c);
                bufferCnt += c;
                if (bufferCnt >= blocksize - piecesize) {
                    out.write(byteBuffer.array(),0,byteBuffer.position());
                    byteBuffer.clear();
                    bufferCnt=0;
                }
                tlocalSize += c;
                long nowProcess = tlocalSize / step;
                if (nowProcess > process) {
                    process = nowProcess;
                    // 回传信息
                    DownloadTask.DownInfo info = null;
                    long afttime = System.currentTimeMillis();
                    if (task.getDownInfos() != null&&task.getDownInfos().size()>0) {
                        info = task.getDownInfos().get(task.getDownInfos().size() - 1);
                    } else {
                        info = task.new DownInfo();
                        task.addInfo(info);
                        info.setId(0);
                    }
                    info.setDownstatus(DownloadStatus.Downloading);
                    Date date = new Date(stime);
                    Date edate = new Date(afttime);
                    SimpleDateFormat sdf = new SimpleDateFormat(ConfigCommon.DATE_FORMAT_GFS_YMDH);
                    info.setStime(sdf.format(date));
                    info.setDownLoadBytes(offset + step * nowProcess);
                    info.setEtime(sdf.format(edate));
                    info.setSpeed(step * 1.0f / (afttime - pretime));
                    DownloadTaskCenter.notifyDownloadStatus(task);
                    if (process % 10 == 0) {
                        logger.info("文件=" + task.getLocal() + "下载进度：" + process + "%");
                    }
                }
            }
            // 文件读完时写入
            if (tlocalSize==lRemoteSize) {
                out.write(byteBuffer.array(),0,byteBuffer.position());
                byteBuffer.clear();
                in.close();
                out.close();
                File file=new File(task.getLocal());
                if(lRemoteSize==file.length())return true;
                else {
                    logger.error("记录下载数据正常，但实际文件大小不一致，"+tlocalSize+"///"+file.length());
                    return false;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkBreakLocalDown(DownloadTask task, long offset, long remoteSize) {
        try {
            String local=task.getLocal();
            String remote=task.getRemote();
            RandomAccessFile out = new RandomAccessFile(local, "rw");
            ftpClient.setRestartOffset(offset - checksize);
            InputStream in = ftpClient.retrieveFileStream(new String(remote));
            byte[] localCheck = new byte[checksize];
            byte[] remoteCheck = new byte[checksize];
            int lc = 0;
            if (offset > Integer.MAX_VALUE) System.out.println("超出整数范围");
//            out.skipBytes((int) (offset-checksize));
            out.seek(offset-checksize);
            lc = out.read(localCheck, 0, checksize);
            int rc = in.read(remoteCheck, 0, checksize);
            long filesize=out.length();
            if (lc != rc||!compareArray(localCheck,remoteCheck)) {
                //文件下载有误时，文件小于10M直接删除，其他循环重试
                out.close();
                in.close();
                if (offset + rollsize >= filesize) {
                    File file = new File(local);
                    while (file.exists()) {
                        file.delete();
                        Thread.sleep(1000);
                    }
                }
                checkBreakLocalDown(task, offset + rollsize, remoteSize);
                return false;
            } else {
                // 启动下载
                download2File(out, in, (int) offset, remoteSize, task);

                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean compareArray(byte[] in,byte[] out){
        for(int i=0;i<in.length;i++){
            if(in[i]!=out[i])return false;
        }
        return true;
    }

}
