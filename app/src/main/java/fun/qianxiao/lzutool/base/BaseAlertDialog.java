package fun.qianxiao.lzutool.base;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

/**
 * Create by QianXiao
 * On 2020/6/11
 */
public abstract class BaseAlertDialog extends AlertDialog {
    public Context context;
    public View view;

    public BaseAlertDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);//去掉标题
        ColorDrawable dw = new ColorDrawable(0x00000000);
        getWindow().setBackgroundDrawable(dw);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = LayoutInflater.from(context).inflate(getLayoutID(),null);
        setContentView(view);
        initView();
        initListener();
        initData();
    }

    protected abstract int getLayoutID();

    protected abstract void initView();

    protected abstract void initListener();

    protected abstract void initData();

    @SuppressWarnings("unchecked")
    protected <E> E f(int id) {
        return (E) view.findViewById(id);
    }
}
