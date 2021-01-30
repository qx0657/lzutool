package fun.qianxiao.lzutool.ui.main.model.healthpunchcloudtrusteeship;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import fun.qianxiao.lzutool.utils.MyVolleyManager;

/**
 * 健康打卡云托管
 * Create by QianXiao
 * On 2020/10/3
 */
public class CloudTrusteeshipModel {
    public interface TrusteeshipSystemLoginCallBack{
        void onTrusteeshipSystemLoginSuccess(String token, boolean ts);
        void onTrusteeshipSystemLoginError(String error);
    }

    /**
     * 云托管系统登录
     * @param uid
     * @param pwd
     * @param callBack
     */
    public void TrusteeshipSystemLogin(String uid,String pwd,TrusteeshipSystemLoginCallBack callBack){
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                "https://mrsb.qianxiao.fun/api/login.php",
                response -> {
                    LogUtils.i(response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.optInt("code")==1){
                            String token = jsonObject.optJSONObject("data").optString("token");
                            callBack.onTrusteeshipSystemLoginSuccess(token,jsonObject.optJSONObject("data").optString("ts").equals("1"));
                        }else{
                            callBack.onTrusteeshipSystemLoginError(jsonObject.optString("msg"));
                        }
                    } catch (JSONException e) {
                        callBack.onTrusteeshipSystemLoginError(e.getMessage());
                    }

                },error -> {callBack.onTrusteeshipSystemLoginError(error.getMessage());}){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("uid",uid);
                map.put("pwd",pwd);
                map.put("tsp",String.valueOf(System.currentTimeMillis()/1000));
                map.put("sign", EncryptUtils.encryptMD5ToString("uid="+uid+"&pwd="+pwd+"&tsp="+
                        map.get("tsp")+
                        "qianxiao"));
                return map;
            }
        };
        MyVolleyManager.getRequestQueue().add(stringRequest);
    }

    public interface TrusteeshipOperationCallBack{
        void onTrusteeshipOperationSuccess(String res);
        void onTrusteeshipOperationError(String error);
    }

    /**
     * 云托管提交
     * @param token
     * @param email
     * @param callBack
     */
    public void trusteeshipSubmit(String token,String email,TrusteeshipOperationCallBack callBack){
        StringRequest stringRequest1 = new StringRequest(Request.Method.POST,
                "https://mrsb.qianxiao.fun/api/trusteeShip.php",
                response1 -> {
                    LogUtils.i(response1);
                    try {
                        JSONObject jsonObject1 = new JSONObject(response1);
                        if(jsonObject1.optInt("code")==1){
                            callBack.onTrusteeshipOperationSuccess("托管提交成功");
                        }else{
                            callBack.onTrusteeshipOperationError(jsonObject1.optString("msg"));
                        }
                    } catch (JSONException e) {
                        callBack.onTrusteeshipOperationError(e.getMessage());
                    }
                },error -> callBack.onTrusteeshipOperationError(error.getMessage())){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("token",token);
                map.put("ts","1");
                map.put("email",email);
                map.put("tsp",String.valueOf(System.currentTimeMillis()/1000));
                map.put("sign", EncryptUtils.encryptMD5ToString("token="+token+"&ts="+map.get("ts")+"&email="+map.get("email")+"&tsp="+
                        map.get("tsp")+
                        "qianxiao"));
                return map;
            }
        };
        MyVolleyManager.getRequestQueue().add(stringRequest1);
    }

    /**
     * 取消托管
     * @param token
     * @param callBack
     */
    public void cancleTrusteeship(String token,TrusteeshipOperationCallBack callBack){
        StringRequest stringRequest1 = new StringRequest(Request.Method.POST,
                "https://mrsb.qianxiao.fun/api/trusteeShip.php",
                response1 -> {
                    LogUtils.i(response1);
                    try {
                        JSONObject jsonObject1 = new JSONObject(response1);
                        if(jsonObject1.optInt("code")==1){
                            callBack.onTrusteeshipOperationSuccess("云托管取消成功");
                        }else{
                            callBack.onTrusteeshipOperationError(jsonObject1.optString("msg"));
                        }
                    } catch (JSONException e) {
                        callBack.onTrusteeshipOperationError(e.getMessage());
                    }
                },error -> callBack.onTrusteeshipOperationError(error.getMessage())){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("token",token);
                map.put("ts","0");
                map.put("tsp",String.valueOf(System.currentTimeMillis()/1000));
                map.put("sign", EncryptUtils.encryptMD5ToString("token="+token+"&ts="+map.get("ts")+"&tsp="+
                        map.get("tsp")+
                        "qianxiao"));
                return map;
            }
        };
        MyVolleyManager.getRequestQueue().add(stringRequest1);
    }
}
