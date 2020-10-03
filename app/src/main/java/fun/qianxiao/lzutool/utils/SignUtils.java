package fun.qianxiao.lzutool.utils;

import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.LogUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * Create by QianXiao
 * On 2020/9/28
 */
public class SignUtils {
    public static String sign(Map<String,String> map){
        ArrayList<String> arrayList = new ArrayList<>(map.keySet());
        Collections.sort(arrayList);
        StringBuilder sb = new StringBuilder();
        for (String key : arrayList) {
            sb.append(map.get(key));
            sb.append("|");
        }
        sb.append("ok15we1@oid8x5afd@");
        return EncryptUtils.encryptMD5ToString(sb.toString()).toLowerCase();
    }
}
