package com.xinhong.util;

/**
 * Created by shijunna on 2016/7/18.
 */
public class ConfigCommon {

    public static final String DATE_DEFAULT_FORMAT_YMDHMS = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_DEFAULT_FORMAT_YMDH = "yyyy-MM-dd HH";
    public static final String DATE_DEFAULT_FORMAT_YMD = "yyyy-MM-dd";
    public static final String DATE_DEFAULT_FORMAT_DATA = "yyyyMM";
    public static final String DATE_FORMAT_GFS_YMDH="yyyyMMddHH";

    //#GFS数据入库REDIS调用地址
    public static final String GFS2REDIS_URL = "gfs2redis.url";
    //解释预报out文件存放位置
    public static final String JSYB_CREATE = "jsyb.create";
    public static final String JSYB_OUTFILE_PATH = "jsyb.outfile.path";
    public static final String THREAD_TIME_PATH = "threadtimePath";
    public static final String JSYB_DATAPATH = "jsyb_path";

    //-----------------CFS------------------------------//
    public static final String CFS_LOCAL_PATH = "cfs.local.path";
    public static final String CFS_REMOTE_PATH = "cfs.remote.path";
    public static final String CFS_ENCODING = "cfs.encoding";
    public static final String CFS_THREAD_CNT = "cfs.threads";
    public static final String CFS_HOST = "cfs.host";
    public static final String CFS_PORT = "cfs.port";
    public static final String CFS_USERNAME = "cfs.username";
    public static final String CFS_PASSWORD = "cfs.password";
    //--------------------------------------------------//

    //-----------------CFS1------------------------------//
    public static final String CFS1_DOWN_TYPE = "cfs1.down.type";
    public static final String CFS1_DOWN_EXT = "cfs1.down.ext";
    public static final String CFS1_LOCAL_PATH = "cfs1.local.path";
    public static final String CFS1_REMOTE_PATH = "cfs1.remote.path";
    public static final String CFS1_ENCODING = "cfs1.encoding";
    public static final String CFS1_THREAD_CNT = "cfs1.threads";
    public static final String CFS1_HOST = "cfs1.host";
    public static final String CFS1_PORT = "cfs1.port";
    public static final String CFS1_USERNAME = "cfs1.username";
    public static final String CFS1_PASSWORD = "cfs1.password";
    //--------------------------------------------------//


    //-----------------RTOFS------------------------------//
    public static final String RTOFS_DOWN_TYPE = "rtofs.down.type";
    public static final String RTOFS_DOWN_FORE = "rtofs.down.fore";
    public static final String RTOFS_LOCAL_PATH = "rtofs.local.path";
    public static final String RTOFS_REMOTE_PATH = "rtofs.remote.path";
    public static final String RTOFS_HOUR = "rtofs.hour";
    public static final String RTOFS_ENCODING = "rtofs.encoding";
    public static final String RTOFS_THREAD_CNT = "rtofs.threads";
    public static final String RTOFS_HOST = "rtofs.host";
    public static final String RTOFS_PORT = "rtofs.port";
    public static final String RTOFS_USERNAME = "rtofs.username";
    public static final String RTOFS_PASSWORD = "rtofs.password";
    public static final String RTOFS_REMOTEFILEPATH = "rtofs.local.path";
    public static final String RTOFS_FIXFILENAME = "rtofs.fixfilename";
    public static final String RTOFS_DATPATH = "rtofs.datpath";
    public static final String RTOFS_NEWNCPATH = "rtofs.newfilepath";

    //--------------------------------------------------//


    //-----------------WARI8------------------------------//
    public static final String WARI8_DOWN_LEVEL = "wari8.down.level";
    public static final String WARI8_DOWN_TYPE = "wari8.down.type";
    public static final String WARI8_DOWN_EXT = "wari8.down.ext";
    public static final String WARI8_LOCAL_PATH = "wari8.local.path";
    //public static final String WARI8_LOCAL_WART = "wari8.down.wari";
    public static final String WARI8_REMOTE_PATH = "wari8.remote.path";
    public static final String WARI8_ENCODING = "wari8.encoding";
    public static final String WARI8_THREAD_CNT = "wari8.threads";
    public static final String WARI8_HOST = "wari8.host";
    public static final String WARI8_PORT = "wari8.port";
    public static final String WARI8_USERNAME = "wari8.username";
    public static final String WARI8_PASSWORD = "wari8.password";
    public static final String WARI8_HOURS = "wari8.hours";
    //public static final String WARI8L1_REMOTE_PATH = "wari8_l1.remote.path";
    public static final String WARI8L1_DOWN_TYPE = "wari8_l1.down.type";
    //--------------------------------------------------//

    //----------------------WARI81L-----------------------//
    public static final String WARI8L1_LOCAL_PATH = "wari8L1.local.path";
    public static final String WARI8L1_REMOTE_PATH = "wari8L1.remote.path";
    public static final String WARI8L1_THREAD_CNT = "wari8L1.threads";
    public static final String WARI8L1_HOURS = "wari8L1.hours";
    public static final String WARI8L1_TEXTPATH = "wari8.test.path";

    //-----------------ocean1------------------------------//
    public static final String OCEAN1_DOWN_ELEM = "ocean1.down.elem";
    public static final String OCEAN1_LOCAL_PATH = "ocean1.local.path";
    public static final String OCEAN1_REMOTE_PATH = "ocean1.remote.path";
    public static final String OCEAN1_ENCODING = "ocean1.encoding";
    public static final String OCEAN1_THREAD_CNT = "ocean1.threads";
    public static final String OCEAN1_HOST = "ocean1.host";
    public static final String OCEAN1_PORT = "ocean1.port";
    public static final String OCEAN1_USERNAME = "ocean1.username";
    public static final String OCEAN1_PASSWORD = "ocean1.password";
    public static final String OCEAN1_HOURS = "ocean1.hours";
    //--------------------------------------------------//

    //-----------------haidiao------------------------------//
    public static final String HAIDIAO_DOWN_ELEM = "haidiao.down.elem";
    public static final String HAIDIAO_LOCAL_PATH = "haidiao.local.path";
    public static final String HAIDIAO_REMOTE_PATH = "haidiao.remote.path";
    public static final String HAIDIAO_ENCODING = "haidiao.encoding";
    public static final String HAIDIAO_THREAD_CNT = "haidiao.threads";
    public static final String HAIDIAO_HOST = "haidiao.host";
    public static final String HAIDIAO_PORT = "haidiao.port";
    public static final String HAIDIAO_USERNAME = "haidiao.username";
    public static final String HAIDIAO_PASSWORD = "haidiao.password";
    public static final String HAIDIAO_HOURS = "haidiao.hours";
    //--------------------------------------------------//

    // -----------------Hycom------------------------------//
    public static final String HYCOM_DOWN_ELEM = "hycom.down.elem";
    public static final String HYCOM_LOCAL_PATH = "hycom.local.path";
    public static final String HYCOM_REMOTE_PATH = "hycom.remote.path";
    public static final String HYCOM_ENCODING = "hycom.encoding";
    public static final String HYCOM_THREAD_CNT = "hycom.threads";
    public static final String HYCOM_HOST = "hycom.host";
    public static final String HYCOM_PORT = "hycom.port";
    public static final String HYCOM_USERNAME = "hycom.username";
    public static final String HYCOM_PASSWORD = "hycom.password";
    public static final String HYCOM_DOWN_TYPE = "hycom.down.type";
    public static final String HYCOM_HOURS = "hycom.hours";
    public static final String WARI_RETRY_CNT="wari8.retry.cnt";
    //--------------------------------------------------//

    //----------------------GTSPP-----------------------//
    public static final String GTSPP_LOCAL_PATH = "gtspp.local.path";
    public static final String GTSPP_REMOTE_PATH = "gtspp.remote.path";
    public static final String GTSPP_THREAD_CNT = "gtspp.threads";
    public static final String GTSPP_HOURS = "gtspp.hours";
    public static final String GTSPP_HOST = "gtspp.host";
    public static final String GTSPP_PORT = "gtspp.port";
    public static final String GTSPP_USERNAME = "gtspp.username";
    public static final String GTSPP_PASSWORD = "gtspp.password";
}
