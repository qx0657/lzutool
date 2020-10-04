package fun.qianxiao.lzutool.utils;

import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Create by QianXiao
 * On 2020/10/4
 */
public class MyCookieUtils {
    /**
     * cookie字符串转map
     * @param cookies
     * @return
     */
    public static Map<String,String> cookieStr2map(String cookies){
        Map<String,String> cookiesMap = new HashMap<>();
        cookies = cookies.trim();
        if(cookies.endsWith(";")){
            cookies = cookies.substring(0,cookies.length()-1);
        }
        for (String s : cookies.split(";")) {
            if(s.contains("=")){
                s = s.trim();
                String key = s.substring(0,s.indexOf("="));
                String value = s.substring(s.indexOf("=")+1);
                cookiesMap.put(key,value);
            }
        }
        return cookiesMap;
    }

    /**
     * map转cookie字符串
     * @param cookiesMap
     * @return
     */
    public static String map2cookieStr(Map<String,String> cookiesMap){
        StringBuilder sb = new StringBuilder();
        Iterator<String> iterator = cookiesMap.keySet().iterator();
        while (iterator.hasNext()){
            String key = iterator.next();
            sb.append(key+"="+cookiesMap.get(key)+";");
        }
        return sb.toString();
    }
}
