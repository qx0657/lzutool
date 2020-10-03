package fun.qianxiao.lzutool.ui.main.sub;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import fun.qianxiao.lzutool.R;
import fun.qianxiao.lzutool.base.BaseDataBadingActivity;
import fun.qianxiao.lzutool.view.ILoadingView;
import fun.qianxiao.lzutool.view.MyLoadingDialog;

/**
 * Create by QianXiao
 * On 2020/10/2
 */
@SuppressLint("Registered")
public abstract class WelcomeActivity extends BaseDataBadingActivity implements ILoadingView {
    private MyLoadingDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        useDatabinding = false;
        isAddToolbarMarginTopEqualStatusBarHeight = true;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.layout_fragment;
    }

    @Override
    protected void initViewModel() {

    }

    @Override
    protected void initView() {
        Fragment fragment = new WelecomeFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.framelayout_lf,fragment)
                .commit();
    }

    @Override
    protected void initListener() {

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
