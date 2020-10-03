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
    public interface TrusteeshipOperationCallBack{
        void onTrusteeshipOperationSuccess(String res);
        void onTrusteeshipOperationError(String error);
    }

    /**
     * 云托管提交
     * @param uid
     * @param pwd
     * @param email
     * @param callBack
     */
    public void trusteeshipSubmit(String uid,String pwd,String email,TrusteeshipOperationCallBack callBack){
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                "http://mrsb.qianxiao.fun/addonedata.php",
                response -> {
                    LogUtils.i(response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.optInt("code")==1){
                            callBack.onTrusteeshipOperationSuccess("托管提交成功");
                        }else{
                            callBack.onTrusteeshipOperationError(jsonObject.optString("message"));
                        }
                    } catch (JSONException e) {
                        callBack.onTrusteeshipOperationError(e.getMessage());
                    }

                },error -> {callBack.onTrusteeshipOperationError(error.getMessage());}){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("username",uid);
                map.put("password",pwd);
                map.put("email",email);
                map.put("timestamp",String.valueOf(System.currentTimeMillis()/1000));
                map.put("parsign", EncryptUtils.encryptSHA1ToString("username="+uid+"&password="+pwd+"&email="+email+"&timestamp="+
                        map.get("timestamp")+
                        "qianxiao"));
                return map;
            }
        };
        MyVolleyManager.getRequestQueue().add(stringRequest);
    }

    /**
     * 取消托管
     * @param uid
     * @param pwd
     * @param email
     * @param callBack
     */
    public void cancleTrusteeship(String uid,String pwd,String email,TrusteeshipOperationCallBack callBack){
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                "http://mrsb.qianxiao.fun/deletedata.php",
                response -> {
                    LogUtils.i(response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.optInt("code")==1){
                            callBack.onTrusteeshipOperationSuccess("取消托管成功");
                        }else{
                            callBack.onTrusteeshipOperationError(jsonObject.optString("message"));
                        }
                    } catch (JSONException e) {
                        callBack.onTrusteeshipOperationError(e.getMessage());
                    }
                },error -> {callBack.onTrusteeshipOperationError(error.getMessage());}){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("username",uid);
                map.put("password",pwd);
                map.put("email",email);
                map.put("timestamp",String.valueOf(System.currentTimeMillis()/1000));
                map.put("parsign", EncryptUtils.encryptSHA1ToString("username="+uid+"&password="+pwd+"&email="+email+"&timestamp="+
                        map.get("timestamp")+
                        "qianxiao"));
                return map;
            }
        };
        MyVolleyManager.getRequestQueue().add(stringRequest);
    }
}
