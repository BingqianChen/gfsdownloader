package com.xinhong.gfs.processor;

import java.io.*;
import java.util.HashMap;

/**
 * Created by shijunna on 2016/7/19.
 */
public class ElemLevel {
    static HashMap<String, String> elemPatchMap;

    public static HashMap<String,String> getElemMap(){
        // read elem properties
        if(elemPatchMap!=null)return elemPatchMap;
        elemPatchMap = new HashMap<>();
        try {
            BufferedReader reader=new BufferedReader(new FileReader("gfsConf/jsybElem.properties"));
            String str=null;
            while((str=reader.readLine())!=null){
                if(str.startsWith("#"))continue;
                String[] strs=str.split("=");
                if(strs.length>1) {
                    elemPatchMap.put(strs[0], strs[1]);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return elemPatchMap;
    }


    //// TODO: 2016/7/21
//    public static HashMap<String,String> getElemLevelMap(){
//        HashMap<String, String> stringStringHashMap = new HashMap<>();
//        stringStringHashMap.put("UGRD_100mb", "UU_0100");
//        stringStringHashMap.put("UGRD_150mb", "UU_0150");
//        stringStringHashMap.put("UGRD_200mb", "UU_0200");
//        stringStringHashMap.put("UGRD_250mb", "UU_0250");
//        stringStringHashMap.put("UGRD_300mb", "UU_0300");
//        stringStringHashMap.put("UGRD_350mb", "UU_0350");
//        stringStringHashMap.put("UGRD_400mb", "UU_0400");
//        stringStringHashMap.put("UGRD_500mb", "UU_0500");
//        stringStringHashMap.put("UGRD_700mb", "UU_0700");
//        stringStringHashMap.put("UGRD_850mb", "UU_0850");
//        stringStringHashMap.put("UGRD_1000mb","UU_1000");
//        stringStringHashMap.put("UGRD_10maboveground","UU_9999");
//
//        stringStringHashMap.put("VGRD_100mb", "VV_0100");
//        stringStringHashMap.put("VGRD_150mb", "VV_0150");
//        stringStringHashMap.put("VGRD_200mb", "VV_0200");
//        stringStringHashMap.put("VGRD_250mb", "VV_0250");
//        stringStringHashMap.put("VGRD_300mb", "VV_0300");
//        stringStringHashMap.put("VGRD_350mb", "VV_0350");
//        stringStringHashMap.put("VGRD_400mb", "VV_0400");
//        stringStringHashMap.put("VGRD_500mb", "VV_0500");
//        stringStringHashMap.put("VGRD_700mb", "VV_0700");
//        stringStringHashMap.put("VGRD_850mb", "VV_0850");
//        stringStringHashMap.put("VGRD_1000mb","VV_1000");
//        stringStringHashMap.put("VGRD_10maboveground","VV_9999");
//
//        stringStringHashMap.put("HGT_100mb", "HH_0100");
//        stringStringHashMap.put("HGT_150mb", "HH_0150");
//        stringStringHashMap.put("HGT_200mb", "HH_0200");
//        stringStringHashMap.put("HGT_250mb", "HH_0250");
//        stringStringHashMap.put("HGT_300mb", "HH_0300");
//        stringStringHashMap.put("HGT_350mb", "HH_0350");
//        stringStringHashMap.put("HGT_400mb", "HH_0400");
//        stringStringHashMap.put("HGT_500mb", "HH_0500");
//        stringStringHashMap.put("HGT_700mb", "HH_0700");
//        stringStringHashMap.put("HGT_850mb", "HH_0850");
//        stringStringHashMap.put("HGT_1000mb","HH_1000");
//
//        stringStringHashMap.put("TMP_100mb", "TT_0100");
//        stringStringHashMap.put("TMP_150mb", "TT_0150");
//        stringStringHashMap.put("TMP_200mb", "TT_0200");
//        stringStringHashMap.put("TMP_250mb", "TT_0250");
//        stringStringHashMap.put("TMP_300mb", "TT_0300");
//        stringStringHashMap.put("TMP_350mb", "TT_0350");
//        stringStringHashMap.put("TMP_400mb", "TT_0400");
//        stringStringHashMap.put("TMP_500mb", "TT_0500");
//        stringStringHashMap.put("TMP_700mb", "TT_0700");
//        stringStringHashMap.put("TMP_850mb", "TT_0850");
//        stringStringHashMap.put("TMP_1000mb","TT_1000");
//        stringStringHashMap.put("TMP_2maboveground","TT_9999");
//
//        stringStringHashMap.put("RH_100mb", "RH_0100");
//        stringStringHashMap.put("RH_150mb", "RH_0150");
//        stringStringHashMap.put("RH_200mb", "RH_0200");
//        stringStringHashMap.put("RH_250mb", "RH_0250");
//        stringStringHashMap.put("RH_300mb", "RH_0300");
//        stringStringHashMap.put("RH_350mb", "RH_0350");
//        stringStringHashMap.put("RH_400mb", "RH_0400");
//        stringStringHashMap.put("RH_500mb", "RH_0500");
//        stringStringHashMap.put("RH_700mb", "RH_0700");
//        stringStringHashMap.put("RH_850mb", "RH_0850");
//        stringStringHashMap.put("RH_1000mb","RH_1000");
//        stringStringHashMap.put("RH_2maboveground","RH_9999");
//        //云水混合比
//        stringStringHashMap.put("CLWMR_100mb", "QC_0100");
//        stringStringHashMap.put("CLWMR_150mb", "QC_0150");
//        stringStringHashMap.put("CLWMR_200mb", "QC_0200");
//        stringStringHashMap.put("CLWMR_250mb", "QC_0250");
//        stringStringHashMap.put("CLWMR_300mb", "QC_0300");
//        stringStringHashMap.put("CLWMR_350mb", "QC_0350");
//        stringStringHashMap.put("CLWMR_400mb", "QC_0400");
//        stringStringHashMap.put("CLWMR_450mb", "QC_0450");
//        stringStringHashMap.put("CLWMR_500mb", "QC_0500");
//        stringStringHashMap.put("CLWMR_550mb", "QC_0550");
//        stringStringHashMap.put("CLWMR_600mb", "QC_0600");
//        stringStringHashMap.put("CLWMR_650mb", "QC_0650");
//        stringStringHashMap.put("CLWMR_700mb", "QC_0700");
//        stringStringHashMap.put("CLWMR_750mb", "QC_0750");
//        stringStringHashMap.put("CLWMR_800mb", "QC_0800");
//        stringStringHashMap.put("CLWMR_850mb", "QC_0850");
//        stringStringHashMap.put("CLWMR_900mb", "QC_0900");
//        stringStringHashMap.put("CLWMR_950mb", "QC_0950");
//        stringStringHashMap.put("CLWMR_975mb", "QC_0975");
//        stringStringHashMap.put("CLWMR_1000mb","QC_1000");
//
//        //比湿
//        stringStringHashMap.put("SPFH_2maboveground","QQ_9999");
//        //2米高相对湿度
//        stringStringHashMap.put("RH_2mb","RH_9999");
//
//
//        stringStringHashMap.put("TMAX_2maboveground","MXT_9999");
//        stringStringHashMap.put("TMIN_2maboveground","MIT_9999");
//        //平均海平面气压
//        stringStringHashMap.put("PRMSL_meansealevel","PR_9999");
//        //降水量
//        //stringStringHashMap.put("PWAT_entireatmosphere(consideredasasinglelayer)","RN_9999");
//        //        总降水量 王烨
//        stringStringHashMap.put("APCP_surface","RN_9999");
//        //总云量
//        stringStringHashMap.put("TCDC_lowcloudlayer","CNL_9999");
////        stringStringHashMap.put("TCDC_middle cloud layer","TR_9999");
////        stringStringHashMap.put("TCDC_high cloud layer","TR_9999");
//        stringStringHashMap.put("TCDC_entireatmosphere","CN_9999");
////        stringStringHashMap.put("TCDC_convective cloud layer","TR_9999");
////        stringStringHashMap.put("TCDC_boundary layer cloud layer","TR_9999");
//
//
//        //解释预报需要用到的要素
//        stringStringHashMap.put("HGT_10mb",  "HH_0010");
//        stringStringHashMap.put("HGT_20mb",  "HH_0020");
//        stringStringHashMap.put("HGT_30mb",  "HH_0030");
//        stringStringHashMap.put("HGT_50mb",  "HH_0050");
//        stringStringHashMap.put("HGT_70mb",  "HH_0070");
//        stringStringHashMap.put("HGT_150mb", "HH_0150");
//        stringStringHashMap.put("HGT_250mb", "HH_0250");
//        stringStringHashMap.put("HGT_350mb", "HH_0350");
//        stringStringHashMap.put("HGT_450mb", "HH_0450");
//        stringStringHashMap.put("HGT_550mb", "HH_0550");
//        stringStringHashMap.put("HGT_600mb", "HH_0600");
//        stringStringHashMap.put("HGT_650mb", "HH_0650");
//        stringStringHashMap.put("HGT_750mb", "HH_0750");
//        stringStringHashMap.put("HGT_800mb", "HH_0800");
//        stringStringHashMap.put("HGT_900mb", "HH_0900");
//        stringStringHashMap.put("HGT_925mb", "HH_0925");
//        stringStringHashMap.put("HGT_950mb", "HH_0950");
//        stringStringHashMap.put("HGT_975mb", "HH_0975");
//
//        stringStringHashMap.put("TMP_10mb",  "TT_0010");
//        stringStringHashMap.put("TMP_20mb",  "TT_0020");
//        stringStringHashMap.put("TMP_30mb",  "TT_0030");
//        stringStringHashMap.put("TMP_50mb",  "TT_0050");
//        stringStringHashMap.put("TMP_70mb",  "TT_0070");
//        stringStringHashMap.put("TMP_150mb", "TT_0150");
//        stringStringHashMap.put("TMP_250mb", "TT_0250");
//        stringStringHashMap.put("TMP_350mb", "TT_0350");
//        stringStringHashMap.put("TMP_450mb", "TT_0450");
//        stringStringHashMap.put("TMP_550mb", "TT_0550");
//        stringStringHashMap.put("TMP_600mb", "TT_0600");
//        stringStringHashMap.put("TMP_650mb", "TT_0650");
//        stringStringHashMap.put("TMP_750mb", "TT_0750");
//        stringStringHashMap.put("TMP_800mb", "TT_0800");
//        stringStringHashMap.put("TMP_900mb", "TT_0900");
//        stringStringHashMap.put("TMP_925mb", "TT_0925");
//        stringStringHashMap.put("TMP_950mb", "TT_0950");
//        stringStringHashMap.put("TMP_975mb", "TT_0975");
//
//        stringStringHashMap.put("UGRD_10mb",  "UU_0010");
//        stringStringHashMap.put("UGRD_20mb",  "UU_0020");
//        stringStringHashMap.put("UGRD_30mb",  "UU_0030");
//        stringStringHashMap.put("UGRD_50mb",  "UU_0050");
//        stringStringHashMap.put("UGRD_70mb",  "UU_0070");
//        stringStringHashMap.put("UGRD_150mb", "UU_0150");
//        stringStringHashMap.put("UGRD_250mb", "UU_0250");
//        stringStringHashMap.put("UGRD_350mb", "UU_0350");
//        stringStringHashMap.put("UGRD_450mb", "UU_0450");
//        stringStringHashMap.put("UGRD_550mb", "UU_0550");
//        stringStringHashMap.put("UGRD_600mb", "UU_0600");
//        stringStringHashMap.put("UGRD_650mb", "UU_0650");
//        stringStringHashMap.put("UGRD_750mb", "UU_0750");
//        stringStringHashMap.put("UGRD_800mb", "UU_0800");
//        stringStringHashMap.put("UGRD_900mb", "UU_0900");
//        stringStringHashMap.put("UGRD_925mb", "UU_0925");
//        stringStringHashMap.put("UGRD_950mb", "UU_0950");
//        stringStringHashMap.put("UGRD_975mb", "UU_0975");
//
//        stringStringHashMap.put("VGRD_10mb",  "VV_0010");
//        stringStringHashMap.put("VGRD_20mb",  "VV_0020");
//        stringStringHashMap.put("VGRD_30mb",  "VV_0030");
//        stringStringHashMap.put("VGRD_50mb",  "VV_0050");
//        stringStringHashMap.put("VGRD_70mb",  "VV_0070");
//        stringStringHashMap.put("VGRD_150mb", "VV_0150");
//        stringStringHashMap.put("VGRD_250mb", "VV_0250");
//        stringStringHashMap.put("VGRD_350mb", "VV_0350");
//        stringStringHashMap.put("VGRD_450mb", "VV_0450");
//        stringStringHashMap.put("VGRD_550mb", "VV_0550");
//        stringStringHashMap.put("VGRD_600mb", "VV_0600");
//        stringStringHashMap.put("VGRD_650mb", "VV_0650");
//        stringStringHashMap.put("VGRD_750mb", "VV_0750");
//        stringStringHashMap.put("VGRD_800mb", "VV_0800");
//        stringStringHashMap.put("VGRD_900mb", "VV_0900");
//        stringStringHashMap.put("VGRD_925mb", "VV_0925");
//        stringStringHashMap.put("VGRD_950mb", "VV_0950");
//        stringStringHashMap.put("VGRD_975mb", "VV_0975");
//
//        stringStringHashMap.put("RH_10mb",  "RH_0010");
//        stringStringHashMap.put("RH_20mb",  "RH_0020");
//        stringStringHashMap.put("RH_30mb",  "RH_0030");
//        stringStringHashMap.put("RH_50mb",  "RH_0050");
//        stringStringHashMap.put("RH_70mb",  "RH_0070");
//        stringStringHashMap.put("RH_150mb", "RH_0150");
//        stringStringHashMap.put("RH_250mb", "RH_0250");
//        stringStringHashMap.put("RH_350mb", "RH_0350");
//        stringStringHashMap.put("RH_450mb", "RH_0450");
//        stringStringHashMap.put("RH_550mb", "RH_0550");
//        stringStringHashMap.put("RH_600mb", "RH_0600");
//        stringStringHashMap.put("RH_650mb", "RH_0650");
//        stringStringHashMap.put("RH_750mb", "RH_0750");
//        stringStringHashMap.put("RH_800mb", "RH_0800");
//        stringStringHashMap.put("RH_900mb", "RH_0900");
//        stringStringHashMap.put("RH_925mb", "RH_0925");
//        stringStringHashMap.put("RH_950mb", "RH_0950");
//        stringStringHashMap.put("RH_975mb", "RH_0975");
//        //垂直速度
//        stringStringHashMap.put("VVEL_100mb", "WXT_0100");
//        stringStringHashMap.put("VVEL_200mb", "WXT_0200");
//        stringStringHashMap.put("VVEL_300mb", "WXT_0300");
//        stringStringHashMap.put("VVEL_400mb", "WXT_0400");
//        stringStringHashMap.put("VVEL_500mb", "WXT_0500");
//        stringStringHashMap.put("VVEL_700mb", "WXT_0700");
//        stringStringHashMap.put("VVEL_850mb", "WXT_0850");
//        stringStringHashMap.put("VVEL_1000mb","WXT_1000");
//        stringStringHashMap.put("VVEL_10mb",  "WXT_0010");
//        stringStringHashMap.put("VVEL_20mb",  "WXT_0020");
//        stringStringHashMap.put("VVEL_30mb",  "WXT_0030");
//        stringStringHashMap.put("VVEL_50mb",  "WXT_0050");
//        stringStringHashMap.put("VVEL_70mb",  "WXT_0070");
//        stringStringHashMap.put("VVEL_150mb", "WXT_0150");
//        stringStringHashMap.put("VVEL_250mb", "WXT_0250");
//        stringStringHashMap.put("VVEL_350mb", "WXT_0350");
//        stringStringHashMap.put("VVEL_450mb", "WXT_0450");
//        stringStringHashMap.put("VVEL_550mb", "WXT_0550");
//        stringStringHashMap.put("VVEL_600mb", "WXT_0600");
//        stringStringHashMap.put("VVEL_650mb", "WXT_0650");
//        stringStringHashMap.put("VVEL_750mb", "WXT_0750");
//        stringStringHashMap.put("VVEL_800mb", "WXT_0800");
//        stringStringHashMap.put("VVEL_900mb", "WXT_0900");
//        stringStringHashMap.put("VVEL_925mb", "WXT_0925");
//        stringStringHashMap.put("VVEL_950mb", "WXT_0950");
//        stringStringHashMap.put("VVEL_975mb", "WXT_0975");
//
//
//        //本站气压
//        stringStringHashMap.put("PRES_surface","LP_9999");
////        雨类型
////        stringStringHashMap.put("CRAIN_surface","TR_9999");
//
//
//
//        //绝对涡度
//        stringStringHashMap.put("ABSV_100mb", "VO_0100");
//        stringStringHashMap.put("ABSV_200mb", "VO_0200");
//        stringStringHashMap.put("ABSV_300mb", "VO_0300");
//        stringStringHashMap.put("ABSV_400mb", "VO_0400");
//        stringStringHashMap.put("ABSV_500mb", "VO_0500");
//        stringStringHashMap.put("ABSV_700mb", "VO_0700");
//        stringStringHashMap.put("ABSV_850mb", "VO_0850");
//        stringStringHashMap.put("ABSV_1000mb","VO_1000");
//        stringStringHashMap.put("ABSV_10mb",  "VO_0010");
//        stringStringHashMap.put("ABSV_20mb",  "VO_0020");
//        stringStringHashMap.put("ABSV_30mb",  "VO_0030");
//        stringStringHashMap.put("ABSV_50mb",  "VO_0050");
//        stringStringHashMap.put("ABSV_70mb",  "VO_0070");
//        stringStringHashMap.put("ABSV_150mb", "VO_0150");
//        stringStringHashMap.put("ABSV_250mb", "VO_0250");
//        stringStringHashMap.put("ABSV_350mb", "VO_0350");
//        stringStringHashMap.put("ABSV_450mb", "VO_0450");
//        stringStringHashMap.put("ABSV_550mb", "VO_0550");
//        stringStringHashMap.put("ABSV_600mb", "VO_0600");
//        stringStringHashMap.put("ABSV_650mb", "VO_0650");
//        stringStringHashMap.put("ABSV_750mb", "VO_0750");
//        stringStringHashMap.put("ABSV_800mb", "VO_0800");
//        stringStringHashMap.put("ABSV_900mb", "VO_0900");
//        stringStringHashMap.put("ABSV_925mb", "VO_0925");
//        stringStringHashMap.put("ABSV_950mb", "VO_0950");
//        stringStringHashMap.put("ABSV_975mb", "VO_0975");
//
////      抬升指数
////      4LFTX	surface
////      stringStringHashMap.put("4LFTX_surface","LI_9999");
////      地面抬升指数
////      LFTX	surface
////      stringStringHashMap.put("LFTX_surface","SLI_9999");
////      对流有效位能
////      CAPE	surface
////      CAPE	180-0 mb above ground
////      CAPE	255-0 mb above ground
//        stringStringHashMap.put("CAPE_surface","CAPE_9999");
//        stringStringHashMap.put("CAPE_180-0mbaboveground","CAPE_0180");
//        stringStringHashMap.put("CAPE_255-0mbaboveground","CAPE_0255");
//
////      对流一直能
////      CIN	surface
////      CIN	180-0 mb above ground
////      CIN	255-0 mb above ground
//        stringStringHashMap.put("CIN_surface","CIN_9999");
//        stringStringHashMap.put("CIN_180-0mbaboveground","CIN_0180");
//        stringStringHashMap.put("CIN_255-0mbaboveground","CIN_0255");
////      垂直风切边
////      VWSH	tropopause
////      VWSH	PV=2e-06 (Km^2/kg/s) surface
////      VWSH	PV=-2e-06 (Km^2/kg/s) surface
////        stringStringHashMap.put("VWSH_tropopause","VWSH_tropopause");
////        stringStringHashMap.put("VWSH_PV=2e-06(Km^2/kg/s)surface","VWSH_0180");
////        stringStringHashMap.put("VWSH_PV=-2e-06(Km^2/kg/s)surface","VWSH_0255");
//
//        return stringStringHashMap;
//    }
}
