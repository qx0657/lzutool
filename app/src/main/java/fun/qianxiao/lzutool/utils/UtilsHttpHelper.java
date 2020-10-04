package fun.qianxiao.lzutool.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * 通用版URLConnection 带cookie下载PDF等资源文件
 * Create by QianXiao
 * On 2020/10/4
 * https://www.cnblogs.com/zeze/p/7007019.html
 */
public class UtilsHttpHelper {

    public interface DownloadNetCallBack{
        void onFinish();
    }
    /****
     * 下载pdf文件
     */
    public static void downloadNet(String urlStr,String cookie, String savePath,DownloadNetCallBack callBack) throws MalformedURLException {
        // 下载网络文件
        int bytesum = 0;
        int byteread = 0;
        // System.out.println(fileName);

        URL url = new URL(urlStr);

        try {
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setRequestProperty("Host", "self.lzu.edu.cn");
            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:81.0) Gecko/20100101 Firefox/81.0");
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
            conn.setRequestProperty("Content-Encoding", "utf8");
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Upgrade-Insecure-Requests", "1");
            conn.setRequestProperty("Cookie", cookie);
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("Content-Type", "application/pdf");

            // savePage(page,savePath,fileName);

            InputStream inStream = conn.getInputStream();
            FileOutputStream fs = new FileOutputStream(savePath);

            byte[] buffer = new byte[1204];
            int length;
            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread;
                // System.out.println(bytesum);
                fs.write(buffer, 0, byteread);
            }
            inStream.close();
            fs.close();
            callBack.onFinish();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
