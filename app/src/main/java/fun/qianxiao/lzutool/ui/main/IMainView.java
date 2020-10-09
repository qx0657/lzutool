package fun.qianxiao.lzutool.ui.main;

import android.view.View;

import fun.qianxiao.lzutool.bean.User;

/**
 * Create by QianXiao
 * On 2020/9/30
 */
public interface IMainView {
    void LoginSuccess(User user);
    void LoginOut();
    int getColorPrimaryId();
    void ShowSnackbar(String msg);
    void ShowSnackbar(int backgroundcolor,String msg);
    void openOrCloseSchoolNetArea(View view);
    void pleaseSelectDorm();
}
