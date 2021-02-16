package fun.qianxiao.lzutool.utils;

import android.widget.TextView;

import com.blankj.utilcode.constant.TimeConstants;
import com.blankj.utilcode.util.TimeUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * Create by QianXiao
 * On 2020/9/28
 */
public class MyTimeUtils {
    /**
     * 获取时间字符串 用于请求兰大部分API的时间参数
     * @return
     */
    public static String getNowString(){
        return TimeUtils.getNowString(TimeUtils.getSafeDateFormat("yyyyMMddHHmmss"));
    }

    public static String getAppropriateDisplayedTime(Date date){
        if(TimeUtils.isToday(date)){
            return (TimeUtils.isAm(date)?"上午":"下午")+TimeUtils.date2String(date, "HH:mm");
        }else if(isYesterday(date)){
            return "昨天";
        }else{
            return TimeUtils.date2String(date, "MM月dd日");
        }
    }

    public static boolean isYesterday(final Date date) {
        long millis = date.getTime();
        long wee = getWeeOfLastDay(-1);
        return millis >= wee && millis < wee + TimeConstants.DAY;
    }

    private static long getWeeOfLastDay(int i) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_MONTH,i);
        return cal.getTimeInMillis();
    }

}
