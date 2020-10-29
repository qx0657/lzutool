package fun.qianxiao.lzutool.ui.personalinf.fragment;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

import fun.qianxiao.lzutool.base.BaseFragment;

/**
 * Create by QianXiao
 * On 2020/10/12
 */
public class FragmentAdapter extends FragmentPagerAdapter {
    private List<BaseFragment> list;

    public FragmentAdapter(@NonNull FragmentManager fm, int behavior, List<BaseFragment> list) {
        super(fm, behavior);
        this.list = list;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
