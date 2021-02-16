package fun.qianxiao.lzutool.ui.mail.inbox;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.ToastUtils;

import java.util.List;

import fun.qianxiao.lzutool.R;
import fun.qianxiao.lzutool.base.BaseDateBadingFeagment;
import fun.qianxiao.lzutool.base.ItemOnClickListener;
import fun.qianxiao.lzutool.databinding.FragmentMailInboxBinding;
import fun.qianxiao.lzutool.ui.mail.ILzuMailView;
import fun.qianxiao.lzutool.ui.mail.adapter.MailAdapter;
import fun.qianxiao.lzutool.ui.mail.adapter.listener.EndlessRecyclerOnScrollListener;
import fun.qianxiao.lzutool.ui.mail.bean.MailInfo;
import fun.qianxiao.lzutool.ui.mail.detail.MailDetailFragment;

public class InBoxFragment extends BaseDateBadingFeagment<FragmentMailInboxBinding> implements IInBoxView{
    private ILzuMailView iLzuMailView;

    public InBoxFragment(ILzuMailView iLzuMailView) {
        this.iLzuMailView = iLzuMailView;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_mail_inbox;
    }

    @Override
    protected void initViewModel() {
        binding.setInBoxViewModel(new InBoxViewModel(context, iLzuMailView, this));
    }

    @Override
    protected void initView() {
        binding.recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            protected void onLoadMore() {
                MailAdapter adapter = (MailAdapter)(binding.recyclerView.getAdapter());
                adapter.setLoadState(adapter.LOADING);
                if (binding.getInBoxViewModel().hasMore) {//是否有更多
                    adapter.setLoadState(adapter.LOADING);
                    binding.getInBoxViewModel().loadMoreData();
                } else {
                    adapter.setLoadState(adapter.LOADING_END);
                }
            }
        });
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void getDataSuccess(List<MailInfo> mailInfoList) {
        binding.recyclerView.setAdapter(new MailAdapter(context, mailInfoList)
        .setInfoItemOnClickListener((data, position) -> iLzuMailView.enterMailDetail(data)));
    }

    @Override
    public void showMoreData(List<MailInfo> mailInfoList) {
        MailAdapter adapter = (MailAdapter) binding.recyclerView.getAdapter();
        adapter.addData(mailInfoList);
    }
}
