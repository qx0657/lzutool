package fun.qianxiao.lzutool.ui.main.model.mail;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.blankj.utilcode.util.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import fun.qianxiao.lzutool.utils.MyCookieUtils;
import fun.qianxiao.lzutool.utils.MyVolleyManager;

/**
 * 暂做测试使用
 * Create by QianXiao
 * On 2020/10/11
 */
public class BaseMailModel {

    public interface MBoxListMessagesCallBack{
        void onMBoxListMessagesSuccess();
        void onMBoxListMessagesError(String error);
    }

    /**
     * 获取收件箱邮件
     * @param mail_cookie
     */
    public void mBoxListMessages(String mail_cookie,int start,MBoxListMessagesCallBack callBack){
        Map<String,String> map = MyCookieUtils.cookieStr2map(mail_cookie);
        String sid = map.get("Coremail.sid");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("start",start);
            jsonObject.put("limit",20);
            jsonObject.put("mode","count");
            jsonObject.put("order","receivedDate");
            jsonObject.put("desc",true);
            jsonObject.put("returnTotal",true);
            jsonObject.put("summaryWindowSize",20);
            jsonObject.put("fid",1);
            jsonObject.put("topFirst",true);

        } catch (JSONException ignored) {
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                "https://mail.lzu.edu.cn/coremail/s/json?sid="+sid+"&func=mbox:listMessages",
                jsonObject,
                response -> {
                    if(response.optString("code").equals("S_OK")){
                        JSONArray lists = response.optJSONArray("var");
                        //……
                        callBack.onMBoxListMessagesSuccess();
                    }else{
                        LogUtils.i(response);
                        callBack.onMBoxListMessagesError("邮件列表获取失败");
                    }
                },error -> callBack.onMBoxListMessagesError(error.getMessage())){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();
                if(headers == null || headers.equals(Collections.emptyMap())){
                    headers = new HashMap<>();
                }
                //headers.put("Accept","text/x-json");
                //headers.put("Content-Type","text/x-json");
                headers.put("Cookie",mail_cookie);
                //headers.put("Referer","https://mail.lzu.edu.cn/coremail/XT5/index.jsp?sid="+sid);
                //headers.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:81.0) Gecko/20100101 Firefox/81.0");
                return headers;
            }

        };
        MyVolleyManager.getRequestQueue().add(jsonObjectRequest);
    }
}
