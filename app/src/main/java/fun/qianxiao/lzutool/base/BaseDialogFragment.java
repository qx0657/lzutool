package fun.qianxiao.lzutool.base;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.blankj.utilcode.util.ScreenUtils;

/**
 * Create by QianXiao
 * On 2020/6/14
 * 重要方法执行顺序：onCreate->onCreateDialog->onCreateView
 * ->onViewCreated->onStart
 * ->mDialog.show()->onResume
 */
public abstract class BaseDialogFragment extends DialogFragment {
    public Context context;
    private View view;
    private int animationsId = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //去除标题栏
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        view = inflater.inflate(getLayoutID(), container,false);
        initView();
        initListener();
        initData();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //设置动画、位置、宽度等属
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawable(ContextCompat.getDrawable(context,android.R.color.transparent));
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            if(animationsId != -1){
                layoutParams.windowAnimations = animationsId;//动画
            }
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.width = displayWidth();
            window.setAttributes(layoutParams);
        }
    }

    protected void setAnimationsId(int animationsId){
        this.animationsId = animationsId;
    }

    protected abstract int getLayoutID();

    protected int displayWidth(){
        return (int) (ScreenUtils.getScreenWidth()*0.85);
    }

    protected abstract void initView();

    protected abstract void initListener();

    protected abstract void initData();

    @SuppressWarnings("unchecked")
    protected <E> E f(int id) {
        return (E) view.findViewById(id);
    }
}
