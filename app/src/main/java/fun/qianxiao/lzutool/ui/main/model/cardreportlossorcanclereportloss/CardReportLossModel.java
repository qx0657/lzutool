package fun.qianxiao.lzutool.ui.main.model.cardreportlossorcanclereportloss;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import fun.qianxiao.lzutool.utils.MyVolleyManager;

/**
 * 校园卡挂失解挂
 * （需智慧一卡通cookie、无需支付密码）
 * 已测试，并不能对他人校园卡号进行挂失
 * Create by QianXiao
 * On 2020/10/4
 */
public class CardReportLossModel {
    public interface CardReportLossOperationCallBack{
        void onCardReportLossOperationSuccess(String res);
        void onCardReportLossOperationError(String error);
    }

    /**
     * 挂失/解挂
     * @param ecardcookie 智慧一卡通cookie(sid和tgt)
     * @param cardAccNum 校园卡AccNum
     * @param cardid 校园卡号
     * @param isLoss 是否丢失（为真为挂失，为假为解挂）
     * @param callBack
     */
    public void report(String ecardcookie,String cardAccNum,String cardid,boolean isLoss,CardReportLossOperationCallBack callBack){
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                "https://ecard.lzu.edu.cn/easytong_portal/reportLoss/lossReportCard",
                response -> {
                    //LogUtils.i(response);
                    if(isLoss){
                        if(response.contains("已经申请挂失")){
                            callBack.onCardReportLossOperationSuccess("挂失成功");
                        }else if(response.contains("账户状态为挂失, 不能挂失")){
                            callBack.onCardReportLossOperationError("校园卡状态已为挂失");
                        }else{
                            callBack.onCardReportLossOperationError("挂失失败");
                        }
                    }else{
                        if(response.contains("解挂成功")){
                            callBack.onCardReportLossOperationSuccess("解挂成功");
                        }else if(response.contains("账户状态为有效, 不能解挂")){
                            callBack.onCardReportLossOperationError("校园卡状态有效");
                        }else{
                            callBack.onCardReportLossOperationError("解挂失败");
                        }
                    }
                },error -> {
                    if(error instanceof TimeoutError){
                        callBack.onCardReportLossOperationError("请求超时，请稍后重试");
                    }else{
                        callBack.onCardReportLossOperationError(error.toString());
                    }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //return super.getHeaders();
                Map<String, String> headers = super.getHeaders();
                if(headers == null || headers.equals(Collections.emptyMap())){
                    headers = new HashMap<>();
                }
                headers.put("Cookie",ecardcookie);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("cardAccNum",cardAccNum);
                map.put("perCode",cardid);
                map.put("alias","CPU");
                map.put("isno",isLoss?"1":"2");
                map.put("password","");
                return map;
            }
        };
        MyVolleyManager.getRequestQueue().add(stringRequest);
    }
}
