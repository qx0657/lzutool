package fun.qianxiao.lzutool.base;

import android.content.Context;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import fun.qianxiao.lzutool.R;

/**
 * Create by QianXiao
 * On 2020/10/1
 */
public abstract class BasePreferenceFragment extends PreferenceFragmentCompat {
    protected Context context;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(getFragmentXmlID(),rootKey);
        context = getActivity();
        intPreference();
        intListener();
        initData();
    }

    protected abstract int getFragmentXmlID();

    protected abstract void intPreference();

    protected abstract void intListener();

    protected abstract void initData();

    @SuppressWarnings("unchecked")
    protected <E extends Preference> E f(CharSequence key){
        return (E)findPreference(key);
    }

}
