package fun.qianxiao.lzutool.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.blankj.utilcode.util.BarUtils;
import com.google.android.material.snackbar.Snackbar;

import fun.qianxiao.lzutool.R;
import fun.qianxiao.lzutool.utils.MySpUtils;

/**
 * Create by QianXiao
 * On 2020/9/30
 */
@SuppressLint("Registered")
public abstract class BaseDataBadingActivity<T extends ViewDataBinding> extends AppCompatActivity {
    protected Context context;
    protected boolean useDatabinding = true;
    /**
     * 泛型传入的ViewDataBinding
     * <T extends ViewDataBinding>
     */
    protected T binding;
    protected boolean setDefaultToolbar = true;
    protected Toolbar toolbar;
    protected boolean isAddToolbarMarginTopEqualStatusBarHeight = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        //初始化主题
        themeInit();
        if(useDatabinding){
            //将View与ViewModel进行绑定 ActivityMainBinding与布局文件名对应
            binding = DataBindingUtil.setContentView(this, getLayoutID());
            initViewModel();
        }else{
            setContentView(getLayoutID());
        }
        if(setDefaultToolbar){
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
        }
        initView();
        initListener();
        initData();

        //状态栏透明
        BarUtils.transparentStatusBar(this);
        if(isAddToolbarMarginTopEqualStatusBarHeight&&setDefaultToolbar){
            BarUtils.addMarginTopEqualStatusBarHeight(toolbar);
        }
        BarUtils.setStatusBarColor(this, ContextCompat.getColor(this, getColorPrimaryId()));
    }

    protected abstract int getLayoutID();

    protected abstract void initViewModel();

    protected abstract void initView();

    protected abstract void initListener();

    protected abstract void initData();

    private void themeInit() {
        int themeindex = MySpUtils.getInt("theme");
        int themeid = R.style.AppTheme1;
        switch (themeindex){
            case 1:
                themeid = R.style.AppTheme2;
                break;
            case 2:
                themeid = R.style.AppTheme3;
                break;
            case 3:
                themeid = R.style.AppTheme4;
                break;
            case 4:
                themeid = R.style.AppTheme5;
                break;
            case 5:
                themeid = R.style.AppTheme6;
                break;
            case 6:
                themeid = R.style.AppTheme7;
                break;
            default:
                break;
        }
        setTheme(themeid);
    }

    /**
     * 动态获取主题的ColorPrimary
     * @return
     */
    public int getColorPrimaryId(){
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.resourceId;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 显示返回按钮
     */
    protected void showBackButton(){
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    public void ShowSnackbar(String msg){
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), msg, Snackbar.LENGTH_SHORT);
        TextView snackbar_text = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        snackbar_text.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
        snackbar_text.setGravity(Gravity.CENTER);
        snackbar_text.setMaxLines(1);
        snackbar.show();
    }

    public void ShowSnackbar(int backgroundcolor,String msg){
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), msg, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(backgroundcolor);
        TextView snackbar_text = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        snackbar_text.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
        snackbar_text.setGravity(Gravity.CENTER);
        snackbar_text.setMaxLines(1);
        snackbar.show();
    }
}
