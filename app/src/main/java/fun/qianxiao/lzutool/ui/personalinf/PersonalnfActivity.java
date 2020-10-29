package fun.qianxiao.lzutool.ui.personalinf;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import fun.qianxiao.lzutool.R;
import fun.qianxiao.lzutool.base.BaseDataBadingActivity;
import fun.qianxiao.lzutool.base.BaseFragment;
import fun.qianxiao.lzutool.ui.main.model.lzulogin.LzuloginModel;
import fun.qianxiao.lzutool.ui.personalinf.fragment.FragmentAdapter;
import fun.qianxiao.lzutool.ui.personalinf.fragment.PersonalnfFragment;
import fun.qianxiao.lzutool.ui.personalinf.fragment.TransitcenterFragment;
import fun.qianxiao.lzutool.view.MyLoadingDialog;

/**
 * 个人网盘和文件中转站
 * Create by QianXiao
 * On 2020/10/12
 */
public class PersonalnfActivity extends BaseDataBadingActivity implements IPersonalnfView {
    private ViewPager vp_personalnfactivity;
    private LzuloginModel lzuloginModel;
    private MyLoadingDialog loadingDialog;
    private List<BaseFragment> fragmentlist;
    private PersonalnfFragment personalnfFragment;
    private TransitcenterFragment transitcenterFragment;
    private BaseFragment currentFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        useDatabinding = false;
        isAddToolbarMarginTopEqualStatusBarHeight = true;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_personalnf;
    }

    @Override
    protected void initViewModel() {

    }

    @Override
    protected void initView() {
        vp_personalnfactivity = findViewById(R.id.vp_personalnfactivity);
    }

    @Override
    protected void initListener() {
        vp_personalnfactivity.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentFragment = fragmentlist.get(position);
                personalnfFragment.exitMulSelect();
                invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void initData() {
        showBackButton();
        setTitle("个人网盘");
        personalnfFragment = new PersonalnfFragment(this);
        currentFragment = personalnfFragment;
        transitcenterFragment = new TransitcenterFragment();
        fragmentlist = new ArrayList<>();
        fragmentlist.add(personalnfFragment);
        fragmentlist.add(transitcenterFragment);
        lzuloginModel = new LzuloginModel(context,null);
        Intent intent = getIntent();
        String user_mailpf = intent.getStringExtra("user_mailpf");
        String login_pwd = intent.getStringExtra("login_pwd");
        if(TextUtils.isEmpty(user_mailpf)||TextUtils.isEmpty(login_pwd)){
            finish();
            return;
        }
        openLoadingDialog("正在登录");
        lzuloginModel.loginLzuMail(user_mailpf, login_pwd, new LzuloginModel.LoginLzuMailCallBack() {
            @Override
            public void onLoginLzuMailSuccess(String coolie_mail) {
                LogUtils.i("邮箱登录Cookie获取成功",coolie_mail);
                closeLoadingDialog();
                personalnfFragment.setMail_cookie(coolie_mail);
                personalnfFragment.loadData();
                FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, fragmentlist);
                vp_personalnfactivity.setAdapter(adapter);
                vp_personalnfactivity.setCurrentItem(0);
            }

            @Override
            public void onLoginLzuMailError(String error) {
                LogUtils.e(error);
                ToastUtils.showShort(error);
                closeLoadingDialog();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    boolean selectMenu = false;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        KeyboardUtils.hideSoftInput(getWindow());
        //动态设置ToolBar的Menu
        switch (vp_personalnfactivity.getCurrentItem()){
            case 0:
                setTitle("个人网盘");
                int menuid = R.menu.menu_personalnf_fragment;
                if(selectMenu){
                    menuid = R.menu.menu_mulselect_personalnf_fragment;
                }
                getMenuInflater().inflate(menuid,menu);
                personalnfFragment.setMenu(menu,menuid);
                break;
            case 1:
                setTitle("文件中转站");
                return false;
            default:
                break;
        }
        return true;
    }

    @Override
    public void openLoadingDialog(String msg) {
        if(loadingDialog == null){
            loadingDialog = new MyLoadingDialog(context);
        }
        if(!TextUtils.isEmpty(msg)){
            loadingDialog.setMessage(msg);
        }
        if(!loadingDialog.isShowing()){
            loadingDialog.show();
        }else{
            loadingDialog.setMessage(msg);
        }
    }

    @Override
    public void closeLoadingDialog() {
        if(loadingDialog != null && loadingDialog.isShowing()){
            loadingDialog.cancel();
        }
    }

    @Override
    public void onBackPressed() {
        if(!currentFragment.onBackPressed()){
            super.onBackPressed();
        }
    }

    @Override
    public void setSelectMenu(boolean flag) {
        selectMenu = flag;
        invalidateOptionsMenu();
    }
}
