package fun.qianxiao.lzutool.ui.addlabel;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import fun.qianxiao.lzutool.R;
import fun.qianxiao.lzutool.base.BaseDataBadingActivity;
import fun.qianxiao.lzutool.databinding.ActivityAddlabelBinding;
import fun.qianxiao.lzutool.ui.main.sub.HealthPunchActivity;
import fun.qianxiao.lzutool.ui.main.sub.LzuLibReserveActivity;

/**
 * Create by QianXiao
 * On 2020/10/2
 */
public class AddLabelActivity extends BaseDataBadingActivity<ActivityAddlabelBinding> {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        isAddToolbarMarginTopEqualStatusBarHeight = true;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_addlabel;
    }

    @Override
    protected void initViewModel() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
        setTitle("添加快捷方式");
        showBackButton();
        List<LabelData> labelDataList = new ArrayList<>();
        labelDataList.add(new LabelData(
                R.drawable.ic_book_outline,
                "图书馆预约",
                LzuLibReserveActivity.class.getName()));
        labelDataList.add(new LabelData(
                R.drawable.ic_medkit_outline,
                "健康打卡",
                HealthPunchActivity.class.getName()));
        LabelBindAdapter adapter = new LabelBindAdapter(labelDataList);
        adapter.setIBindingClick(new AddLabelViewModel(this));
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.recyclerView.setAdapter(adapter);
    }
}
