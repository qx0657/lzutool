package fun.qianxiao.lzutool.bean;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.TimeUtils;

import java.io.Serializable;

import fun.qianxiao.lzutool.utils.NumToDateString;

/**
 * Create by QianXiao
 * On 2020/9/30
 */
public class SchoolNetInfo extends BaseObservable implements Serializable {
    /**
     * 校园网登录用户名
     */
    private String user_name;
    /**
     * 已用流量（字节）
     */
    private long sum_bytes;
    /**
     * 已用时长（秒）
     */
    private long sum_seconds;
    /**
     * ip
     */
    private String online_ip;
    /**
     * 网络区域
     */
    private String billing_name;
    /**
     * 账户余额
     */
    private double wallet_balance;

    @Bindable
    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public long getSum_bytes() {
        return sum_bytes;
    }

    @Bindable
    public String getHasusedLL() {
        if(sum_bytes == 0L){
            return null;
        }
        //字节数转合适内存大小
        return ConvertUtils.byte2FitMemorySize(sum_bytes);
    }

    public void setSum_bytes(long sum_bytes) {
        this.sum_bytes = sum_bytes;
    }

    public long getSum_seconds() {
        return sum_seconds;
    }

    @Bindable
    public String getHasusedSC() {
        if(sum_seconds == 0L){
            return null;
        }
        return NumToDateString.getPatchedTimeStr(sum_seconds);
    }

    public void setSum_seconds(long sum_seconds) {
        this.sum_seconds = sum_seconds;
    }

    @Bindable
    public String getOnline_ip() {
        return online_ip;
    }

    public void setOnline_ip(String online_ip) {
        this.online_ip = online_ip;
    }

    @Bindable
    public String getBilling_name() {
        return billing_name;
    }

    public void setBilling_name(String billing_name) {
        this.billing_name = billing_name;
    }

    public double getWallet_balance() {
        return wallet_balance;
    }

    @Bindable
    public String getZHYE() {
        /*if(wallet_balance == 0f){
            return null;
        }*/
        return wallet_balance+"元";
    }

    public void setWallet_balance(double wallet_balance) {
        this.wallet_balance = wallet_balance;
    }
}
