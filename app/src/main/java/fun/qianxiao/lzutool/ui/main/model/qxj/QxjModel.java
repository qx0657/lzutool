package fun.qianxiao.lzutool.ui.main.model.qxj;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.TimeUtils;

import org.json.JSONObject;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import fun.qianxiao.lzutool.utils.MyVolleyManager;

/**
 * 请假状态获取
 * Create by QianXiao
 * On 2020/10/2
 */
public class QxjModel {
    public enum QxjStatu{
        DSP("待审批"),YSP("已审批"),QJZ("请假中"),DXJ("待销假"),YXJ("已销假");
        private String describe;
        QxjStatu(String describe) {
            this.describe = describe;
        }

        @Override
        public String toString() {
            return describe;
        }
    }

    public interface GetListStuCallBack{
        void onGetUserSomeInfoSuccess(String dh,String wx,String qq);
        void onGetListStuSuccess(QxjStatu qxjStatu);
        void onGetListStuError(String error);
    }

    /**
     * 获取当前请假状态
     * @param zhxgcookies 智慧学工cookie
     * @param isYjs 是否研究生
     * @param callBack
     */
    public void getListStu(String zhxgcookies,boolean isYjs,GetListStuCallBack callBack){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                "http://" +
                        (isYjs?"yjsxg.lzu.edu.cn/lzuygb":
                                "zhxg.lzu.edu.cn/lzuyz")+
                        "/qxj/rcqj/liststu?page=1&limit=10&_="+System.currentTimeMillis()/1000,
                null,
                response -> {
                    if(response.optInt("code",-999)==0){
                        JSONObject jsonObject = Objects.requireNonNull(response.optJSONArray("data")).optJSONObject(0);
                        if(jsonObject !=null){
                            LogUtils.i(jsonObject);
                            callBack.onGetUserSomeInfoSuccess(
                                    jsonObject.optString("dh"),
                                    jsonObject.optString("wx"),
                                    jsonObject.optString("qq")
                            );
                            Date startDate = TimeUtils.string2Date(jsonObject.optString("startDate"));
                            Date endDate = TimeUtils.string2Date(jsonObject.optString("endDate"));
                            boolean isPass = jsonObject.optString("qjzt").equals("09");
                            boolean isXj = jsonObject.optString("sfxj").equals("1");
                            if(new Date().compareTo(startDate)<0){
                                //当前时间在请假开始时间之前
                                if(isPass){
                                    callBack.onGetListStuSuccess(QxjStatu.YSP);
                                }else{
                                    callBack.onGetListStuSuccess(QxjStatu.DSP);
                                }
                            }else if(new Date().compareTo(endDate)>0){
                                if(isXj){
                                    callBack.onGetListStuSuccess(QxjStatu.YXJ);
                                }else{
                                    callBack.onGetListStuSuccess(QxjStatu.DXJ);
                                }
                            }else{
                                if(isPass){
                                    callBack.onGetListStuSuccess(QxjStatu.QJZ);
                                }else{
                                    callBack.onGetListStuSuccess(QxjStatu.DSP);
                                }
                            }
                        }else{
                            callBack.onGetListStuError("最近一次请假数据为空");
                        }

                    }else{
                        callBack.onGetListStuError(response.optString("msg")+"("+response.optInt("code",-999)+")");
                    }
                },error -> callBack.onGetListStuError(error.getMessage())){
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
        MyVolleyManager.getRequestQueue().add(jsonObjectRequest);
    }
}
