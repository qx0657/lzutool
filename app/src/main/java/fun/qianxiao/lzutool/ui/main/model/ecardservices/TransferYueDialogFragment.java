package fun.qianxiao.lzutool.ui.main.model.ecardservices;

import android.text.TextUtils;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import fun.qianxiao.lzutool.R;
import fun.qianxiao.lzutool.base.BaseDialogFragment;

public class TransferYueDialogFragment extends BaseDialogFragment {
    private TextInputEditText tie_fromcardid_transferyue_df,tie_tocardid_transferyue_df,tie_money_transferyue_df,tie_paypwd_transferyue_df;
    private TextView tv_transfer_transferyue_df;
    private RadioGroup rg_fromtype_transferyue_df,rg_totype_transferyue_df;
    private OnTransferClickListener onTransferClickListener;

    private String defaultFromCardid;
    private boolean isFromEWallet = false;
    private boolean isToEWallet = false;

    public TransferYueDialogFragment(String defaultFromCardid, OnTransferClickListener onTransferClickListener) {
        this.defaultFromCardid = defaultFromCardid;
        this.onTransferClickListener = onTransferClickListener;
    }

    public interface OnTransferClickListener{
        void transfer(TransferYueDialogFragment dialog,String fromCardid,boolean isFromEWallet,String toCardid,boolean isToEWallet,String money,String paypwd);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.dialogfragment_transferyue;
    }

    @Override
    protected void initView() {
        tie_fromcardid_transferyue_df = f(R.id.tie_fromcardid_transferyue_df);
        tie_tocardid_transferyue_df = f(R.id.tie_tocardid_transferyue_df);
        tie_money_transferyue_df = f(R.id.tie_money_transferyue_df);
        tie_paypwd_transferyue_df = f(R.id.tie_paypwd_transferyue_df);
        tv_transfer_transferyue_df = f(R.id.tv_transfer_transferyue_df);
        rg_fromtype_transferyue_df = f(R.id.rg_fromtype_transferyue_df);
        rg_totype_transferyue_df = f(R.id.rg_totype_transferyue_df);
    }

    @Override
    protected void initListener() {
        rg_fromtype_transferyue_df.setOnCheckedChangeListener((group, checkedId) -> isFromEWallet = checkedId != R.id.rb_fromyue_transferyue_df);
        rg_totype_transferyue_df.setOnCheckedChangeListener((group, checkedId) -> isToEWallet = checkedId != R.id.rb_toyue_transferyue_df);
        tv_transfer_transferyue_df.setOnClickListener(v -> {
            String fromCardid = Objects.requireNonNull(tie_fromcardid_transferyue_df.getText()).toString();
            if(TextUtils.isEmpty(fromCardid)){
                ToastUtils.showShort("请输入转出校园卡号");
                tie_fromcardid_transferyue_df.setText("");
                tie_fromcardid_transferyue_df.requestFocus();
                return;
            }
            if(fromCardid.length()!=12){
                ToastUtils.showShort("校园卡号应为12位");
                tie_fromcardid_transferyue_df.requestFocus();
                return;
            }
            String toCardid = Objects.requireNonNull(tie_tocardid_transferyue_df.getText()).toString();
            if(TextUtils.isEmpty(toCardid)){
                ToastUtils.showShort("请输入转入校园卡号");
                tie_tocardid_transferyue_df.setText("");
                tie_tocardid_transferyue_df.requestFocus();
                return;
            }
            if(toCardid.length()!=12){
                ToastUtils.showShort("校园卡号应为12位");
                tie_tocardid_transferyue_df.requestFocus();
                return;
            }
            String money = Objects.requireNonNull(tie_money_transferyue_df.getText()).toString();
            if(TextUtils.isEmpty(money)){
                ToastUtils.showShort("请输入转移金额");
                tie_money_transferyue_df.setText("");
                tie_money_transferyue_df.requestFocus();
                return;
            }
            String paypwd = Objects.requireNonNull(tie_paypwd_transferyue_df.getText()).toString();
            if(TextUtils.isEmpty(paypwd)){
                ToastUtils.showShort("请输入6位支付密码");
                tie_paypwd_transferyue_df.setText("");
                tie_paypwd_transferyue_df.requestFocus();
                return;
            }
            if(paypwd.length()!=6){
                ToastUtils.showShort("支付密码应为6位");
                tie_paypwd_transferyue_df.requestFocus();
                return;
            }
            if(onTransferClickListener != null){
                KeyboardUtils.hideSoftInput(tie_paypwd_transferyue_df);
                onTransferClickListener.transfer(this,fromCardid,isFromEWallet,toCardid,isToEWallet,money,paypwd);
            }
        });
    }

    @Override
    protected void initData() {
        tie_fromcardid_transferyue_df.setText(defaultFromCardid);
        tie_tocardid_transferyue_df.requestFocus();
    }
}
