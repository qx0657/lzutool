package fun.qianxiao.lzutool.ui.main.model.lzuoafileupload;

/**
 * Create by QianXiao
 * On 2020/10/9
 */
public class UploadItem {
    /**
     * 本地文件路径
     */
    private String localFilePath;
    /**
     * 生成成功后的网络路径
     */
    private String urlPath;
    /**
     * 上传进度
     */
    private int percent = 0;
    /**
     * 出错信息 为null表示未出错
     */
    private String error;

    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
