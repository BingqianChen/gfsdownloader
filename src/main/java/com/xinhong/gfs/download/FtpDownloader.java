package com.xinhong.gfs.download;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xinhong.ftp.util.DownloadStatus;
import com.xinhong.util.FileHandler;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

import java.io.*;

/**
 * Created by wingsby on 2017/8/11.
 */
public abstract class FtpDownloader implements Runnable {
    private final Logger logger = Logger.getLogger(PoolFTPDownloader.class);

    public FTPClient ftpClient = new FTPClient();
    protected String ftpURL,username,pwd,remotefile, localfile;
    protected int ftpport;

    int defaultTimeoutSecond = 3 * 60;
    int connectTimeoutSecond = 3 * 60;
    int dataTimeoutSecond = 3 * 60;
    int controlKeepAliveReplyTimeoutSecond = 3 * 60;

    public JSONObject info;



    public boolean connect() throws IOException{
        ftpClient.connect(this.ftpURL, this.ftpport);
        ftpClient.setControlEncoding("GBK");
        if(FTPReply.isPositiveCompletion(ftpClient.getReplyCode())){
            if(ftpClient.login(this.username, this.pwd)){
                return true;
            }
        }
        disconnect();
        return false;
    }

    /** *//**
     * 断开与远程服务器的连接
     * @throws IOException
     */
    public void disconnect() {
        try {
            if(ftpClient.isConnected()){
                ftpClient.disconnect();
                logger.info("FTP断开连接" );
            }
        }catch(IOException E){
            E.printStackTrace();
            logger.info("FTP断开连接失败" );
        }
    }



    /**
     * 获取远程文件列表
     * @param remoteFolder
     * @return
     */
    public FTPFile[] getRemoteFiles(String remoteFolder){
        if(remoteFolder == null || remoteFolder.length() < 1){
            return null;
        }
        try {
            ftpClient.enterLocalPassiveMode(); //设置被动模式
            ftpClient.setDefaultPort(168);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            FTPFile[] files = ftpClient.listFiles(remoteFolder);
            if(files == null || files.length < 1){
                System.out.println("远程文件不存在:remoteFolder:" + remoteFolder);
                return null;
            }else{
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

    public DownloadStatus download(String remote, String local) throws Exception{
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
        DownloadStatus result = DownloadStatus.Download_New_Failed;
        FTPFile[] files = ftpClient.listFiles(remote);
        if (files.length < 1) {
            logger.info("远程文件不存在:list命令执行失败！remote = " + remote);
            return DownloadStatus.Remote_File_Noexist;
        }
        long lRemoteSize = files[0].getSize();
        File f = new File(local);
        //本地存在文件，进行断点下载
        if (f.exists()) {
            long localSize = f.length();
            //判断本地文件大小是否大于远程文件大小
            if (localSize >= lRemoteSize) {
                logger.info("本地文件大于等于远程文件，下载中止,local="+local);
                isDownload = true;
                result =  DownloadStatus.Local_Bigger_Remote;
                return result;
            }
            //进行断点续传，并记录状态
            FileOutputStream out = new FileOutputStream(f, true);
            ftpClient.setRestartOffset(localSize);
            InputStream in = ftpClient.retrieveFileStream(new String(remote));
            isDownload=download2File(out,in,lRemoteSize,local);
            result = DownloadStatus.Download_From_Break_Success;
        } else {
            if (!f.getParentFile().exists()) {
                f.getParentFile().mkdirs();
            }
            f.createNewFile();
            OutputStream out = new FileOutputStream(f);
            InputStream in = ftpClient.retrieveFileStream(new String(remote));
            isDownload=download2File(out,in,lRemoteSize,local);
            logger.info("文件下载完毕");
        }
        JSONObject info=new JSONObject();
        info.put("localname",local);
        info.put("remotename",remote);
        if(isDownload){
            result = DownloadStatus.Download_From_Break_Success;
            logger.info("文件=" + local + "下载完毕,写入txt");
            postProcess(info);
            logger.info("写入成功");
        }
        this.disconnect();
        return result;
    }

    protected abstract void postProcess(JSONObject info);


    public boolean download2File(OutputStream out,InputStream in,long lRemoteSize,String local){
        try {
            logger.info("开始下载文件");
            byte[] bytes = new byte[1024];
            long step = lRemoteSize / 100;
            long process = 0;
            long localSize = 0L;
            int c;
            while ((c = in.read(bytes)) != -1) {
                out.write(bytes, 0, c);
                //logger.info("开始计算文件下载百分比");
                localSize += c;
                long nowProcess = localSize / step;
                if (nowProcess > process) {
                    process = nowProcess;
                    if (process % 10 == 0) {
                        logger.info("文件=" + local + "下载进度：" + process + "%");
                        if (process == 100) {
                            return true;
                        }
                    }
                } else {
//                    logger.error("nowProcess < process");
                }
            }
            in.close();
            out.close();
        }catch (Exception e){

        }
        return false;
    }

}
