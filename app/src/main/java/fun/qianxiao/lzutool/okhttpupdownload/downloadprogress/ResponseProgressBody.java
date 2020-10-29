package fun.qianxiao.lzutool.okhttpupdownload.downloadprogress;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import fun.qianxiao.lzutool.okhttpupdownload.Progress;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Create by QianXiao
 * On 2020/10/13
 */
public class ResponseProgressBody extends ResponseBody {
    private final ResponseBody responseBody;
    private DownloadProgressListener downloadProgressListener;
    private BufferedSource bufferedSource;

    public ResponseProgressBody(ResponseBody responseBody, DownloadProgressListener downloadProgressListener) {
        this.responseBody = responseBody;
        this.downloadProgressListener = downloadProgressListener;
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @NotNull
    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            Source source = responseBody.source();
            bufferedSource = Okio.buffer(source(source));
        }
        return bufferedSource;
    }

    private Source source(Source source){
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;
            @Override public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                downloadProgressListener.onProgress(new Progress(totalBytesRead, responseBody.contentLength(), bytesRead == -1));
                return bytesRead;
            }
        };
    }
}
