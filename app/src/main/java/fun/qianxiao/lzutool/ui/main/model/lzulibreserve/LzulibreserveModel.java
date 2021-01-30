package fun.qianxiao.lzutool.ui.main.model.lzulibreserve;

import com.android.volley.toolbox.JsonObjectRequest;
import com.blankj.utilcode.util.LogUtils;

import fun.qianxiao.lzutool.utils.MyVolleyManager;

/**
 * 图书馆预约
 * Create by QianXiao
 * On 2020/9/27
 */
public class LzulibreserveModel {

    public interface ReserveCallBack{
        void onResult(String res);
    }

    /**
     * 图书馆预约
     * （无需登录，调用自写接口）
     * @param cardid 校园卡号
     * @param callBack
     */
    public void reserve(String cardid,boolean ischengguan,ReserveCallBack callBack){
        JsonObjectRequest reserveJsonObjectRequest = new JsonObjectRequest("http://api.qianxiao.fun/lzulibreserve/reserve.php?cardid="+cardid+(ischengguan?"&xq=2":""),
                null,response1 -> {
            int code = response1.optInt("code");
            if(code==1){
                callBack.onResult(ischengguan?"城关校区预约成功":"榆中校区预约成功");
            }else if(code == 2){
                callBack.onResult("已预约");//时间冲突（可能是已预约或已预约其他校区）
            }else{
                callBack.onResult(response1.optString("msg"));
            }
        },error -> callBack.onResult(error.getMessage()));
        MyVolleyManager.getRequestQueue().add(reserveJsonObjectRequest);
    }
}
