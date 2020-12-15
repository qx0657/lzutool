package fun.qianxiao.lzutool.ui.main.model.lzuoafileupload;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.UriUtils;

import java.io.File;
import java.util.List;

import fun.qianxiao.lzutool.R;
import fun.qianxiao.lzutool.utils.ClipboardUtils;

/**
 * Create by QianXiao
 * On 2020/10/9
 */
public class UploadAdapter extends RecyclerView.Adapter<UploadAdapter.ViewHolder> {
    private List<UploadItem> uploadFiles;
    private boolean isInf = false;

    public UploadAdapter(List<UploadItem> uploadFiles,boolean isInf) {
        this.uploadFiles = uploadFiles;
        this.isInf = isInf;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_upload,parent,false));
    }

    //重写此方法通过调用notifyItemChanged(position,payloads传入非空值)进行局部更新
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        //super.onBindViewHolder(holder, position, payloads);
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        }else{
            holder.update(uploadFiles.get(position));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UploadItem uploadItem = uploadFiles.get(position);
        holder.bind(uploadItem);
    }

    @Override
    public int getItemCount() {
        return uploadFiles==null?0:uploadFiles.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_item_upload;
        TextView tv_item_upload,tv_upload_statu_item_upload;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_item_upload = itemView.findViewById(R.id.iv_item_upload);
            tv_item_upload = itemView.findViewById(R.id.tv_item_upload);
            tv_upload_statu_item_upload = itemView.findViewById(R.id.tv_upload_statu_item_upload);
        }
        void update(UploadItem uploadItem){
            itemView.setOnClickListener(null);
            if(!TextUtils.isEmpty(uploadItem.getUrlPath())){
                if(isInf){
                    tv_upload_statu_item_upload.setText("上传成功");
                    itemView.setOnClickListener(null);
                }else{
                    tv_upload_statu_item_upload.setText("上传成功，点击复制链接");
                    itemView.setOnClickListener(v -> {
                        ClipboardUtils.Copy2Clipboard(uploadItem.getUrlPath());
                        ToastUtils.showShort("链接已复制至剪贴板");
                    });
                }
            }else if(!TextUtils.isEmpty(uploadItem.getError())) {
                tv_upload_statu_item_upload.setText("上传出错("+uploadItem.getError()+")");
            }else{
                tv_upload_statu_item_upload.setText(String.format("正在上传:%d%%", uploadItem.getPercent()));
            }
        }
        void bind(UploadItem uploadItem){
            String localfilepath = uploadItem.getLocalFilePath();
            if(!tv_item_upload.getText().equals(localfilepath.substring(localfilepath.lastIndexOf("/")+1))){
                tv_item_upload.setText(localfilepath.substring(localfilepath.lastIndexOf("/")+1));
            }
            if(ImageUtils.isImage(localfilepath)){
                iv_item_upload.setImageURI(UriUtils.file2Uri(new File(localfilepath)));
            }
        }
    }
}
