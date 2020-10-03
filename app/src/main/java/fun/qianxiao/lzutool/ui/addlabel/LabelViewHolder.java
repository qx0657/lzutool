package fun.qianxiao.lzutool.ui.addlabel;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Create by QianXiao
 * On 2020/10/2
 */
public class LabelViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {
    final T binding;

    public LabelViewHolder( T binding) {
        //super(itemView);
        super(binding.getRoot());
        this.binding = binding;
    }
}
