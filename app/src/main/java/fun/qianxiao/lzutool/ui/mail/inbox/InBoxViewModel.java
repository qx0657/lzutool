package fun.qianxiao.lzutool.ui.mail.inbox;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.List;

import fun.qianxiao.lzutool.bean.User;
import fun.qianxiao.lzutool.ui.BaseLzuMailViewModel;
import fun.qianxiao.lzutool.ui.mail.ILzuMailView;
import fun.qianxiao.lzutool.ui.mail.model.LzuMailModel;
import fun.qianxiao.lzutool.ui.mail.bean.MailInfo;
import fun.qianxiao.lzutool.ui.main.model.lzulogin.LzuloginModel;
import fun.qianxiao.lzutool.utils.MySpUtils;

public class InBoxViewModel extends BaseLzuMailViewModel {
    private IInBoxView iInBoxView;
    private LzuMailModel lzuMailModel;
    private LzuloginModel lzuloginModel;
    private String coolie_mail;
    public LzuMailModel.MailType currerntMailType = LzuMailModel.MailType.InBox;
    public boolean hasMore;
    private int start;
    public OrderType currentOrderType = OrderType.ReceivedDate;
    public boolean isDesc = true;
    public FItterType currentFItterType = FItterType.None;

    public enum FItterType{
        None,
        NoRead,//未读
        Agent,//待办
        Flag,//红旗
        Reply//已回复
    }

    public enum OrderType{
        ReceivedDate("receivedDate"),
        From("from"),
        Subject("subject"),
        FlagsAttached("flags.attached");

        String value;

        OrderType(String value) {
            this.value = value;
        }

        @NonNull
        @Override
        public String toString() {
            return value;
        }
    }

    public InBoxViewModel(Context context, ILzuMailView iLzuMailView, IInBoxView iInBoxView) {
        super(context, iLzuMailView);
        this.iInBoxView = iInBoxView;
        //Refresh();
    }

    public void Refresh(){
        iLzuMailView.openLoadingDialog("获取邮件中");
        start = 0;
        getMails();
    }

    /**
     * 获取邮件
     */
    public void getMails(){
        if(coolie_mail == null){
            String login_uid = ((User)MySpUtils.getObjectData("user")).getMailpf();
            String login_pwd = MySpUtils.getString("login_pwd");
            if(TextUtils.isEmpty(login_uid)||TextUtils.isEmpty(login_pwd)){
                return;
            }
            LogUtils.i(login_uid, login_pwd);
            if(lzuloginModel == null){
                lzuloginModel = new LzuloginModel(context, null);
            }
            lzuloginModel.loginLzuMail(login_uid, login_pwd, new LzuloginModel.LoginLzuMailCallBack() {
                @Override
                public void onLoginLzuMailSuccess(String coolie_mail) {
                    InBoxViewModel.this.coolie_mail = coolie_mail;
                    MySpUtils.save("coolie_mail",coolie_mail);
                    loadData();
                }

                @Override
                public void onLoginLzuMailError(String error) {
                    ToastUtils.showShort(error);
                }
            });
        }else{
            loadData();
        }
    }

    private void loadData(){
        if(lzuMailModel == null){
            lzuMailModel = new LzuMailModel(coolie_mail);
        }
        lzuMailModel.mBoxListMessages(currerntMailType, start, currentOrderType, isDesc, currentFItterType, new LzuMailModel.MBoxListMessagesCallBack() {
            @Override
            public void onMBoxListMessagesSuccess(List<MailInfo> mailInfoList, boolean hasMore) {
                InBoxViewModel.this.hasMore = hasMore;
                start += 20;
                iLzuMailView.closeLoadingDialog();
                iInBoxView.getDataSuccess(mailInfoList);
            }

            @Override
            public void onMBoxListMessagesError(String error) {
                iLzuMailView.closeLoadingDialog();
                ToastUtils.showShort(error);
            }
        });
    }

    public void loadMoreData(){
        lzuMailModel.mBoxListMessages(currerntMailType, start, currentOrderType, isDesc, currentFItterType, new LzuMailModel.MBoxListMessagesCallBack() {
            @Override
            public void onMBoxListMessagesSuccess(List<MailInfo> mailInfoList, boolean hasMore) {
                InBoxViewModel.this.hasMore = hasMore;
                start += 20;
                iLzuMailView.closeLoadingDialog();
                iInBoxView.showMoreData(mailInfoList);
            }

            @Override
            public void onMBoxListMessagesError(String error) {
                iLzuMailView.closeLoadingDialog();
                ToastUtils.showShort(error);
            }
        });
    }
}
