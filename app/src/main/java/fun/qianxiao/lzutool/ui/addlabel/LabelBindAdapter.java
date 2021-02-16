package fun.qianxiao.lzutool.ui.addlabel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fun.qianxiao.lzutool.BR;
import fun.qianxiao.lzutool.R;

/**
 * Create by QianXiao
 * On 2020/10/2
 */
public class LabelBindAdapter<D extends ViewDataBinding> extends RecyclerView.Adapter<DataBindingRecyclerHolder<D>> {
    private List<LabelData> mDatas;
    //用于设置Item的事件Presenter
    private IBindingClick iBindingClick;

    public LabelBindAdapter(List<LabelData> mDatas) {
        this.mDatas = mDatas;
    }

    public LabelBindAdapter setIBindingClick(IBindingClick iBindingClick) {
        this.iBindingClick = iBindingClick;
        return this;
    }

    @NonNull
    @Override
    public DataBindingRecyclerHolder<D> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new DataBindingRecyclerHolder<>(DataBindingUtil.inflate(inflater, R.layout.item_label_layout, parent, false));
    }

    @Override
    public int getItemCount() {
        return mDatas==null?0:mDatas.size();
    }

    @Override
    public void onBindViewHolder(@NonNull DataBindingRecyclerHolder<D> holder, int position) {
        holder.binding.setVariable(BR.labelData,mDatas.get(position));
        holder.binding.setVariable(BR.addLabelViewModel,iBindingClick);
        holder.binding.executePendingBindings();
    }

}
