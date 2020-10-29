package fun.qianxiao.lzutool.ui.personalinf;

import fun.qianxiao.lzutool.view.ILoadingView;

/**
 * Create by QianXiao
 * On 2020/10/12
 */
public interface IPersonalnfView extends ILoadingView {
    int getColorPrimaryId();
    void setTitle(CharSequence title);
    void ShowSnackbar(String msg);
    void ShowSnackbar(int backgroundcolor,String msg);

    void setSelectMenu(boolean flag);
}
