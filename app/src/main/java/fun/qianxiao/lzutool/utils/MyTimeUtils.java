package fun.qianxiao.lzutool.utils;

import com.blankj.utilcode.util.TimeUtils;

/**
 * Create by QianXiao
 * On 2020/9/28
 */
public class MyTimeUtils {
    public static String getNowString(){
        return TimeUtils.getNowString(TimeUtils.getSafeDateFormat("yyyMMddHHmmss"));
    }
}
