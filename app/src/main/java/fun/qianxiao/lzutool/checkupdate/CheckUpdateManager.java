package fun.qianxiao.lzutool.checkupdate;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

import fun.qianxiao.lzutool.checkupdate.download.A;
import fun.qianxiao.lzutool.checkupdate.download.DownLoadUtils;
import fun.qianxiao.lzutool.utils.MyVolleyManager;
import fun.qianxiao.lzutool.view.ILoadingView;
import fun.qianxiao.lzutool.view.MyLoadingDialog;


/**
 * Create by QianXiao
 * On 2020/7/29
 */
public class CheckUpdateManager implements ILoadingView {
    private Context context;
    private MyLoadingDialog loadingDialog;

    private AlertDialog dialog;
    private Button mButtonPositive;

    private boolean isupdating = false;
    private boolean isApkFullyDownloaded = false;

    public CheckUpdateManager(Context context) {
        this.context = context;
    }

    public void setLoadingDialog(MyLoadingDialog loadingDialog) {
        this.loadingDialog = loadingDialog;
    }

    /**
     * 检查更新
     * @param issilent 是否静默
     */
    public void check(boolean issilent){
        if(!issilent){
            openLoadingDialog("正在检查更新");
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                "http://qianxiao.fun/app/lzutool/updateconfig.txt?t="+System.currentTimeMillis(),
                null,
                response -> {
                    try {
                        LogUtils.i(response);
                        JSONObject jsonObject = response;
                        if(jsonObject.getInt("newversioncode")> AppUtils.getAppVersionCode()){
                            //发现新版本
                            String newversionname  = jsonObject.getString("newversionname");
                            String newapkmd5 = jsonObject.getString("newapkmd5");
                            String downloadurl = jsonObject.getString("downloadurl");
                            String filename = AppUtils.getAppName()+".ver."+newversionname+".apk";
                            final String apksavefilepath = Environment.getExternalStorageDirectory() + File.separator + AppUtils.getAppName() + File.separator + filename;

                            if(FileUtils.isFileExists(apksavefilepath) && FileUtils.getFileMD5ToString(apksavefilepath).equals(newapkmd5.toUpperCase())){
                                isApkFullyDownloaded = true;
                            }
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder = builder.setTitle("发现新版本(V." + newversionname + ")")
                                    .setMessage(jsonObject.getString("updatacontent"))
                                    .setCancelable(false)
                                    //点击事件在下面设置 防止点击后dialog消失
                                    .setPositiveButton(isApkFullyDownloaded?"立即安装":"立即更新", null);
                            if (!(jsonObject.getInt("isforceupdate")==1)) {
                                builder = builder.setNegativeButton(isApkFullyDownloaded?"暂不安装":"暂不更新", null);
                            }

                            AlertDialog.Builder finalBuilder = builder;
                            ThreadUtils.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog = finalBuilder.create();
                                    dialog.show();
                                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if(isApkFullyDownloaded){
                                                //立即安装
                                                AppUtils.installApp(apksavefilepath);
                                                return;
                                            }
                                            if(!PermissionUtils.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                                                PermissionUtils.permission(PermissionConstants.STORAGE)
                                                        .rationale((activity, shouldRequest) -> shouldRequest.again(true))
                                                        .callback(new PermissionUtils.FullCallback() {
                                                            @Override
                                                            public void onGranted(@NonNull List<String> granted) {

                                                                onClick(v);
                                                            }

                                                            @Override
                                                            public void onDenied(@NonNull List<String> deniedForever, @NonNull List<String> denied) {
                                                                if (!deniedForever.isEmpty()) {
                                                                    //永久禁止
                                                                    AlertDialog.Builder builder = new AlertDialog.Builder(Utils.getApp())
                                                                            .setTitle("温馨提示")
                                                                            .setMessage("您已拒绝本软件再次请求存储权限，请前往设置页面手动授予本如那件存储权限。")
                                                                            .setPositiveButton("前往设置页面", (dialog, which) -> {
                                                                                PermissionUtils.launchAppDetailsSettings();
                                                                            })
                                                                            .setCancelable(false);
                                                                    builder.show();
                                                                }else{
                                                                    onClick(v);
                                                                }
                                                            }
                                                        })
                                                        .request();
                                                return;
                                            }
                                            if(!isupdating){
                                                isupdating = true;
                                            }else{
                                                return;
                                            }
                                            hideNegativeButton(true);
                                            //调用系统下载器下载
                                            new DownLoadUtils((Activity) context, downloadurl, "破解笔记.v."+newversionname)
                                                    .downLoad(apksavefilepath,new Handler(){
                                                        @Override
                                                        public void handleMessage(@NonNull Message msg) {
                                                            super.handleMessage(msg);
                                                            switch (msg.what){
                                                                case A.DOWNLOADPREPARE:
                                                                    setDownloadProgress("正在下载");
                                                                    break;
                                                                case A.DOWNLOADING:
                                                                    setDownloadProgress(msg.arg1);
                                                                    break;
                                                                case A.DOWNLOADFINISH:
                                                                    if (FileUtils.getFileMD5ToString(apksavefilepath).equals(newapkmd5.toUpperCase())) {
                                                                        AppUtils.installApp(apksavefilepath);
                                                                        setDownloadProgress("立即安装");
                                                                        isupdating = false;
                                                                        isApkFullyDownloaded = true;
                                                                    }else{
                                                                        ToastUtils.showShort("文件校验失败,请联系浅笑");
                                                                        FileUtils.delete(apksavefilepath);
                                                                        setDownloadProgress("文件校验失败");
                                                                    }

                                                                    break;
                                                                case A.DOWNLOADERROR:
                                                                    String error = (String) msg.obj;
                                                                    ToastUtils.showShort(error);
                                                                    hideNegativeButton(false);
                                                                    isupdating = false;
                                                                    setDownloadProgress("立即更新");
                                                                    break;
                                                                case A.DOWNLOADPAUSE:
                                                                    setDownloadProgress("下载被暂停");
                                                                    break;
                                                            }
                                                        }
                                                    });
                                        }
                                    });
                                    if(!issilent){
                                        closeLoadingDialog();
                                    }
                                }
                            });

                        }else{
                            if(!issilent){
                                ToastUtils.showShort("当前已是最新版本");
                                ThreadUtils.runOnUiThread(() -> closeLoadingDialog());
                            }
                        }
                    } catch (JSONException e) {
                        LogUtils.e(e.toString());
                        if(!issilent){
                            ToastUtils.showShort(e.toString());
                            ThreadUtils.runOnUiThread(() -> closeLoadingDialog());
                        }
                    }
                },
                error -> {
                    LogUtils.e(error.toString());
                    if(!issilent){
                        ToastUtils.showShort(error.getMessage());
                        ThreadUtils.runOnUiThread(() -> closeLoadingDialog());
                    }
                }
                );
        MyVolleyManager.getRequestQueue().add(jsonObjectRequest);
    }

    /**
     * 使用反射更改dialog积极按钮文字
     *
     * @param percent
     */
    private void setDownloadProgress(int percent) {
        setDownloadProgress(percent + "%");
    }

    @Keep
    private void setDownloadProgress(String text) {
        if (mButtonPositive == null) {
            try {
                Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
                mAlert.setAccessible(true);
                Object mAlertController = mAlert.get(dialog);
                //通过反射修改message字体大小和颜色
                Field mMessage = mAlertController.getClass().getDeclaredField("mButtonPositive");
                mMessage.setAccessible(true);
                Button mMessageView = (Button) mMessage.get(mAlertController);
                mMessageView.setText(text);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        } else {
            mButtonPositive.setText(text);
        }
    }

    /**
     * 隐藏取消更新按钮
     */
    @Keep
    private void hideNegativeButton(boolean flag){
        try {
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object mAlertController = mAlert.get(dialog);
            //通过反射修改message字体大小和颜色
            Field mMessage = mAlertController.getClass().getDeclaredField("mButtonNegative");
            mMessage.setAccessible(true);
            Button mMessageView = (Button) mMessage.get(mAlertController);
            if(mMessageView!=null){
                mMessageView.setVisibility(flag? View.GONE: View.VISIBLE);
            }
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void openLoadingDialog(String msg) {
        if(loadingDialog == null){
            loadingDialog = new MyLoadingDialog(context);
        }
        if(!loadingDialog.isShowing()){
            loadingDialog.setMessage(msg);
            loadingDialog.show();
        }
    }

    @Override
    public void closeLoadingDialog() {
        if(loadingDialog!=null && loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }
}
