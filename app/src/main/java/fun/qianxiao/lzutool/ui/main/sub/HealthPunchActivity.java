package fun.qianxiao.lzutool.ui.main.sub;

import android.text.TextUtils;

import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;

import fun.qianxiao.lzutool.bean.User;
import fun.qianxiao.lzutool.ui.main.model.healthpunch.HealthPunchModel;
import fun.qianxiao.lzutool.ui.main.model.lzulogin.LzuloginModel;
import fun.qianxiao.lzutool.utils.MySpUtils;

/**
 * Create by QianXiao
 * On 2020/10/2
 */
public class HealthPunchActivity extends WelcomeActivity{
    private LzuloginModel lzuloginModel;
    private HealthPunchModel healthPunchModel;

    @Override
    protected void initData() {
        LogUtils.i("HealthPunchActivity->initData");
        setTitle("健康打卡");
        if(lzuloginModel == null){
            lzuloginModel = new LzuloginModel(context,null);
        }
        if(healthPunchModel == null){
            healthPunchModel = new HealthPunchModel();
        }
        String login_uid = MySpUtils.getString("login_uid");
        String login_pwd = MySpUtils.getString("login_pwd");
        User user = MySpUtils.getObjectData("user");
        if(TextUtils.isEmpty(login_uid)||TextUtils.isEmpty(login_pwd)||user == null){
            ToastUtils.showShort("请登录后使用");
        }else{
            openLoadingDialog("正在打卡");
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
                                    ShowSnackbar(ContextCompat.getColor(context, getColorPrimaryId()),"打卡成功");
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
    }
}
