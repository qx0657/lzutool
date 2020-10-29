package fun.qianxiao.lzutool.ui.personalinf.fragment.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import fun.qianxiao.lzutool.R;
import fun.qianxiao.lzutool.ui.personalinf.bean.FileOrFolderItem;
import fun.qianxiao.lzutool.ui.personalinf.fragment.IPersoncalnfFragmentView;

/**
 * Create by QianXiao
 * On 2020/10/12
 */
public class FileOrFolderAdapter extends RecyclerView.Adapter<FileOrFolderAdapter.MyViewHolder> {
    private List<FileOrFolderItem> list;
    private IPersoncalnfFragmentView iPersoncalnfFragmentView;
    private boolean isMulSelect = false;
    private Set<Integer> selectedList = new HashSet<>();

    private OnFileOnClickOrFolderOnLongClickListener onFileOnClickOrFolderOnLongClickListener;

    public interface OnFileOnClickOrFolderOnLongClickListener{
        void OnClick(FileOrFolderItem fileOrFolderItem,View view,int position);
        boolean OnLongClick(FileOrFolderItem fileOrFolderItem,View view,int position);
    }
    public void setOnItemOnClickListener(OnFileOnClickOrFolderOnLongClickListener onItemOnClickListener) {
        this.onFileOnClickOrFolderOnLongClickListener = onItemOnClickListener;
    }

    public FileOrFolderAdapter(List<FileOrFolderItem> list,IPersoncalnfFragmentView iPersoncalnfFragmentView) {
        this.list = list;
        this.iPersoncalnfFragmentView = iPersoncalnfFragmentView;
    }


    @NonNull
    @Override
    public FileOrFolderAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fileorfolder_personalnf,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileOrFolderAdapter.MyViewHolder holder, int position) {
        FileOrFolderItem fileOrFolderItem = list.get(position);
        MyViewHolder myViewHolder = holder;
        myViewHolder.bind(fileOrFolderItem,position);
    }

    public boolean isMulSelect() {
        return isMulSelect;
    }

    public Iterator<FileOrFolderItem> getSelectedList() {
        List<FileOrFolderItem> selected = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if(selectedList.contains(i)){
                selected.add(list.get(i));
            }
        }
        return selected.iterator();
    }

    /**
     * @param flag 1 全选  0 全不选  2 反选
     */
    public boolean select(int flag){
        switch (flag){
            case 1:
                for (int i = 0; i < getItemCount(); i++) {
                    selectedList.add(i);
                }
                break;
            case 2:
                Set<Integer> integers = new HashSet<>();
                for (int i = 0; i < getItemCount(); i++) {
                    if(!selectedList.contains(i)){
                        integers.add(i);
                    }
                }
                selectedList.clear();
                selectedList.addAll(integers);
                break;
            case 0:
                selectedList.clear();
                break;
        }
        notifyDataSetChanged();
        return true;
    }

    public void enterMulSelect(int pos){
        iPersoncalnfFragmentView.setMulSelectDisplay(true);
        isMulSelect = true;
        selectedList.clear();
        selectedList.add(pos);
        notifyDataSetChanged();
    }

    public void exitMulSelect(){
        iPersoncalnfFragmentView.setMulSelectDisplay(false);
        isMulSelect = false;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_name_fileorfolderitem,tv_time_fileorfolderitem,tv_size_fileorfolderitem;
        LinearLayout ll_timeandsize_fileorfolderitem;
        ImageView iv_icon_fileorfolderitem;
        CheckBox cb_isselect_fileorfolderitem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name_fileorfolderitem = f(itemView,R.id.tv_name_fileorfolderitem);
            tv_time_fileorfolderitem = f(itemView,R.id.tv_time_fileorfolderitem);
            tv_size_fileorfolderitem = f(itemView,R.id.tv_size_fileorfolderitem);
            ll_timeandsize_fileorfolderitem = f(itemView,R.id.ll_timeandsize_fileorfolderitem);
            iv_icon_fileorfolderitem = f(itemView,R.id.iv_icon_fileorfolderitem);
            cb_isselect_fileorfolderitem = f(itemView,R.id.cb_isselect_fileorfolderitem);
        }

        void bind(FileOrFolderItem fileOrFolderItem, int position){
            tv_name_fileorfolderitem.setText(fileOrFolderItem.getName());
            if(!fileOrFolderItem.isFolder()){
                if(fileOrFolderItem.getFile_content_type().contains("image")){
                    iv_icon_fileorfolderitem.setImageResource(R.drawable.ic_image);
                }else if(fileOrFolderItem.getFile_content_type().contains("text")){
                    iv_icon_fileorfolderitem.setImageResource(R.drawable.ic_file_outline);
                }else if(fileOrFolderItem.getFile_content_type().contains("compressed")){
                    iv_icon_fileorfolderitem.setImageResource(R.drawable.ic_compressed);
                }
                ll_timeandsize_fileorfolderitem.setVisibility(View.VISIBLE);
                tv_time_fileorfolderitem.setText(TimeUtils.date2String(fileOrFolderItem.getFile_upload_time()));
                tv_size_fileorfolderitem.setText(ConvertUtils.byte2FitMemorySize(fileOrFolderItem.getFile_size(),2));
            }else{
                iv_icon_fileorfolderitem.setImageResource(R.drawable.ic_folder);
                ll_timeandsize_fileorfolderitem.setVisibility(View.GONE);
            }
            itemView.setOnClickListener(null);
            itemView.setOnLongClickListener(null);
            if(isMulSelect){
                cb_isselect_fileorfolderitem.setVisibility(View.VISIBLE);
                cb_isselect_fileorfolderitem.setChecked(selectedList.contains(position));
                itemView.setOnClickListener(v -> {
                    if(selectedList.contains(position)){
                        selectedList.remove(position);
                    }else{
                        selectedList.add(position);
                    }
                    notifyItemChanged(position);
                });
                cb_isselect_fileorfolderitem.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if(isChecked){
                        selectedList.add(position);
                    }else{
                        selectedList.remove(position);
                    }
                });
            }else{
                cb_isselect_fileorfolderitem.setVisibility(View.GONE);
                itemView.setOnLongClickListener(v -> onFileOnClickOrFolderOnLongClickListener.OnLongClick(fileOrFolderItem,v,position));
                itemView.setOnClickListener(v -> onFileOnClickOrFolderOnLongClickListener.OnClick(fileOrFolderItem,v,position));
            }
        }

        @SuppressWarnings("unchecked")
        private <E> E f(View view,int id){
            return (E) view.findViewById(id);
        }
    }
}
