package fun.qianxiao.lzutool.ui.main.sub;

import android.text.TextUtils;

import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.Map;

import fun.qianxiao.lzutool.bean.User;
import fun.qianxiao.lzutool.ui.main.model.baseinfo.GetBaseInfoModel;
import fun.qianxiao.lzutool.ui.main.model.cardreportlossorcanclereportloss.CardReportLossModel;
import fun.qianxiao.lzutool.ui.main.model.ecardservices.EcardServicesModel;
import fun.qianxiao.lzutool.ui.main.model.lzulogin.LzuloginModel;
import fun.qianxiao.lzutool.utils.MyCookieUtils;
import fun.qianxiao.lzutool.utils.MySpUtils;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Create by QianXiao
 * On 2020/10/4
 */
public class CardReportLossActivity extends WelcomeActivity {
    protected boolean isLose = true;
    private LzuloginModel lzuloginModel;
    private CardReportLossModel cardReportLossModel;
    private GetBaseInfoModel getBaseInfoModel;

    @Override
    protected void initData() {
        setTitle(isLose?"校园卡挂失":"校园卡解挂");
        if(lzuloginModel == null){
            lzuloginModel = new LzuloginModel(context,null);
        }
        User user = MySpUtils.getObjectData("user");
        if(user==null){
            ToastUtils.showShort("请登录后使用");
        }else{
            String login_uid = MySpUtils.getString("login_uid");
            String login_pwd = MySpUtils.getString("login_pwd");
            if(TextUtils.isEmpty(login_uid)||TextUtils.isEmpty(login_pwd)){
                ToastUtils.showShort("请登录后使用");
            }else{
                if(cardReportLossModel == null){
                    cardReportLossModel = new CardReportLossModel();
                }
                if(getBaseInfoModel == null){
                    getBaseInfoModel = new GetBaseInfoModel(context);
                }
                openLoadingDialog(isLose?"正在挂失":"正在解挂");
                lzuloginModel.loginGetTGT(login_uid, login_pwd, new LzuloginModel.LoginGetTGTCallBack() {
                    @Override
                    public void onLoginGetTGTSuccess(String tgt) {
                        lzuloginModel.loginEcardGetSid(tgt, new LzuloginModel.LoginEcardGetSidCallBack() {
                            @Override
                            public void onLoginEcardGetSidSuccess(Map<String,String> ecardcookie) {
                                LogUtils.i(ecardcookie);
                                ecardcookie.put("iPlanetDirectoryPro",tgt);
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
                                                        ShowSnackbar(ContextCompat.getColor(context, getColorPrimaryId()),res);
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
                                        ToastUtils.showShort(error);
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
        }
    }
}
