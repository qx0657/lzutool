package fun.qianxiao.lzutool.ui.personalinf.model.lzupersonalnf;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.TimeUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fun.qianxiao.lzutool.okhttpupdownload.downloadprogress.DownloadProgressListener;
import fun.qianxiao.lzutool.okhttpupdownload.downloadprogress.ResponseProgressBody;
import fun.qianxiao.lzutool.ui.personalinf.bean.FileOrFolderItem;
import fun.qianxiao.lzutool.utils.MyCookieUtils;
import fun.qianxiao.lzutool.utils.MyOkhttpUtils;
import fun.qianxiao.lzutool.utils.MyVolleyManager;
import fun.qianxiao.lzutool.utils.android10downloadfile.FileSDCardUtil;
import fun.qianxiao.lzutool.utils.android10downloadfile.HttpDownFileUtils;
import fun.qianxiao.lzutool.utils.android10downloadfile.MyFileUtils;
import fun.qianxiao.lzutool.utils.android10downloadfile.OnFileDownListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

/**
 * LZU邮箱个人网盘
 * Create by QianXiao
 * On 2020/10/11
 */
public class PersonalnfModel {
    private Context context;
    private int root_fid;
    private final String DOACTION_URL = "https://mail.lzu.edu.cn/coremail/XT5/nf/doAction.jsp";

    public PersonalnfModel(Context context) {
        this.context = context;
    }

    public interface GetListFloderFileCallBack {
        void onListFloderFileSuccess(int currentfid,List<FileOrFolderItem> fileOrFolderItems);
        void onListFloderFileError(String error);
    }

    /**
     * 列出文件夹下的内容
     * @param mail_cookie
     * @param fid
     * @param callBack
     */
    public void listFloderFile(String mail_cookie, int fid , GetListFloderFileCallBack callBack){
        Map<String,String> map = MyCookieUtils.cookieStr2map(mail_cookie);
        String sid = map.get("Coremail.sid");
        List<FileOrFolderItem> fileOrFolderItems = new ArrayList<>();
        String url = "https://mail.lzu.edu.cn/coremail/XT5/nf/list.jsp?sid="+sid+"&view=&fid=";
        if(fid!=0&&fid!=root_fid){
            url += fid;
            FileOrFolderItem fileOrFolderItem = new FileOrFolderItem();
            fileOrFolderItem.setName("..");
            fileOrFolderItem.setFolder(true);
            fileOrFolderItem.setFolder_fid(-1);
            fileOrFolderItems.add(fileOrFolderItem);
        }
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    //LogUtils.i(response);
                    Document doc = Jsoup.parse(response);

                    int currentFid = Integer.parseInt(doc.selectFirst("input[name=currentFid]").val());
                    //currentFid = currentFid;
                    //LogUtils.i(currentFid);
                    if(fid==0){
                        root_fid = currentFid;
                    }
                    Elements elements = doc.selectFirst("form#nfList").select("div.nffile");
                    for (Element element : elements) {
                        if(element.hasAttr("data-fid")){
                            //文件夹
                            int data_fid = Integer.parseInt(element.attr("data-fid"));
                            String data_name = element.attr("data-name");
                            boolean share = false;
                            if(element.is(".nf_folder_shared")){
                                share = true;
                            }
                            FileOrFolderItem fileOrFolderItem = new FileOrFolderItem();
                            fileOrFolderItem.setFolder(true);
                            fileOrFolderItem.setName(data_name);
                            fileOrFolderItem.setFolder_fid(data_fid);
                            fileOrFolderItem.setFolder_share(share);
                            //打包下载文件夹时用到 下载的是zip
                            fileOrFolderItem.setFile_content_type("application/x-zip-compressed");
                            fileOrFolderItems.add(fileOrFolderItem);
                        }else{
                            //文件
                            String data_name = element.attr("data-name");
                            long data_size = Long.parseLong(element.attr("data-size"));
                            String data_mid = element.attr("data-mid");
                            String data_content_type = element.attr("data-contenttype");
                            Date upload_time = TimeUtils.string2Date(element.select("table:nth-child(1) > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(5) > span:nth-child(1)").first().attr("title"));
                            FileOrFolderItem fileOrFolderItem = new FileOrFolderItem();
                            fileOrFolderItem.setName(data_name);
                            fileOrFolderItem.setFile_size(data_size);
                            fileOrFolderItem.setFile_mid(data_mid);
                            fileOrFolderItem.setFile_content_type(data_content_type);
                            fileOrFolderItem.setFile_upload_time(upload_time);
                            fileOrFolderItems.add(fileOrFolderItem);
                        }
                    }
                    callBack.onListFloderFileSuccess(currentFid,fileOrFolderItems);
                },error -> callBack.onListFloderFileError(error.getMessage())){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();
                if(headers == null || headers.equals(Collections.emptyMap())){
                    headers = new HashMap<>();
                }
                headers.put("Cookie",mail_cookie);
                return headers;
            }
        };
        MyVolleyManager.getRequestQueue().add(stringRequest);
    }

    public interface CreateNewFolderCallBack{
        void onCreateNewFolderSuccess();
        void onCreateNewFolderError(String error);
    }

    /**
     * 创建新文件夹
     * @param mail_cookie
     * @param newfoldername
     * @param currentfid
     * @param callBack
     */
    public void createNewFolder(String mail_cookie,String newfoldername,int currentfid,CreateNewFolderCallBack callBack){
        doAction302Request(mail_cookie,new FormBody.Builder()
                .add("currentFid",String.valueOf(currentfid))
                .add("fileName","")
                .add("bigfile","bigfile")
                .add("ctype","attach")
                .add("name","")
                .add("folderName",newfoldername)
                .add("action:createfolder","true")
                .add("action:delete","")
                .add("action","")
                .add("action:movetofolder","")
                .add("upURL","")
                .build(),new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callBack.onCreateNewFolderError(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String header = response.headers().toString();
                if(header.contains("createfolderSuccess=true")){
                    callBack.onCreateNewFolderSuccess();
                }else{
                    callBack.onCreateNewFolderError("创建文件夹失败");
                }
            }
        });
    }

    public interface ShareFileOrFolderCallBack{
        void onShareFileOrFolderSuccess(JSONObject jsonObject);
        void onShareFileOrFolderError(String error);
    }

    /**
     * 分享文件或文件夹
     * @param fid 文件夹id 为0时表示分享的是文件
     * @param mid 文件id
     * @param callBack
     */
    public void shareFileOrFolder(String mail_cookie,int fid,String mid,ShareFileOrFolderCallBack callBack){
        Map<String,String> map = MyCookieUtils.cookieStr2map(mail_cookie);
        String sid = map.get("Coremail.sid");
        //以下注释的请求为获取是否存在分享链接，有则返回分享链接，无则返回isExist为false
        //不如直接用下面getOrCreateShareLink请求，有则返回，无则创建再返回
        /*String uid = map.get("uid");
        String url = "";
        if(fid!=0){
            url = "https://mail.lzu.edu.cn/coremail/XT5/jsp/file.jsp?func=file:getShareLinkInfo&sid="+sid+"&file_id="+fid+"&net_disk_uid="+uid+"&type=folder";
        }else {
            url = "https://mail.lzu.edu.cn/coremail/XT5/jsp/file.jsp?func=file:getShareLinkInfo&sid="+sid+"&file_id="+mid+"&net_disk_uid="+uid+"&type=file";
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                null,
                response -> {
                    LogUtils.i(response);
                    if(response.optString("code").equals("S_OK")&&response.has("var")){
                        if(response.optJSONObject("var").optBoolean("isExist")){
                            callBack.onShareFileOrFolderSuccess(response.optJSONObject("var").optJSONObject("shareInfo"));
                        }else{

                        }
                    }else{
                        callBack.onShareFileOrFolderError("获取分享链接失败");
                    }
                },error -> callBack.onShareFileOrFolderError(error.getMessage())){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();
                if(headers == null || headers.equals(Collections.emptyMap())){
                    headers = new HashMap<>();
                }
                headers.put("Cookie",mail_cookie);
                return headers;
            }
        };
        MyVolleyManager.getRequestQueue().add(jsonObjectRequest);*/
        JSONObject jsonObject_req = new JSONObject();
        try {
            if(fid!=0){
                jsonObject_req.put("fid",fid);
            }else{
                jsonObject_req.put("mid",mid);
            }
        }catch (Exception ignore){
        }
        JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(
                Request.Method.POST,
                "https://mail.lzu.edu.cn/coremail/s/json?func=nf:getOrCreateShareLink&sid="+sid,
                jsonObject_req,
                response1 -> {
                    LogUtils.i(response1);
                    if(response1.optString("code").equals("S_OK")&&response1.has("var")){
                        callBack.onShareFileOrFolderSuccess(response1.optJSONObject("var"));
                    }else{
                        callBack.onShareFileOrFolderError("获取分享链接失败");
                    }
                },error -> callBack.onShareFileOrFolderError(error.getMessage())){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();
                if(headers == null || headers.equals(Collections.emptyMap())){
                    headers = new HashMap<>();
                }
                /*
                必须加请求接受类型，不如返回数据为空
                 */
                headers.put("Accept","text/x-json");
                headers.put("Content-Type","text/x-json");
                headers.put("Cookie",mail_cookie);
                return headers;
            }
        };
        MyVolleyManager.getRequestQueue().add(jsonObjectRequest1);
    }

    /**
     * 下载文件夹
     * @param mail_cookie
     * @param folder_id 文件夹id
     */
    public void downloadFolderPack(String mail_cookie,int folder_id,OnFileDownListener onFileDownListener){
        Map<String,String> map = MyCookieUtils.cookieStr2map(mail_cookie);
        String sid = map.get("Coremail.sid");
        String downloadurl = "https://mail.lzu.edu.cn/coremail/XT5/nf/doPackFiles.jsp?fid="+folder_id+"&sid="+sid;
        //LogUtils.i(downloadurl);
        HttpDownFileUtils.getInstance().downFileFromServiceToPublicDir(
                downloadurl,
                mail_cookie,
                context,
                DIRECTORY_DOWNLOADS,
                onFileDownListener
        );
    }

    /**
     * 下载文件
     * @param mail_cookie
     * @param mid
     * @param onFileDownListener
     */
    public void downloadFile(String mail_cookie, String mid,OnFileDownListener onFileDownListener){
        Map<String,String> map = MyCookieUtils.cookieStr2map(mail_cookie);
        String sid = map.get("Coremail.sid");
        String downloadurl = "https://mail.lzu.edu.cn/coremail/XT5/nf/doGetFile.jsp?mid="+mid+"&sid="+sid+"&mode=download";
        HttpDownFileUtils.getInstance().downFileFromServiceToPublicDir(
                downloadurl,
                mail_cookie,
                context,
                DIRECTORY_DOWNLOADS,
                onFileDownListener
        );
    }

    /**
     * 批量下载文件、文件夹
     * @param mail_cookie
     * @param fids
     * @param mids
     */
    public void downloadBatchFilesAndFolders(String mail_cookie, int currentfid, String name, List<Integer> fids, List<String> mids, DownloadProgressListener downloadProgressListener){
        FormBody.Builder builder = new FormBody.Builder()
                .add("currentFid",String.valueOf(currentfid))
                .add("fileName","")
                .add("bigfile","bigfile")
                .add("ctype","attach")
                .add("name",name+".zip")
                .add("folderName","")
                .add("action:createfolder","")
                .add("action:delete","")
                .add("action:download","action:download")
                .add("action:movetofolder","");
        for (Integer fid : fids) {
            builder.add("fid", String.valueOf(fid));
        }
        for (String mid : mids) {
            builder.add("mid",mid);
        }
        Map<String,String> map = MyCookieUtils.cookieStr2map(mail_cookie);
        String sid = map.get("Coremail.sid");
        OkHttpClient okHttpClient = MyOkhttpUtils.getUnsafeOkHttpClientBuilder()
                .addNetworkInterceptor(chain -> {
                    //拦截
                    Response originalResponse = chain.proceed(chain.request());
                    //包装响应体并返回
                    return originalResponse.newBuilder()
                            .body(new ResponseProgressBody(originalResponse.body(), downloadProgressListener))
                            .build();
                })
                .build();
        okHttpClient.newCall(new okhttp3.Request.Builder()
                .url(DOACTION_URL+"?sid="+sid)
                .header("Cookie",mail_cookie)
                .header("Content-Type","application/x-www-form-urlencoded; charset=utf-8")
                .post(builder.build())
                .build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                LogUtils.e(e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                InputStream is = Objects.requireNonNull(response.body()).byteStream();
                MyFileUtils.saveByteStream2File(context,is,name+".zip");
            }
        });
    }

    public interface RenameCallBack{
        void onRenameSuccess(String newname);
        void onRenameError(String error);
    }

    /**
     * 文件夹重命名
     * @param mail_cookie
     * @param folder_id 要改名的文件夹id,不能为10（我的文档）
     * @param new_folder_name 新文件夹名
     */
    public void renameFolder(String mail_cookie,int currentfid,int folder_id,String new_folder_name,RenameCallBack callBack){
        doAction302Request(mail_cookie,new FormBody.Builder()
                .add("currentFid",String.valueOf(currentfid))
                .add("fileName","")
                .add("bigfile","bigfile")
                .add("ctype","attach")
                .add("name","")
                .add("folderName",new_folder_name)
                .add("action:createfolder","")
                .add("action:delete","")
                .add("action","")
                .add("action:movetofolder","")
                .add("fid", String.valueOf(folder_id))
                .add("action:rename_folder_save","action:rename_folder_save")
                .build(),new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callBack.onRenameError(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String header = response.headers().toString();
                //LogUtils.i(header);
                if(header.contains("renameFolderSuccess=true")){
                    //重命名成功
                    callBack.onRenameSuccess(new_folder_name);
                }else{
                    callBack.onRenameError("重命名失败");
                }
            }
        });
    }

    /**
     * 文件重命名
     * @param mail_cookie
     * @param currentfid
     * @param mid
     * @param newfilename
     * @param callBack
     */
    public void renameFile(String mail_cookie, int currentfid, String mid, String newfilename, RenameCallBack callBack){
        doAction302Request(mail_cookie,new FormBody.Builder()
                .add("currentFid",String.valueOf(currentfid))
                .add("fileName",newfilename)
                .add("bigfile","bigfile")
                .add("ctype","attach")
                .add("name","")
                .add("folderName","")
                .add("action:createfolder","")
                .add("action:delete","")
                .add("action","")
                .add("action:movetofolder","")
                .add("mid",mid)
                .add("action:edit_file_save","action:edit_file_save")
                .build(),new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callBack.onRenameError(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String header = response.headers().toString();
                //LogUtils.i(header);
                if(header.contains("renameFileSuccess=true")){
                    //重命名成功
                    callBack.onRenameSuccess(newfilename);
                }else{
                    callBack.onRenameError("重命名失败");
                }
            }
        });
    }

    public interface DeleteCallBack{
        void onDeleteSuccess();
        void onDeleteError(String error);
    }

    /**
     * 批量删除文件或文件夹
     * @param mail_cookie
     * @param currentfid
     * @param fids
     * @param mids
     * @param callBack
     */
    public void deleteFileOrFolder(String mail_cookie,int currentfid, List<Integer> fids,List<String> mids, DeleteCallBack callBack){
        FormBody.Builder builder = new FormBody.Builder()
                .add("currentFid",String.valueOf(currentfid))
                .add("fileName","")
                .add("bigfile","bigfile")
                .add("ctype","attach")
                .add("name","")
                .add("folderName","")
                .add("action:createfolder","")
                .add("action:delete","true")
                .add("action","")
                .add("action:movetofolder","");
        for (int fid : fids) {
            builder.add("fid", String.valueOf(fid));
        }
        for (String mid : mids) {
            builder.add("mid", mid);
        }
        doAction302Request(mail_cookie,builder
                .build(),new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callBack.onDeleteError(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String header = response.headers().toString();
                //LogUtils.i(header);
                if(header.contains("deleteSuccess=true")){
                    //删除成功
                    callBack.onDeleteSuccess();
                }else{
                    callBack.onDeleteError("删除错误");
                }
            }
        });
    }

    private void doAction302Request(String mail_cookie, RequestBody requestBody,Callback callback){
        Map<String,String> map = MyCookieUtils.cookieStr2map(mail_cookie);
        String sid = map.get("Coremail.sid");
        OkHttpClient okHttpClient = MyOkhttpUtils.getUnsafeOkHttpClientBuilder()
                .followRedirects(false)//禁止302
                .build();
        okHttpClient.newCall(new okhttp3.Request.Builder()
                .url(DOACTION_URL+"?sid="+sid)
                .header("Cookie",mail_cookie)
                .post(requestBody)
                .build()).enqueue(callback);
    }
}
