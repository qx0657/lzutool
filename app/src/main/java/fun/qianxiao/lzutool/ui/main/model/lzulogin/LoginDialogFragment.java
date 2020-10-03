package fun.qianxiao.lzutool.ui.main.model.lzulogin;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import fun.qianxiao.lzutool.R;
import fun.qianxiao.lzutool.base.BaseDialogFragment;

/**
 * Create by QianXiao
 * On 2020/9/27
 */
public class LoginDialogFragment extends BaseDialogFragment {
    private LzuloginModel lzuloginModel;
    private TextInputEditText tie_uid_login_df,tie_pwd_login_df;
    private TextView tv_loginbt_login_df;
    private OnLoginClickListener onLoginClickListener;

    public LoginDialogFragment(LzuloginModel lzuloginModel) {
        this.lzuloginModel = lzuloginModel;
    }

    public interface OnLoginClickListener{
        void login(String uid,String pwd);
    }

    public LoginDialogFragment setOnLoginClickListener(OnLoginClickListener onLoginClickListener) {
        this.onLoginClickListener = onLoginClickListener;
        return this;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.dialogfragment_login;
    }

    @Override
    protected void initView() {
        tie_uid_login_df = f(R.id.tie_uid_login_df);
        tie_pwd_login_df = f(R.id.tie_pwd_login_df);
        tv_loginbt_login_df = f(R.id.tv_loginbt_login_df);
    }

    @Override
    protected void initListener() {
        tv_loginbt_login_df.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = Objects.requireNonNull(tie_uid_login_df.getText()).toString();
                if(TextUtils.isEmpty(uid)){
                    ToastUtils.showShort("请输入邮箱前缀或校园卡号");
                    tie_uid_login_df.requestFocus();
                    return;
                }
                String pwd = Objects.requireNonNull(tie_pwd_login_df.getText()).toString();
                if(TextUtils.isEmpty(pwd)){
                    ToastUtils.showShort("请输入密码");
                    tie_pwd_login_df.requestFocus();
                    return;
                }
                tie_pwd_login_df.requestFocus();
                KeyboardUtils.hideSoftInput(tie_pwd_login_df);
                if(onLoginClickListener != null){
                    onLoginClickListener.login(uid,pwd);
                }
            }
        });
    }

    @Override
    protected void initData() {
        //setCancelable(false);
        tie_uid_login_df.requestFocus();
        KeyboardUtils.hideSoftInput(tie_pwd_login_df);
    }
}
