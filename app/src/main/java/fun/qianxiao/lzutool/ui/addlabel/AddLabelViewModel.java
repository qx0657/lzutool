package fun.qianxiao.lzutool.ui.addlabel;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import androidx.databinding.BaseObservable;
import androidx.databinding.BindingAdapter;

import com.blankj.utilcode.util.ToastUtils;

import fun.qianxiao.lzutool.utils.MyLabelUtils;

/**
 * Create by QianXiao
 * On 2020/10/2
 */
public class AddLabelViewModel implements IBindingClick{
    private Context context;
    public static final String TIP_TEXT = "已添加“%s”快捷方式，如无效请检查是否给与添加快捷方式权限";

    public AddLabelViewModel(Context context) {
        this.context = context;
    }

    @Override
    public void onItemClick(LabelData data) {
        MyLabelUtils.addShortcut((Activity) context,
                data.getName(),
                data.getShortCutActivityName());
        ToastUtils.showShort(String.format(TIP_TEXT, data.getName()));
        data.setDisplayName(data.getName()+"（已添加）");
    }

    @BindingAdapter("android:src")
    public static void setImageViewResource(ImageView view, int icon) {
        view.setImageResource(icon);
    }
}
