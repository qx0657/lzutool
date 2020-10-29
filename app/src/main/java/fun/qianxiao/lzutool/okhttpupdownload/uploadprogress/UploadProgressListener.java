package fun.qianxiao.lzutool.okhttpupdownload.uploadprogress;


import fun.qianxiao.lzutool.okhttpupdownload.Progress;

public interface UploadProgressListener {
    void onProgress(Progress progress);
}