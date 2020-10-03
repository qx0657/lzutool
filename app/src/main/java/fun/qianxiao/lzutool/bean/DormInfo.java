package fun.qianxiao.lzutool.bean;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.io.Serializable;

/**
 * Create by QianXiao
 * On 2020/9/30
 */
public class DormInfo extends BaseObservable implements Serializable {
    /**
     * 宿舍自编号
     */
    private String dormno;
    /**
     * 宿舍地点名称
     */
    private String dorm;
    /**
     * 宿舍剩余电量
     */
    private String blance;

    public String getDormno() {
        return dormno;
    }

    public void setDormno(String dormno) {
        this.dormno = dormno;
    }

    @Bindable
    public String getDorm() {
        return dorm;
    }

    public void setDorm(String dorm) {
        this.dorm = dorm;
    }

    @Bindable
    public String getBlance() {
        return blance;
    }

    public void setBlance(String blance) {
        this.blance = blance;
    }

    @Override
    public String toString() {
        return "DormInfo{" +
                "dormno='" + dormno + '\'' +
                ", dorm='" + dorm + '\'' +
                ", blance='" + blance + '\'' +
                '}';
    }
}
