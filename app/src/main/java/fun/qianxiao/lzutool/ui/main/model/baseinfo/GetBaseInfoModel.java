package fun.qianxiao.lzutool.ui.main.model.baseinfo;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.TimeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import fun.qianxiao.lzutool.bean.DormInfo;
import fun.qianxiao.lzutool.bean.SchoolNetInfo;
import fun.qianxiao.lzutool.utils.MyTimeUtils;
import fun.qianxiao.lzutool.utils.MyVolleyManager;
import fun.qianxiao.lzutool.utils.SignUtils;
import fun.qianxiao.lzutool.utils.Xml2JsonUtils;

/**
 * 基础信息获取
 * （校园卡余额、宿舍信息、宿舍电费、校园网信息）
 * Create by QianXiao
 * On 2020/9/28
 */
public class GetBaseInfoModel {
    private Context context;

    public GetBaseInfoModel(Context context) {
        this.context = context;
    }

    public interface GetWalletMoneyModelCallBack{
        void onGetWalletMoneySuccess(String yu_e,String dzzh_yu_e);
        void onGetWalletMoneyError(String error);
    }

    /**
     * 获取校园卡余额
     * （无需登录认证）
     * @param accnum 对校园卡号唯一对应
     * @param callBack
     */
    public void getWalletMoney(String accnum,GetWalletMoneyModelCallBack callBack){
        StringRequest getWalletMoneyJsonObjectRequest = new StringRequest(
                Request.Method.POST,
                "https://appservice.lzu.edu.cn/easytong_app/easytong-app/easytong_app/GetWalletMoney",
                response -> {
                    LogUtils.i(response);
                    JSONObject jsonObject = Xml2JsonUtils.xml2json(response).optJSONObject("EasyTong");
                    //LogUtils.i(jsonObject);
                    assert jsonObject != null;
                    if(jsonObject.optString("Code").equals("1")){
                        JSONObject xyk = Objects.requireNonNull(jsonObject.optJSONArray("Table")).optJSONObject(0);
                        String yu_e = xyk.optString("WalletMoney")
                                + xyk.optString("Unit");
                        JSONObject dzzh = Objects.requireNonNull(jsonObject.optJSONArray("Table")).optJSONObject(1);
                        String dzzh_yu_e = dzzh.optString("WalletMoney")
                                + xyk.optString("Unit");
                        callBack.onGetWalletMoneySuccess(yu_e,dzzh_yu_e);
                    }else{
                        callBack.onGetWalletMoneyError(jsonObject.optString("Msg"));
                    }
                }, error -> callBack.onGetWalletMoneyError(error.toString())){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("AccNum", accnum);
                map.put("EPID", "1");
                map.put("Time", MyTimeUtils.getNowString());
                map.put("Sign", SignUtils.sign(map));
                return map;
            }
        };
        MyVolleyManager.getRequestQueue().add(getWalletMoneyJsonObjectRequest);
    }

    public interface GetDormInfoCallBack{
        void onGetDormInfoSuccess(DormInfo dormInfo);
        void onGetDormInfoError(String error);
    }

    /**
     * 获取用户宿舍信息
     * （需登录智慧学工）
     * @param zhxgcookies 智慧学工cookie
     * @param callBack
     */
    public void getDormInfo(String zhxgcookies,GetDormInfoCallBack callBack){
        JsonObjectRequest getDormInfoJsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                "http://zhxg.lzu.edu.cn/lzuyz/xsxxwh/XsxxwhWeb/getShowData",
                null,
                response -> {
                    //LogUtils.i(response);
                    String xq = response.optString("Xqmc");//校区。例：榆中校区
                    String ssl = response.optString("jzwmc");//宿舍楼。例：13号楼
                    String ssh = response.optString("Stux");//宿舍号。例：523
                    LogUtils.i(xq,ssl,ssh);
                    StringBuilder sb = new StringBuilder();
                    if(xq.equals("榆中校区")){
                        sb.append("01");
                    }else if(xq.contains("医学")){
                        sb.append("03");
                    }else if(xq.contains("本部")||xq.contains("城关")){
                        sb.append("02");
                    }else{
                        sb.append("00");
                    }
                    sb.append(String.format("%2d", Integer.valueOf(ssl.replaceAll("[^0-9]",""))));
                    sb.append(ssh);
                    DormInfo dormInfo = new DormInfo();
                    dormInfo.setDormno(sb.toString());
                    dormInfo.setDorm(xq+ssl+ssh);
                    callBack.onGetDormInfoSuccess(dormInfo);
                },error -> callBack.onGetDormInfoError(error.getMessage())){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //return super.getHeaders();
                Map<String, String> headers = super.getHeaders();
                if(headers == null || headers.equals(Collections.emptyMap())){
                    headers = new HashMap<>();
                }
                headers.put("Cookie",zhxgcookies);
                return headers;
            }
        };
        MyVolleyManager.getRequestQueue().add(getDormInfoJsonObjectRequest);
    }

    public interface GetDormBlanceCallBack{
        void onGetDormBlanceSuccess(String blance);
        void onGetDormBlanceError(String error);
    }

    /**
     * 获取宿舍电费
     * （无需登录，调用自写php接口）
     * @param dormno 宿舍自编号
     * @param callBack
     */
    public void getDormBlance(String dormno,GetDormBlanceCallBack callBack){
        StringRequest getDormBlanceRequest = new StringRequest(
                Request.Method.POST,
                "http://api.qianxiao.fun/lzudormblance/get.php",
                response -> {
                    LogUtils.i(response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.optInt("code")==1){
                            String blance = Objects.requireNonNull(jsonObject.optJSONObject("data")).optString("balance");
                            if(!TextUtils.isEmpty(blance)){
                                /*DormInfo dormInfo = new DormInfo();
                                dormInfo.setDormno(dormno);
                                dormInfo.setBlance(blance+"度");
                                dormInfo.setDorm(Objects.requireNonNull(jsonObject.optJSONObject("data")).optString("dormname"));
                                */
                                callBack.onGetDormBlanceSuccess(blance+"度");
                            }else{
                                callBack.onGetDormBlanceError("宿舍电费获取失败");
                            }
                        }else{
                            callBack.onGetDormBlanceError(jsonObject.optString("msg")+"("+jsonObject.optInt("code")+")");
                        }
                    } catch (JSONException e) {
                        LogUtils.e(e.toString());
                        callBack.onGetDormBlanceError(e.getMessage());
                    }
                }, error -> callBack.onGetDormBlanceError(error.toString())){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("dormno", dormno);
                map.put("timestamp", String.valueOf(TimeUtils.getNowMills()/1000));
                map.put("parsign", EncryptUtils.encryptSHA1ToString("dormno="+dormno+"&timestamp="+
                        map.get("timestamp")
                        +"lzudormblance"));
                return map;
            }
        };
        getDormBlanceRequest.setRetryPolicy(new DefaultRetryPolicy(5000,3,1f));
        MyVolleyManager.getRequestQueue().add(getDormBlanceRequest);
    }

    public interface GetLzuNetInfoCallBack{
        void onGetLzuNetInfoSuccess(SchoolNetInfo schoolNetInfo);
        void onGetLzuNetInfoError(String error);
    }

    /**
     * 获取校园网信息
     * （直接访问指定接口，超时则视非校园网，如果时校园网，响应会很快）
     * @param callBack
     */
    public void getLzuNetInfo(GetLzuNetInfoCallBack callBack){
        StringRequest getLzuNetInfoRequest = new StringRequest(
                Request.Method.POST,
                "http://login.lzu.edu.cn/cgi-bin/rad_user_info?callback=c&_="+TimeUtils.getNowMills()/1000,
                response -> {
                    response = response.substring(2,response.length()-1);
                    LogUtils.i(response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        SchoolNetInfo schoolNetInfo = new SchoolNetInfo();
                        schoolNetInfo.setBilling_name(jsonObject.optString("billing_name"));
                        schoolNetInfo.setOnline_ip(jsonObject.optString("online_ip"));
                        schoolNetInfo.setSum_bytes(jsonObject.optLong("sum_bytes"));
                        schoolNetInfo.setSum_seconds(jsonObject.optLong("sum_seconds"));
                        schoolNetInfo.setUser_name(jsonObject.optString("user_name"));
                        schoolNetInfo.setWallet_balance(jsonObject.optDouble("wallet_balance"));
                        callBack.onGetLzuNetInfoSuccess(schoolNetInfo);
                    } catch (JSONException e) {
                        LogUtils.e(e.toString());
                        callBack.onGetLzuNetInfoError(e.getMessage());
                    }
                }, error -> callBack.onGetLzuNetInfoError(error.toString())){
            @Override
            public void deliverError(VolleyError error) {
                if(error instanceof TimeoutError){
                    callBack.onGetLzuNetInfoError("非校园网");
                }
                super.deliverError(error);
            }
        };
        getLzuNetInfoRequest.setRetryPolicy(new DefaultRetryPolicy(1000,0,1f));
        MyVolleyManager.getRequestQueue().add(getLzuNetInfoRequest);
    }
}
