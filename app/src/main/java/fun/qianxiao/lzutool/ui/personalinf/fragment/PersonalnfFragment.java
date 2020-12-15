package fun.qianxiao.lzutool.ui.personalinf.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.UriUtils;
import com.blankj.utilcode.util.Utils;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import fun.qianxiao.lzutool.R;
import fun.qianxiao.lzutool.base.BaseFragment;
import fun.qianxiao.lzutool.okhttpupdownload.Progress;
import fun.qianxiao.lzutool.okhttpupdownload.downloadprogress.DownloadProgressListener;
import fun.qianxiao.lzutool.ui.main.model.lzuoafileupload.FileUploadModel;
import fun.qianxiao.lzutool.ui.personalinf.bean.FileOrFolderItem;
import fun.qianxiao.lzutool.ui.personalinf.fragment.adapter.FileOrFolderAdapter;
import fun.qianxiao.lzutool.ui.personalinf.model.lzupersonalnf.PersonalnfModel;
import fun.qianxiao.lzutool.ui.personalinf.IPersonalnfView;
import fun.qianxiao.lzutool.utils.ClipboardUtils;
import fun.qianxiao.lzutool.utils.android10downloadfile.OnFileDownListener;
import okhttp3.MediaType;

import static android.util.TypedValue.COMPLEX_UNIT_SP;

/**
 * Create by QianXiao
 * On 2020/10/12
 */
public class PersonalnfFragment extends BaseFragment implements IPersoncalnfFragmentView {
    private String mail_cookie;
    private IPersonalnfView iPersonalnfView;
    private PersonalnfModel personalnfModel;
    private RecyclerView rv_personalnffragment;
    private LinearLayout ll_personalnffragmentr;
    private TextView tv_download_personalnffragmentr,tv_delete_personalnffragmentr;
    private FileOrFolderAdapter adapter;
    private FileOrFolderAdapter.OnFileOnClickOrFolderOnLongClickListener onFileOnClickOrFolderOnLongClickListener;
    private MenuItem.OnMenuItemClickListener[][] menuItemClickListener = new MenuItem.OnMenuItemClickListener[2][3];
    private int currentFid = 0;
    private String currentDisplayDir = "个人网盘";
    private final int CHOISEFILE_REQUESTCODE = 1201;

    public PersonalnfFragment(IPersonalnfView iPersonalnfView) {
        this.iPersonalnfView = iPersonalnfView;
    }

    public void setMail_cookie(String mail_cookie) {
        this.mail_cookie = mail_cookie;
    }

    @Override
    protected int getFragmentLayoutId() {
        return R.layout.fragmnet_personalnf;
    }

    @Override
    protected void initView() {
        rv_personalnffragment = f(R.id.rv_personalnffragment);
        rv_personalnffragment.setLayoutManager(new LinearLayoutManager(getContext()));
        ll_personalnffragmentr = f(R.id.ll_personalnffragmentr);
        tv_download_personalnffragmentr = f(R.id.tv_download_personalnffragmentr);
        tv_delete_personalnffragmentr = f(R.id.tv_delete_personalnffragmentr);
    }

    @Override
    protected void initListener() {
        //文件列表操作
        onFileOnClickOrFolderOnLongClickListener = new FileOrFolderAdapter.OnFileOnClickOrFolderOnLongClickListener() {
            @Override
            public void OnClick(FileOrFolderItem fileOrFolderItem, View view,int position) {
                if(fileOrFolderItem.isFolder()){
                    if(fileOrFolderItem.getFolder_fid()!=-1){
                        currentDisplayDir = currentDisplayDir+ File.separator + fileOrFolderItem.getName();
                    }else{
                        //返回上一层
                        currentDisplayDir = currentDisplayDir.substring(0,
                                currentDisplayDir.lastIndexOf(File.separator)==-1?currentDisplayDir.length():currentDisplayDir.lastIndexOf(File.separator)
                        );
                    }
                    iPersonalnfView.setTitle(currentDisplayDir);
                    iPersonalnfView.openLoadingDialog("正在加载");
                    loadFloder(fileOrFolderItem.getFolder_fid());
                }else{
                    //文件点击
                    showFileOrFolderDetailDialog(fileOrFolderItem);
                }
            }

            @Override
            public boolean OnLongClick(FileOrFolderItem fileOrFolderItem, View view,int position) {
                if(fileOrFolderItem.isFolder()){
                    showFileOrFolderDetailDialog(fileOrFolderItem);
                    return true;
                }else{
                    //长按文件
                    adapter.enterMulSelect(position);
                    return true;
                }
            }
        };

        //菜单事件
        menuItemClickListener = new MenuItem.OnMenuItemClickListener[][]{{
                //刷新
                item -> {
                    iPersonalnfView.openLoadingDialog("正在刷新");
                    loadFloder(currentFid);
                    return true;
                },
                //上传文件
                item -> {
                    if(!PermissionUtils.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                        PermissionUtils.permission(PermissionConstants.STORAGE)
                                .rationale((activity, shouldRequest) -> shouldRequest.again(true))
                                .callback(new PermissionUtils.FullCallback() {
                                    @Override
                                    public void onGranted(@NonNull List<String> granted) {
                                        startChooseFileIntent();
                                    }

                                    @Override
                                    public void onDenied(@NonNull List<String> deniedForever, @NonNull List<String> denied) {
                                        //永久禁止
                                        AlertDialog.Builder builder = new AlertDialog.Builder(Utils.getApp())
                                                .setTitle("温馨提示")
                                                .setMessage("您已拒绝本软件再次请求存储权限，请前往设置页面手动授予本如那件存储权限。")
                                                .setPositiveButton("前往设置页面", (dialog, which) -> {
                                                    PermissionUtils.launchAppDetailsSettings();
                                                })
                                                .setCancelable(false);
                                        builder.show();
                                    }
                                }).request();
                        return false;
                    }
                    startChooseFileIntent();
                    return true;
                },
                //新建文件夹
                item -> {
                    EditText editText = new EditText(context);
                    editText.setHint("请输入文件夹名称");
                    LinearLayout l1 = new LinearLayout(context);
                    l1.setOrientation(LinearLayout.VERTICAL);
                    LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp1.setMargins(0, ConvertUtils.dp2px(20),0,0);
                    l1.setLayoutParams(lp1);
                    l1.setPadding(ConvertUtils.dp2px(10),0,ConvertUtils.dp2px(10),0);
                    l1.addView(editText);
                    AlertDialog alertDialog = new AlertDialog.Builder(context)
                            .setTitle("创建文件夹")
                            .setView(l1)
                            .setPositiveButton("确定",null)
                            .setNegativeButton("取消",null)
                            .show();
                    editText.requestFocus();
                    if(!KeyboardUtils.isSoftInputVisible((Activity) context)){
                        KeyboardUtils.showSoftInput(editText);
                    }
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                        String newfolder_name = editText.getText().toString().trim();
                        if(TextUtils.isEmpty(newfolder_name)){
                            ToastUtils.showShort("请输入文件夹名称");
                            editText.requestFocus();
                            if(!KeyboardUtils.isSoftInputVisible((Activity) context)){
                                KeyboardUtils.showSoftInput(editText);
                            }
                        }else{
                            iPersonalnfView.openLoadingDialog("正在创建");
                            if(KeyboardUtils.isSoftInputVisible((Activity) context)){
                                KeyboardUtils.hideSoftInput(editText);
                            }
                            createNewFolder(newfolder_name);
                            alertDialog.dismiss();
                        }
                    });
                    return true;
                }
            },{
                //全选
                item -> adapter.select(1),
                //反选
                item -> adapter.select(2),
                //全不选
                item -> adapter.select(0)
            }
        };

        //多选后下载事件
        tv_download_personalnffragmentr.setOnClickListener(v -> {
            Iterator<FileOrFolderItem> fileOrFolderItemIterator = adapter.getSelectedList();
            if(fileOrFolderItemIterator.hasNext()){
                List<Integer> fids = new ArrayList<>();
                List<String> mids = new ArrayList<>();
                while (fileOrFolderItemIterator.hasNext()){
                    FileOrFolderItem fileOrFolderItem = fileOrFolderItemIterator.next();
                    if(fileOrFolderItem.isFolder()){
                        fids.add(fileOrFolderItem.getFolder_fid());
                    }else{
                        mids.add(fileOrFolderItem.getFile_mid());
                    }
                }
                adapter.exitMulSelect();

                iPersonalnfView.openLoadingDialog("正在打包下载");
                personalnfModel.downloadBatchFilesAndFolders(mail_cookie, currentFid, String.valueOf(TimeUtils.getNowMills()), fids, mids, progress -> {
                    LogUtils.i(progress.getCurrentBytes()+"/"+progress.getTotalBytes(),progress.isFinish());
                    ThreadUtils.runOnUiThread(() -> {
                        if(progress.isFinish()){
                            iPersonalnfView.closeLoadingDialog();
                        }else{
                            iPersonalnfView.openLoadingDialog("正在打包下载\n"+ConvertUtils.byte2FitMemorySize(progress.getCurrentBytes()));
                        }
                    });

                });
            }else{
                ToastUtils.showShort("你没有选择任何文件或文件夹");
            }
        });
        //多选后删除事件
        tv_delete_personalnffragmentr.setOnClickListener(v -> {
            Iterator<FileOrFolderItem> fileOrFolderItemIterator = adapter.getSelectedList();
            if(fileOrFolderItemIterator.hasNext()){
                List<Integer> fids = new ArrayList<>();
                List<String> mids = new ArrayList<>();
                StringBuilder stringBuilder = new StringBuilder();
                StringBuilder stringBuilder2 = new StringBuilder();
                while (fileOrFolderItemIterator.hasNext()){
                    FileOrFolderItem fileOrFolderItem = fileOrFolderItemIterator.next();
                    if(fileOrFolderItem.isFolder()){
                        if(fileOrFolderItem.getFolder_fid()!=10){
                            fids.add(fileOrFolderItem.getFolder_fid());
                        }
                        stringBuilder.append("文件夹\""+fileOrFolderItem.getName()+"\"、");
                    }else{
                        mids.add(fileOrFolderItem.getFile_mid());
                        stringBuilder2.append("文件\""+fileOrFolderItem.getName()+"\"、");
                    }
                }
                stringBuilder2.deleteCharAt(stringBuilder2.length()-1);
                new AlertDialog.Builder(context)
                        .setTitle("温馨提示")
                        .setMessage("确定删除"+stringBuilder.toString()+stringBuilder2.toString())
                        .setPositiveButton("确定",(dialog, which) -> {
                            adapter.exitMulSelect();

                            iPersonalnfView.openLoadingDialog("正在删除");
                            personalnfModel.deleteFileOrFolder(mail_cookie, currentFid, fids, mids, new PersonalnfModel.DeleteCallBack() {
                                @Override
                                public void onDeleteSuccess() {
                                    iPersonalnfView.closeLoadingDialog();
                                    iPersonalnfView.ShowSnackbar(ContextCompat.getColor(context, iPersonalnfView.getColorPrimaryId()),"删除成功");
                                    loadFloder(currentFid);
                                }

                                @Override
                                public void onDeleteError(String error) {
                                    LogUtils.e(error);
                                    ToastUtils.showShort(error);
                                    iPersonalnfView.closeLoadingDialog();
                                }
                            });
                        })
                        .setNegativeButton("取消",null)
                        .show();
            }else{
                ToastUtils.showShort("你没有选择任何文件或文件夹");
            }
        });
    }
    private void startChooseFileIntent(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);//意图：文件浏览器
        intent.setType("*/*");//无类型限制
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);//关键！多选参数
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, CHOISEFILE_REQUESTCODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CHOISEFILE_REQUESTCODE:
                    assert data != null;
                    List<String> uploadFileList = new ArrayList<>();
                    if (data.getData() != null) {
                        //单次点击未使用多选的情况
                        try {
                            Uri uri = data.getData();
                            File file = UriUtils.uri2File(uri);
                            uploadFileList.add(file.toString());
                        } catch (Exception ignored) { }
                    }else{
                        //长按使用多选的情况
                        ClipData clipData = data.getClipData();
                        if (clipData != null) {
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                ClipData.Item item = clipData.getItemAt(i);
                                Uri uri = item.getUri();
                                File file = UriUtils.uri2File(uri);
                                uploadFileList.add(file.toString());
                            }
                        }
                    }
                    LogUtils.i(uploadFileList);
                    personalnfModel.uploadFiles(((FragmentActivity)context).getSupportFragmentManager(),
                            mail_cookie,currentFid,uploadFileList,
                            this);
                    break;
                default:
                    break;
            }
        }
    }

    private void createNewFolder(String newfolder_name) {
        if(personalnfModel == null){
            personalnfModel = new PersonalnfModel(context);
        }
        personalnfModel.createNewFolder(mail_cookie, newfolder_name, currentFid, new PersonalnfModel.CreateNewFolderCallBack() {
            @Override
            public void onCreateNewFolderSuccess() {
                iPersonalnfView.ShowSnackbar(ContextCompat.getColor(context, iPersonalnfView.getColorPrimaryId()),"文件夹创建成功");
                //iPersonalnfView.closeLoadingDialog();
                loadFloder(currentFid);
            }

            @Override
            public void onCreateNewFolderError(String error) {
                LogUtils.e(error);
                ToastUtils.showShort(error);
                iPersonalnfView.closeLoadingDialog();
            }
        });
    }

    Stack<Integer> stack = new Stack<>();

    private void loadFloder(int floder_id){
        if(floder_id==-1){
            if(stack.size()==0){
                floder_id = 0;
            }else {
                floder_id = stack.pop();
            }
        }else if(currentFid!=floder_id){
            stack.push(currentFid);
        }
        currentFid = floder_id;

        LogUtils.i(stack,currentFid);
        if(personalnfModel == null){
            personalnfModel = new PersonalnfModel(context);
        }
        personalnfModel.listFloderFile(mail_cookie,floder_id , new PersonalnfModel.GetListFloderFileCallBack() {
            @Override
            public void onListFloderFileSuccess(int cfid,List<FileOrFolderItem> fileOrFolderItems) {
                currentFid = cfid;//同步真实当前文件夹id
                iPersonalnfView.closeLoadingDialog();
                iPersonalnfView.setTitle(currentDisplayDir);
                adapter = new FileOrFolderAdapter(fileOrFolderItems,PersonalnfFragment.this);
                adapter.setOnItemOnClickListener(onFileOnClickOrFolderOnLongClickListener);
                rv_personalnffragment.setAdapter(adapter);
            }

            @Override
            public void onListFloderFileError(String error) {
                LogUtils.e(error);
                ToastUtils.showShort(error);
                iPersonalnfView.closeLoadingDialog();
            }
        });
    }

    @Override
    protected void initData() {
        //fileOrFolderItems = new ArrayList<>();
    }

    public void loadData(){
        iPersonalnfView.openLoadingDialog("正在加载");
        loadFloder(0);
    }

    @Override
    public void setMenu(Menu menu,int menu_res_id) {
        if(menu_res_id == R.menu.menu_personalnf_fragment){
            menu.findItem(R.id.menuitem_refresh_personalnf_fragment).setOnMenuItemClickListener(menuItemClickListener[0][0]);
            menu.findItem(R.id.menuitem_uploadfile_personalnf_fragment).setOnMenuItemClickListener(menuItemClickListener[0][1]);
            menu.findItem(R.id.menuitem_createnewfloder_personalnf_fragment).setOnMenuItemClickListener(menuItemClickListener[0][2]);
        }else if(menu_res_id == R.menu.menu_mulselect_personalnf_fragment){
            menu.findItem(R.id.menuitem_allselect_mulselect_personalnf_fragment).setOnMenuItemClickListener(menuItemClickListener[1][0]);
            menu.findItem(R.id.menuitem_reverseselect_mulselect_personalnf_fragment).setOnMenuItemClickListener(menuItemClickListener[1][1]);
            menu.findItem(R.id.menuitem_allnoselect_mulselect_personalnf_fragment).setOnMenuItemClickListener(menuItemClickListener[1][2]);
        }
    }

    /**
     * 文件或文件夹详情弹窗
     * @param fileOrFolderItem
     */
    private void showFileOrFolderDetailDialog(FileOrFolderItem fileOrFolderItem){
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setPositiveButton("分享",null)
                .setNeutralButton("下载",null)
                .setNegativeButton("取消",null);
        TextView tv_fid = new TextView(context);
        if(fileOrFolderItem.isFolder()){
            tv_fid.setText("文件夹id: "+fileOrFolderItem.getFolder_fid());
        }else{
            tv_fid.setText("文件id: "+fileOrFolderItem.getFile_mid()+"\n" +
                    "文件大小: "+ConvertUtils.byte2FitMemorySize(fileOrFolderItem.getFile_size(),2)+"\n" +
                    "上传时间: "+ TimeUtils.date2String(fileOrFolderItem.getFile_upload_time()));
        }
        tv_fid.setOnClickListener(v -> {
            if(fileOrFolderItem.isFolder()){
                ClipboardUtils.Copy2Clipboard(String.valueOf(fileOrFolderItem.getFolder_fid()));
                ToastUtils.showShort("文件夹id已复制至剪贴板");
            }else{
                ClipboardUtils.Copy2Clipboard(fileOrFolderItem.getFile_mid());
                ToastUtils.showShort("文件id已复制至剪贴板");
            }
        });
        tv_fid.setTextSize(COMPLEX_UNIT_SP,16);
        tv_fid.setTextColor(Color.parseColor("#696969"));
        tv_fid.setPadding(0, ConvertUtils.dp2px(15),0,0);
        LinearLayout l1 = new LinearLayout(context);
        l1.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        //lp1.setMargins(0, ConvertUtils.dp2px(35),0,0);
        l1.setLayoutParams(lp1);
        l1.setPadding(ConvertUtils.dp2px(25),0,ConvertUtils.dp2px(25),0);
        l1.addView(tv_fid);
        builder.setView(l1);
        if(fileOrFolderItem.isFolder()){
            builder.setTitle("文件夹\""+fileOrFolderItem.getName()+"\"");
        }else{
            builder.setTitle(fileOrFolderItem.getName());
        }
        AlertDialog alertDialog = builder.show();
        //重写下载按钮点击事件
        downloadFileOrFolder(alertDialog, fileOrFolderItem);

        //title点击重命名文件名
        renameFileOrFolder(alertDialog,fileOrFolderItem);

        //分享事件
        shareFileOrFolder(alertDialog,fileOrFolderItem);
    }

    /**
     * 获取文件或文件夹分享链接
     * @param alertDialog
     * @param fileOrFolderItem
     */
    private void shareFileOrFolder(AlertDialog alertDialog,FileOrFolderItem fileOrFolderItem){
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            iPersonalnfView.openLoadingDialog("创建分享链接");
            personalnfModel.shareFileOrFolder(mail_cookie, fileOrFolderItem.isFolder()?fileOrFolderItem.getFolder_fid():0, fileOrFolderItem.getFile_mid(), new PersonalnfModel.ShareFileOrFolderCallBack() {
                @Override
                public void onShareFileOrFolderSuccess(JSONObject jsonObject) {
                    iPersonalnfView.closeLoadingDialog();
                    String share_link = jsonObject.optString("share_link");
                    String uid = jsonObject.optString("share_from_uid");
                    String share_url = String.format("https://mail.lzu.edu.cn/coremail/XT5/jsp/download.jsp?share_link=%s&uid=%s", share_link, EncodeUtils.urlEncode(uid));
                    String pwd = jsonObject.optString("password");
                    ClipboardUtils.Copy2Clipboard("链接:"+share_url+"\n" +
                            "提取码:"+pwd);
                    if (fileOrFolderItem.isFolder()){
                        ToastUtils.showShort("文件夹链接已复制至剪贴板");
                    }else{
                        ToastUtils.showShort("文件链接已复制至剪贴板");
                    }
                }

                @Override
                public void onShareFileOrFolderError(String error) {
                    LogUtils.e(error);
                    ToastUtils.showShort(error);
                    iPersonalnfView.closeLoadingDialog();
                }
            });
        });
    }

    /**
     * 重命名文件或文件夹
     * @param alertDialog
     * @param fileOrFolderItem
     */
    private void renameFileOrFolder(AlertDialog alertDialog,FileOrFolderItem fileOrFolderItem){
        if(fileOrFolderItem.getFolder_fid()==10){
            return;
        }
        final boolean[] isrefresh = {false};
        alertDialog.setOnDismissListener(dialog -> {
            //如果重命名成功 则刷新
            if(isrefresh[0]){
                iPersonalnfView.openLoadingDialog("刷新列表");
                loadFloder(currentFid);
            }
        });
        //title点击重命名文件名
        TextView title = alertDialog.findViewById(androidx.appcompat.R.id.alertTitle);
        title.setOnClickListener(v -> {
            LinearLayout l2 = new LinearLayout(context);
            l2.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            //lp1.setMargins(0, ConvertUtils.dp2px(35),0,0);
            l2.setLayoutParams(lp2);
            l2.setPadding(ConvertUtils.dp2px(25),0,ConvertUtils.dp2px(25),0);

            EditText editText = new EditText(context);
            editText.setHint("请输入文件名");
            editText.setText(fileOrFolderItem.getName());
            if(fileOrFolderItem.isFolder()){
                editText.setSelection(0,editText.getText().length());
            }else{
                int end = editText.getText().toString().lastIndexOf(".");
                editText.setSelection(0,end==-1?editText.getText().length():end);
            }
            l2.addView(editText);
            AlertDialog alertDialog1 = new AlertDialog.Builder(context)
                    .setTitle("重命名文件名")
                    .setView(l2)
                    .setPositiveButton("确定",null)
                    .setNegativeButton("取消",null)
                    .show();
            alertDialog1.setOnDismissListener(dialog -> {
                if(KeyboardUtils.isSoftInputVisible((Activity) context)){
                    KeyboardUtils.hideSoftInput(editText);
                }
            });
            editText.requestFocus();
            if(!KeyboardUtils.isSoftInputVisible((Activity) context)){
                KeyboardUtils.showSoftInput(editText);
            }
            alertDialog1.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v1 -> {
                String newfilename = editText.getText().toString().trim();
                if(TextUtils.isEmpty(newfilename)){
                    ToastUtils.showShort("请输入文件名");
                    editText.requestFocus();
                    if(!KeyboardUtils.isSoftInputVisible((Activity) context)){
                        KeyboardUtils.showSoftInput(editText);
                    }
                }else if(!newfilename.equals(fileOrFolderItem.getName())){
                    if(KeyboardUtils.isSoftInputVisible((Activity) context)){
                        KeyboardUtils.hideSoftInput(editText);
                    }
                    PersonalnfModel.RenameCallBack renameCallBack = new PersonalnfModel.RenameCallBack() {
                        @Override
                        public void onRenameSuccess(String newname) {
                            iPersonalnfView.closeLoadingDialog();
                            ToastUtils.showShort("重命名成功");
                            if(fileOrFolderItem.isFolder()){
                                title.setText("文件夹\""+newname+"\"");
                            }else{
                                title.setText(newname);
                            }
                            isrefresh[0] = true;
                            alertDialog1.dismiss();
                        }

                        @Override
                        public void onRenameError(String error) {
                            LogUtils.e(error);
                            ToastUtils.showShort(error);
                            iPersonalnfView.closeLoadingDialog();
                        }
                    };
                    iPersonalnfView.openLoadingDialog("正在重命名");
                    if(fileOrFolderItem.isFolder()){
                        personalnfModel.renameFolder(mail_cookie, currentFid, fileOrFolderItem.getFolder_fid(), newfilename,renameCallBack );
                    }else{
                        personalnfModel.renameFile(mail_cookie, currentFid, fileOrFolderItem.getFile_mid(), newfilename,renameCallBack );
                    }
                }else{
                    alertDialog1.dismiss();
                }
            });
        });
    }

    /**
     * 下载文件或文件夹
     * @param alertDialog
     * @param fileOrFolderItem
     */
    private void downloadFileOrFolder(AlertDialog alertDialog,FileOrFolderItem fileOrFolderItem) {
        Button bt_neuttal = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        Button bt_positive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button bt_newgative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        bt_neuttal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //存储权限
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
                bt_positive.setEnabled(false);
                bt_newgative.setEnabled(false);
                alertDialog.setCancelable(false);
                long file_size = fileOrFolderItem.getFile_size();
                boolean hasfilesize = file_size==0;
                OnFileDownListener onFileDownListener = (status, object, proGress, currentDownProGress, totalProGress) -> ThreadUtils.runOnUiThread(()->{
                    if(status==0){
                        if(totalProGress==-1){
                            if(hasfilesize){
                                bt_neuttal.setText("已下载:"+ConvertUtils.byte2FitMemorySize(currentDownProGress));
                            }else{
                                bt_neuttal.setText("正在下载:"+currentDownProGress*100/file_size+"%");
                            }
                        }else{
                            bt_neuttal.setText("正在下载:"+proGress+"%");
                        }
                    }else if(status==-1){
                        ToastUtils.showShort("下载失败");
                        bt_neuttal.setText("下载");
                        bt_positive.setEnabled(true);
                        bt_newgative.setEnabled(true);
                        alertDialog.setCancelable(true);
                    }else{
                        File file = null;
                        Uri openfilepath = null;
                        if (object instanceof File){
                            file = (File) object;
                            openfilepath = UriUtils.file2Uri(file);
                        }else if (object instanceof Uri){
                            openfilepath = (Uri) object;
                            file = UriUtils.uri2File(openfilepath);
                        }
                        File finalFile = file;
                        new AlertDialog.Builder(context)
                                .setTitle("下载成功")
                                .setMessage("文件保存至"+(fileOrFolderItem.isFolder()?(file.getParent()+File.separator+fileOrFolderItem.getName()+".zip"):finalFile.toString()))
                                .setNeutralButton("确定",null)
                                .setPositiveButton("立即打开", (dialog, which) -> {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);

                                    intent.setDataAndType(UriUtils.file2Uri(new File(finalFile.toString())), fileOrFolderItem.getFile_content_type());
                                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//授予目录临时共享权限
                                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    try {
                                        context.startActivity(intent);
                                    } catch (ActivityNotFoundException e) {
                                        ToastUtils.showShort("No Application Available To View This File");
                                    }
                                })
                                .show();
                        alertDialog.dismiss();
                    }
                });
                if(fileOrFolderItem.isFolder()){
                    personalnfModel.downloadFolderPack(mail_cookie, fileOrFolderItem.getFolder_fid(),onFileDownListener);
                }else{
                    personalnfModel.downloadFile(mail_cookie,fileOrFolderItem.getFile_mid(),onFileDownListener);
                }
            }
        });
    }

    public void exitMulSelect(){
        adapter.exitMulSelect();
    }

    @Override
    public void setMulSelectDisplay(boolean ismulSelect) {
        ll_personalnffragmentr.setVisibility(ismulSelect?View.VISIBLE:View.GONE);
        iPersonalnfView.setSelectMenu(ismulSelect);
    }

    @Override
    public void refresh() {
        iPersonalnfView.openLoadingDialog("正在刷新");
        loadFloder(currentFid);
    }

    /**
     * 返回按钮事件
     * 由Activity调用判断
     * @return
     */
    @Override
    public boolean onBackPressed(){
        if(adapter.isMulSelect()){
            adapter.exitMulSelect();
            return true;
        }
        if(currentDisplayDir.contains(File.separator)){
            //返回上一层目录
            FileOrFolderItem fileOrFolderItem = new FileOrFolderItem();
            fileOrFolderItem.setName("..");
            fileOrFolderItem.setFolder(true);
            fileOrFolderItem.setFolder_fid(-1);
            onFileOnClickOrFolderOnLongClickListener.OnClick(fileOrFolderItem,null,0);
            return true;
        }
        return false;
    }
}
