package fun.qianxiao.lzutool.ui;

import android.content.Context;

import fun.qianxiao.lzutool.ui.mail.ILzuMailView;

public abstract class BaseLzuMailViewModel {
    protected Context context;
    protected ILzuMailView iLzuMailView;

    public BaseLzuMailViewModel(Context context, ILzuMailView iLzuMailView) {
        this.context = context;
        this.iLzuMailView = iLzuMailView;
    }
}
