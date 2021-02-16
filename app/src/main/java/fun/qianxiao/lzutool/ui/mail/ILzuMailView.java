package fun.qianxiao.lzutool.ui.mail;

import androidx.fragment.app.Fragment;

import fun.qianxiao.lzutool.base.BaseDateBadingFeagment;
import fun.qianxiao.lzutool.ui.mail.bean.MailInfo;
import fun.qianxiao.lzutool.view.ILoadingView;

public interface ILzuMailView extends ILoadingView {
    void enterMailDetail(MailInfo mailInfo);
    void back();
}
