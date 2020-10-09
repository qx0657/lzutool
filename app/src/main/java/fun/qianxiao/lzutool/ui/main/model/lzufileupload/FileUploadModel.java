package fun.qianxiao.lzutool.ui.main.model.lzufileupload;

import android.text.TextUtils;

import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.TimeUtils;

import java.io.File;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fun.qianxiao.lzutool.ui.main.model.lzufileupload.uploadprogress.RequestProgressBody;
import fun.qianxiao.lzutool.utils.HttpConnectionUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 利用OA系统中附件上传功能搭建
 * Create by QianXiao
 * On 2020/10/8
 */
public class FileUploadModel {
    /**
     * 总进度监听
     */
    public interface MyProgressListener{
        /**
         * 进度回调
         * @param currentfile 当前第几个文件
         * @param currentpercent 当前文件的进度
         * @param allpercent 总进度
         */
        void onProgress(int currentfile,int currentpercent,int allpercent);
        void onFinish(int currentfile,String urlpath);
        void onError(int currentfile,String error);
    }

    public void upload(String cookie_jsessionid, List<String> filepaths,MyProgressListener progressListener){
        int filenum = filepaths.size();
        final int[] currentFile = {0};
        Observable.create((ObservableOnSubscribe<String>) emitter -> {
            try{
                for (String filepath : filepaths) {
                    currentFile[0]++;
                    File file = new File(filepath);
                    if(FileUtils.getFileLength(filepath)>=419430400){
                        progressListener.onError(currentFile[0],"上传文件大小不能超过400M");
                        continue;
                    }
                    String fileName = file.getName();
                    String contentTypeFor = URLConnection.getFileNameMap().getContentTypeFor(fileName);
                    if(TextUtils.isEmpty(contentTypeFor)){
                        contentTypeFor = "application/octet-stream";
                    }
                    RequestBody fileBody = RequestBody.create(MediaType.parse(contentTypeFor), file);
                    MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                    //用URLEncoder.encode(fileName, "UTF-8")解决中文编码问题
                    //不用URLEncoder.encode(fileName, "UTF-8")是因为文件名过长时无法上传
                    //直接上传时将文件名改为"file.{houzhui}"
                    String houzhui = ".oafilepath";
                    if(fileName.lastIndexOf(".")!=-1){
                        houzhui = fileName.substring(fileName.lastIndexOf("."));
                    }
                    multipartBuilder.addPart(MultipartBody.Part.createFormData("file", "file"+houzhui,fileBody));
                    String url = "http://oa.lzu.edu.cn/jsoa/public/jsp/smartUpload.jsp?isMenu=0&path=cooperate&mode=add&fileName=attachName&saveName=attachSaveName&tableName=co_attach_table&fileMaxSize=419430400&fileMaxNum=10&fileType=&fileMinWidth=0&fileMinHeight=0&fileMaxWidth=0&fileMaxHeight=0&layParent=layui-layer-iframe1";
                    Request request = new Request.Builder()
                            .addHeader("Cookie",cookie_jsessionid)
                            .url(url)
                            .post(new RequestProgressBody(multipartBuilder.build(), progress -> {
                                //单个文件上传进度回调
                                //LogUtils.i(progress.getCurrentBytes()+"/"+progress.getTotalBytes());
                                int percent = (int) (progress.getCurrentBytes()*100/progress.getTotalBytes());
                                int allpercent = (currentFile[0]-1)*100/filenum + (int) (progress.getCurrentBytes()*100/progress.getTotalBytes()/filenum);
                                //LogUtils.i(percent);
                                ThreadUtils.runOnUiThread(() -> progressListener.onProgress(currentFile[0],percent,allpercent));
                            }))
                            .build();
                    OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            .build();
                    Call call = okHttpClient.newCall(request);
                    Response response = call.execute();//同步请求
                    String rsp = Objects.requireNonNull(response.body()).string();
                    if(rsp.contains("附件上传成功")){
                        String pattern = "value=\'(2020_+.*?)\'>";
                        Pattern r = Pattern.compile(pattern);
                        Matcher m = r.matcher(rsp);
                        if(m.find()){
                            String filename = m.group(1);
                            //http://oa.lzu.edu.cn/jsoa/upload/2020/cooperate/
                            String urlpath = HttpConnectionUtil.getHttp().getRequset("http://lzutool.qianxiao.fun/oafilepath.txt");
                            assert filename != null;
                            String fileurl = urlpath.concat(filename);
                            emitter.onNext(fileurl);
                            Thread.sleep(500);
                        }else{
                            //附件上传成功但未找到链接信息
                            emitter.onError(new Throwable("附件上传成功但未找到链接信息，请联系浅笑修复"));
                        }
                    }else{
                        LogUtils.i(rsp);
                        emitter.onError(new Throwable("附件上传失败，请联系浅笑反馈"));
                    }
                }
            }catch (Exception e){
                emitter.onError(e);
            }
        }).subscribeOn(Schedulers.io())// 切换到IO线程进行网络请求
            .observeOn(AndroidSchedulers.mainThread())// 切换回到主线程 处理请求结果
            .subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                progressListener.onFinish(currentFile[0],s);
            }

            @Override
            public void onError(Throwable e) {
                progressListener.onError(currentFile[0],e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });

    }
}
