package fun.qianxiao.lzutool.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;

import fun.qianxiao.lzutool.R;
import fun.qianxiao.lzutool.base.BaseDataBadingActivity;
import fun.qianxiao.lzutool.ui.main.MainReceiver;

/**
 * 桌面快捷方式管理
 * Create by QianXiao
 * On 2020/9/30
 */
public class MyLabelUtils {
    /**
     * 添加桌面快捷方式
     * @param cx
     * @param name
     * @param shortCutActivityName
     */
    public static void addShortcut(Activity cx, String name, String shortCutActivityName){
        int icon = R.drawable.icon;
        Intent shortCutIntent = new Intent(Intent.ACTION_MAIN);//快捷方式启动页面
        shortCutIntent.setAction(Intent.ACTION_VIEW);
        shortCutIntent.setClassName(cx.getPackageName(), shortCutActivityName);
        addShortcut(cx, name, icon, shortCutIntent);
    }

    /**
     *  添加桌面快捷方式
     * @param cx
     * @param name 快捷方式名称
     */
    public static void addShortcut(Activity cx, String name,int icon,Intent shortCutIntent) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ShortcutManager shortcutManager = (ShortcutManager) cx.getSystemService(Context.SHORTCUT_SERVICE);//获取shortcutManager
//如果默认桌面支持requestPinShortcut（ShortcutInfo，IntentSender）方法，则返回TRUE。
            if(shortcutManager != null && shortcutManager.isRequestPinShortcutSupported()){

                //快捷方式创建相关信息。图标名字 id
                ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(cx,name)
                        .setIcon(Icon.createWithResource(cx,icon))
                        .setShortLabel(name)
                        .setIntent(shortCutIntent)
                        .build();
                //创建快捷方式时候回调
                PendingIntent pendingIntent = PendingIntent.getBroadcast(cx,0,new
                        Intent(cx, MainReceiver.class),PendingIntent.FLAG_UPDATE_CURRENT);
                shortcutManager.requestPinShortcut(shortcutInfo,pendingIntent.getIntentSender());
            }
        }else{
            Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
            shortcut.putExtra("duplicate", false);
            Intent.ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(cx, icon);
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
            Intent carryIntent = new Intent(Intent.ACTION_MAIN);
            carryIntent.putExtra("name", name);
            carryIntent.setClassName(cx.getPackageName(),cx.getClass().getName());
            carryIntent.addCategory(Intent.CATEGORY_LAUNCHER);//添加categoryCATEGORY_LAUNCHER 应用被卸载时快捷方式也随之删除
            carryIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, carryIntent);
            cx.sendBroadcast(shortcut);
        }

    }
}
