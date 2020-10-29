package fun.qianxiao.lzutool.view;


import android.content.Context;
import androidx.annotation.NonNull;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.blankj.utilcode.util.ScreenUtils;

import java.util.Objects;

import fun.qianxiao.lzutool.R;
import fun.qianxiao.lzutool.base.BaseAlertDialog;

public class MyLoadingDialog extends BaseAlertDialog {
    private TextView tv_message_dialogloading;

    private String message = "加载中";

    public MyLoadingDialog(@NonNull Context context) {
        super(context);
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        if(tv_message_dialogloading!=null){
            tv_message_dialogloading.setText(message);
        }
    }

    @Override
    protected int getLayoutID() {
        return R.layout.dialog_loading;
    }

    @Override
    protected void initView() {
        tv_message_dialogloading = f(R.id.tv_message_dialogloading);
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
        Objects.requireNonNull(this.getWindow()).setGravity(Gravity.CENTER);
        this.setCancelable(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Window window = getWindow();
        assert window != null;
        WindowManager.LayoutParams params = window.getAttributes();
        params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;//显示dialog的时候,就显示软键盘
        params.width = (int) (ScreenUtils.getAppScreenWidth() * 0.35);
        params.height = params.width;
        window.setAttributes(params);
    }

    @Override
    public void show() {
        super.show();
        tv_message_dialogloading.setText(message);
    }
}

