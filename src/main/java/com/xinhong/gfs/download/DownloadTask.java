package com.xinhong.gfs.download;

import com.xinhong.ftp.util.DownloadStatus;
import com.xinhong.util.ConfigCommon;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wingsby on 2017/8/14.
 */
public class DownloadTask implements Comparable<DownloadTask> {
    String remote;
    String local;
    String timeVTI;
    // 下载序号、下载起始时间、下载速度，下载字节数、错误代号、other
    List<DownInfo> downInfos;

    public DownloadTask() {
    }


    public DownloadTask(String remote, String local, String timeVTI, List<DownInfo> info) {
        this.remote = remote;
        this.local = local;
        this.downInfos = info;
        this.timeVTI = timeVTI;
    }



    public DownloadStatus getNewestStatus(){
        if(downInfos==null||(downInfos!=null&&downInfos.size()==0)){
            return null;
        }else{
            return downInfos.get(downInfos.size()-1).getDownstatus();
        }
    }


    public String getRemote() {
        return remote;
    }

    public void setRemote(String remote) {
        this.remote = remote;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public List<DownInfo> getDownInfos() {
        return downInfos;
    }

    public void setDownInfos(List<DownInfo> info) {
        this.downInfos = info;
    }


    public void addInfo(DownInfo info) {
        if (downInfos == null) {
            downInfos = new ArrayList<DownInfo>();
        }
        downInfos.add(info);
    }


    private long difHour() {
        String ymdh = timeVTI.substring(0, 10);
        String vti = timeVTI.substring(10);
        SimpleDateFormat sdf = new SimpleDateFormat(ConfigCommon.DATE_FORMAT_GFS_YMDH);
        long dif = 9999;
        long time = 0l;
        try {
            time = sdf.parse(ymdh).getTime();

            time = time + Integer.valueOf(vti) * 60 * 60 * 1000l;
            dif = Math.abs((System.currentTimeMillis() - time) / (60 * 60 * 1000) - 8+1);
            if(getRemote().contains("idx"))dif-=5;
            if(Integer.valueOf(vti)>24){
                dif+=12;
                if(Integer.valueOf(vti)>48) dif+=24;
            }
            //8 代表北京时与世界时之差
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dif;
    }


    @Override
    public int compareTo(DownloadTask o) {
        if (difHour() > o.difHour()) return 1;
        else if (difHour() < o.difHour()) return -1;
        else
            return 0;
    }

    class DownInfo {
        int id;
        String stime;
        String etime;
        float speed;
        long downLoadBytes;
        DownloadStatus downstatus;
        String comment;

        public DownInfo(int id, String stime, String etime, float speed, long downLoadBytes, DownloadStatus downstatus, String comment) {
            this.id = id;
            this.stime = stime;
            this.etime = etime;
            this.speed = speed;
            this.downLoadBytes = downLoadBytes;
            this.downstatus = downstatus;
            this.comment = comment;
        }

        public DownInfo() {

        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getStime() {
            return stime;
        }

        public void setStime(String stime) {
            this.stime = stime;
        }

        public String getEtime() {
            return etime;
        }

        public void setEtime(String etime) {
            this.etime = etime;
        }

        public float getSpeed() {
            return speed;
        }

        public void setSpeed(float speed) {
            this.speed = speed;
        }

        public long getDownLoadBytes() {
            return downLoadBytes;
        }

        public void setDownLoadBytes(long downLoadBytes) {
            this.downLoadBytes = downLoadBytes;
        }

        public DownloadStatus getDownstatus() {
            return downstatus;
        }

        public void setDownstatus(DownloadStatus downstatus) {
            this.downstatus = downstatus;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String toString() {
            String res = "";
            res += (id + ";");
            res += (stime + ";");
            res += (etime + ";");
            res += (speed + ";");
            res += (downLoadBytes + ";");
            res += (downstatus + ";");
            res += (comment );
            return res;
        }

    }

    public String getTimeVTI() {
        return timeVTI;
    }

    public void setTimeVTI(String timeVTI) {
        this.timeVTI = timeVTI;
    }

    public String getPersistentString() {
        String res = "";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("@head@");
        stringBuilder.append("\n");
        stringBuilder.append(remote==null?"":remote);
        stringBuilder.append("\n");
        stringBuilder.append(local==null?"":local);
        stringBuilder.append("\n");
        stringBuilder.append(timeVTI==null?"":timeVTI);
        stringBuilder.append("\n");
        stringBuilder.append("@info@\n");
        if(downInfos!=null) {
            for (DownInfo info : downInfos) {
                stringBuilder.append(info.toString()==null?"":info);
                stringBuilder.append("\n");
            }
        }
        stringBuilder.append("@end@\n");
        res = stringBuilder.toString();
        return res;
    }

    public void loadPersistentString(String str) {
        String[] strs = str.split("\n");
        if (strs == null) return ;
        if (strs.length < 6) return ;
        if (strs[0].startsWith("@head@")) {
            setRemote(strs[1]);
            setLocal(strs[2]);
            setTimeVTI(strs[3]);
            if(strs[4].startsWith("@info@")){
                List<DownInfo> infos=new ArrayList<>();
                for(int i=5;i<strs.length-1;i++){
                    String[] infostrs=strs[i].split(";");
                    DownInfo info=new DownInfo();
                    if(infostrs!=null&&infostrs.length==7){
                        info.setId(Integer.valueOf(infostrs[0]));
                        info.setStime(infostrs[1]);
                        info.setEtime(infostrs[2]);
                        info.setSpeed(Float.valueOf(infostrs[3]));
                        info.setDownLoadBytes(Long.valueOf(infostrs[4]));
                        info.setDownstatus(DownloadStatus.fromString(infostrs[5]));
                        info.setComment(infostrs[6]);
                    }
                }
                setDownInfos(infos);
            }

        }else return ;
        return ;
    }

    @Override
    public boolean equals(Object o){
        if(this==o)return true;
        else if(o instanceof DownloadTask){
            if(((DownloadTask) o).getRemote().equals(getRemote()))return true;
        }
        return false;
    }

}
