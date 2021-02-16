package fun.qianxiao.lzutool.ui.mail.adapter.listener;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    /**
     * 标记是否正在向上滑动
     */
    private boolean isSlidingUpward = false;
    private int laseItemPosition = -1;

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        //不滑动
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            //获取最后一个完全显示的项目下标
            //laseItemPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
            int itemCount = linearLayoutManager.getItemCount();
            //如果向上滑动到了最后一个item就加载更多
            if (laseItemPosition == (itemCount - 1) && isSlidingUpward) {
                onLoadMore();
            }
        }
    }

    /**
     * 加载更多
     */
    protected abstract void onLoadMore();

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        isSlidingUpward = dy > 0;
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        laseItemPosition = linearLayoutManager.findLastVisibleItemPosition();
    }
}
