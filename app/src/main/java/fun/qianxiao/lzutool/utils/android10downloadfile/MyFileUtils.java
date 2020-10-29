package fun.qianxiao.lzutool.utils.android10downloadfile;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

/**
 * Create by QianXiao
 * On 2020/10/13
 */
public class MyFileUtils {
    /**
     * 将InputStream写出到手机DOWNLOAD目录 文件
     * 适配android10
     * @param is
     */
    public static void saveByteStream2File(Context context,InputStream is, String fileName) throws IOException {
        OutputStream out = null;
        if (Build.VERSION.SDK_INT>=29) {//android 10
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
            String contentTypeFor = URLConnection.getFileNameMap().getContentTypeFor(fileName);
            if(TextUtils.isEmpty(contentTypeFor)){
                contentTypeFor = "application/octet-stream";
            }
            contentValues.put(MediaStore.Downloads.MIME_TYPE,contentTypeFor);
            contentValues.put(MediaStore.Downloads.DATE_TAKEN, System.currentTimeMillis());
            Uri uri = context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
            out = context.getContentResolver().openOutputStream(uri);
        }else{
            File file = new File(FileSDCardUtil.getInstance().getPublickDiskFileDirAndroid9(DIRECTORY_DOWNLOADS),fileName);
            out = new FileOutputStream(file);
        }
        byte[] buff = new byte[1024];
        int len = 0;
        while((len = is.read(buff))!=-1){
            out.write(buff, 0, len);
        }
        is.close();
        out.close();
    }
}
