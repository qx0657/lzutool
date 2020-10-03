package fun.qianxiao.lzutool.ui.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ToastUtils;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;

import fun.qianxiao.lzutool.R;
import fun.qianxiao.lzutool.base.BaseDataBadingActivity;
import fun.qianxiao.lzutool.bean.User;
import fun.qianxiao.lzutool.checkupdate.CheckUpdateManager;
import fun.qianxiao.lzutool.databinding.ActivityMainBinding;
import fun.qianxiao.lzutool.ui.about.AboutActivity;
import fun.qianxiao.lzutool.ui.addlabel.AddLabelActivity;
import fun.qianxiao.lzutool.ui.addlabel.AddLabelViewModel;
import fun.qianxiao.lzutool.ui.main.model.MainViewModel;
import fun.qianxiao.lzutool.ui.main.sub.HealthPunchActivity;
import fun.qianxiao.lzutool.ui.main.sub.LzuLibReserveActivity;
import fun.qianxiao.lzutool.ui.setting.SettingActivity;
import fun.qianxiao.lzutool.ui.setting.SettingFragment;
import fun.qianxiao.lzutool.utils.MyLabelUtils;
import fun.qianxiao.lzutool.utils.MySpUtils;

/**
 * 主界面
 */
public class MainDataBadingActivity
        extends BaseDataBadingActivity<ActivityMainBinding>
        implements IMainView{

    @Override
    protected int getLayoutID() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViewModel() {
        binding.setMainViewModel(new MainViewModel(this));
    }

    @Override
    protected void initView() {
        View headerView = binding.navView.getHeaderView(0);

        //设置侧滑栏和toolbar绑定 即toolbar左侧显示菜单按钮，可点击切换
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, binding.drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawerToggle.syncState();
        binding.drawerLayout.addDrawerListener(drawerToggle);
    }

    @Override
    protected void initListener() {
        //设置侧滑栏点击事件
        binding.navView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.drawer_menu_userinfo:
                    binding.getMainViewModel().Login();
                    return true;
                case R.id.drawer_menu_loginout:
                    new AlertDialog.Builder(context)
                            .setTitle("提示")
                            .setMessage("确认退出登录？")
                            .setPositiveButton("确定", (dialog, which) -> {
                                binding.getMainViewModel().LoginOut();
                                binding.drawerLayout.closeDrawers();
                            })
                            .setNegativeButton("取消",null)
                            .show();
                    return true;
                case R.id.drawer_menu_about:
                    startActivity(new Intent(context, AboutActivity.class));
                    return true;
                case R.id.drawer_menu_setting:
                    startActivity(new Intent(context, SettingActivity.class));
                    return true;
                case R.id.drawer_menu_addlabel:
                    startActivity(new Intent(context, AddLabelActivity.class));
                    return true;
                case R.id.drawer_menu_themecolor:
                    int checkItem = MySpUtils.getInt("theme");
                    AlertDialog alertDialog = new AlertDialog.Builder(MainDataBadingActivity.this)
                            .setTitle("主题色更换")
                            .setSingleChoiceItems(R.array.themes, checkItem, (dialog12, which) -> {
                                dialog12.dismiss();
                                if(checkItem != which){
                                    MySpUtils.save("theme",which);

                                    MySpUtils.SaveObjectData("user",binding.getMainViewModel().user);

                                    Intent intent = getIntent();
                                    intent.putExtra("select_theme",true);
                                    intent.addFlags(
                                            Intent.FLAG_ACTIVITY_NEW_TASK
                                                    | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    );
                                    startActivity(intent);
                                }
                            })
                            .show();
                    return true;
                default:
                    return false;
            }
        });
        binding.layoutAppbarmain.llLzulibreserve.setOnLongClickListener(v -> {
            if(binding.getMainViewModel().user==null){
                return true;
            }
            if(TextUtils.isEmpty(binding.getMainViewModel().user.getCardid())){
                return true;
            }
            new AlertDialog.Builder(context)
                    .setTitle("添加桌面快捷方式")
                    .setMessage("是否添加“图书馆预约”功能为桌面快捷方式？使用将更方便。")
                    .setPositiveButton("确定",(dialog, which) -> {
                        if(MySpUtils.getString(SettingFragment.LZULIBRESERVE_KEY).equals("0")){
                            ToastUtils.showShort("仅支持自动预约时使用");
                            return;
                        }
                        User user = MySpUtils.getObjectData("user");
                        if(user==null){
                            ToastUtils.showShort("请登录后使用");
                            return;
                        }
                        MySpUtils.SaveObjectData("user",binding.getMainViewModel().user);
                        MyLabelUtils.addShortcut(MainDataBadingActivity.this,
                                "图书馆预约",
                                LzuLibReserveActivity.class.getName());

                        ToastUtils.showShort(String.format(AddLabelViewModel.TIP_TEXT, "图书馆预约"));
                    })
                    .setNegativeButton("取消",null)
                    .show();
            return true;
        });
        binding.layoutAppbarmain.llHealthpunch.setOnLongClickListener(v -> {
            if(binding.getMainViewModel().user==null){
                return true;
            }
            if(TextUtils.isEmpty(binding.getMainViewModel().user.getCardid())){
                return true;
            }
            new AlertDialog.Builder(context)
                    .setTitle("添加桌面快捷方式")
                    .setMessage("是否添加“健康打卡”功能为桌面快捷方式？使用将更方便。")
                    .setPositiveButton("确定",(dialog, which) -> {
                        String login_uid = MySpUtils.getString("login_uid");
                        String login_pwd = MySpUtils.getString("login_pwd");
                        User user = MySpUtils.getObjectData("user");
                        if(TextUtils.isEmpty(login_uid)||TextUtils.isEmpty(login_pwd)||user == null){
                            ToastUtils.showShort("请登录后使用");
                            return;
                        }
                        MySpUtils.SaveObjectData("user",binding.getMainViewModel().user);
                        MyLabelUtils.addShortcut(MainDataBadingActivity.this,
                                "健康打卡",
                                HealthPunchActivity.class.getName());

                        ToastUtils.showShort(String.format(AddLabelViewModel.TIP_TEXT, "健康打卡"));
                    })
                    .setNegativeButton("取消",null)
                    .show();
            return true;
        });
        binding.layoutAppbarmain.llHealthpunchCloudtrusteeship.setOnLongClickListener(v -> {
            binding.getMainViewModel().healthPunchCloudTrusteeshipCancle(v);
            return true;
        });
    }

    @SuppressLint("RtlHardcoded")
    @Override
    protected void initData() {
        //左侧侧滑栏下移状态栏距离
        BarUtils.addMarginTopEqualStatusBarHeight(binding.navView);
        if(getIntent().getBooleanExtra("select_theme",false)){
            binding.drawerLayout.openDrawer(Gravity.LEFT,false);
            binding.navView.getMenu().performIdentifierAction(R.id.drawer_menu_themecolor,0);
        }
        new CheckUpdateManager(context).check(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_refresh:
                binding.getMainViewModel().Refresh(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void LoginSuccess(User user) {
        if(user == null){
            return;
        }
        MenuItem infoMenuItem = binding.navView.getMenu().findItem(R.id.drawer_menu_userinfo);
        binding.navView.getMenu().setGroupVisible(R.id.group_menu3,true);
        infoMenuItem.setTitle(user.getName()+"（"+user.getMailpf()+"）");
    }

    @Override
    public void LoginOut() {
        binding.navView.getMenu().findItem(R.id.drawer_menu_userinfo).setTitle("立即登录");
        binding.navView.getMenu().setGroupVisible(R.id.group_menu3,false);
    }

    @Override
    public void openOrCloseSchoolNetArea(View view) {
        if(binding.layoutAppbarmain.llSchoolNetArea.getVisibility()==View.GONE){
            binding.layoutAppbarmain.llSchoolNetArea.setVisibility(View.VISIBLE);
            binding.layoutAppbarmain.ivSchoolNetArea.setImageResource(R.drawable.ic_chevron_up_outline);
        }else{
            binding.layoutAppbarmain.llSchoolNetArea.setVisibility(View.GONE);
            binding.layoutAppbarmain.ivSchoolNetArea.setImageResource(R.drawable.ic_chevron_down_outline);
        }
    }

    @SuppressLint("RtlHardcoded")
    @Override
    public void onBackPressed() {
        if(binding.drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            binding.drawerLayout.closeDrawers();
        }else if(MySpUtils.getBoolean(SettingFragment.APPRUNINBACKGROUND_KEY)){
            moveTaskToBack(false);
        }else{
            super.onBackPressed();
        }
    }
}
