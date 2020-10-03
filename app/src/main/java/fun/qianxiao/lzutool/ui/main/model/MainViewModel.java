package fun.qianxiao.lzutool.ui.main.model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ViewDataBinding;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.ToastUtils;
import fun.qianxiao.lzutool.BR;
import fun.qianxiao.lzutool.R;
import fun.qianxiao.lzutool.bean.CardInfo;
import fun.qianxiao.lzutool.bean.DormInfo;
import fun.qianxiao.lzutool.bean.SchoolNetInfo;
import fun.qianxiao.lzutool.bean.User;
import fun.qianxiao.lzutool.ui.main.model.baseinfo.GetBaseInfoModel;
import fun.qianxiao.lzutool.ui.main.model.healthpunch.HealthPunchModel;
import fun.qianxiao.lzutool.ui.main.model.healthpunchcloudtrusteeship.CloudTrusteeshipModel;
import fun.qianxiao.lzutool.ui.main.model.lzulibreserve.LzulibreserveModel;
import fun.qianxiao.lzutool.ui.main.model.lzulogin.LzuloginModel;
import fun.qianxiao.lzutool.ui.main.IMainView;
import fun.qianxiao.lzutool.ui.main.MainDataBadingActivity;
import fun.qianxiao.lzutool.ui.main.model.qxj.QxjModel;
import fun.qianxiao.lzutool.ui.setting.SettingFragment;
import fun.qianxiao.lzutool.utils.MySpUtils;
import fun.qianxiao.lzutool.view.MyLoadingDialog;

/**
 * Create by QianXiao
 * On 2020/9/27
 */
public class MainViewModel extends BaseObservable implements IClickView {
    private Context context;
    public IMainView iMainView;
    @Bindable
    public User user;
    private LzulibreserveModel lzulibreserveModel;
    private HealthPunchModel healthPunchModel;
    private CloudTrusteeshipModel cloudTrusteeshipModel;
    private LzuloginModel lzuloginModel;
    private GetBaseInfoModel getBaseInfoModel;
    private QxjModel qxjModel;
    private MyLoadingDialog loadingDialog;

    public MainViewModel(MainDataBadingActivity mainActivity) {
        context = mainActivity;
        iMainView = mainActivity;
        lzuloginModel = new LzuloginModel(context,new LzuloginModel.LoginCallback() {
            @Override
            public void onLogining() {
                openLoadingDialog("正在登录");
            }

            @Override
            public void onLoginFail(String error) {
                closeLoadingDialog();
                ToastUtils.showShort(error);
            }

            @Override
            public void onLoginSuccess(User user) {
                MySpUtils.SaveObjectData("user",user);
                closeLoadingDialog();
                iMainView.ShowSnackbar(ContextCompat.getColor(context, iMainView.getColorPrimaryId()),"登录成功");
                MainViewModel.this.user = user;
                iMainView.LoginSuccess(user);
                //更新UI
                notifyPropertyChanged(BR.user);
                Refresh(false);
            }
        });
        User user_local = MySpUtils.getObjectData("user");
        if(user_local == null){
            Login();
        }else{
            user = user_local;
            DormInfo dormInfo_local = MySpUtils.getObjectData("dormInfo");
            if(dormInfo_local != null){
                user.setDormInfo(dormInfo_local);
            }
            iMainView.LoginSuccess(user);
            //更新UI
            notifyPropertyChanged(BR.user);
            if(!((MainDataBadingActivity)context).getIntent().getBooleanExtra("select_theme",false)){
                Refresh(true);
            }
        }
    }

    public void Login(){
        if(user != null){
            ToastUtils.showShort("已登录"+user.getCardid());
            return;
        }
        String login_uid = MySpUtils.getString("login_uid");
        String login_pwd = MySpUtils.getString("login_pwd");
        if(!TextUtils.isEmpty(login_uid) && !TextUtils.isEmpty(login_pwd)){
            //自动登录
            lzuloginModel.login(login_uid, login_pwd);
        }else{
            lzuloginModel.showLoginDialog();
        }
    }

    public void LoginOut(){
        iMainView.LoginOut();
        MySpUtils.save("login_uid","");
        MySpUtils.save("login_pwd","");
        user = null;
        notifyPropertyChanged(BR.user);
    }

    public void Refresh(boolean flag){
        if(user == null){
            lzuloginModel.showLoginDialog();
            return;
        }
        if(flag){
            if(user.getDormInfo()!=null){
                user.getDormInfo().setBlance(null);
            }
            user.setCardInfo(null);
            user.setSchoolNetInfo(null);
            user.setQxjStatu(null);
            //更新UI
            notifyPropertyChanged(BR.user);
            openLoadingDialog("正在刷新");
        }
        if(getBaseInfoModel == null){
            getBaseInfoModel = new GetBaseInfoModel(context);
        }
        getBaseInfoModel.getWalletMoney(user.getAccnum(), new GetBaseInfoModel.GetWalletMoneyModelCallBack() {
            @Override
            public void onGetWalletMoneySuccess(String yu_e,String dzzh_yu_e) {
                if(flag){
                    closeLoadingDialog();
                }
                CardInfo cardInfo = new CardInfo();
                cardInfo.setCard_yu_e(yu_e);
                cardInfo.setCard_dzzh_yu_e(dzzh_yu_e);
                user.setCardInfo(cardInfo);
                //更新UI
                notifyPropertyChanged(BR.user);
            }

            @Override
            public void onGetWalletMoneyError(String error) {
                if(flag){
                    closeLoadingDialog();
                }
                ToastUtils.showShort(error);
                user.setCardInfo(null);
                //更新UI
                notifyPropertyChanged(BR.user);
            }
        });
        String login_uid = MySpUtils.getString("login_uid");
        String login_pwd = MySpUtils.getString("login_pwd");
        boolean isYjs = user.getCardid().startsWith("2");
        //登录获取tgt然后登录智慧学工然后获取宿舍信息然后获取宿舍电费
        lzuloginModel.loginGetTGT(login_uid, login_pwd, new LzuloginModel.LoginGetTGTCallBack() {
            @Override
            public void onLoginGetTGTSuccess(String tgt) {
                LogUtils.i(tgt);
                //登录智慧学工
                lzuloginModel.loginZhxgGetJsessionidAndRoute(tgt, new LzuloginModel.LoginZhxgGetJsessionidAndRouteCallBack() {
                    @Override
                    public void onLoginZhxgGetJsessionidAndRouteSuccess(String zhxgcookies) {
                        LogUtils.i(zhxgcookies);
                        if(user.getDormInfo()==null){
                            //获取宿舍信息
                            getBaseInfoModel.getDormInfo(zhxgcookies, new GetBaseInfoModel.GetDormInfoCallBack() {
                                @Override
                                public void onGetDormInfoSuccess(DormInfo dormInfo) {
                                    LogUtils.i(dormInfo);
                                    MySpUtils.SaveObjectData("dormInfo",dormInfo);
                                    user.setDormInfo(dormInfo);
                                    //更新UI 宿舍名称
                                    notifyPropertyChanged(BR.user);
                                    //获取宿舍电费
                                    getDromBlance(dormInfo.getDormno());
                                }

                                @Override
                                public void onGetDormInfoError(String error) {
                                    LogUtils.e(error);
                                    ToastUtils.showShort("宿舍信息获取失败（"+error+")");
                                }
                            });
                        }else{
                            //获取宿舍电费
                            getDromBlance(user.getDormInfo().getDormno());
                        }
                        //获取请假状态
                        if(qxjModel == null){
                            qxjModel = new QxjModel();
                        }
                        qxjModel.getListStu(zhxgcookies, isYjs, new QxjModel.GetListStuCallBack() {
                            @Override
                            public void onGetListStuSuccess(QxjModel.QxjStatu qxjStatu) {
                                LogUtils.i(qxjStatu);
                                user.setQxjStatu(qxjStatu);
                                //更新UI
                                notifyPropertyChanged(BR.user);
                            }

                            @Override
                            public void onGetListStuError(String error) {
                                LogUtils.e(error);
                                ToastUtils.showShort("onGetListStuError:"+error);
                            }
                        });
                    }
                    //获取宿舍电费
                    void getDromBlance(String dormno){
                        LogUtils.i("获取宿舍电费:"+dormno);
                        getBaseInfoModel.getDormBlance(dormno, new GetBaseInfoModel.GetDormBlanceCallBack() {
                            @Override
                            public void onGetDormBlanceSuccess(String blance) {
                                LogUtils.i(blance);
                                if(flag){
                                    closeLoadingDialog();
                                }
                                user.getDormInfo().setBlance(blance);
                                //更新UI 电费
                                notifyPropertyChanged(BR.user);
                            }

                            @Override
                            public void onGetDormBlanceError(String error) {
                                LogUtils.e(error);
                                if(flag){
                                    closeLoadingDialog();
                                }
                                //ToastUtils.showShort(error);
                            }
                        });
                    }
                    @Override
                    public void onLoginZhxgGetJsessionidAndRouteError(String error) {
                        LogUtils.e(error);
                        ToastUtils.showShort("onLoginZhxgGetJsessionidAndRouteError("+error+")");
                    }
                });
            }

            @Override
            public void onLoginGetTGTError(String error) {
                LogUtils.e(error);
                ToastUtils.showShort("onLoginGetTGTError("+error+")");
            }
        });
        //获取校园网信息
        getBaseInfoModel.getLzuNetInfo(new GetBaseInfoModel.GetLzuNetInfoCallBack() {
            @Override
            public void onGetLzuNetInfoSuccess(SchoolNetInfo schoolNetInfo) {
                user.setSchoolNetInfo(schoolNetInfo);
                //更新UI 校园网信息
                notifyPropertyChanged(BR.schoolNetInfo);
            }

            @Override
            public void onGetLzuNetInfoError(String error) {
                LogUtils.e(error);
                //ToastUtils.showShort(error);
            }
        });
    }

    /**
     * 图书馆预约点击
     * @param view
     */
    @Override
    public void LzuLibReserve(View view){
        if(lzulibreserveModel == null){
            lzulibreserveModel = new LzulibreserveModel();
        }
        if(user == null){
            lzuloginModel.showLoginDialog();
            LogUtils.i("user is null");
            return;
        }
        int lastselectedcampus = MySpUtils.getInt("reserve_campus");
        if(!MySpUtils.getString(SettingFragment.LZULIBRESERVE_KEY).equals("0")){
            reserve(Integer.parseInt(MySpUtils.getString(SettingFragment.LZULIBRESERVE_KEY)));
        }else{
            final int[] selectcampus = {lastselectedcampus};
            new AlertDialog.Builder(context)
                    .setTitle("图书馆预约")
                    .setSingleChoiceItems(R.array.reserve_campus, lastselectedcampus, (dialog, which) -> selectcampus[0] = which)
                    .setPositiveButton("确定", (dialog, which) -> {
                        MySpUtils.save("reserve_campus",selectcampus[0]);
                        dialog.dismiss();
                        reserve(selectcampus[0]);
                    })
                    .setNegativeButton("确定并不再提示", (dialog, which) -> {
                        MySpUtils.save("reserve_campus",selectcampus[0]);
                        MySpUtils.save(SettingFragment.LZULIBRESERVE_KEY,String.valueOf(selectcampus[0]+1));
                        dialog.dismiss();
                        ToastUtils.showShort("可在设置中修改");
                        reserve(selectcampus[0]);
                    }).show();
        }
    }

    private void reserve(int selectcampus) {
        if(!TextUtils.isEmpty(user.getCardid())){
            openLoadingDialog("正在预约");
            lzulibreserveModel.reserve(user.getCardid(),selectcampus==1, res -> {
                closeLoadingDialog();
                if(res.contains("已预约")){
                    iMainView.ShowSnackbar(ContextCompat.getColor(context,R.color.warning),res);
                }else if(res.contains("预约成功")){
                    iMainView.ShowSnackbar(ContextCompat.getColor(context, iMainView.getColorPrimaryId()),res);
                }else{
                    iMainView.ShowSnackbar(res);
                }
            });
        }
    }

    /**
     * 健康打卡点击
     * @param view
     */
    @Override
    public void healthPunch(View view){
        if(healthPunchModel == null){
            healthPunchModel = new HealthPunchModel();
        }
        if(user == null){
            lzuloginModel.showLoginDialog();
            LogUtils.i("user is null");
            return;
        }
        openLoadingDialog("正在打卡");
        String login_uid = MySpUtils.getString("login_uid");
        String login_pwd = MySpUtils.getString("login_pwd");
        lzuloginModel.loginGetTGT(login_uid, login_pwd, new LzuloginModel.LoginGetTGTCallBack() {

            @Override
            public void onLoginGetTGTSuccess(String tgt) {
                LogUtils.i(tgt);
                lzuloginModel.getStByTGT(tgt, new LzuloginModel.GetStByTGTCallBack() {
                    @Override
                    public void onGetStByTGTSuccess(String st) {
                        LogUtils.i(st);
                        healthPunchModel.punch(st, user.getCardid(), new HealthPunchModel.HealthPunchCallBack() {
                            @Override
                            public void onHealthPunchSuccess() {
                                closeLoadingDialog();
                                iMainView.ShowSnackbar(ContextCompat.getColor(context, iMainView.getColorPrimaryId()),"打卡成功");
                            }

                            @Override
                            public void onHealthPunchError(String error) {
                                LogUtils.e(error);
                                ToastUtils.showShort(error);
                                closeLoadingDialog();
                            }
                        });
                    }

                    @Override
                    public void onGetStByTGTError(String error) {
                        LogUtils.e(error);
                        ToastUtils.showShort(error);
                        closeLoadingDialog();
                    }
                });
            }

            @Override
            public void onLoginGetTGTError(String error) {
                LogUtils.e(error);
                ToastUtils.showShort(error);
                closeLoadingDialog();
            }
        });
    }

    /**
     * 健康打卡云托管点击
     * @param view
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void healthPunchCloudTrusteeship(View view) {
        if(user == null){
            ToastUtils.showShort("请登录后使用");
            lzuloginModel.showLoginDialog();
            return;
        }
        String login_uid = MySpUtils.getString("login_uid");
        String login_pwd = MySpUtils.getString("login_pwd");
        if(TextUtils.isEmpty(login_uid)||TextUtils.isEmpty(login_pwd)){
            return;
        }
        new AlertDialog.Builder(context)
                .setTitle("健康打卡云托管")
                .setMessage("目前支持打卡云托管服务，提交账户信息至服务器，每日由服务器自动上报打卡，免去您每日打卡的烦恼。\n" +
                        "您也可以访问云托管网页系统（http://mrsb.qianxiao.fun）手动提交或查看更多详情。\n" +
                        "温馨提示：如需提交托管后取消托管，您可以长按健康打卡云托管图标取消或访问网页系统进行操作。\n" +
                        "是否立即提交云托管？")
                .setPositiveButton("立即云托管",(dialog, which) -> {
                    if(cloudTrusteeshipModel == null){
                        cloudTrusteeshipModel = new CloudTrusteeshipModel();
                    }
                    EditText editText = new EditText(context);
                    editText.setHint("请输入提醒邮箱");
                    LinearLayout l1 = new LinearLayout(context);
                    l1.setOrientation(LinearLayout.VERTICAL);
                    LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp1.setMargins(0, ConvertUtils.dp2px(20),0,0);
                    l1.setLayoutParams(lp1);
                    l1.setPadding(ConvertUtils.dp2px(10),0,ConvertUtils.dp2px(10),0);
                    l1.addView(editText);
                    TextView textView = new TextView(context);
                    textView.setText("邮箱用于接受打卡结果，建议填写常用邮箱。（如未能正常接受邮件，请设置qianxiao.fun为域名白名单）");
                    l1.addView(textView);
                    AlertDialog dialog1 = new AlertDialog.Builder(context)
                            .setTitle("健康打卡云托管")
                            .setView(l1)
                            .setPositiveButton("确定",null)
                            .setNegativeButton("取消",null)
                            .setNeutralButton("使用兰大邮箱",null)
                            .show();
                    editText.requestFocus();
                    dialog1.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(v -> {
                        editText.setText(user.getMailpf()+"@lzu.edu.cn");
                        editText.requestFocus();
                        editText.setSelection(editText.getText().length());
                    });
                    dialog1.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                        String email = editText.getText().toString().trim();
                        if(TextUtils.isEmpty(email)){
                            ToastUtils.showShort("请输入提醒邮箱");
                            editText.requestFocus();
                            if(!KeyboardUtils.isSoftInputVisible((Activity) context)){
                                KeyboardUtils.showSoftInput(editText);
                            }
                        }else if(!RegexUtils.isEmail(email)){
                            ToastUtils.showShort("邮箱非法");
                            editText.requestFocus();
                        }else{
                            KeyboardUtils.hideSoftInput(editText);
                            dialog1.dismiss();
                            openLoadingDialog("正在提交");
                            cloudTrusteeshipModel.trusteeshipSubmit(login_uid, login_pwd, email, new CloudTrusteeshipModel.TrusteeshipOperationCallBack() {
                                @Override
                                public void onTrusteeshipOperationSuccess(String res) {
                                    closeLoadingDialog();
                                    iMainView.ShowSnackbar(ContextCompat.getColor(context, iMainView.getColorPrimaryId()),res);
                                }

                                @Override
                                public void onTrusteeshipOperationError(String error) {
                                    LogUtils.e(error);
                                    closeLoadingDialog();
                                    ToastUtils.showShort(error);
                                }
                            });
                        }
                    });
                })
                .setNegativeButton("取消",null)
                .setNeutralButton("访问云托管系统",(dialog, which) -> {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    intent.setData(Uri.parse("http://mrsb.qianxiao.fun"));
                    context.startActivity(intent);
                })
                .show();
    }

    /**
     * 健康打卡云托管长按
     * @param view
     */
    @SuppressLint("SetTextI18n")
    public void healthPunchCloudTrusteeshipCancle(View view){
        new AlertDialog.Builder(context)
                .setTitle("健康打卡云托管")
                .setMessage("是否取消托管？")
                .setPositiveButton("取消托管",(dialog, which) -> {
                    if(user == null){
                        ToastUtils.showShort("请登录后使用");
                        lzuloginModel.showLoginDialog();
                        return;
                    }
                    String login_uid = MySpUtils.getString("login_uid");
                    String login_pwd = MySpUtils.getString("login_pwd");
                    if(TextUtils.isEmpty(login_uid)||TextUtils.isEmpty(login_pwd)){
                        return;
                    }
                    if(cloudTrusteeshipModel == null){
                        cloudTrusteeshipModel = new CloudTrusteeshipModel();
                    }
                    EditText editText = new EditText(context);
                    editText.setHint("请输入提醒邮箱进行验证");
                    LinearLayout l1 = new LinearLayout(context);
                    l1.setOrientation(LinearLayout.VERTICAL);
                    LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp1.setMargins(0, ConvertUtils.dp2px(20),0,0);
                    l1.setLayoutParams(lp1);
                    l1.setPadding(ConvertUtils.dp2px(10),0,ConvertUtils.dp2px(10),0);
                    l1.addView(editText);
                    AlertDialog dialog1 = new AlertDialog.Builder(context)
                            .setTitle("健康打卡云托管")
                            .setView(l1)
                            .setPositiveButton("确定",null)
                            .setNegativeButton("取消",null)
                            .setNeutralButton("使用兰大邮箱",null)
                            .show();
                    editText.requestFocus();
                    dialog1.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(v -> {
                        editText.setText(user.getMailpf()+"@lzu.edu.cn");
                        editText.requestFocus();
                        editText.setSelection(editText.getText().length());
                    });
                    dialog1.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                        String email = editText.getText().toString().trim();
                        if(TextUtils.isEmpty(email)){
                            ToastUtils.showShort("请输入提醒邮箱");
                            editText.requestFocus();
                            if(!KeyboardUtils.isSoftInputVisible((Activity) context)){
                                KeyboardUtils.showSoftInput(editText);
                            }
                        }else if(!RegexUtils.isEmail(email)){
                            ToastUtils.showShort("邮箱非法");
                            editText.requestFocus();
                        }else{
                            KeyboardUtils.hideSoftInput(editText);
                            dialog1.dismiss();
                            openLoadingDialog("正在取消");
                            cloudTrusteeshipModel.cancleTrusteeship(login_uid,login_pwd,email, new CloudTrusteeshipModel.TrusteeshipOperationCallBack(){

                                @Override
                                public void onTrusteeshipOperationSuccess(String res) {
                                    closeLoadingDialog();
                                    iMainView.ShowSnackbar(ContextCompat.getColor(context, iMainView.getColorPrimaryId()),res);
                                }

                                @Override
                                public void onTrusteeshipOperationError(String error) {
                                    LogUtils.e(error);
                                    ToastUtils.showShort(error);
                                    closeLoadingDialog();
                                }
                            });
                        }
                    });
                })
                .setNegativeButton("取消",null)
                .show();
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
}
