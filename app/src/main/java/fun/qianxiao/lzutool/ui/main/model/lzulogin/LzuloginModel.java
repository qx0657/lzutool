package fun.qianxiao.lzutool.ui.main.model.lzulogin;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import androidx.fragment.app.FragmentActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.MapUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import fun.qianxiao.lzutool.bean.User;
import fun.qianxiao.lzutool.utils.HttpConnectionUtil;
import fun.qianxiao.lzutool.utils.MyCookieUtils;
import fun.qianxiao.lzutool.utils.MySpUtils;
import fun.qianxiao.lzutool.utils.MyVolleyManager;

/**
 * LZU登录
 * Create by QianXiao
 * On 2020/9/27
 */
public class LzuloginModel {
    public Context context;
    private LoginDialogFragment loginDialogFragment;
    private LoginCallback loginCallback;

    public LzuloginModel(Context context, LoginCallback loginCallback) {
        this.context = context;
        this.loginCallback = loginCallback;
    }

    public interface LoginCallback{
        void onLogining();
        void onLoginFail(String error);
        void onLoginSuccess(User user);
    }

    /**
     * 兰州大学App登录
     * @param uid
     * @param pwd
     */
    public void login(String uid,String pwd){
        LogUtils.i("正在登录："+uid);
        loginCallback.onLogining();
        try{
            JsonObjectRequest loginJsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    "https://appservice.lzu.edu.cn/api/eusp-unify-terminal/app-user/login",
                    new JSONObject()
                            .put("app_os",2)
                            .put("name",uid)
                            .put("pwd",pwd),
                    response -> {
                        LogUtils.i(response);
                        int code = response.optInt("code",-1);
                        switch (code){
                            case 1:
                                //登录成功
                                if(loginDialogFragment != null){
                                    loginDialogFragment.dismiss();
                                    MySpUtils.save("login_uid",uid);
                                    MySpUtils.save("login_pwd", pwd);
                                }
                                String login_token = Objects.requireNonNull(response.optJSONObject("data")).optString("login_token");
                                if(TextUtils.isEmpty(login_token)){
                                    loginCallback.onLoginFail("登陆失败（002）");
                                }else{
                                    //获取用户信息
                                    JsonObjectRequest getUserinfoJsonObjectRequest = new JsonObjectRequest(
                                            Request.Method.GET,
                                            "https://appservice.lzu.edu.cn/api/eusp-unify-terminal/app-user/userInfo?loginToken="+login_token,
                                            null,response1 -> {
                                                LogUtils.i(response1);
                                            if(response1.optInt("code")==1){
                                                JSONObject userInfo = response1.optJSONObject("data");
                                                assert userInfo != null;
                                                User user = new User();
                                                user.setCardid(userInfo.optString("xykh"));
                                                user.setMailpf(userInfo.optString("dzxx"));
                                                user.setName(userInfo.optString("xm"));
                                                user.setPhone(userInfo.optString("yddh"));
                                                user.setCollege(userInfo.optString("dwmc"));
                                                user.setMarjor(userInfo.optString("zymc"));
                                                user.setAccnum(userInfo.optString("etong_acc_no"));
                                                loginCallback.onLoginSuccess(user);
                                            }else{
                                                loginCallback.onLoginFail("获取用户信息失败");
                                            }
                                    },error -> loginCallback.onLoginFail(error.getMessage()));
                                    MyVolleyManager.getRequestQueue().add(getUserinfoJsonObjectRequest);
                                }

                                break;
                            case 0:
                                loginCallback.onLoginFail(response.optString("message"));
                                break;
                            case -1:
                                loginCallback.onLoginFail("未知错误（001）");
                                break;
                        }
                    }, error -> loginCallback.onLoginFail(error.getMessage()));
            MyVolleyManager.getRequestQueue().add(loginJsonObjectRequest);
        } catch (JSONException e) {
            LogUtils.e(e.toString());
            loginCallback.onLoginFail(e.getMessage());
        }

    }

    public void showLoginDialog(){
        if(loginDialogFragment == null){
            loginDialogFragment = new LoginDialogFragment(this)
                    .setOnLoginClickListener(LzuloginModel.this::login);

        }
        loginDialogFragment.show(((FragmentActivity)context).getSupportFragmentManager(),"LoginDialogFragment");
    }

    public interface LoginGetTGTCallBack{
        void onLoginGetTGTSuccess(String tgt);
        void onLoginGetTGTError(String error);
    }

    /**
     * 登录获取TGT
     * @param uid
     * @param pwd
     */
    public void loginGetTGT(String uid,String pwd,LoginGetTGTCallBack callBack){
        StringRequest loginGetTGTJsonObjectRequest = new StringRequest(
                Request.Method.POST,
                "https://appservice.lzu.edu.cn/api/lzu-cas/v1/tickets",
                response -> {
                    //LogUtils.i(response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String action = jsonObject.getString("action");
                        String[] strings = action.split("/");
                        String tgt = strings[strings.length-1];
                        callBack.onLoginGetTGTSuccess(tgt);
                    } catch (JSONException e) {
                        LogUtils.e(e.toString());
                        callBack.onLoginGetTGTError(e.getMessage());
                    }
                }, error -> callBack.onLoginGetTGTError(error.toString())){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("username", uid);
                map.put("password", pwd);
                return map;
            }
        };
        MyVolleyManager.getRequestQueue().add(loginGetTGTJsonObjectRequest);
    }

    public interface GetStByTGTCallBack{
        void onGetStByTGTSuccess(String st);
        void onGetStByTGTError(String error);
    }

    /**
     * 通过TGT获取st
     * @param tgt
     * @param callBack
     */
    public void getStByTGT(String tgt,GetStByTGTCallBack callBack){
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                "https://appservice.lzu.edu.cn/api/lzu-cas/v1/tickets/"+tgt,
                callBack::onGetStByTGTSuccess, error -> callBack.onGetStByTGTError(error.getMessage())){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("service","http://127.0.0.1");
                return map;
            }
        };
        MyVolleyManager.getRequestQueue().add(stringRequest);

    }

    public interface LoginZhxgGetJsessionidAndRouteCallBack{
        void onLoginZhxgGetJsessionidAndRouteSuccess(String zhxgcookies);
        void onLoginZhxgGetJsessionidAndRouteError(String error);
    }

    /**
     * tgt登录智慧学工获取Jsessionid、Route
     * @param tgt
     */
    public void loginZhxgGetJsessionidAndRoute(String tgt,LoginZhxgGetJsessionidAndRouteCallBack callBack){
        /*StringRequest loginZhxgGetJsessionidAndRouteJsonObjectRequest = new StringRequest(
                Request.Method.GET,
                "http://zhxg.lzu.edu.cn/lzuyz/sys/sysuser/loginPortal",
                response -> {
                }, error -> callBack.onLoginZhxgGetJsessionidAndRouteError(error.toString())){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //return super.getHeaders();
                Map<String, String> headers = super.getHeaders();
                if(headers == null || headers.equals(Collections.emptyMap())){
                    headers = new HashMap<>();
                }
                headers.put("Cookie","iPlanetDirectoryPro="+tgt);
                return headers;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                List<Header> headers = response.allHeaders;
                StringBuilder sb = new StringBuilder();
                for (Header header : headers) {
                    if(header.getName().equals("Set-Cookie")){
                        //LogUtils.i(header.getName()+":"+header.getValue());
                        String setcookie = header.getValue();
                        setcookie = setcookie.substring(0, !setcookie.contains(" Path=") ?0:setcookie.indexOf(" Path="));
                        sb.append(setcookie);
                    }
                }
                LogUtils.i(sb.toString());
                return super.parseNetworkResponse(response);

            }
        };
        MyVolleyManager.getRequestQueue().add(loginZhxgGetJsessionidAndRouteJsonObjectRequest);*/
        //volley不能禁止302，改用HttpURLConnection携带tgt的cookie302请求获取响应cookie(Jsessionid、Route)
        String cookies = HttpConnectionUtil.getHttp().request302getResponseCookie("http://zhxg.lzu.edu.cn/lzuyz/sys/sysuser/loginPortal",
                "iPlanetDirectoryPro="+tgt);
        if(!TextUtils.isEmpty(cookies)){
            callBack.onLoginZhxgGetJsessionidAndRouteSuccess(cookies);
        }else{
            callBack.onLoginZhxgGetJsessionidAndRouteError("智慧学工登录失败");
        }
    }

    public interface LoginEcardGetSidCallBack{
        void onLoginEcardGetSidSuccess(Map<String,String> ecardcookie);
        void onLoginEcardGetSidError(String error);
    }

    /**
     * tgt登录智慧一卡通获取sid
     * @param tgt
     * @param callBack
     */
    public void loginEcardGetSid(String tgt,LoginEcardGetSidCallBack callBack){
        String cookies = HttpConnectionUtil.getHttp().request302getResponseCookie(
                "https://ecard.lzu.edu.cn/lzulogin",
                "iPlanetDirectoryPro="+tgt);
        if(!TextUtils.isEmpty(cookies)){
            callBack.onLoginEcardGetSidSuccess(MyCookieUtils.cookieStr2map(cookies));
        }else{
            callBack.onLoginEcardGetSidError("智慧一卡通登录失败");
        }
    }
}
