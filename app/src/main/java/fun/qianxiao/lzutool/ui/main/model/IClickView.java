package fun.qianxiao.lzutool.ui.main.model;

import android.view.View;

import fun.qianxiao.lzutool.view.ILoadingView;

/**
 * Create by QianXiao
 * On 2020/9/30
 */
public interface IClickView extends ILoadingView {
    /**
     * 图书馆预约
     * @param view
     */
    void LzuLibReserve(View view);

    /**
     * 健康打卡
     * @param view
     */
    void healthPunch(View view);

    /**
     * 健康打卡云托管
     * @param view
     */
    void healthPunchCloudTrusteeship(View view);
}
