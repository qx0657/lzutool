package fun.qianxiao.lzutool.utils;

import org.json.JSONObject;
import fr.arnaudguyon.xmltojsonlib.XmlToJson;


/**
 * Create by QianXiao
 * On 2020/9/29
 */
public class Xml2JsonUtils {

    public static JSONObject xml2json(String xml){
        XmlToJson xmlToJson = new XmlToJson
                .Builder(xml)
                .build();
        return xmlToJson.toJson();
    }
}
