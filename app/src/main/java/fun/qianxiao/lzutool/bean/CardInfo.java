package fun.qianxiao.lzutool.bean;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.io.Serializable;

/**
 * Create by QianXiao
 * On 2020/9/30
 */
public class CardInfo extends BaseObservable implements Serializable {
    /**
     * 校园卡余额
     */
    private String card_yu_e;
    /**
     * 电子账户余额
     */
    private String card_dzzh_yu_e;

    @Bindable
    public String getCard_yu_e() {
        return card_yu_e;
    }

    public void setCard_yu_e(String card_yu_e) {
        this.card_yu_e = card_yu_e;
    }

    @Bindable
    public String getCard_dzzh_yu_e() {
        return card_dzzh_yu_e;
    }

    public void setCard_dzzh_yu_e(String card_dzzh_yu_e) {
        this.card_dzzh_yu_e = card_dzzh_yu_e;
    }
}
