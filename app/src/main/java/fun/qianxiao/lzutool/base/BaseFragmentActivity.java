package fun.qianxiao.lzutool.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import fun.qianxiao.lzutool.R;
import fun.qianxiao.lzutool.ui.main.sub.WelecomeFragment;

public abstract class BaseFragmentActivity extends BaseDataBadingActivity{
    protected Fragment fragment;

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
        setFragment(getFragment());
    }

    public void setFragment(Fragment fragment){
        this.fragment = fragment;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.framelayout_lf,this.fragment)
                .commit();
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {

    }

    protected abstract Fragment getFragment();
}
