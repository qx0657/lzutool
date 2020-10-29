package fun.qianxiao.lzutool.okhttpupdownload.downloadprogress;

import fun.qianxiao.lzutool.okhttpupdownload.Progress;

/**
 * Create by QianXiao
 * On 2020/10/13
 */
public interface DownloadProgressListener {
    void onProgress(Progress progress);
}
