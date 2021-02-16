package fun.qianxiao.lzutool.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

public abstract class BaseDateBadingFeagment<T extends ViewDataBinding> extends Fragment {
    public T binding;
    public Context context;
    protected View view;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        initViewModel();
        initView();
        initListener();
        initData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = LayoutInflater.from(this.getContext()).inflate(getLayoutId(), null);
        binding = DataBindingUtil.bind(view);
        return view;
    }

    protected abstract int getLayoutId();

    protected abstract void initViewModel();

    protected abstract void initView();

    protected abstract void initListener();

    protected abstract void initData();

    public boolean onBackPressed(){
        return false;
    }
}
