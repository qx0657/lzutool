package fun.qianxiao.lzutool;

import android.app.Application;

import com.baidu.mobstat.StatService;
import com.blankj.utilcode.util.LogUtils;

import fun.qianxiao.lzutool.utils.MySpUtils;
import fun.qianxiao.lzutool.utils.MyVolleyManager;

/**
 * Create by QianXiao
 * On 2020/9/27
 */
public class MyApplication extends Application {
    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        //初始化vollery单例
        MyVolleyManager.init(this);
        //开启Log日志
        LogUtils.getConfig().setLogSwitch(true);
        LogUtils.getConfig().setGlobalTag("LZUTOOL_TAG");

        //百度埋点
        // 通过该接口可以控制敏感数据采集，true表示可以采集，false表示不可以采集，
        // 该方法一定要最优先调用，请在StatService.start(this)之前调用，采集这些数据可以帮助App运营人员更好的监控App的使用情况，
        // 建议有用户隐私策略弹窗的App，用户未同意前设置false,同意之后设置true
        StatService.setAuthorizedState(this,true);
        // setSendLogStrategy已经@deprecated，建议使用新的start接口
        // 如果没有页面和自定义事件统计埋点，此代码一定要设置，否则无法完成统计
        // 进程第一次执行此代码，会导致发送上次缓存的统计数据；若无上次缓存数据，则发送空启动日志
        // 由于多进程等可能造成Application多次执行，建议此代码不要埋点在Application中，否则可能造成启动次数偏高
        // 建议此代码埋点在统计路径触发的第一个页面中，若可能存在多个则建议都埋点
        StatService.start(this);
    }

    public static MyApplication getInstance() {
        return mInstance;
    }

}
