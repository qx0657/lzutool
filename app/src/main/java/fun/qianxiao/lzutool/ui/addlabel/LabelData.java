package fun.qianxiao.lzutool.ui.addlabel;

import android.widget.ImageView;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.library.baseAdapters.BR;

/**
 * Create by QianXiao
 * On 2020/10/2
 */
public class LabelData extends BaseObservable {
    private int icon;
    private String name;
    private String displayName;
    private String shortCutActivityName;

    public LabelData(int icon, String name, String shortCutActivityName) {
        this.icon = icon;
        this.name = name;
        this.displayName = name;
        this.shortCutActivityName = shortCutActivityName;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Bindable
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        notifyPropertyChanged(BR.displayName);
    }

    public String getShortCutActivityName() {
        return shortCutActivityName;
    }

    public void setShortCutActivityName(String shortCutActivityName) {
        this.shortCutActivityName = shortCutActivityName;
    }
}
