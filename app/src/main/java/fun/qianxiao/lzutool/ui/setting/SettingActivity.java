package fun.qianxiao.lzutool.ui.setting;

import android.os.Bundle;

import androidx.annotation.Nullable;

import fun.qianxiao.lzutool.R;
import fun.qianxiao.lzutool.base.BaseDataBadingActivity;

/**
 * 设置页面
 * Create by QianXiao
 * On 2020/10/1
 */
public class SettingActivity extends BaseDataBadingActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        useDatabinding = false;
        isAddToolbarMarginTopEqualStatusBarHeight = true;
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.framelayout_lf,new SettingFragment())
                .commit();
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

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
        setTitle("设置");
        showBackButton();
    }
}
