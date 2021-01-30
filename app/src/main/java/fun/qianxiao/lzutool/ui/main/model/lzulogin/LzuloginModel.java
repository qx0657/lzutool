package fun.qianxiao.lzutool.ui.main.model.lzulogin;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import androidx.fragment.app.FragmentActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Header;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.MapUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fun.qianxiao.lzutool.bean.User;
import fun.qianxiao.lzutool.ui.main.model.baseinfo.GetBaseInfoModel;
import fun.qianxiao.lzutool.utils.HttpConnectionUtil;
import fun.qianxiao.lzutool.utils.MyCookieUtils;
import fun.qianxiao.lzutool.utils.MyOkhttpUtils;
import fun.qianxiao.lzutool.utils.MySpUtils;
import fun.qianxiao.lzutool.utils.MyVolleyManager;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

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
                                                if(userInfo.isNull("dzxx")){
                                                    LogUtils.i("邮箱前缀为空");
                                                    new GetBaseInfoModel(context).getMailPf(userInfo.optString("xykh"), new Observer<String>() {
                                                        @Override
                                                        public void onSubscribe(@NonNull Disposable d) {

                                                        }

                                                        @Override
                                                        public void onNext(@NonNull String s) {
                                                            LogUtils.i(s);
                                                            user.setMailpf(s);
                                                            user.setName(userInfo.optString("xm"));
                                                            user.setPhone(userInfo.optString("yddh"));
                                                            user.setCollege(userInfo.optString("dwmc"));
                                                            if(!userInfo.isNull("zymc")){
                                                                user.setMarjor(userInfo.optString("zymc"));
                                                            }
                                                            user.setAccnum(userInfo.optString("etong_acc_no"));
                                                            loginCallback.onLoginSuccess(user);
                                                        }

                                                        @Override
                                                        public void onError(@NonNull Throwable e) {
                                                            loginCallback.onLoginFail(e.getMessage());
                                                        }

                                                        @Override
                                                        public void onComplete() {

                                                        }
                                                    });
                                                }else {
                                                    String mailpf = userInfo.optString("dzxx");
                                                    if(mailpf.contains("@")){
                                                        mailpf = mailpf.substring(0,mailpf.indexOf("@"));
                                                    }
                                                    user.setMailpf(mailpf);
                                                    user.setName(userInfo.optString("xm"));
                                                    user.setPhone(userInfo.optString("yddh"));
                                                    user.setCollege(userInfo.optString("dwmc"));
                                                    if(!userInfo.isNull("zymc")){
                                                        user.setMarjor(userInfo.optString("zymc"));
                                                    }
                                                    user.setAccnum(userInfo.optString("etong_acc_no"));
                                                    loginCallback.onLoginSuccess(user);
                                                }
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
     * tgt登录智慧学/研工获取Jsessionid、Route
     * @param tgt
     */
    public void loginZhxgGetJsessionidAndRoute(String tgt,boolean isYjs,LoginZhxgGetJsessionidAndRouteCallBack callBack){
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
        String cookies = HttpConnectionUtil.getHttp().request302getResponseCookie(
                isYjs?"http://yjsxg.lzu.edu.cn/lzuygb/sys/sysuser/loginPortal":
                        "http://zhxg.lzu.edu.cn/lzuyz/sys/sysuser/loginPortal",
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
            Map<String,String> ecardcookie = MyCookieUtils.cookieStr2map(cookies);
            ecardcookie.put("iPlanetDirectoryPro",tgt);
            callBack.onLoginEcardGetSidSuccess(ecardcookie);
        }else{
            callBack.onLoginEcardGetSidError("智慧一卡通登录失败");
        }
    }

    public interface LoginOACallBack{
        void onLoginOASuccess(String cookie_JSESSIONID);
        void onLoginOAErrot(String error);
    }

    /**
     * tgt登录OA系统
     * @param tgt
     * @param callBack
     */
    public void loginOA(String tgt,LoginOACallBack callBack){
        String cookies = HttpConnectionUtil.getHttp().request302getResponseCookie(
                "http://oa.lzu.edu.cn/jsoa/LDCheckUser.do",
                "iPlanetDirectoryPro="+tgt);
        if(!TextUtils.isEmpty(cookies)){
            callBack.onLoginOASuccess(cookies);
        }else{
            callBack.onLoginOAErrot("智慧一卡通登录失败");
        }
    }

    /**
     * 登录个人工作台
     * @param tgt
     * @param st
     * @param observer
     */
    public void loginMyLzu(String tgt, String st, Observer<String> observer){
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> emitter) throws Exception {
                Map<String,String> map = new HashMap<>();
                map.put("CASTGC",tgt);
                map.put("iPlanetDirectoryPro",tgt);
                new OkHttpClient.Builder()
                        .followRedirects(false)
                        .cookieJar(new CookieJar() {
                            @Override
                            public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
                                StringBuilder stringBuilder = new StringBuilder();
                                for (Cookie cookie : list) {
                                    stringBuilder.append(cookie.name());
                                    stringBuilder.append("=");
                                    stringBuilder.append(cookie.value());
                                    stringBuilder.append(";");
                                }
                                emitter.onNext(stringBuilder.toString());
                            }

                            @NotNull
                            @Override
                            public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
                                return new ArrayList<>();
                            }
                        })
                        .build().
                        newCall(new okhttp3.Request.Builder()
                            .addHeader("Cookie",MyCookieUtils.map2cookieStr(map))
                            .url("http://my.lzu.edu.cn/?ticket="+st)
                            .get()
                            .build())
                        .execute().body().string();

            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public interface LoginLzuMailCallBack{
        void onLoginLzuMailSuccess(String coolie_mail);
        void onLoginLzuMailError(String error);
    }

    public void loginLzuMail(String mylzucookie, LoginLzuMailCallBack callBack){
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                "http://my.lzu.edu.cn/getMailUrl?t="+System.currentTimeMillis(),
                response -> {
                    LogUtils.i(response);
                },
                error -> {

                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();
                if(headers == null || headers.equals(Collections.emptyMap())){
                    headers = new HashMap<>();
                }
                headers.put("Cookie",mylzucookie);
                return headers;
            }
        };
        MyVolleyManager.getRequestQueue().add(stringRequest);
    }

    /**
     * 登录兰大邮箱
     * @param callBack
     */
    public void loginLzuMail(String login_pid,String login_pwd,LoginLzuMailCallBack callBack){
        Map<String,String> map = new HashMap<>();
        map.put("face","undefined");
        map.put("locale","zh_CN");
        map.put("saveUsername","true");
        map.put("uid",login_pid+"@lzu.edu.cn");
        String pattern = "sid=([A-Za-z]{32})\"";
        Pattern r = Pattern.compile(pattern);
        StringRequest stringRequest1 = new StringRequest(
                Request.Method.POST,
                "https://mail.lzu.edu.cn/coremail/index.jsp?cus=1&sid=",
                response1 -> {
                    LogUtils.i(response1);
                    Matcher m = r.matcher(response1);
                    if(m.find()){
                        String newsid = m.group(1);
                        map.put("Coremail.sid",newsid);
                        callBack.onLoginLzuMailSuccess(MyCookieUtils.map2cookieStr(map));
                    }else{
                        callBack.onLoginLzuMailError("登录失败001（未找到sid）");
                    }
                },error -> callBack.onLoginLzuMailError("LoginCoremailError("+error.getMessage()+")")){
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                StringBuilder rsp_cookies = new StringBuilder();
                for (Header header : response.allHeaders) {
                    if(header.getName().equals("Set-Cookie")){
                        String setcookie = header.getValue();
                        setcookie = setcookie.substring(0, !setcookie.contains(" Path=") ?0:setcookie.indexOf(" Path="));
                        rsp_cookies.append(setcookie);
                    }
                }
                map.putAll(MyCookieUtils.cookieStr2map(rsp_cookies.toString()));
                return super.parseNetworkResponse(response);
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();
                if(headers == null || headers.equals(Collections.emptyMap())){
                    headers = new HashMap<>();
                }
                headers.put("Cookie",MyCookieUtils.map2cookieStr(map));
                //headers.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:81.0) Gecko/20100101 Firefox/81.0");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("locale", "zh_CN");
                map.put("nodetect", "false");
                map.put("destURL", "");
                map.put("supportLoginDevice", "true");
                map.put("accessToken", "");
                map.put("timestamp", "");
                map.put("signature", "");
                map.put("nonce", "");
                map.put("device", "{\"uuid\":\"webmail_windows\",\"imie\":\"webmail_windows\",\"friendlyName\":\"firefox+81\",\"model\":\"windows\",\"os\":\"windows\",\"osLanguage\":\"zh-CN\",\"deviceType\":\"Webmail\"}");
                map.put("supportDynamicPwd", "true");
                map.put("supportBind2FA", "true");
                map.put("authorizeDevice", "");
                map.put("loginType", "");
                map.put("uid", login_pid);
                map.put("password", login_pwd);
                map.put("action:login", "");
                return map;
            }
        };
        MyVolleyManager.getRequestQueue().add(stringRequest1);

    }
}
