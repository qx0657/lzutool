package fun.qianxiao.lzutool.ui.setting;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import fun.qianxiao.lzutool.R;
import fun.qianxiao.lzutool.base.BaseDataBadingActivity;
import fun.qianxiao.lzutool.base.BaseFragmentActivity;

/**
 * 设置页面
 * Create by QianXiao
 * On 2020/10/1
 */
public class SettingActivity extends BaseFragmentActivity {

    @Override
    protected void initData() {
        setTitle("设置");
        showBackButton();
    }

    @Override
    protected Fragment getFragment() {
        return new SettingFragment();
    }
}
