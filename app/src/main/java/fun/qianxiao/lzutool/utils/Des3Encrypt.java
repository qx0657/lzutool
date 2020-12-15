package fun.qianxiao.lzutool.utils;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class Des3Encrypt {
    public static String encrypt(String data,String key) throws Exception {
        if(key.length()==8){
            key = key + "0000000000000000";
        }
        SecretKey secretKey = SecretKeyFactory.getInstance("DESede").generateSecret(new DESedeKeySpec(key.getBytes()));
        Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        cipher.init(1,secretKey,new IvParameterSpec("12347890".getBytes()));
        return Base64.encodeToString(cipher.doFinal(data.trim().getBytes("utf-8")),0).trim().replace("\n","");
    }
}
