package fun.qianxiao.lzutool.ui.setting;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;

import fun.qianxiao.lzutool.R;
import fun.qianxiao.lzutool.base.BasePreferenceFragment;
import fun.qianxiao.lzutool.utils.MySpUtils;

/**
 * 设置PreferenceFragment
 * Create by QianXiao
 * On 2020/10/1
 */
public class SettingFragment extends BasePreferenceFragment {
    private ListPreference preference_list_lzulibreserve;

    public static final String APPRUNINBACKGROUND_KEY = "preference_switch_backgroundrun_setting";
    public static final String LZULIBRESERVE_KEY = "preference_list_lzulibreserve";

    @Override
    protected int getFragmentXmlID() {
        return R.xml.setting;
    }

    @Override
    protected void intPreference() {
        preference_list_lzulibreserve = f(LZULIBRESERVE_KEY);
    }

    @Override
    protected void intListener() {
        preference_list_lzulibreserve.setOnPreferenceChangeListener((preference, newValue) -> {
            changeLzulibreserveSummary((String) newValue);
            if(newValue.equals("2")){
                MySpUtils.save("reserve_campus",1);
            }else if(newValue.equals("1")){
                MySpUtils.save("reserve_campus",0);
            }
            return true;
        });
    }

    @Override
    protected void initData() {
        changeLzulibreserveSummary(preference_list_lzulibreserve.getValue());
    }

    private void changeLzulibreserveSummary(String value) {
        String lzulibreserveSummary = "点击预约后选择校区";
        switch (value){
            case "1":
                lzulibreserveSummary = "点击后自动预约榆中校区";
                break;
            case "2":
                lzulibreserveSummary = "点击后自动预约城关校区";
                break;
            default:
                break;
        }
        preference_list_lzulibreserve.setSummary(lzulibreserveSummary);
    }
}
