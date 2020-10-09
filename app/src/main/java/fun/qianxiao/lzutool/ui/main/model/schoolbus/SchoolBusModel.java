package fun.qianxiao.lzutool.ui.main.model.schoolbus;

import android.app.AlertDialog;
import android.content.Context;
import android.webkit.WebView;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ToastUtils;

/**
 * 校车
 * Create by QianXiao
 * On 2020/10/8
 */
public class SchoolBusModel {
    private Context context;

    public SchoolBusModel(Context context) {
        this.context = context;
    }

    public void showSchoolbu(){
        WebView webView = new WebView(context);
        webView.loadUrl("file:///android_asset/xctq.html");
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(webView)
                .setPositiveButton("确定",null)
                .setNeutralButton("加载官网数据",null)
                .show();
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(v -> {
            if(webView.getUrl().startsWith("http")){
                ToastUtils.showShort("已是官网数据");
            }else{
                webView.loadUrl("http://202.201.1.183/appsy/xctq/xctq.html");
            }
        } );

    }
}
