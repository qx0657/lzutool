package fun.qianxiao.lzutool.bean;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.io.Serializable;

import fun.qianxiao.lzutool.ui.main.model.qxj.QxjModel;

/**
 * Create by QianXiao
 * On 2020/9/27
 */
public class User extends BaseObservable implements Serializable {
    /**
     * 校园卡号
     */
    private String cardid;
    /**
     * AccNum
     */
    private String accnum;
    /**
     * 邮箱前缀
     */
    private String mailpf;
    /**
     * 姓名
     */
    private String name;
    /**
     * 电话
     */
    private String phone;
    /**
     * 学院
     */
    private String college;
    /**
     * 专业
     */
    private String marjor;
    /**
     * 校园卡信息
     */
    @Bindable
    private CardInfo cardInfo;
    /**
     * 宿舍信息
     */
    @Bindable
    private DormInfo dormInfo;
    /**
     * 校园网信息
     */
    @Bindable
    private SchoolNetInfo schoolNetInfo;
    /**
     * 请假状态
     */
    @Bindable
    private QxjModel.QxjStatu qxjStatu;

    private String qq;
    private String wx;
    private String dh;

    @Bindable
    public String getCardid() {
        return cardid;
    }

    public void setCardid(String cardid) {
        this.cardid = cardid;
    }

    public String getAccnum() {
        return accnum;
    }

    public void setAccnum(String accnum) {
        this.accnum = accnum;
    }

    @Bindable
    public String getMailpf() {
        return mailpf;
    }

    public void setMailpf(String mailpf) {
        this.mailpf = mailpf;
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Bindable
    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    @Bindable
    public String getMarjor() {
        return marjor;
    }

    public void setMarjor(String marjor) {
        this.marjor = marjor;
    }

    @Bindable
    public CardInfo getCardInfo() {
        return cardInfo;
    }

    public void setCardInfo(CardInfo cardInfo) {
        this.cardInfo = cardInfo;
    }

    @Bindable
    public DormInfo getDormInfo() {
        return dormInfo;
    }

    public void setDormInfo(DormInfo dormInfo) {
        this.dormInfo = dormInfo;
    }

    @Bindable
    public SchoolNetInfo getSchoolNetInfo() {
        return schoolNetInfo;
    }

    public void setSchoolNetInfo(SchoolNetInfo schoolNetInfo) {
        this.schoolNetInfo = schoolNetInfo;
    }

    @Bindable
    public QxjModel.QxjStatu getQxjStatu() {
        return qxjStatu;
    }

    public void setQxjStatu(QxjModel.QxjStatu qxjStatu) {
        this.qxjStatu = qxjStatu;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getWx() {
        return wx;
    }

    public void setWx(String wx) {
        this.wx = wx;
    }

    public String getDh() {
        return dh;
    }

    public void setDh(String dh) {
        this.dh = dh;
    }
}
