package fun.qianxiao.lzutool.ui.main.model.undergraduatecertificate;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ThreadUtils;

import java.net.MalformedURLException;

import fun.qianxiao.lzutool.utils.UtilsHttpHelper;

/**
 * 成绩单一键下载
 * Create by QianXiao
 * On 2020/10/4
 */
public class SchoolReportDownloadModel {
    public interface DownloadSchoolReportCallBack{
        void onDownloadSchoolReportSuccess();
        void onDownloadSchoolReportError(String error);
    }

    /**
     * 成绩单下载
     * @param tgt
     * @param flag
     * @param callBack
     */
    public void downloadSchoolReport(String tgt,int flag,String savepath,DownloadSchoolReportCallBack callBack){
        if(!FileUtils.createOrExistsFile(savepath)){
            callBack.onDownloadSchoolReportError("创建文件失败");
        }else{
            new Thread(() -> {
                try {
                    UtilsHttpHelper.downloadNet(
                            "http://self.lzu.edu.cn/app_stamp/printLadpLogin?bizType="+flag,
                            "iPlanetDirectoryPro=" + tgt,
                            savepath,
                            () -> ThreadUtils.runOnUiThread(callBack::onDownloadSchoolReportSuccess));
                } catch (MalformedURLException e) {
                    ThreadUtils.runOnUiThread(() -> callBack.onDownloadSchoolReportError(e.getMessage()));
                }
            }).start();
        }
    }
}
