package com.xinhong.gfs.processor;

import com.xinhong.util.ConfigCommon;
import com.xinhong.util.ConfigUtil;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Observable;

/**
 * Created by shijunna on 2016/7/25.
 */
public class Redis extends Observable implements Runnable {
//public class Redis extends Observable implements Runnable{
    private final Logger logger = Logger.getLogger(Redis.class);
    private static final String encoding = "UTF-8";
    @Override
    public void run() {
        try {
//            System.out.println(pathJson);
//            System.out.println(ConfigUtil.getProperty(ConfigCommon.GFS2REDIS_URL));
            sendRequest(pathJson, ConfigUtil.getProperty(ConfigCommon.GFS2REDIS_URL));
        } catch (Exception e) {
            super.setChanged();
//            notifyObservers(pathJson);
            logger.error("GFS入REDIS数据库出错："+e.getMessage());
            logger.error("文件路径名称 : " + pathJson);
        }
    }

    public void sendRequest(String json, String uri) {
        logger.info("public void sendRequest(String json, String uri) start");
        logger.info("json=" + json);
        logger.info("uri=" + uri);
        URL url;HttpURLConnection conn = null;
        try {
            url = new URL(uri);
            byte[] data = json.getBytes(encoding);
            conn = (HttpURLConnection) url.openConnection();
            // conn.setConnectTimeout(5000);
            conn.setRequestMethod("POST");//设置POST方式获取数据
            conn.setDoOutput(true);

            /*conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json; charset=" + encoding);
            conn.setRequestProperty("Content-Length", String.valueOf(data.length));*/

            //------------------------add by shijunna 20160801---------------------------------
            // Post 请求不能使用缓存
            conn.setUseCaches(false);
//            // 设定传送的内容类型是可序列化的java对象
//            // (如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException)
//            conn.setRequestProperty("Content-type", "application/x-java-serialized-object");
//            conn.setRequestProperty("Content-Type", "application/x-javascript; charset="+ encoding);
//            conn.setRequestProperty("Content-Length", String.valueOf(data.length));
            //设置连接超时时间
            conn.setConnectTimeout(30*1000);
            //--------------------------------------------------------------------------------------


            OutputStream outStream = conn.getOutputStream();
            outStream.write(data);
            outStream.flush();
            logger.info("执行：InputStream in = conn.getInputStream();");
            InputStream in = conn.getInputStream();
            logger.info("执行：BufferedReader bbb = new   BufferedReader(new InputStreamReader(in));");
            BufferedReader bbb = new   BufferedReader(new InputStreamReader(in));
//            System.out.println(bbb.readLine());
            logger.info("BufferedReader 返回结果：" + bbb.readLine());
//            if(bbb.readLine().equals("200")){
//                logger.info("成功加载至redis");
//            }else{
//                logger.error("加载至redis失败，jsonPath=" + json);
//            }
            //bbb.readLine() == 200 表示成功
            logger.info("执行：outStream.close();");
            outStream.close();
            logger.info("执行：conn.disconnect();");
//            conn.disconnect();
        } catch (Exception e) {
            logger.error("捕捉到Exception e异常");
            logger.error("加载至redis失败,请检查接口是否开启等原因，jsonPath=" + json);
            logger.error(e.getMessage());
            String sOut = "";
            StackTraceElement[] trace = e.getStackTrace();
            for (StackTraceElement s : trace) {
                sOut += "\t at " + s + "\r\n";
            }
            logger.error(sOut);
        }finally{
            logger.error("执行finally");
            if(conn != null) {
                logger.error("conn.disconnect();");
                conn.disconnect();
            }
        }
    }

    private String pathJson;
    public Redis(String pathJson){
        this.pathJson = pathJson;
    }
}
