package fun.qianxiao.lzutool.ui.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.blankj.utilcode.util.LogUtils;

/**
 * Create by QianXiao
 * On 2020/10/2
 */
public class MainReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.i("MainReceiver->onReceive:",intent.toString());
    }
}
