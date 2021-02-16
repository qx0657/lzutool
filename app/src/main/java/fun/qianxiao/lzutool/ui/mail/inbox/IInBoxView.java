package fun.qianxiao.lzutool.ui.mail.inbox;

import java.util.List;

import fun.qianxiao.lzutool.ui.mail.bean.MailInfo;

public interface IInBoxView {
    void getDataSuccess(List<MailInfo> mailInfoList);

    void showMoreData(List<MailInfo> mailInfoList);
}
