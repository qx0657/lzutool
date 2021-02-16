package fun.qianxiao.lzutool.ui.mail.adapter;

import android.content.Context;

import androidx.databinding.ViewDataBinding;

import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.List;

import fun.qianxiao.lzutool.BR;
import fun.qianxiao.lzutool.R;
import fun.qianxiao.lzutool.base.ItemOnClickListener;
import fun.qianxiao.lzutool.base.adapter.BaseDataBindingAdapter;
import fun.qianxiao.lzutool.databinding.ItemMailBinding;
import fun.qianxiao.lzutool.databinding.ListRefreshFooterBinding;
import fun.qianxiao.lzutool.ui.addlabel.DataBindingRecyclerHolder;
import fun.qianxiao.lzutool.ui.mail.bean.MailInfo;

public class MailAdapter extends BaseDataBindingAdapter<MailInfo, ItemMailBinding> {
    private Context context;
    private ItemOnClickListener<MailInfo> infoItemOnClickListener;

    public MailAdapter(Context context, List<MailInfo> list) {
        super(list);
        this.context = context;
    }

    public void addData(List<MailInfo> addedList){
        list.addAll(addedList);
        notifyDataSetChanged();
    }

    public MailAdapter setInfoItemOnClickListener(ItemOnClickListener<MailInfo> infoItemOnClickListener) {
        this.infoItemOnClickListener = infoItemOnClickListener;
        return this;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_mail;
    }

    @Override
    protected void onBind(DataBindingRecyclerHolder<ItemMailBinding> holder, MailInfo mailInfo, int position) {
        holder.binding.setVariable(BR.mailInfo, mailInfo);
        holder.binding.executePendingBindings();
        holder.binding.tvMailitemSubject.getPaint().setFakeBoldText(!mailInfo.isFlag_read());
        holder.itemView.setOnClickListener(v -> {
            if(infoItemOnClickListener!=null){
                infoItemOnClickListener.onItemClick(mailInfo, position);
            }
        });
    }
}
