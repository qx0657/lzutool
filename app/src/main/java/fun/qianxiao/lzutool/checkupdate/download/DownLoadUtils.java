package fun.qianxiao.lzutool.checkupdate.download;

import android.app.Activity;
import android.app.DownloadManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;

import java.io.File;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Create by QianXiao
 * On 2020/7/29
 * https://github.com/shenglintang/downloadUtils
 */
public class DownLoadUtils {
    private DownloadManager mDownloadManager;
    private DownloadManager.Request mRequest;
    private String mTitle;
    private final static String sLoadPath = "/myLoadApk/";
    Timer timer;
    long id;
    TimerTask task;
    private Activity mActivity;
    public File mFile;

    public DownLoadUtils(Activity activity, String downloadUrl, String title) {
        this.mActivity = activity;
        this.mTitle = title;
        mDownloadManager = (DownloadManager) activity.getSystemService(DOWNLOAD_SERVICE);
        mRequest = new DownloadManager.Request(Uri.parse(downloadUrl));
    }

    public void downLoad(String savefilepath, Handler handler) {
        mRequest.setTitle(mTitle);
        mRequest.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI|DownloadManager.Request.NETWORK_MOBILE);
        mRequest.setAllowedOverRoaming(false);
        mRequest.setMimeType("application/vnd.android.package-archive");
        mRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        mFile = new File(Environment.getExternalStorageDirectory() + File.separator + AppUtils.getAppName() + File.separator);
        if(!mFile.exists()){
            boolean mddirsresult = mFile.mkdirs();
            if(!mddirsresult){
                Message message = new Message();
                message.what = A.DOWNLOADERROR;
                message.obj = "创建目录失败，请检查是否给与软件存储权限";
                handler.sendMessage(message);
                return;
            }
        }

        mFile = new File(savefilepath);
        String externaldir = Objects.requireNonNull(mFile.getParent()).substring(Environment.getExternalStorageDirectory().toString().length()+1);
        LogUtils.i(externaldir);
        String filename = FileUtils.getFileName(savefilepath);
        //设置文件存放路径
        mRequest.setDestinationInExternalPublicDir(externaldir, filename);
        final DownloadManager.Query query = new DownloadManager.Query();
        id = mDownloadManager.enqueue(mRequest);
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                Cursor cursor = mDownloadManager.query(query.setFilterById(id));
                if (cursor != null && cursor.moveToFirst()) {
                    //已经下载文件大小
                    int hasdown = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    //下载文件的总大小
                    int all = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    int precent = hasdown*100/all;
                    Message message = new Message();
                    switch (cursor.getInt(
                            cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))){
                        case DownloadManager.STATUS_PENDING:
                            //正在准备
                            message.what = A.DOWNLOADPREPARE;
                            handler.sendMessage(message);
                            break;
                        case DownloadManager.STATUS_RUNNING:
                            //正在下载
                            message.what = A.DOWNLOADING;
                            message.arg1 = precent;
                            handler.sendMessage(message);
                            break;
                        case DownloadManager.STATUS_SUCCESSFUL:
                            //下载完成
                            message.what = A.DOWNLOADFINISH;
                            handler.sendMessage(message);
                            task.cancel();
                            break;
                        case DownloadManager.STATUS_FAILED:
                            //下载失败
                            message.what = A.DOWNLOADERROR;
                            message.obj = "下载出错";
                            handler.sendMessage(message);
                            break;
                        case DownloadManager.STATUS_PAUSED:
                            //下载被暂停
                            message.what = A.DOWNLOADPAUSE;
                            handler.sendMessage(message);
                            break;
                    }
                }
                cursor.close();
            }
        };
        timer.schedule(task, 0, 500);
    }

}
