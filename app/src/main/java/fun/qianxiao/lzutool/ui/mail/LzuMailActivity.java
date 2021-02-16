package fun.qianxiao.lzutool.ui.mail;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.blankj.utilcode.util.ToastUtils;

import fun.qianxiao.lzutool.R;
import fun.qianxiao.lzutool.base.BaseDateBadingFeagment;
import fun.qianxiao.lzutool.base.BaseFragment;
import fun.qianxiao.lzutool.base.BaseFragmentActivity;
import fun.qianxiao.lzutool.databinding.FragmentMailInboxBinding;
import fun.qianxiao.lzutool.ui.mail.adapter.MailAdapter;
import fun.qianxiao.lzutool.ui.mail.adapter.listener.EndlessRecyclerOnScrollListener;
import fun.qianxiao.lzutool.ui.mail.bean.MailInfo;
import fun.qianxiao.lzutool.ui.mail.detail.MailDetailFragment;
import fun.qianxiao.lzutool.ui.mail.inbox.InBoxFragment;
import fun.qianxiao.lzutool.ui.mail.inbox.InBoxViewModel;
import fun.qianxiao.lzutool.ui.mail.model.LzuMailModel;
import fun.qianxiao.lzutool.view.MyLoadingDialog;

public class LzuMailActivity extends BaseFragmentActivity implements ILzuMailView{
    private MyLoadingDialog loadingDialog;
    private BaseDateBadingFeagment inboxFragment,mailDetailFragment;

    @Override
    protected void initData() {
        setTitle("LZU邮箱");
        showBackButton();
        Spinner spinner_toolbar = findViewById(R.id.spinner_toolbar);
        spinner_toolbar.setVisibility(View.VISIBLE);
        spinner_toolbar.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"收件箱", "已发送", "草稿箱", "已删除", "垃圾邮件", "病毒文件夹"}));
        spinner_toolbar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view;
                textView.setTextColor(-1);//Spinner文字显示白色
                switch (position){
                    case 0:
                        ((InBoxFragment)inboxFragment).binding.getInBoxViewModel().currerntMailType = LzuMailModel.MailType.InBox;
                        break;
                    case 1:
                        ((InBoxFragment)inboxFragment).binding.getInBoxViewModel().currerntMailType = LzuMailModel.MailType.HasSent;
                        break;
                    case 2:
                        ((InBoxFragment)inboxFragment).binding.getInBoxViewModel().currerntMailType = LzuMailModel.MailType.DraftsBox;
                        break;
                    case 3:
                        ((InBoxFragment)inboxFragment).binding.getInBoxViewModel().currerntMailType = LzuMailModel.MailType.HasDeleted;
                        break;
                    case 4:
                        ((InBoxFragment)inboxFragment).binding.getInBoxViewModel().currerntMailType = LzuMailModel.MailType.JunkMail;
                        break;
                    case 5:
                        ((InBoxFragment)inboxFragment).binding.getInBoxViewModel().currerntMailType = LzuMailModel.MailType.VirusMail;
                        break;
                }
                //清除过滤
                ((InBoxFragment)fragment).binding.getInBoxViewModel().currentFItterType = InBoxViewModel.FItterType.None;
                //切换默认排序
                ((InBoxFragment)fragment).binding.getInBoxViewModel().currentOrderType = InBoxViewModel.OrderType.ReceivedDate;
                ((InBoxFragment)fragment).binding.getInBoxViewModel().isDesc = true;

                ((InBoxFragment)fragment).binding.getInBoxViewModel().Refresh();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_toolbar.setSelection(0);



    }

    @Override
    protected Fragment getFragment() {
        inboxFragment = new InBoxFragment(this);
        return inboxFragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mail_activity, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_refresh:
                ((InBoxFragment)fragment).binding.getInBoxViewModel().Refresh();
                return true;
            //排序
            case R.id.mail_menu_sort_time:
                ((InBoxFragment)fragment).binding.getInBoxViewModel().currentOrderType = InBoxViewModel.OrderType.ReceivedDate;
                return true;
            case R.id.mail_menu_sort_from:
                ((InBoxFragment)fragment).binding.getInBoxViewModel().currentOrderType = InBoxViewModel.OrderType.From;
                return true;
            case R.id.mail_menu_sort_subject:
                ((InBoxFragment)fragment).binding.getInBoxViewModel().currentOrderType = InBoxViewModel.OrderType.Subject;
                return true;
            case R.id.mail_menu_sort_attachedsize:
                ((InBoxFragment)fragment).binding.getInBoxViewModel().currentOrderType = InBoxViewModel.OrderType.FlagsAttached;
                return true;
            case R.id.menu_sort_desc:
                ((InBoxFragment)fragment).binding.getInBoxViewModel().isDesc = true;
                break;
            case R.id.menu_sort_asc:
                ((InBoxFragment)fragment).binding.getInBoxViewModel().isDesc = false;
                break;
            //过滤
            case R.id.mail_menu_fitter_noread:
                ((InBoxFragment)fragment).binding.getInBoxViewModel().currentFItterType = InBoxViewModel.FItterType.NoRead;
                break;
            case R.id.mail_menu_fitter_agent:
                ((InBoxFragment)fragment).binding.getInBoxViewModel().currentFItterType = InBoxViewModel.FItterType.Agent;
                break;
            case R.id.mail_menu_fitter_flag:
                ((InBoxFragment)fragment).binding.getInBoxViewModel().currentFItterType = InBoxViewModel.FItterType.Flag;
                break;
            case R.id.mail_menu_fitter_reply:
                ((InBoxFragment)fragment).binding.getInBoxViewModel().currentFItterType = InBoxViewModel.FItterType.Reply;
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        ((InBoxFragment)fragment).binding.getInBoxViewModel().Refresh();
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
        if(mailDetailFragment == null){
            super.onBackPressed();
        }else if(!mailDetailFragment.onBackPressed()){
            super.onBackPressed();
        }
    }

    @Override
    public void enterMailDetail(MailInfo mailInfo) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction()
                .hide(inboxFragment);
        mailDetailFragment = new MailDetailFragment(this ,mailInfo);
        fragmentTransaction.add(R.id.framelayout_lf, mailDetailFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void back() {
        if(mailDetailFragment != null){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction()
                    .remove(mailDetailFragment);
            fragmentTransaction.show(inboxFragment);
            fragmentTransaction.commit();
            mailDetailFragment = null;
        }
    }
}
