package fun.qianxiao.lzutool.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Create by QianXiao
 * On 2020/10/12
 */
public abstract class BaseFragment extends Fragment {
    public Context context;
    private View view;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        initView();
        initListener();
        initData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(getFragmentLayoutId(),container,false);
        return view;
    }

    protected abstract int getFragmentLayoutId();

    protected abstract void initView();

    protected abstract void initListener();

    protected abstract void initData();

    public abstract boolean onBackPressed();

    @SuppressWarnings("unchecked")
    protected <E> E f(int id) {
        return (E) view.findViewById(id);
    }
}
