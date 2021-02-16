package fun.qianxiao.lzutool.ui.mail.model;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.TimeUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fun.qianxiao.lzutool.ui.mail.bean.MailInfo;
import fun.qianxiao.lzutool.ui.mail.inbox.InBoxViewModel;
import fun.qianxiao.lzutool.utils.MyCookieUtils;
import fun.qianxiao.lzutool.utils.MyVolleyManager;

/**
 * 暂做测试使用
 * Create by QianXiao
 * On 2020/10/11
 */
public class LzuMailModel {
    private String mail_cookie;

    public LzuMailModel(String mail_cookie) {
        this.mail_cookie = mail_cookie;
    }

    public interface MBoxListMessagesCallBack{
        void onMBoxListMessagesSuccess(List<MailInfo> mailInfoList, boolean hasMore);
        void onMBoxListMessagesError(String error);
    }

    public enum MailType{
        /**
         * 收件箱
         */
        InBox,
        /**
         * 待办邮件
         */
        AgentMail,
        /**
         * 草稿箱
         */
        DraftsBox,
        /**
         * 已发送
         */
        HasSent,
        /**
         * 已删除
         */
        HasDeleted,
        /**
         * 垃圾邮件
         */
        JunkMail,
        /**
         * 病毒邮件
         */
        VirusMail
    }

    /**
     * 获取邮件
     * @param mailType
     * @param start
     * @param order
     * @param isDesc 是否降序
     * @param callBack
     */
    public void mBoxListMessages(MailType mailType, int start, InBoxViewModel.OrderType order, boolean isDesc, InBoxViewModel.FItterType fItter, MBoxListMessagesCallBack callBack){
        Map<String,String> map = MyCookieUtils.cookieStr2map(mail_cookie);
        String sid = map.get("Coremail.sid");
        JSONObject jsonObject = new JSONObject();
        try {
            switch (mailType){
                case InBox:
                    jsonObject.put("fid",1);
                    break;
                case DraftsBox:
                    jsonObject.put("fid",2);
                    break;
                case HasSent:
                    jsonObject.put("fid",3);
                    break;
                case HasDeleted:
                    jsonObject.put("fid",4);
                    break;
                case JunkMail:
                    jsonObject.put("fid",5);
                    break;
                case VirusMail:
                    jsonObject.put("fid",6);
                    break;
                default:
                    break;
            }
            switch (fItter){
                case Flag:
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("flagged",true);
                    jsonObject.put("filterFlags",jsonObject1);
                    break;
                case Reply:
                    JSONObject jsonObject2 = new JSONObject();
                    jsonObject2.put("draft",false);//非草稿
                    jsonObject2.put("replied",true);
                    jsonObject.put("filterFlags",jsonObject2);
                    break;
                case NoRead:
                    JSONObject jsonObject3 = new JSONObject();
                    jsonObject3.put("read",false);
                    jsonObject.put("filterFlags",jsonObject3);
                    break;
                case Agent:
                    JSONObject jsonObject4 = new JSONObject();
                    jsonObject4.put("deferHandle",true);
                    JSONObject jsonObject5 = new JSONObject();
                    jsonObject5.put("flags",jsonObject4);
                    jsonObject.put("filter",jsonObject5);
                    break;
                default:
                    break;
            }

            jsonObject.put("desc",isDesc);
            jsonObject.put("limit",20);
            jsonObject.put("mode","count");
            jsonObject.put("order",order);
            jsonObject.put("returnTotal",true);
            jsonObject.put("start",start);
            jsonObject.put("summaryWindowSize",20);
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
                        List<MailInfo> mailInfoList = new ArrayList<>();
                        for (int i = 0; i < lists.length(); i++) {
                            JSONObject jsonObject1 = lists.optJSONObject(i);
                            MailInfo mailInfo = new MailInfo();
                            mailInfo.setId(jsonObject1.optString("id"));
                            mailInfo.setSize(jsonObject1.optInt("size"));
                            mailInfo.setFrom(jsonObject1.optString("from"));
                            mailInfo.setTo(jsonObject1.optString("to"));
                            mailInfo.setSubject(jsonObject1.optString("subject"));
                            mailInfo.setSummary(jsonObject1.optString("summary").replace("\n"," "));
                            mailInfo.setSentDate(TimeUtils.string2Date(jsonObject1.optString("sentDate")));
                            mailInfo.setReceivedDate(TimeUtils.string2Date(jsonObject1.optString("receivedDate")));
                            mailInfo.setFlag_read(jsonObject1.optJSONObject("flags").optBoolean("read"));
                            mailInfo.setFlag_system(jsonObject1.optJSONObject("flags").optBoolean("system"));
                            mailInfo.setFlag_attached(jsonObject1.optJSONObject("flags").optBoolean("attached"));
                            mailInfo.setFlag_replied(jsonObject1.optJSONObject("flags").optBoolean("replied"));
                            mailInfo.setFlag_flagged(jsonObject1.optJSONObject("flags").optBoolean("flagged"));
                            mailInfo.setFlag_deferHandle(jsonObject1.optJSONObject("flags").optBoolean("deferHandle"));
                            mailInfo.setFlag_top(jsonObject1.optJSONObject("flags").optBoolean("top"));
                            mailInfo.setPriority(jsonObject1.optInt("priority"));
                            mailInfoList.add(mailInfo);
                        }
                        callBack.onMBoxListMessagesSuccess(mailInfoList,start+20  < response.optInt("total"));
                    }else{
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
                headers.put("Content-Type","text/x-json");//重要
                headers.put("Cookie",mail_cookie);
                //headers.put("Referer","https://mail.lzu.edu.cn/coremail/XT5/index.jsp?sid="+sid);
                //headers.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:81.0) Gecko/20100101 Firefox/81.0");
                return headers;
            }

        };
        MyVolleyManager.getRequestQueue().add(jsonObjectRequest);
    }

    public void mBoxListFitter(){

    }
}
