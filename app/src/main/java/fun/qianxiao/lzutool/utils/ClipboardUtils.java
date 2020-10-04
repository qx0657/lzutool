package fun.qianxiao.lzutool.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;

import fun.qianxiao.lzutool.MyApplication;

/**
 * 2019年11月14日19点13分
 */
public class ClipboardUtils {

    /**
     * 设置内容到剪贴板
     *
     * @param text
     */
    public static void Copy2Clipboard(String text) {
        if (text == null || TextUtils.isEmpty(text)) {
            return;
        }
        //获取剪贴板管理器
        ClipboardManager cm = (ClipboardManager) MyApplication.getInstance().getSystemService(Context.CLIPBOARD_SERVICE);
        //创建普通文本ClipData
        ClipData mClipData = ClipData.newPlainText("Label", text);//text
        //将ClipData设置到系统剪贴板
        cm.setPrimaryClip(mClipData);
    }

    public static String GetClipboardContent() {
        ClipboardManager cm = (ClipboardManager) MyApplication.getInstance().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = cm.getPrimaryClip();
        if (clipData != null && clipData.getItemCount() > 0) {
            ClipData.Item item = clipData.getItemAt(0);
            return item.getText().toString();
        }
        return "";
    }

}
