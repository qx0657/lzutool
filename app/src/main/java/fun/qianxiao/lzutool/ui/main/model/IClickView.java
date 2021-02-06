package fun.qianxiao.lzutool.ui.main.model;

import android.view.View;

import fun.qianxiao.lzutool.view.ILoadingView;

/**
 * Create by QianXiao
 * On 2020/9/30
 */
public interface IClickView extends ILoadingView {
    /**
     * 图书馆预约点击
     * @param view
     */
    void LzuLibReserve(View view);

    /**
     * 健康打卡点击
     * @param view
     */
    void healthPunch(View view);

    /**
     * 健康打卡云托管点击
     * @param view
     */
    void healthPunchCloudTrusteeship(View view);

    /**
     * ip点击
     */
    void copyIp();

    void schoolBusInfo();

    /**
     * 下载在读证明/成绩单 点击
     */
    void downSchoolResport();

    /**
     * 校园卡挂失解挂点击
     * @param isLose
     */
    void resportCardLoss(boolean isLose);

    /**
     * LZU文件床 点击
     */
    void lzuFileUpload();

    /**
     * 个人网盘
     */
    void lzuPersonalnf();

    /**
     * 余额互转
     */
    void transferYue();

    /**
     * 交电费
     */
    void payForElectricity();

    /**
     * 修改密码
     */
    void reSetPwd();

    void setDormInfo();

    /**
     * LZU邮箱
     */
    void lzuMail();

    /**
     * 信息查询
     */
    void queryInfoByCardid();
}
