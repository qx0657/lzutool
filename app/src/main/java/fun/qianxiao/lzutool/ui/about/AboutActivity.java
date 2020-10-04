package fun.qianxiao.lzutool.ui.about;

import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.AlertDialog;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ToastUtils;

import fun.qianxiao.lzutool.R;
import fun.qianxiao.lzutool.base.BaseDataBadingActivity;
import fun.qianxiao.lzutool.checkupdate.CheckUpdateManager;
import fun.qianxiao.lzutool.databinding.ActivityAboutBinding;

/**
 * Create by QianXiao
 * On 2020/10/3
 */
public class AboutActivity extends BaseDataBadingActivity<ActivityAboutBinding> implements IAboutViewModel{
    private CheckUpdateManager checkUpdateManager;

    @Override
    protected int getLayoutID() {
        isAddToolbarMarginTopEqualStatusBarHeight = true;
        return R.layout.activity_about;
    }

    @Override
    protected void initViewModel() {
        binding.setVersion(AppUtils.getAppVersionName());
        binding.setIAvoutViewModel(this);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
        setTitle("关于");
        showBackButton();
    }

    @Override
    public void aboutApp() {
        new AlertDialog.Builder(this)
                    .setTitle("关于")
                    .setMessage("这是一款LZU小工具\n" +
                            "方便您查校园卡余额、宿舍电费、校园网信息、请假状态，支持一键图书馆预约、一键健康打卡、一键下载成绩单、一键校园卡挂失解挂，更多功能，一切只为简单。" +
                            "\n\n" +
                            "作者：浅笑\n" +
                            "软件已完全开源，欢迎技术交流或一起努力完善。")
                    .setPositiveButton("Github地址", (dialog1, which) -> {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri content_url = Uri.parse("https://github.com/qx0657/lzutool");
                        intent.setData(content_url);
                        startActivity(intent);
                    })
                    .setNeutralButton("访问作者主页", (dialog1, which) -> {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri content_url = Uri.parse("http://qianxiao.fun");
                        intent.setData(content_url);
                        startActivity(intent);
                    })
                    .show();
    }

    @Override
    public void checkUpdate() {
        if(checkUpdateManager == null){
            checkUpdateManager = new CheckUpdateManager(context);
        }
        checkUpdateManager.check(false);
    }

    @Override
    public void joinQQGroup() {
        String qqgroupkey = "h3iICuehFOCxoGcTD7uobt6idMZcaP8U";
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D" + qqgroupkey));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            ToastUtils.showShort("未安装手Q或安装的版本不支持");
        }
    }

    @Override
    public void shareApp() {
        String ShareStr = "LZU小工具是一款方便您查校园卡余额、宿舍电费、校园网信息、请假状态等的软件，软件支持一键图书馆预约、一键健康打卡、一键下载成绩单、一键校园卡挂失解挂，更多功能，一切只为简单。\n下载地址：\n" +
                "http://lzutool.qianxiao.fun/";
        Intent StringIntent = new Intent(Intent.ACTION_SEND);
        StringIntent.setType("text/plain");
        StringIntent.putExtra(Intent.EXTRA_TEXT, ShareStr);
        startActivity(Intent.createChooser(StringIntent, "分享应用"));
    }

    @Override
    public void contactDeveloper() {
        String url = "mqqwpa://im/chat?chat_type=wpa&uin=1540223760";
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    @Override
    public void supportDeveloper() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setData(Uri.parse("https://ko-fi.com/qx0657"));
        startActivity(intent);
    }
}
