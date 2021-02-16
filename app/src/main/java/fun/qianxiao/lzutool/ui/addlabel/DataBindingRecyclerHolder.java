package fun.qianxiao.lzutool.ui.addlabel;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import fun.qianxiao.lzutool.databinding.ListRefreshFooterBinding;

/**
 * Create by QianXiao
 * On 2020/10/2
 */
public class DataBindingRecyclerHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {
    public final T binding;

    public DataBindingRecyclerHolder(T binding) {
        //super(itemView);
        super(binding.getRoot());
        this.binding = binding;
    }
}
