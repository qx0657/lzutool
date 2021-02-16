package fun.qianxiao.lzutool.base.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fun.qianxiao.lzutool.R;
import fun.qianxiao.lzutool.databinding.ListRefreshFooterBinding;
import fun.qianxiao.lzutool.ui.addlabel.DataBindingRecyclerHolder;

public abstract class BaseDataBindingAdapter<T,D extends ViewDataBinding> extends RecyclerView.Adapter<DataBindingRecyclerHolder<D>>  {
    protected List<T> list;
    /**
     * 普通布局
     */
    private final int TYPE_ITEM = 1;
    /**
     * 脚布局
     */
    private final int TYPE_FOOTER = 2;
    /**
     * 当前脚布局状态 默认正在加载完成
     */
    private int footer_state = 2;
    /**
     * 加载中
     */
    public final int LOADING = 1;
    /**
     * 加载完成
     */
    public final int LOADING_COMPLETE = 2;
    /**
     * 加载到底 没有更多
     */
    public final int LOADING_END = 3;

    public BaseDataBindingAdapter(List<T> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public DataBindingRecyclerHolder<D> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            return new DataBindingRecyclerHolder<>(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), getLayoutId(), parent, false));
        }else{
            return new DataBindingRecyclerHolder<>(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.list_refresh_footer, parent, false));
        }
    }

    protected abstract int getLayoutId();

    @Override
    public int getItemViewType(int position) {
        if (position+1 == getItemCount()) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(@NonNull DataBindingRecyclerHolder<D> holder, int position) {
        if(holder.binding instanceof ListRefreshFooterBinding){
            switch (footer_state) {
                case LOADING:
                    ((ListRefreshFooterBinding) holder.binding).listFotterProgressbar.setVisibility(View.VISIBLE);
                    ((ListRefreshFooterBinding) holder.binding).listFotterTvLine1.setVisibility(View.GONE);
                    ((ListRefreshFooterBinding) holder.binding).listFooterTvLine2.setVisibility(View.GONE);
                    ((ListRefreshFooterBinding) holder.binding).listFotterTvTip.setVisibility(View.VISIBLE);
                    ((ListRefreshFooterBinding) holder.binding).listFotterTvTip.setText("正在加载...");
                    break;
                case LOADING_COMPLETE:
                    ((ListRefreshFooterBinding) holder.binding).listFotterProgressbar.setVisibility(View.GONE);
                    ((ListRefreshFooterBinding) holder.binding).listFotterTvLine1.setVisibility(View.GONE);
                    ((ListRefreshFooterBinding) holder.binding).listFooterTvLine2.setVisibility(View.GONE);
                    ((ListRefreshFooterBinding) holder.binding).listFotterTvTip.setVisibility(View.GONE);
                    break;
                case LOADING_END:
                    ((ListRefreshFooterBinding) holder.binding).listFotterProgressbar.setVisibility(View.GONE);
                    ((ListRefreshFooterBinding) holder.binding).listFotterTvLine1.setVisibility(View.VISIBLE);
                    ((ListRefreshFooterBinding) holder.binding).listFooterTvLine2.setVisibility(View.VISIBLE);
                    ((ListRefreshFooterBinding) holder.binding).listFotterTvTip.setVisibility(View.VISIBLE);
                    ((ListRefreshFooterBinding) holder.binding).listFotterTvTip.setText("我也是有底线的");
                    ((ListRefreshFooterBinding) holder.binding).listFotterTvTip.setTextColor(Color.parseColor("#969696"));
                    break;
                default:
                    break;
            }
        }else{
            final T data = list.get(position);
            onBind(holder, data, position);
        }
    }

    protected abstract void onBind(DataBindingRecyclerHolder<D> holder, T t, int position);

    @Override
    public int getItemCount() {
        return list == null ? 1 : list.size()+1;
    }

    /**
     * 设置当前加载状态
     *
     * @param loadstation
     */
    public void setLoadState(int loadstation) {
        this.footer_state = loadstation;
        notifyDataSetChanged();
    }
}
