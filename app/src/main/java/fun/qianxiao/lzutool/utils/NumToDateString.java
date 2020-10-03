package fun.qianxiao.lzutool.utils;

/**
 * Create by QianXiao
 * On 2020/9/30
 * https://blog.csdn.net/qingquanyingyue/article/details/81135544
 */
public class NumToDateString {
    public static String getPatchedTimeStr(long timeNum) {
        long dayTime;
        long hourTime;
        long minTime;
        long secondTime;
        String day = "天";
        String hour = "小时";
        String min = "分钟";
        String second = "秒";
        String date = "";
        dayTime = timeNum / 86400;
        hourTime = (timeNum % 86400) / 3600;
        minTime = ((timeNum % 86400) % 3600) / 60;
        secondTime = ((timeNum % 86400) % 3600) % 60;
        if (dayTime > 0) {
            date = dayTime + day;
        }
        if (hourTime > 0) {
            date = date + hourTime + hour;
        }
        /*if (minTime > 0) {
            date = date + minTime + min;
        }
        if (minTime > 0) {
            date = date + secondTime + second;
        }*/
        return date;
    }
}
