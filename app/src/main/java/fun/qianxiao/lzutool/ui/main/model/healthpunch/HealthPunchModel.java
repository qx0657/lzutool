package fun.qianxiao.lzutool.ui.main.model.healthpunch;

import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.blankj.utilcode.util.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import fun.qianxiao.lzutool.utils.MySpUtils;
import fun.qianxiao.lzutool.utils.MyVolleyManager;
import io.reactivex.Observer;

/**
 * 健康打卡
 * Create by QianXiao
 * On 2020/9/30
 */
public class HealthPunchModel {

    interface GetAccessTokenCallBack{
        void onGetAccessTokenSuccess(String accesstoken);
        void onGetAccessTokenError(String error);
    }

    /**
     * st获取健康打卡AccessToken
     * @param st
     * @param cardid
     * @param callBack
     */
    private void getAccessToken(String st,String cardid,GetAccessTokenCallBack callBack){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("http://appservice.lzu.edu.cn/dailyReportAll/api/auth/login?st="+st+"&PersonID="+cardid,
                null,response -> {
            //LogUtils.i(response);
            if(response.optInt("code")==1){
                String accesstoken = Objects.requireNonNull(response.optJSONObject("data")).optString("accessToken");
                callBack.onGetAccessTokenSuccess(accesstoken);
            }else{
                callBack.onGetAccessTokenSuccess(response.optString("message"));
            }

        },error -> callBack.onGetAccessTokenError(error.getMessage()));
        MyVolleyManager.getRequestQueue().add(jsonObjectRequest);
    }

    interface GetMd5CallBack{
        void onGetMd5Success(String md5);
        void onGetMd5Error(String error);
    }

    /**
     * 获取用户md5
     * @param cardid
     * @param callBack
     */
    private void getMd5(String accesstoken,String cardid,GetMd5CallBack callBack){
        String cardid_md5 = MySpUtils.getString("cardid_md5");
        if(!TextUtils.isEmpty(cardid_md5)){
            callBack.onGetMd5Success(cardid_md5);
            return;
        }
        StringRequest objectRequest = new StringRequest(
                Request.Method.POST,
                "http://appservice.lzu.edu.cn/dailyReportAll/api/encryption/getMD5",
                response -> {
                    //LogUtils.i(response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.optInt("code")==1){
                            String md5 = jsonObject.optString("data");
                            MySpUtils.save("cardid_md5",md5);
                            callBack.onGetMd5Success(md5);
                        }else{
                            callBack.onGetMd5Error(jsonObject.optString("message"));
                        }
                    } catch (JSONException e) {
                        LogUtils.e(e.toString());
                        callBack.onGetMd5Error(e.getMessage());
                    }

        },error -> callBack.onGetMd5Error(error.getMessage())){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("cardId",cardid);
                return map;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();
                if(headers == null || headers.equals(Collections.emptyMap())){
                    headers = new HashMap<>();
                }
                headers.put("Authorization",accesstoken);
                return headers;
            }
        };
        MyVolleyManager.getRequestQueue().add(objectRequest);
    }

    interface GetInfoCallBack{
        void onGetInfoSuccess(JSONObject jsonObject);
        void onGetInfoError(String error);
    }

    /**
     * 获取上次打卡信息
     * @param accesstoken
     * @param cardid
     * @param md5
     * @param callBack
     */
    private void getInfo(String accesstoken,String cardid,String md5,GetInfoCallBack callBack){
        StringRequest objectRequest = new StringRequest(
                Request.Method.POST,
                "http://appservice.lzu.edu.cn/dailyReportAll/api/grtbMrsb/getInfo",
                response -> {
                    //LogUtils.i(response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.optInt("code")==1){
                            callBack.onGetInfoSuccess(Objects.requireNonNull(Objects.requireNonNull(jsonObject.optJSONObject("data")).optJSONArray("list")).optJSONObject(0));
                        }else{
                            callBack.onGetInfoError(jsonObject.optString("message"));
                        }
                    } catch (JSONException e) {
                        LogUtils.e(e.toString());
                        callBack.onGetInfoError(e.getMessage());
                    }

                },error -> callBack.onGetInfoError(error.getMessage())){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("cardId",cardid);
                map.put("md5",md5);
                return map;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();
                if(headers == null || headers.equals(Collections.emptyMap())){
                    headers = new HashMap<>();
                }
                headers.put("Authorization",accesstoken);
                return headers;
            }
        };
        MyVolleyManager.getRequestQueue().add(objectRequest);
    }

    interface SubmitInfoCallBack{
        void onSubmitInfoSuccess();
        void onSubmitInfoError(String error);
    }

    /**
     * 打卡信息提交
     * @param accesstoken
     * @param submitinfo
     * @param callBack
     */
    private void submitInfo(String accesstoken,JSONObject submitinfo,SubmitInfoCallBack callBack){
        StringRequest objectRequest = new StringRequest(
                Request.Method.POST,
                "http://appservice.lzu.edu.cn/dailyReportAll/api/grtbMrsb/submit",
                response -> {
                    //LogUtils.i(response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.optInt("code")==1){
                            callBack.onSubmitInfoSuccess();
                        }else{
                            callBack.onSubmitInfoError(jsonObject.optString("message"));
                        }
                    } catch (JSONException e) {
                        LogUtils.e(e.toString());
                        callBack.onSubmitInfoError(e.getMessage());
                    }

                },error -> callBack.onSubmitInfoError(error.getMessage())){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                Iterator<String> iterator = submitinfo.keys();
                while (iterator.hasNext()){
                    String key = iterator.next();
                    try {
                        map.put(key,submitinfo.getString(key));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return map;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();
                if(headers == null || headers.equals(Collections.emptyMap())){
                    headers = new HashMap<>();
                }
                headers.put("Authorization",accesstoken);
                return headers;
            }
        };
        MyVolleyManager.getRequestQueue().add(objectRequest);
    }

    public interface GetPunchStatusCallBack{
        void onGetPunchStatusSuccess(boolean dkzt);
        void onGetPunchStatusError(String error);
    }

    /**
     * 获取打卡状态
     */
    public void getPunchStatus(String st, String cardid, GetPunchStatusCallBack callBack){
        getAccessToken(st, cardid, new GetAccessTokenCallBack() {
            @Override
            public void onGetAccessTokenSuccess(String accesstoken) {
                MySpUtils.save("healthpunch_accesstoken",accesstoken);
                getMd5(accesstoken, cardid, new GetMd5CallBack() {
                    @Override
                    public void onGetMd5Success(String md5) {
                        StringRequest objectRequest = new StringRequest(
                                Request.Method.POST,
                                "http://appservice.lzu.edu.cn/dailyReportAll/api/grtbMrsb/getInfo",
                                response -> {
                                    //LogUtils.i(response);
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        if(jsonObject.optInt("code")==1){
                                            JSONObject info = Objects.requireNonNull(Objects.requireNonNull(jsonObject.optJSONObject("data")).optJSONArray("list")).optJSONObject(0);
                                            MySpUtils.save("healthpunch_info",info.toString());
                                            boolean sbzt = info.optString("sbzt").equals("1");
                                            callBack.onGetPunchStatusSuccess(sbzt);
                                        }else{
                                            callBack.onGetPunchStatusError(jsonObject.optString("message"));
                                        }
                                    } catch (JSONException e) {
                                        LogUtils.e(e.toString());
                                        callBack.onGetPunchStatusError(e.getMessage());
                                    }

                                },error -> callBack.onGetPunchStatusError(error.getMessage())){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> map = new HashMap<>();
                                map.put("cardId",cardid);
                                map.put("md5",md5);
                                return map;
                            }

                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> headers = super.getHeaders();
                                if(headers == null || headers.equals(Collections.emptyMap())){
                                    headers = new HashMap<>();
                                }
                                headers.put("Authorization",accesstoken);
                                return headers;
                            }
                        };
                        MyVolleyManager.getRequestQueue().add(objectRequest);
                    }

                    @Override
                    public void onGetMd5Error(String error) {
                        callBack.onGetPunchStatusError(error);
                    }
                });
            }

            @Override
            public void onGetAccessTokenError(String error) {

            }
        });
    }

    public interface HealthPunchCallBack{
        void onHealthPunchSuccess();
        void onHealthPunchError(String error);
    }

    /**
     * 直接打卡
     */
    public void punch(HealthPunchCallBack callBack){
        JSONObject submitJSONObject = new JSONObject();
        try {
            JSONObject jsonObject = new JSONObject(MySpUtils.getString("healthpunch_info"));
            submitJSONObject.put("bh",getValue(jsonObject,"bh"));
            submitJSONObject.put("xykh",getValue(jsonObject,"xykh"));
            submitJSONObject.put("twfw",getValue(jsonObject,"twfw"));
            submitJSONObject.put("sfzx",getValue(jsonObject,"sfzx"));
            submitJSONObject.put("sfgl",getValue(jsonObject,"sfgl"));
            submitJSONObject.put("szsf",getValue(jsonObject,"szsf"));
            submitJSONObject.put("szds",getValue(jsonObject,"szds"));
            submitJSONObject.put("szxq",getValue(jsonObject,"szxq"));
            submitJSONObject.put("sfcg",getValue(jsonObject,"sfcg"));
            submitJSONObject.put("cgdd",getValue(jsonObject,"cgdd"));
            submitJSONObject.put("gldd",getValue(jsonObject,"gldd"));
            submitJSONObject.put("jzyy",getValue(jsonObject,"jzyy"));
            submitJSONObject.put("bllb",getValue(jsonObject,"bllb"));
            submitJSONObject.put("sfjctr",getValue(jsonObject,"sfjctr"));
            submitJSONObject.put("jcrysm",getValue(jsonObject,"jcrysm"));
            submitJSONObject.put("xgjcjlsj",getValue(jsonObject,"xgjcjlsj"));
            submitJSONObject.put("xgjcjldd",getValue(jsonObject,"xgjcjldd"));
            submitJSONObject.put("xgjcjlsm",getValue(jsonObject,"xgjcjlsm"));
            submitJSONObject.put("zcwd",getValue(jsonObject,"zcwd"));
            submitJSONObject.put("zwwd",getValue(jsonObject,"zwwd"));
            submitJSONObject.put("wswd",getValue(jsonObject,"wswd"));
            submitJSONObject.put("sbr",getValue(jsonObject,"sbr"));
            submitJSONObject.put("sjd",getValue(jsonObject,"sjd"));
            submitInfo(MySpUtils.getString("healthpunch_accesstoken"),submitJSONObject , new SubmitInfoCallBack() {
                @Override
                public void onSubmitInfoSuccess() {
                    callBack.onHealthPunchSuccess();
                }

                @Override
                public void onSubmitInfoError(String error) {
                    callBack.onHealthPunchError(error);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 健康打卡
     * （总逻辑控制）
     * @param st st
     * @param cardid 校园卡号
     * @param callBack
     */
    public void punch(String st,String cardid,HealthPunchCallBack callBack){
        getAccessToken(st, cardid, new GetAccessTokenCallBack() {
            @Override
            public void onGetAccessTokenSuccess(String accesstoken) {
                LogUtils.i(accesstoken);
                getMd5(accesstoken,cardid, new GetMd5CallBack() {
                    @Override
                    public void onGetMd5Success(String md5) {
                        LogUtils.i(md5);
                        getInfo(accesstoken, cardid, md5, new GetInfoCallBack() {
                            @Override
                            public void onGetInfoSuccess(JSONObject jsonObject) {
                                //LogUtils.i(jsonObject);
                                JSONObject submitJSONObject = new JSONObject();
                                try {
                                    submitJSONObject.put("bh",getValue(jsonObject,"bh"));
                                    submitJSONObject.put("xykh",getValue(jsonObject,"xykh"));
                                    submitJSONObject.put("twfw",getValue(jsonObject,"twfw"));
                                    submitJSONObject.put("sfzx",getValue(jsonObject,"sfzx"));
                                    submitJSONObject.put("sfgl",getValue(jsonObject,"sfgl"));
                                    submitJSONObject.put("szsf",getValue(jsonObject,"szsf"));
                                    submitJSONObject.put("szds",getValue(jsonObject,"szds"));
                                    submitJSONObject.put("szxq",getValue(jsonObject,"szxq"));
                                    submitJSONObject.put("sfcg",getValue(jsonObject,"sfcg"));
                                    submitJSONObject.put("cgdd",getValue(jsonObject,"cgdd"));
                                    submitJSONObject.put("gldd",getValue(jsonObject,"gldd"));
                                    submitJSONObject.put("jzyy",getValue(jsonObject,"jzyy"));
                                    submitJSONObject.put("bllb",getValue(jsonObject,"bllb"));
                                    submitJSONObject.put("sfjctr",getValue(jsonObject,"sfjctr"));
                                    submitJSONObject.put("jcrysm",getValue(jsonObject,"jcrysm"));
                                    submitJSONObject.put("xgjcjlsj",getValue(jsonObject,"xgjcjlsj"));
                                    submitJSONObject.put("xgjcjldd",getValue(jsonObject,"xgjcjldd"));
                                    submitJSONObject.put("xgjcjlsm",getValue(jsonObject,"xgjcjlsm"));
                                    submitJSONObject.put("zcwd",getValue(jsonObject,"zcwd"));
                                    submitJSONObject.put("zwwd",getValue(jsonObject,"zwwd"));
                                    submitJSONObject.put("wswd",getValue(jsonObject,"wswd"));
                                    submitJSONObject.put("sbr",getValue(jsonObject,"sbr"));
                                    submitJSONObject.put("sjd",getValue(jsonObject,"sjd"));
                                    submitInfo(accesstoken,submitJSONObject , new SubmitInfoCallBack() {
                                        @Override
                                        public void onSubmitInfoSuccess() {
                                            callBack.onHealthPunchSuccess();
                                        }

                                        @Override
                                        public void onSubmitInfoError(String error) {
                                            callBack.onHealthPunchError(error);
                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onGetInfoError(String error) {
                                callBack.onHealthPunchError(error);
                            }
                        });
                    }

                    @Override
                    public void onGetMd5Error(String error) {
                        callBack.onHealthPunchError(error);
                    }
                });
            }

            @Override
            public void onGetAccessTokenError(String error) {
                callBack.onHealthPunchError(error);
            }
        });
    }

    private String getValue(JSONObject jsonObject,String key) {
        if(jsonObject.isNull(key)||TextUtils.isEmpty(jsonObject.optString(key))||jsonObject.optString(key).equals("None")){
            if(key.equals("zcwd")||key.equals("zwwd")||key.equals("wswd")){
                return "0.0";
            }else{
                return "";
            }
        }else{
            return jsonObject.optString(key);
        }
    }
}
