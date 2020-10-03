package fun.qianxiao.lzutool.ui.main.sub;

import android.text.TextUtils;

import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.ToastUtils;

import fun.qianxiao.lzutool.R;
import fun.qianxiao.lzutool.bean.User;
import fun.qianxiao.lzutool.ui.main.model.lzulibreserve.LzulibreserveModel;
import fun.qianxiao.lzutool.ui.setting.SettingFragment;
import fun.qianxiao.lzutool.utils.MySpUtils;

/**
 * Create by QianXiao
 * On 2020/10/2
 */
public class LzuLibReserveActivity extends WelcomeActivity{
    private LzulibreserveModel lzulibreserveModel;

    @Override
    protected void initData() {
        setTitle("图书馆预约");
        if(lzulibreserveModel == null){
            lzulibreserveModel = new LzulibreserveModel();
        }
        if(MySpUtils.getString(SettingFragment.LZULIBRESERVE_KEY).equals("0")){
            ToastUtils.showShort("仅支持自动预约时有效");
        }else{
            User user = MySpUtils.getObjectData("user");
            if(user==null){
                ToastUtils.showShort("请登录后使用");
            }else{
                String cardid = user.getCardid();
                if(TextUtils.isEmpty(cardid)){
                    ToastUtils.showShort("用户校园卡号为空，请重新登录后使用");
                }else{
                    openLoadingDialog("正在预约");
                    lzulibreserveModel.reserve(cardid,MySpUtils.getString(SettingFragment.LZULIBRESERVE_KEY).equals("2"), res -> {
                        closeLoadingDialog();
                        if(res.contains("已预约")){
                            ShowSnackbar(ContextCompat.getColor(context,R.color.warning),res);
                        }else if(res.contains("预约成功")){
                            ShowSnackbar(ContextCompat.getColor(context, getColorPrimaryId()),res);
                        }else{
                            ShowSnackbar(res);
                        }
                    });
                }
            }
        }

    }
}
