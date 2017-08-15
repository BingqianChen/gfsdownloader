package com.xinhong.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xin on 2016/6/15.
 */
public class FtpConfig {
    // / 需要下载的文件前缀
    private static String _ext = "";
    private static String scale = "";
    // / 需要下载的文件预报时效
    private static String[] vtis = {};
    // / 需要加载到redis的文件预报时效
    private static List<String> redisvtis = null;
    private static List<String> hours = null;

    // / 本地存储目录
    private static String local_path = "";
    // / 远程下载目录
    private static String path = "";


    // encode ,default GBK
    private static String encoding = "";
    // /username
    private static String username = "";
    // /password
    private static String password = "";
    // / HOST IP
    private static String HOST = "";
    // /fpt port
    private static int PORT = 21;
    // /下载文件大小限制
    //private static long MAX_SIZE = 1;// (1MB)
    // /被动模式
    private static boolean isPASV = false;
    // /是否覆盖
   // private static boolean cover = false;
    // /线程池大小
    private static int threads = 1;



    public static String getExt() {return  _ext;}
    public static String getScale() {return scale;}
    // / 需要下载的文件预报时效
    public static String[] getVtis() { return vtis;}
    public static List<String> getRedisvtis() { return redisvtis;}
    public static List<String> getHours() { return hours;}

    // / 本地存储目录
    public static String getLocalPath() {
        return local_path;
    }
    // / 远程下载目录
    public static String getPath(){
        return path;
    }

    // encode ,default GBK
    public static String getEncoding() {return encoding;}
    // /username
    public static String getUsername() {return username;}
    // /password
    public static String getPassword() {return password;}
    // / HOST IP
    public static String getHOST() {return HOST;}
    public static int getPORT() {return PORT;}
    // /fpt port
    // private static int PORT = 21;
    // /下载文件大小限制
    //private static long MAX_SIZE = 1;// (1MB)
    // /被动模式
    public static boolean isPASV() {return isPASV;}
    // /是否覆盖
    //public static boolean isCover() {return cover;}
    // /线程池大小
    public static int getThreads() {return threads;}

    /**
     * 初始化参数
     */
    public static void init() {
        _ext = ConfigUtil.getProperty("_ext", "").trim();
        scale = ConfigUtil.getProperty("scale", "").trim();
        local_path = ConfigUtil.getProperty("local_path", "").trim();
        path = ConfigUtil.getProperty("path", "").trim();
        username = ConfigUtil.getProperty("username", "").trim();
        password = ConfigUtil.getProperty("password", "").trim();
        HOST = ConfigUtil.getProperty("HOST", "").trim();
        PORT = Integer.parseInt(ConfigUtil.getProperty("PORT", "").trim());
        encoding = ConfigUtil.getProperty("encoding", "GBK").trim();
        isPASV = ConfigUtil.getProperty("isPASV", "true").trim().equals("true");
        //cover = ConfigUtil.getProperty("cover", "false").trim().equals("true");
        threads = Integer.parseInt(ConfigUtil.getProperty("threads", "1").trim()); // /线程池大小
        //	MAX_SIZE = Long.valueOf(ConfigUtil.getProperty("MAX_SIZE", "1").trim());
        String _ext_string = ConfigUtil.getProperty("_ext", "").trim().replaceAll(" ", "").replaceAll(",,", ",");
        //	String _not_string = ConfigUtil.getProperty("_not", "").trim().replaceAll(" ", "").replaceAll(",,", ",");
        String _vti_string = ConfigUtil.getProperty("vtis", "").trim().replaceAll(" ", "").replaceAll(",,", ",");
        String _redisvti_string = ConfigUtil.getProperty("redisvtis", "").trim().replaceAll(" ", "").replaceAll(",,", ",");
        String _hour_string = ConfigUtil.getProperty("hours", "").trim().replaceAll(" ", "").replaceAll(",,", ",");

        if (threads < 1) {
            threads = 1;
        }


        if(_vti_string == null || _vti_string.equals("")){
            _vti_string = "*";
        }
        vtis = _vti_string.split(",");


        if(_redisvti_string == null || _redisvti_string.equals("")){
            //_redisvtis_string = "*";
        }else{
            String[] redisvtiAry = _redisvti_string.split(",");
            redisvtis = new ArrayList<>(redisvtiAry.length);
            for(int i=0;i<redisvtiAry.length;i++){
                redisvtis.add(redisvtiAry[i]);
            }
        }


        if(_hour_string == null || _hour_string.equals("")){
            //_hour_string = "*";
        }else{
            String[] hourAry = _hour_string.split(",");
            hours = new ArrayList<>(hourAry.length);
            for(int i=0;i<hourAry.length;i++){
                hours.add(hourAry[i]);
            }
        }

    }

}
