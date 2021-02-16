package fun.qianxiao.lzutool.ui.main.sub;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import fun.qianxiao.lzutool.R;
import fun.qianxiao.lzutool.base.BaseDataBadingActivity;
import fun.qianxiao.lzutool.base.BaseFragmentActivity;
import fun.qianxiao.lzutool.view.ILoadingView;
import fun.qianxiao.lzutool.view.MyLoadingDialog;

/**
 * Create by QianXiao
 * On 2020/10/2
 */
@SuppressLint("Registered")
public abstract class WelcomeActivity extends BaseFragmentActivity implements ILoadingView {
    private MyLoadingDialog loadingDialog;

    @Override
    protected Fragment getFragment() {
        return new WelecomeFragment();
    }

    @Override
    public void openLoadingDialog(String msg) {
        if(loadingDialog == null){
            loadingDialog = new MyLoadingDialog(context);
        }
        if(!TextUtils.isEmpty(msg)){
            loadingDialog.setMessage(msg);
        }
        if(!loadingDialog.isShowing()){
            loadingDialog.show();
        }
    }

    @Override
    public void closeLoadingDialog() {
        if(loadingDialog != null && loadingDialog.isShowing()){
            loadingDialog.cancel();
        }
    }
}
