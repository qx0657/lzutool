package fun.qianxiao.lzutool.ui.main.model.lzuoafileupload.view;

import android.os.Build;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

import fun.qianxiao.lzutool.R;
import fun.qianxiao.lzutool.base.BaseDialogFragment;
import fun.qianxiao.lzutool.ui.main.model.lzuoafileupload.FileUploadModel;
import fun.qianxiao.lzutool.ui.main.model.lzuoafileupload.UploadAdapter;
import fun.qianxiao.lzutool.ui.main.model.lzuoafileupload.UploadItem;

/**
 * Create by QianXiao
 * On 2020/10/9
 */
public class UploadFragmentDialog extends BaseDialogFragment implements FileUploadModel.MyProgressListener{
    private List<UploadItem> uploadItems;
    private RecyclerView recyclerView;
    private TextView tv_allpercent_uploaddialogfragment;
    private UploadAdapter adapter;
    private boolean isInf = false;

    public UploadFragmentDialog(List<String> uploadFiles,boolean isInf) {
        uploadItems = new ArrayList<>();
        this.isInf = isInf;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uploadFiles.forEach(s -> {
                UploadItem uploadItem = new UploadItem();
                uploadItem.setLocalFilePath(s);
                uploadItems.add(uploadItem);
            });
        }else{
            for (String s : uploadFiles) {
                UploadItem uploadItem = new UploadItem();
                uploadItem.setLocalFilePath(s);
                uploadItems.add(uploadItem);
            }
        }
    }

    @Override
    protected int getLayoutID() {
        return R.layout.dialogfragment_upload;
    }

    @Override
    protected void initView() {
        recyclerView = f(R.id.rv_main_uploaddialogfragment);
        tv_allpercent_uploaddialogfragment = f(R.id.tv_allpercent_uploaddialogfragment);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
        setCancelable(false);
        adapter = new UploadAdapter(uploadItems,isInf);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onProgress(int currentfile, int currentpercent, int allpercent) {
        uploadItems.get(currentfile-1).setPercent(currentpercent);
        tv_allpercent_uploaddialogfragment.setText(String.format("正在上传，总进度:%d%%", allpercent));
        adapter.notifyItemChanged(currentfile-1,1);
    }

    @Override
    public void onFinish(int currentfile,String urlpath) {
        LogUtils.i(currentfile,urlpath);
        uploadItems.get(currentfile-1).setUrlPath(urlpath);
        adapter.notifyItemChanged(currentfile-1,1);
        if(currentfile == uploadItems.size()){
            //全部上传完成
            tv_allpercent_uploaddialogfragment.setText("全部上传完成");
            setCancelable(true);
        }
    }

    @Override
    public void onError(int currentfile,String error) {
        LogUtils.e(currentfile,error);
        uploadItems.get(currentfile-1).setError(error);
        adapter.notifyItemChanged(currentfile-1,1);
        setCancelable(true);
    }
}
