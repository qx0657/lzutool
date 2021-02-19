package fun.qianxiao.lzutool.ui.main.model;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.fragment.app.FragmentActivity;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.UriUtils;
import com.blankj.utilcode.util.Utils;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import fun.qianxiao.lzutool.BR;
import fun.qianxiao.lzutool.R;
import fun.qianxiao.lzutool.bean.CardInfo;
import fun.qianxiao.lzutool.bean.DormInfo;
import fun.qianxiao.lzutool.bean.SchoolNetInfo;
import fun.qianxiao.lzutool.bean.User;
import fun.qianxiao.lzutool.ui.mail.LzuMailActivity;
import fun.qianxiao.lzutool.ui.main.model.baseinfo.GetBaseInfoModel;
import fun.qianxiao.lzutool.ui.main.model.cardreportlossorcanclereportloss.CardReportLossModel;
import fun.qianxiao.lzutool.ui.main.model.ecardservices.EcardServicesModel;
import fun.qianxiao.lzutool.ui.main.model.ecardservices.TransferYueDialogFragment;
import fun.qianxiao.lzutool.ui.main.model.ecardservices.TransferYueModel;
import fun.qianxiao.lzutool.ui.main.model.healthpunch.HealthPunchModel;
import fun.qianxiao.lzutool.ui.main.model.healthpunchcloudtrusteeship.CloudTrusteeshipModel;
import fun.qianxiao.lzutool.ui.main.model.lzuoafileupload.view.UploadFragmentDialog;
import fun.qianxiao.lzutool.ui.main.model.lzulibreserve.LzulibreserveModel;
import fun.qianxiao.lzutool.ui.main.model.lzulogin.LzuloginModel;
import fun.qianxiao.lzutool.ui.main.IMainView;
import fun.qianxiao.lzutool.ui.main.MainDataBadingActivity;
import fun.qianxiao.lzutool.ui.main.model.lzuoafileupload.FileUploadModel;
import fun.qianxiao.lzutool.ui.main.model.qxj.QxjModel;
import fun.qianxiao.lzutool.ui.main.model.resetpwd.ReSetPwdModel;
import fun.qianxiao.lzutool.ui.main.model.schoolbus.SchoolBusModel;
import fun.qianxiao.lzutool.ui.main.model.undergraduatecertificate.SchoolReportDownloadModel;
import fun.qianxiao.lzutool.ui.personalinf.PersonalnfActivity;
import fun.qianxiao.lzutool.ui.setting.SettingFragment;
import fun.qianxiao.lzutool.utils.ClipboardUtils;
import fun.qianxiao.lzutool.utils.MyCookieUtils;
import fun.qianxiao.lzutool.utils.MySpUtils;
import fun.qianxiao.lzutool.view.MyLoadingDialog;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

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
    private String zhxgcookie;
    private final int CHOISEFILE_REQUESTCODE = 1001;

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
        MySpUtils.remove("login_uid");
        MySpUtils.remove("login_pwd");
        user = null;
        MySpUtils.remove("user");
        MySpUtils.remove("dormInfo");
        MySpUtils.remove("cardid_md5");
        ((MainDataBadingActivity)context).binding.layoutAppbarmain.tvHealthpunchText.setText("健康打卡");
        ((MainDataBadingActivity)context).binding.layoutAppbarmain.tvHealthpunchText.setTextColor(Color.parseColor("#888888"));
        notifyPropertyChanged(BR.user);
    }

    public void Refresh(boolean flag){
        Refresh(flag,false);
    }

    public void Refresh(boolean flag,boolean onlyRefreshWallet){
        if(user == null){
            lzuloginModel.showLoginDialog();
            return;
        }
        if(getBaseInfoModel == null){
            getBaseInfoModel = new GetBaseInfoModel(context);
        }
        if(onlyRefreshWallet){
            user.setCardInfo(null);
            notifyPropertyChanged(BR.user);
            if(flag){
                openLoadingDialog("正在刷新");
            }
        }
        MySpUtils.remove("mrsb_token");
        ((MainDataBadingActivity)context).binding.layoutAppbarmain.tvHealthpunchText.setText("健康打卡");
        ((MainDataBadingActivity)context).binding.layoutAppbarmain.tvHealthpunchText.setTextColor(Color.parseColor("#888888"));

        getBaseInfoModel.getWalletMoney(user.getAccnum(), new GetBaseInfoModel.GetWalletMoneyModelCallBack() {
            @Override
            public void onGetWalletMoneySuccess(String yu_e,String dzzh_yu_e) {
                LogUtils.i("yu_e:"+yu_e,
                        "dzzh_yu_e:"+dzzh_yu_e);
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
        if(onlyRefreshWallet){
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
        String login_uid = MySpUtils.getString("login_uid");
        String login_pwd = MySpUtils.getString("login_pwd");
        boolean isYjs = user.getCardid().startsWith("2");
        //登录获取tgt然后登录智慧学（研）工然后获取宿舍信息然后获取宿舍电费And获取请假状态
        lzuloginModel.loginGetTGT(login_uid, login_pwd, new LzuloginModel.LoginGetTGTCallBack() {
            @Override
            public void onLoginGetTGTSuccess(String tgt) {
                LogUtils.i(tgt);
                //登录智慧学工/研工--->获取宿舍信息/电费--->获取请假状态
                lzuloginModel.loginZhxgGetJsessionidAndRoute(tgt,isYjs, new LzuloginModel.LoginZhxgGetJsessionidAndRouteCallBack() {
                    @Override
                    public void onLoginZhxgGetJsessionidAndRouteSuccess(String zhxgcookies) {
                        LogUtils.i(zhxgcookies);
                        MainViewModel.this.zhxgcookie = zhxgcookies;
                        if(user.getDormInfo()==null){
                            if(!isYjs){
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
                                        getDromBlance(dormInfo.getDormno(),true);
                                    }

                                    @Override
                                    public void onGetDormInfoError(String error) {
                                        LogUtils.e(error);
                                        ToastUtils.showShort("宿舍信息获取失败（"+error+")，请刷新重试");
                                    }
                                });
                            }
                        }else{
                            //获取宿舍电费
                            getDromBlance(user.getDormInfo().getDormno(),true);
                        }
                        //获取请假状态
                        if(qxjModel == null){
                            qxjModel = new QxjModel();
                        }
                        qxjModel.getListStu(zhxgcookies, isYjs, new QxjModel.GetListStuCallBack() {
                            @Override
                            public void onGetUserSomeInfoSuccess(String dh, String wx, String qq) {
                                user.setDh(dh);
                                user.setWx(wx);
                                user.setQq(qq);
                            }

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
                                ToastUtils.showShort("请销假列表获取失败（"+error+"），请刷新重试");
                            }
                        });
                    }
                    @Override
                    public void onLoginZhxgGetJsessionidAndRouteError(String error) {
                        LogUtils.e(error);
                        ToastUtils.showShort(error);
                    }
                });

                lzuloginModel.getStByTGT(tgt, new LzuloginModel.GetStByTGTCallBack() {
                    @Override
                    public void onGetStByTGTSuccess(String st) {
                        //获取健康打卡状态
                        if(healthPunchModel == null){
                            healthPunchModel = new HealthPunchModel();
                        }
                        healthPunchModel.getPunchStatus(st, user.getCardid(), new HealthPunchModel.GetPunchStatusCallBack() {
                            @Override
                            public void onGetPunchStatusSuccess(boolean dkzt) {
                                if(dkzt){
                                    ((MainDataBadingActivity)context).binding.layoutAppbarmain.tvHealthpunchText.setText("已打卡");
                                    ((MainDataBadingActivity)context).binding.layoutAppbarmain.tvHealthpunchText.setTextColor(Color.parseColor("#32CD32"));
                                }else{
                                    ((MainDataBadingActivity)context).binding.layoutAppbarmain.tvHealthpunchText.setText("未打卡");
                                    ((MainDataBadingActivity)context).binding.layoutAppbarmain.tvHealthpunchText.setTextColor(Color.parseColor("#FF4500"));
                                }
                            }

                            @Override
                            public void onGetPunchStatusError(String error) {
                                ToastUtils.showShort("健康打卡状态获取失败");
                            }
                        });
                    }

                    @Override
                    public void onGetStByTGTError(String error) {
                        ToastUtils.showShort("健康打卡状态获取失败");
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


    //获取宿舍电费
    private void getDromBlance(String dormno,boolean isSilent){
        LogUtils.i("获取宿舍电费:"+dormno);
        if(!isSilent){
            openLoadingDialog("正在获取宿舍电费");
        }
        getBaseInfoModel.getDormDetailInfo(dormno, new GetBaseInfoModel.GetDormDetailInfoCallBack() {
            @Override
            public void onGetDormDetailInfoSuccess(String areano,String buildingno,String floorno,String roomid) {
                user.getDormInfo().setBlance("正在查询");
                notifyPropertyChanged(BR.user);
                LogUtils.i("从浅笑服务器获取宿舍基本信息",
                        "areano:"+areano,
                        "buildingno:"+buildingno,
                        "floorno:"+floorno,
                        "roomid:"+roomid
                        );
                //if(flag){
                //closeLoadingDialog();
                //}
                user.getDormInfo().setAreano(areano);
                user.getDormInfo().setBuildingno(buildingno);
                user.getDormInfo().setFloorno(floorno);
                user.getDormInfo().setRoomno(roomid);
                //重新获取电费
                String login_uid = MySpUtils.getString("login_uid");
                String login_pwd = MySpUtils.getString("login_pwd");
                if(TextUtils.isEmpty(login_uid)||TextUtils.isEmpty(login_pwd)){
                    return;
                }
                lzuloginModel.loginGetTGT(login_uid, login_pwd, new LzuloginModel.LoginGetTGTCallBack() {
                    @Override
                    public void onLoginGetTGTSuccess(String tgt) {
                        lzuloginModel.loginEcardGetSid(tgt, new LzuloginModel.LoginEcardGetSidCallBack() {
                            @Override
                            public void onLoginEcardGetSidSuccess(Map<String, String> ecardcookie) {
                                new EcardServicesModel().queryDormBlance(MyCookieUtils.map2cookieStr(ecardcookie),
                                        user.getDormInfo().getAreano(),
                                        user.getDormInfo().getBuildingno(),
                                        user.getDormInfo().getFloorno(),
                                        user.getDormInfo().getRoomno(),
                                        new Observer<String>() {
                                            @Override
                                            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                                            }

                                            @Override
                                            public void onNext(@io.reactivex.annotations.NonNull String blance) {
                                                LogUtils.i(blance);
                                                closeLoadingDialog();
                                                user.getDormInfo().setBlance(blance+"度");
                                                //更新UI 电费
                                                notifyPropertyChanged(BR.user);
                                            }

                                            @Override
                                            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                                closeLoadingDialog();
                                                LogUtils.e(e.getMessage());
                                                if(!isSilent){
                                                    ToastUtils.showShort(e.getMessage());
                                                }
                                            }

                                            @Override
                                            public void onComplete() {

                                            }
                                        });
                            }

                            @Override
                            public void onLoginEcardGetSidError(String error) {
                                closeLoadingDialog();
                                LogUtils.e(error);
                                if(!isSilent){
                                    ToastUtils.showShort(error);
                                }
                            }
                        });
                    }

                    @Override
                    public void onLoginGetTGTError(String error) {
                        closeLoadingDialog();
                        LogUtils.e(error);
                        if(!isSilent){
                            ToastUtils.showShort(error);
                        }
                    }
                });

            }

            @Override
            public void onGetDormDetailInfoError(String error) {
                LogUtils.e(error);
                //if(flag){
                closeLoadingDialog();
                //}
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
        if(!TextUtils.isEmpty(MySpUtils.getString(SettingFragment.LZULIBRESERVE_KEY))&&!MySpUtils.getString(SettingFragment.LZULIBRESERVE_KEY).equals("0")){
            reserve(Integer.parseInt(MySpUtils.getString(SettingFragment.LZULIBRESERVE_KEY))-1);
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
        if(((MainDataBadingActivity)context).binding.layoutAppbarmain.tvHealthpunchText.getText().equals("健康打卡")){
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
                                    ((MainDataBadingActivity)context).binding.layoutAppbarmain.tvHealthpunchText.setText("已打卡");
                                    ((MainDataBadingActivity)context).binding.layoutAppbarmain.tvHealthpunchText.setTextColor(Color.parseColor("#32CD32"));
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
        }else{
            //已通过获取状态获取到了accesstoken和info 直接提交
            healthPunchModel.punch(new HealthPunchModel.HealthPunchCallBack() {
                @Override
                public void onHealthPunchSuccess() {
                    closeLoadingDialog();
                    iMainView.ShowSnackbar(ContextCompat.getColor(context, iMainView.getColorPrimaryId()),"打卡成功");
                }

                @Override
                public void onHealthPunchError(String error) {
                    //存在一种可能 accesstoken过期
                    ((MainDataBadingActivity)context).binding.layoutAppbarmain.tvHealthpunchText.setText("健康打卡");
                    healthPunch(view);
                }
            });
        }

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
        if(cloudTrusteeshipModel == null){
            cloudTrusteeshipModel = new CloudTrusteeshipModel();
        }
        String mrsb_token = MySpUtils.getString("mrsb_token");
        if(TextUtils.isEmpty(mrsb_token)){
            openLoadingDialog("登录系统");
            cloudTrusteeshipModel.TrusteeshipSystemLogin(login_uid, login_pwd, new CloudTrusteeshipModel.TrusteeshipSystemLoginCallBack() {
                @Override
                public void onTrusteeshipSystemLoginSuccess(String token, boolean ts) {
                    MySpUtils.save("mrsb_token",token);
                    MySpUtils.save("mrsb_ts",ts);
                    closeLoadingDialog();
                    mrsb(token,ts);
                }


                @Override
                public void onTrusteeshipSystemLoginError(String error) {
                    ToastUtils.showShort(error);
                    closeLoadingDialog();
                }
            });
        }else{
            mrsb(mrsb_token,MySpUtils.getBoolean("mrsb_ts"));
        }
    }

    private void mrsb(String token,Boolean ts){
        if(ts){
            new AlertDialog.Builder(context)
                    .setTitle("健康打卡云托管")
                    .setMessage("您已提交托管，是否取消云托管？")
                    .setPositiveButton("取消托管",(dialog, which) -> {
                        openLoadingDialog("正在取消");
                        cloudTrusteeshipModel.cancleTrusteeship(token, new CloudTrusteeshipModel.TrusteeshipOperationCallBack(){

                            @Override
                            public void onTrusteeshipOperationSuccess(String res) {
                                closeLoadingDialog();
                                MySpUtils.save("mrsb_ts",false);
                                iMainView.ShowSnackbar(ContextCompat.getColor(context, iMainView.getColorPrimaryId()),res);
                            }

                            @Override
                            public void onTrusteeshipOperationError(String error) {
                                LogUtils.e(error);
                                ToastUtils.showShort(error);
                                closeLoadingDialog();
                            }
                        });
                    })
                    .setNegativeButton("取消",null)
                    .show();
        }else{
            new AlertDialog.Builder(context)
                    .setTitle("健康打卡云托管")
                    .setMessage("目前支持打卡云托管服务，提交账户信息至服务器，每日由服务器自动上报打卡，免去您每日打卡的烦恼。\n" +
                            "您也可以访问云托管网页系统（http://mrsb.qianxiao.fun）手动提交或查看更多详情。\n" +
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
                                cloudTrusteeshipModel.trusteeshipSubmit(token, email, new CloudTrusteeshipModel.TrusteeshipOperationCallBack() {
                                    @Override
                                    public void onTrusteeshipOperationSuccess(String res) {
                                        closeLoadingDialog();
                                        MySpUtils.save("mrsb_ts",true);
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
    }

    @Override
    public void copyIp() {
        if(user!=null&&user.getSchoolNetInfo()!=null&&!TextUtils.isEmpty(user.getSchoolNetInfo().getOnline_ip())){
            ClipboardUtils.Copy2Clipboard(user.getSchoolNetInfo().getOnline_ip());
            ToastUtils.showShort("IP已复制至剪贴板");
        }
    }

    @Override
    public void schoolBusInfo() {
        new SchoolBusModel(context).showSchoolbu();
    }

    /**
     * 下载成绩单点击
     */
    @Override
    public void downSchoolResport() {
        if(user == null){
            ToastUtils.showShort("请登录后使用");
            lzuloginModel.showLoginDialog();
            return;
        }
        if(user.getCardid().startsWith("2")){
            ToastUtils.showShort("暂不支持研究生成绩单下载");
            return;
        }
        String login_uid = MySpUtils.getString("login_uid");
        String login_pwd = MySpUtils.getString("login_pwd");
        if(TextUtils.isEmpty(login_uid)||TextUtils.isEmpty(login_pwd)){
            return;
        }
        if(!PermissionUtils.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            PermissionUtils.permission(PermissionConstants.STORAGE)
                    .rationale((activity, shouldRequest) -> shouldRequest.again(true))
                    .callback(new PermissionUtils.FullCallback() {

                        @Override
                        public void onGranted(@NonNull List<String> granted) {
                            downSchoolResport();
                        }

                        @Override
                        public void onDenied(@NonNull List<String> deniedForever, @NonNull List<String> denied) {
                            if (!deniedForever.isEmpty()) {
                                //永久禁止
                                AlertDialog.Builder builder = new AlertDialog.Builder(Utils.getApp())
                                        .setTitle("温馨提示")
                                        .setMessage("您已拒绝本软件再次请求存储权限，请前往设置页面手动授予本如那件存储权限。")
                                        .setPositiveButton("前往设置页面", (dialog, which) -> {
                                            PermissionUtils.launchAppDetailsSettings();
                                        })
                                        .setCancelable(false);
                                builder.show();
                            }else{
                                downSchoolResport();
                            }
                        }
                    })
                    .request();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //Android11
            if (!Environment.isExternalStorageManager()) {
                ToastUtils.showShort("Android11请授权文件管理权限");
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivity(intent);
                return;
            }
        }
        /*
        1001 成绩单（中）
        1000 成绩单（英）
        1002 在读证明（中）
        1003 在读证明（英）
         */
        AtomicInteger flag = new AtomicInteger(1001);
        new AlertDialog.Builder(context)
                .setTitle("请选择要下载的证明")
                .setSingleChoiceItems(new String[]{
                        "成绩证明（中）",
                        "成绩证明（英）",
                        "在读证明（中）",
                        "在读证明（英）"},
                        0, (dialog, which) -> {
                            switch (which){
                                case 0:
                                    flag.set(1001);
                                    break;
                                case 1:
                                    flag.set(1000);
                                    break;
                                case 2:
                                    flag.set(1002);
                                    break;
                                case 3:
                                    flag.set(1003);
                                    break;
                            }
                        })
                .setPositiveButton("确定",(dialog, which) -> {
                    final String[] savefilepath = {""};
                    switch (flag.get()){
                        case 1000:
                            savefilepath[0] = Environment.getExternalStorageDirectory() + File.separator + AppUtils.getAppName() + File.separator +
                                    user.getName()+"成绩单(英).pdf";
                            break;
                        case 1001:
                            savefilepath[0] = Environment.getExternalStorageDirectory() + File.separator + AppUtils.getAppName() + File.separator +
                                    user.getName()+"成绩单(中).pdf";
                            break;
                        case 1002:
                            savefilepath[0] = Environment.getExternalStorageDirectory() + File.separator + AppUtils.getAppName() + File.separator +
                                    user.getName()+"在读证明(中).pdf";
                            break;
                        case 1003:
                            savefilepath[0] = Environment.getExternalStorageDirectory() + File.separator + AppUtils.getAppName() + File.separator +
                                    user.getName()+"在读证明(英).pdf";
                            break;
                        default:
                            break;

                    }
                    if(TextUtils.isEmpty(savefilepath[0])){
                        return;
                    }
                    if(FileUtils.isFileExists(savefilepath[0])){
                        new AlertDialog.Builder(context)
                                .setTitle("提示")
                                .setMessage("“"+savefilepath[0].substring(savefilepath[0].lastIndexOf(File.separator)+1)+"”已下载。保存至"+savefilepath[0]+"。")
                                .setPositiveButton("立即打开",(dialog1, which1) -> {
                                    Uri path = UriUtils.file2Uri(new File(savefilepath[0]));
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setDataAndType(path, "application/pdf");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    try {
                                        context.startActivity(intent);
                                    } catch (ActivityNotFoundException e) {
                                        ToastUtils.showShort("No Application Available to View PDF");
                                    }
                                })
                                .setNeutralButton("重新下载",(dialog1, which1) -> {
                                    FileUtils.delete(savefilepath[0]);
                                    downSchoolResport();
                                })
                                .show();
                        return;
                    }
                    if(flag.get() == 1002 || flag.get() == 1003){
                        openLoadingDialog("请耐心等待\n4秒左右");
                    }else{
                        openLoadingDialog("请耐心等待\n10秒左右");
                    }
                    lzuloginModel.loginGetTGT(login_uid, login_pwd, new LzuloginModel.LoginGetTGTCallBack() {
                        @Override
                        public void onLoginGetTGTSuccess(String tgt) {
                            new SchoolReportDownloadModel()
                                    .downloadSchoolReport(
                                            tgt,
                                            flag.get(),
                                            savefilepath[0],
                                            new SchoolReportDownloadModel.DownloadSchoolReportCallBack() {
                                                @Override
                                                public void onDownloadSchoolReportSuccess() {
                                                    closeLoadingDialog();
                                                    new AlertDialog.Builder(context)
                                                            .setTitle("下载成功")
                                                            .setMessage("文件保存至"+savefilepath[0])
                                                            .setPositiveButton("确定",null)
                                                            .setNeutralButton("立即打开",(dialog, which) -> {
                                                                Uri path = UriUtils.file2Uri(new File(savefilepath[0]));
                                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                                intent.setDataAndType(path, "application/pdf");
                                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                try {
                                                                    context.startActivity(intent);
                                                                } catch (ActivityNotFoundException e) {
                                                                    ToastUtils.showShort("No Application Available To View PDF");
                                                                }
                                                            })
                                                            .show();
                                                }

                                                @Override
                                                public void onDownloadSchoolReportError(String error) {
                                                    LogUtils.e(error);
                                                    closeLoadingDialog();
                                                    ToastUtils.showShort("onDownloadSchoolReportError:"+error);
                                                }
                                            }
                                    );
                        }

                        @Override
                        public void onLoginGetTGTError(String error) {
                            LogUtils.e(error);
                            closeLoadingDialog();
                            ToastUtils.showShort("onLoginGetTGTError:"+error);
                        }
                    });
                })
                .setNegativeButton("取消",null)
                .show();
    }

    /**
     * 校园卡挂失 解挂点击
     * @param isLose
     */
    @Override
    public void resportCardLoss(boolean isLose) {
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
        openLoadingDialog(isLose?"正在挂失":"正在解挂");
        lzuloginModel.loginGetTGT(login_uid, login_pwd, new LzuloginModel.LoginGetTGTCallBack() {
            @Override
            public void onLoginGetTGTSuccess(String tgt) {
                lzuloginModel.loginEcardGetSid(tgt, new LzuloginModel.LoginEcardGetSidCallBack() {
                    @Override
                    public void onLoginEcardGetSidSuccess(Map<String,String> ecardcookie) {
                        LogUtils.i(ecardcookie);
                        getBaseInfoModel.getCardAccNum(ecardcookie, new GetBaseInfoModel.GetCardAccNumCallBack() {
                            @Override
                            public void onGetCardAccNumSuccess(String cardAccNum) {
                                LogUtils.i(cardAccNum);

                                new CardReportLossModel().report(
                                        MyCookieUtils.map2cookieStr(ecardcookie),
                                        cardAccNum,
                                        user.getCardid(),
                                        isLose,
                                        new CardReportLossModel.CardReportLossOperationCallBack() {
                                            @Override
                                            public void onCardReportLossOperationSuccess(String res) {
                                                closeLoadingDialog();
                                                iMainView.ShowSnackbar(ContextCompat.getColor(context, iMainView.getColorPrimaryId()),res);
                                            }

                                            @Override
                                            public void onCardReportLossOperationError(String error) {
                                                LogUtils.e(error);
                                                ToastUtils.showShort(error);
                                                closeLoadingDialog();
                                            }
                                        }
                                );

                            }

                            @Override
                            public void onGetCardAccNumError(String error) {
                                LogUtils.e(error);
                                ToastUtils.showShort("onGetCardAccNumError("+error+")");
                                closeLoadingDialog();
                            }
                        });

                    }

                    @Override
                    public void onLoginEcardGetSidError(String error) {
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

    @Override
    public void lzuFileUpload() {
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
        if(!PermissionUtils.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            PermissionUtils.permission(PermissionConstants.STORAGE)
                    .rationale((activity, shouldRequest) -> shouldRequest.again(true))
                    .callback(new PermissionUtils.FullCallback() {

                        @Override
                        public void onGranted(@NonNull List<String> granted) {
                            lzuFileUpload();
                        }

                        @Override
                        public void onDenied(@NonNull List<String> deniedForever, @NonNull List<String> denied) {
                            if (!deniedForever.isEmpty()) {
                                //永久禁止
                                AlertDialog.Builder builder = new AlertDialog.Builder(Utils.getApp())
                                        .setTitle("温馨提示")
                                        .setMessage("您已拒绝本软件再次请求存储权限，请前往设置页面手动授予本如那件存储权限。")
                                        .setPositiveButton("前往设置页面", (dialog, which) -> {
                                            PermissionUtils.launchAppDetailsSettings();
                                        })
                                        .setCancelable(false);
                                builder.show();
                            }else{
                                lzuFileUpload();
                            }
                        }
                    })
                    .request();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //Android11
            if (!Environment.isExternalStorageManager()) {
                ToastUtils.showShort("Android11请授权文件管理权限");
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivity(intent);
                return;
            }
        }
        if(!MySpUtils.getBoolean("tip_oa_filecloud")){
            new AlertDialog.Builder(context)
                    .setTitle("温馨提示")
                    .setMessage("该功能基于OA办公系统附件上传功能实现，仅以获取校园网下较快访问速度为目的。\n" +
                            "上传文件以您的账号登录作为前提。切勿上传色情、反动、暴力等违法违规图片或文件，否因造成的一切后果由用户自行承担。\n" +
                            "上传建议可在LZU校园网环境下进行以获取较快的上传速度，上传获取的文件链接存放于兰大服务器，适用于在校园网下快速访问，也可在外网进行上传和下载。\n" +
                            "本功能支持多文件（文件数量不限）串行上传，单个文件最大需小于400M。\n" +
                            "请务必合理使用本功能。")
                    .setPositiveButton("同意",(dialog, which) -> {
                        startChooseFileIntent();
                    })
                    .setNegativeButton("同意并不再提示",(dialog, which) -> {
                        MySpUtils.save("tip_oa_filecloud",true);
                        startChooseFileIntent();
                    })
                    //.setCancelable(false)
                    .show();
            return;
        }
        startChooseFileIntent();
    }

    @Override
    public void lzuPersonalnf() {
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
        Intent intent = new Intent(context, PersonalnfActivity.class);
        intent.putExtra("user_mailpf",user.getMailpf());
        intent.putExtra("login_pwd",login_pwd);
        context.startActivity(intent);

    }

    /**
     * 余额互转
     */
    @Override
    public void transferYue() {
        if(user == null){
            ToastUtils.showShort("请登录后使用");
            lzuloginModel.showLoginDialog();
            return;
        }
        new TransferYueDialogFragment(user.getCardid(), (dialog, fromCardid, isFromEWallet, toCardid, isToEWallet, money, paypwd) -> {
            openLoadingDialog("转移余额");
            new TransferYueModel().transferYueToOtherCard(fromCardid, isFromEWallet,
                    toCardid, isToEWallet,
                    paypwd, money, new Observer<Boolean>() {
                        @Override
                        public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@io.reactivex.annotations.NonNull Boolean aBoolean) {
                            closeLoadingDialog();
                            ToastUtils.showShort("转移成功");
                            dialog.dismiss();
                            Refresh(true,true);
                        }

                        @Override
                        public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                            closeLoadingDialog();
                            LogUtils.e(e.getMessage());
                            ToastUtils.showShort(e.getMessage());
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }).show(((FragmentActivity)context).getSupportFragmentManager(),"TransferYueDialogFragment");
    }

    /**
     * 快交电费
     */
    @Override
    public void payForElectricity() {
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
        if(user.getDormInfo()==null){
            ToastUtils.showShort("请先设置宿舍");
            setDormInfo();
            return;
        }
        View view = View.inflate(context, R.layout.dialog_payelectricity, null);
        TextView tv_mydorminfo_payelectricity_dialog = view.findViewById(R.id.tv_mydorminfo_payelectricity_dialog);
        tv_mydorminfo_payelectricity_dialog.setText(user.getDormInfo().getDorm());
        TextView tv_mydormblance_payelectricity_dialog = view.findViewById(R.id.tv_mydormblance_payelectricity_dialog);
        tv_mydormblance_payelectricity_dialog.setText(user.getDormInfo().getBlance());
        final boolean[] isUseYue = {true};
        RadioGroup rg_fromtype_transferyue_df = view.findViewById(R.id.rg_fromtype_transferyue_df);
        rg_fromtype_transferyue_df.setOnCheckedChangeListener((group, checkedId) -> isUseYue[0] =checkedId == R.id.rb_yue_payelectricity_dialog);
        AlertDialog mydialog = new AlertDialog.Builder(context)
                .setTitle("电费缴纳")
                .setView(view)
                .setNegativeButton("取消",null)
                .setPositiveButton("确定",(dialog, which) -> {
                    TextInputEditText tie_money_payelectricity_dialog = view.findViewById(R.id.tie_money_payelectricity_dialog);
                    TextInputEditText tie_paypwd_payelectricity_dialog = view.findViewById(R.id.tie_paypwd_payelectricity_dialog);
                    String money = tie_money_payelectricity_dialog.getText().toString();
                    if(TextUtils.isEmpty(money)){
                        ToastUtils.showShort("请输入充值金额");
                        tie_money_payelectricity_dialog.requestFocus();
                        return;
                    }
                    String paypwd = tie_paypwd_payelectricity_dialog.getText().toString();
                    if(TextUtils.isEmpty(paypwd)){
                        ToastUtils.showShort("请输入支付密码");
                        tie_paypwd_payelectricity_dialog.requestFocus();
                        return;
                    }
                    if(paypwd.length()!=6){
                        ToastUtils.showShort("请输入6位支付密码");
                        tie_paypwd_payelectricity_dialog.requestFocus();
                        return;
                    }
                    KeyboardUtils.hideSoftInput(tie_paypwd_payelectricity_dialog);
                    openLoadingDialog("正在缴费");
                    lzuloginModel.loginGetTGT(login_uid, login_pwd, new LzuloginModel.LoginGetTGTCallBack() {
                        @Override
                        public void onLoginGetTGTSuccess(String tgt) {
                            lzuloginModel.loginEcardGetSid(tgt, new LzuloginModel.LoginEcardGetSidCallBack() {
                                @Override
                                public void onLoginEcardGetSidSuccess(Map<String, String> ecardcookie) {
                                    try{
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("areano",user.getDormInfo().getAreano());
                                        jsonObject.put("buildingno",user.getDormInfo().getBuildingno());
                                        jsonObject.put("floorno",user.getDormInfo().getFloorno());
                                        jsonObject.put("roomno",user.getDormInfo().getRoomno());
                                        getBaseInfoModel.getCardAccNum(ecardcookie, new GetBaseInfoModel.GetCardAccNumCallBack() {
                                            @Override
                                            public void onGetCardAccNumSuccess(String cardAccNum) {
                                                new EcardServicesModel().useEcardPayForElectricity(MyCookieUtils.map2cookieStr(ecardcookie),
                                                        isUseYue[0],cardAccNum,paypwd, money, jsonObject, new Observer<Boolean>() {
                                                            @Override
                                                            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                                                            }

                                                            @Override
                                                            public void onNext(@io.reactivex.annotations.NonNull Boolean aBoolean) {
                                                                closeLoadingDialog();
                                                                ToastUtils.showShort("缴费成功");
                                                                Refresh(true,true);
                                                            }

                                                            @Override
                                                            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                                                closeLoadingDialog();
                                                                ToastUtils.showShort(e.getMessage());
                                                            }

                                                            @Override
                                                            public void onComplete() {

                                                            }
                                                        });
                                            }

                                            @Override
                                            public void onGetCardAccNumError(String error) {

                                            }
                                        });
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onLoginEcardGetSidError(String error) {
                                    closeLoadingDialog();
                                    ToastUtils.showShort(error);
                                }
                            });
                        }

                        @Override
                        public void onLoginGetTGTError(String error) {
                            closeLoadingDialog();
                            ToastUtils.showShort(error);
                        }
                    });
                })
                .show();
        view.findViewById(R.id.tie_money_payelectricity_dialog).requestFocus();
        tv_mydorminfo_payelectricity_dialog.setOnClickListener(v -> {
            mydialog.dismiss();
            setDormInfo();
        });
    }

    /**
     * 修改密码点击
     */
    @Override
    public void reSetPwd() {
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
        AtomicInteger resetItem = new AtomicInteger();
        new AlertDialog.Builder(context)
                .setTitle("请选择要修改的密码")
                .setSingleChoiceItems(new String[]{"账户密码","校园卡支付密码"}, 0, (dialog, which) -> resetItem.set(which))
                .setPositiveButton("确定",(dialog, which) -> {
                    LinearLayout linearLayout = new LinearLayout(context);
                    linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                    linearLayout.setPadding(ConvertUtils.dp2px(20),ConvertUtils.dp2px(15),ConvertUtils.dp2px(20),0);
                    EditText editText = new EditText(context);
                    if(resetItem.get()==0){
                        editText.setHint("请输入新密码");
                    }else{
                        editText.setHint("请输入新支付密码");
                    }
                    linearLayout.addView(editText,-1,-2);
                    new AlertDialog.Builder(context)
                            .setTitle("请输入新密码")
                            .setView(linearLayout)
                            .setPositiveButton("修改",(dialog1, which1) -> {
                                String newpwd = editText.getText().toString().trim();
                                if(TextUtils.isEmpty(newpwd)){
                                    editText.requestFocus();
                                    ToastUtils.showShort(resetItem.get()==0?"请输入新密码":"请输入新支付密码");
                                }else{
                                    if(resetItem.get()==1&&newpwd.length()!=6){
                                        ToastUtils.showShort("支付密码应为6位");
                                        return;
                                    }
                                    KeyboardUtils.hideSoftInput(editText);
                                    openLoadingDialog("正在修改");
                                    lzuloginModel.loginGetTGT(login_uid, login_pwd, new LzuloginModel.LoginGetTGTCallBack() {
                                        @Override
                                        public void onLoginGetTGTSuccess(String tgt) {
                                            if(resetItem.get()==0){
                                                //修改个人工作台密码
                                                lzuloginModel.getStByTGT(tgt, new LzuloginModel.GetStByTGTCallBack() {
                                                    @Override
                                                    public void onGetStByTGTSuccess(String st) {
                                                        lzuloginModel.loginMyLzu(tgt, st, new Observer<String>() {
                                                            @Override
                                                            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                                                            }

                                                            @Override
                                                            public void onNext(@io.reactivex.annotations.NonNull String mylzucookie) {
                                                                //LogUtils.i(mylzucookie);
                                                                Map<String,String> map = MyCookieUtils.cookieStr2map(mylzucookie);
                                                                map.put("CASTGC",tgt);
                                                                map.put("iPlanetDirectoryPro",tgt);
                                                                new ReSetPwdModel().reSetPwd(MyCookieUtils.map2cookieStr(map), login_pwd, newpwd, new Observer<Boolean>() {
                                                                    @Override
                                                                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                                                                    }

                                                                    @Override
                                                                    public void onNext(@io.reactivex.annotations.NonNull Boolean aBoolean) {
                                                                        closeLoadingDialog();
                                                                        ToastUtils.showShort("个人密码修改成功");
                                                                        MySpUtils.save("login_pwd",newpwd);
                                                                    }

                                                                    @Override
                                                                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                                                        closeLoadingDialog();
                                                                        LogUtils.e(e.getMessage());
                                                                        ToastUtils.showShort(e.getMessage());
                                                                    }

                                                                    @Override
                                                                    public void onComplete() {

                                                                    }
                                                                });
                                                            }

                                                            @Override
                                                            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                                                closeLoadingDialog();
                                                                LogUtils.e(e.getMessage());
                                                                ToastUtils.showShort(e.getMessage());
                                                            }

                                                            @Override
                                                            public void onComplete() {

                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onGetStByTGTError(String error) {
                                                        closeLoadingDialog();
                                                        LogUtils.e(error);
                                                        ToastUtils.showShort(error);
                                                    }
                                                });
                                            }else{
                                                //修改支付密码
                                                lzuloginModel.loginEcardGetSid(tgt, new LzuloginModel.LoginEcardGetSidCallBack() {
                                                    @Override
                                                    public void onLoginEcardGetSidSuccess(Map<String, String> ecardcookie) {
                                                        new ReSetPwdModel().reSetPayPwd(MyCookieUtils.map2cookieStr(ecardcookie), newpwd, new Observer<Boolean>() {
                                                            @Override
                                                            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                                                            }

                                                            @Override
                                                            public void onNext(@io.reactivex.annotations.NonNull Boolean aBoolean) {
                                                                closeLoadingDialog();
                                                                ToastUtils.showShort("支付密码修改成功");
                                                            }

                                                            @Override
                                                            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                                                closeLoadingDialog();
                                                                LogUtils.e(e.getMessage());
                                                                ToastUtils.showShort(e.getMessage());
                                                            }

                                                            @Override
                                                            public void onComplete() {

                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onLoginEcardGetSidError(String error) {
                                                        closeLoadingDialog();
                                                        LogUtils.e(error);
                                                        ToastUtils.showShort(error);
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onLoginGetTGTError(String error) {
                                            closeLoadingDialog();
                                            LogUtils.e(error);
                                            ToastUtils.showShort(error);
                                        }
                                    });

                                }
                            })
                            .setNegativeButton("取消",null)
                            .show();
                    delayOpenKeyBoard(editText);
                })
                .setNegativeButton("取消",null)
                .show();
    }

    private void startChooseFileIntent(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);//意图：文件浏览器
        intent.setType("*/*");//无类型限制
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);//关键！多选参数
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        ((Activity)context).startActivityForResult(intent, CHOISEFILE_REQUESTCODE);
    }

    //OA文件上传
    private void filesUpload(List<String> uploadFileList){
        UploadFragmentDialog uploadFragmentDialog = new UploadFragmentDialog(uploadFileList,false);
        uploadFragmentDialog.show(((FragmentActivity)context).getSupportFragmentManager(),"UploadFragmentDialog");
        String login_uid = MySpUtils.getString("login_uid");
        String login_pwd = MySpUtils.getString("login_pwd");
        lzuloginModel.loginGetTGT(login_uid, login_pwd, new LzuloginModel.LoginGetTGTCallBack() {
            @Override
            public void onLoginGetTGTSuccess(String tgt) {
                lzuloginModel.loginOA(tgt, new LzuloginModel.LoginOACallBack() {
                    @Override
                    public void onLoginOASuccess(String cookie_JSESSIONID) {
                        LogUtils.i("OA Response Cookie:\n"+cookie_JSESSIONID);
                        new FileUploadModel().upload(
                                cookie_JSESSIONID,
                                uploadFileList,
                                uploadFragmentDialog);
                    }

                    @Override
                    public void onLoginOAErrot(String error) {
                        LogUtils.e(error);
                        ToastUtils.showShort(error);
                    }
                });
            }

            @Override
            public void onLoginGetTGTError(String error) {
                LogUtils.e(error);
                ToastUtils.showShort(error);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode){
                case CHOISEFILE_REQUESTCODE:
                    assert data != null;
                    List<String> uploadFileList = new ArrayList<>();
                    if (data.getData() != null) {
                        //单次点击未使用多选的情况
                        try {
                            Uri uri = data.getData();
                            File file = UriUtils.uri2File(uri);
                            uploadFileList.add(file.toString());
                        } catch (Exception ignored) { }
                    }else{
                        //长按使用多选的情况
                        ClipData clipData = data.getClipData();
                        if (clipData != null) {
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                ClipData.Item item = clipData.getItemAt(i);
                                Uri uri = item.getUri();
                                File file = UriUtils.uri2File(uri);
                                uploadFileList.add(file.toString());
                            }
                        }
                    }
                    LogUtils.i(uploadFileList);
                    filesUpload(uploadFileList);
                    break;
                default:
                    break;
            }
        }
    }

    private String lastDormno = "0101101";

    @Override
    public void setDormInfo() {
        if(user==null){
            return;
        }
        if(user.getDormInfo()!=null){
            new AlertDialog.Builder(context)
                    .setTitle("提示")
                    .setMessage("是否更换宿舍？")
                    .setPositiveButton("确定",(dialog, which) -> {
                        lastDormno = user.getDormInfo().getDormno();
                        user.setDormInfo(null);
                        notifyPropertyChanged(BR.user);
                        setDormInfo();
                    })
                    .show();
            return;
        }
        NumberPicker xq_picker = new NumberPicker(context);
        xq_picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        xq_picker.setMinValue(0);
        xq_picker.setMaxValue(3);
        String[] xqs = new String[]{"榆中校区","本部","医学校区","一分部"};
        final String[] xq = {"01"};
        xq_picker.setDisplayedValues(xqs);
        //xq_picker.setValue(0);
        xq_picker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            switch (newVal){
                case 0:
                    xq[0] = "01";
                    break;
                case 1:
                    xq[0] = "02";
                    break;
                case 2:
                    xq[0] = "03";
                    break;
                case 3:
                    xq[0] = "04";
                    break;
                default:
                    break;
            }
        });
        NumberPicker ssl_picker = new NumberPicker(context);
        ssl_picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        int maxssl = 35;
        String[] ssls = new String[maxssl];
        final String[] ssl = {"01"};
        for (int i = 0; i < maxssl; ) {
            ssls[i] = ++i+"号楼";
        }
        ssl_picker.setMinValue(0);
        ssl_picker.setMaxValue(maxssl-1);
        ssl_picker.setDisplayedValues(ssls);
        //%2d格式化一位数字结果为空格加数字，要格式化成0+1位数字需要使用%02d
        ssl_picker.setOnValueChangedListener((picker, oldVal, newVal) -> ssl[0] = String.format("%02d", newVal+1));
        NumberPicker room_picker = new NumberPicker(context);
        room_picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        final int[] lc = {0};
        int maxroom = 40;
        final String[] room = {"101"};
        String[] rooms = new String[maxroom];
        for (int i = 0; i < maxroom;) {
            rooms[i] = String.valueOf(++i+(lc[0] +1)*100);
        }
        room_picker.setMinValue(0);
        room_picker.setMaxValue(maxroom-1);
        room_picker.setDisplayedValues(rooms);
        NumberPicker lc_picker = new NumberPicker(context);
        lc_picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        int maxlc = 9;
        String[] lcs = new String[maxlc];
        for (int i = 0; i < maxlc; ) {
            lcs[i] = ++i+"层";
        }
        lc_picker.setMinValue(0);
        lc_picker.setMaxValue(maxlc-1);
        lc_picker.setDisplayedValues(lcs);
        lc_picker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            lc[0] = newVal;
            for (int i = 0; i < maxroom;) {
                rooms[i] = String.valueOf(++i+(lc[0] +1)*100);
            }
            room_picker.setDisplayedValues(rooms);
            //2次setValue使值发生变化 才会更新视图
            if(room_picker.getValue()==0){
                room_picker.setValue(room_picker.getValue()+1);
            }else{
                room_picker.setValue(room_picker.getValue()-1);
            }
            room_picker.setValue(Integer.parseInt(room[0].substring(1))-1);
            room[0] = rooms[Integer.parseInt(room[0].substring(1))-1];
        });
        room_picker.setOnValueChangedListener((picker, oldVal, newVal) -> room[0] = rooms[newVal]);
        xq_picker.setValue(Integer.parseInt(lastDormno.substring(0,2))-1);
        switch (Integer.parseInt(lastDormno.substring(0,2))-1){
            case 0:
                xq[0] = "01";
                break;
            case 1:
                xq[0] = "02";
                break;
            case 2:
                xq[0] = "03";
                break;
            case 3:
                xq[0] = "04";
                break;
            default:
                break;
        }
        ssl_picker.setValue(Integer.parseInt(lastDormno.substring(2,4))-1);
        ssl[0] = String.format("%02d", Integer.parseInt(lastDormno.substring(2,4)));
        lc_picker.setValue(Integer.parseInt(lastDormno.substring(4,5))-1);
        lc[0] = Integer.parseInt(lastDormno.substring(4,5))-1;
        for (int i = 0; i < maxroom;) {
            rooms[i] = String.valueOf(++i+(lc[0] +1)*100);
        }
        room_picker.setDisplayedValues(rooms);
        room_picker.setValue(Integer.parseInt(lastDormno.substring(5,7))-1);
        room[0] = rooms[Integer.parseInt(lastDormno.substring(5,7))-1];
        LinearLayout l1 = new LinearLayout(context);
        l1.setOrientation(LinearLayout.HORIZONTAL);
        l1.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        lp1.setMargins(0, ConvertUtils.dp2px(20),0,0);
        l1.setLayoutParams(lp1);
        l1.setPadding(ConvertUtils.dp2px(10),0,ConvertUtils.dp2px(10),0);
        l1.addView(xq_picker);
        l1.addView(ssl_picker);
        l1.addView(lc_picker);
        l1.addView(room_picker);
        new AlertDialog.Builder(context)
                .setTitle("请选择宿舍")
                .setView(l1)
                .setPositiveButton("确定",(dialog, which) -> {
                    String dormno = xq[0]+ssl[0]+room[0];
                    DormInfo dormInfo = new DormInfo();
                    dormInfo.setDormno(dormno);
                    dormInfo.setDorm(xqs[Integer.parseInt(xq[0])-1]+(Integer.parseInt(ssl[0]))+"号楼"+room[0]);
                    user.setDormInfo(dormInfo);
                    MySpUtils.SaveObjectData("dormInfo",dormInfo);
                    notifyPropertyChanged(BR.user);
                    ToastUtils.showShort("设置成功，正在查询电费");
                    getDromBlance(dormno,false);
                })
                .setNegativeButton("取消",null)
                .show();
    }

    @Override
    public void lzuMail() {
        if(!MySpUtils.getBoolean("lzuMailTip1")){
            new AlertDialog.Builder(context)
                    .setTitle("温馨提示")
                    .setMessage("新增LZU邮箱功能初步完成，目前仅支持邮件查看，更多功能敬请期待，如遇Bug或有任何建议，欢迎加入交流群交流。")
                    .setPositiveButton("我知道了",(dialog, which) -> {
                        MySpUtils.save("lzuMailTip1",true);
                        context.startActivity(new Intent(context, LzuMailActivity.class));
                    })
            .show();
        }else{
            context.startActivity(new Intent(context, LzuMailActivity.class));
        }
    }

    @Override
    public void queryInfoByCardid() {
        if(!MySpUtils.getBoolean("queryInfoByCardidTip")){
            new AlertDialog.Builder(context)
                    .setTitle("使用协议")
                    .setMessage("该功能通过校园卡号获取姓名、邮箱等信息。获取的信息请勿非法使用，否因造成的一切后果由使用者自行承担，作者不承担任何责任。")
                    .setPositiveButton("同意",(dialog, which) -> {
                        MySpUtils.save("queryInfoByCardidTip",true);
                        queryInfoByCardid();
                    })
                    .setNegativeButton("取消",null)
                    .show();
            return;
        }
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.setPadding(ConvertUtils.dp2px(20),ConvertUtils.dp2px(15),ConvertUtils.dp2px(20),0);
        EditText editText = new EditText(context);
        editText.setHint("请输入要查询的校园卡号");
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setSingleLine();
        linearLayout.addView(editText,-1,-2);
        new AlertDialog.Builder(context)
                .setTitle("请输入要查询的校园卡号")
                .setView(linearLayout)
                .setPositiveButton("查询",null)
                .setNegativeButton("取消",null)
                .show().getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String cardid = editText.getText().toString();
            if(TextUtils.isEmpty(cardid)){
                ToastUtils.showShort("请输入要查询的校园卡号");
                return;
            }
            if(StringUtils.length(cardid)!=12){
                ToastUtils.showShort("请输入正确的校园卡号");
                return;
            }
            KeyboardUtils.hideSoftInput(editText);
            openLoadingDialog("正在查询");
            getBaseInfoModel.getInfoByCardid(cardid, new Observer<JSONObject>() {
                @Override
                public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                }

                @Override
                public void onNext(@io.reactivex.annotations.NonNull JSONObject jsonObject) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("姓名：");
                    stringBuilder.append(jsonObject.optString("AccName"));
                    stringBuilder.append("\n邮箱：");
                    stringBuilder.append(jsonObject.optString("Email"));
                    stringBuilder.append("@lzu.edu.cn");
                    AtomicInteger i = new AtomicInteger();
                    AlertDialog alertDialog = new AlertDialog.Builder(context)
                            .setTitle("查询成功")
                            .setMessage(stringBuilder.toString())
                            .setPositiveButton("确定",null)
                            .show();
                    alertDialog.findViewById(androidx.appcompat.R.id.alertTitle).setOnClickListener(v -> {
                        i.getAndIncrement();
                        if(i.get() >= 5){
                            Iterator<String> iterator = jsonObject.keys();
                            StringBuilder sb = new StringBuilder();
                            while(iterator.hasNext()){
                                String s = iterator.next();
                                sb.append(s);
                                sb.append("：");
                                sb.append(jsonObject.opt(s));
                                sb.append("\n");
                            }
                            sb.deleteCharAt(sb.length()-1);
                            alertDialog.setMessage(sb.toString());
                            ToastUtils.showShort("请勿泄露敏感信息");
                        }
                    });
                }

                @Override
                public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                    onComplete();
                    ToastUtils.showShort(e.getMessage());
                }

                @Override
                public void onComplete() {
                    closeLoadingDialog();
                }
            });
        });
        delayOpenKeyBoard(editText);
    }

    private void delayOpenKeyBoard(EditText editText){
        editText.requestFocus();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager inputManager =
                        (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editText, 0);
            }
        },200);
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
