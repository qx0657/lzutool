package fun.qianxiao.lzutool.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

//LZUͨ�żӽ����㷨
public class LZUEncryptUtils{
	private static final String KEY = "QSfg2" + "6hg4" + a(3, 12) + "BV42a";
	public static void main(String[] args) {
		System.out.println(encrypt("123"));
		System.out.println(decrypt(encrypt("123")));
	}
	//����
	public static String encrypt(String s){
		return a(s, KEY);
	}
	//����
	public static String decrypt(String s){
		return b(s, KEY);
	}
	private static int a(int arg2, int arg3) {
        int v0;
        for(v0 = arg2 * arg3 - 1; v0 > 0; --v0) {
            if(v0 % arg2 == 0 && v0 % arg3 == 0) {
                return arg2 * (arg3 - 1);
            }
        }
        return (arg2 + 14) % arg3;
    }
    private static String b(String arg5, String arg6) {
        byte[] v0 = arg6.getBytes();
        try {
            if(arg6.length() != 16) {
                System.out.print("Key���Ȳ���16λ");
                return null;
            }
            SecretKeySpec v2 = new SecretKeySpec(arg6.getBytes("utf-8"), "AES");
            Cipher v6 = Cipher.getInstance("AES/CBC/NoPadding");
            v6.init(2, v2, new IvParameterSpec(v0));
            byte[] v5_1 = v6.doFinal(a(arg5));
            int v6_1 = v5_1.length;
            int v0_1;
            for(v0_1 = v5_1.length - 1; v0_1 > v5_1.length - 16 && v5_1[v0_1] == 0; --v0_1) {
                --v6_1;
            }
            return new String(v5_1, 0, v6_1, "utf-8");
        }
        catch(Exception v5) {
        	System.out.print("����ʧ�ܣ�" + v5.toString());
            return null;
        }
    }
    private static String a(String arg6, String arg7) {
        try {
            byte[] v1 = arg7.getBytes();
            if(arg7.length() != 16) {
                System.out.print("Key���Ȳ���16λ");
                return null;
            }
            SecretKeySpec v2 = new SecretKeySpec(arg7.getBytes("utf-8"), "AES");
            Cipher v7 = Cipher.getInstance("AES/CBC/NoPadding");
            int v3 = v7.getBlockSize();
            byte[] v6_1 = arg6.getBytes("utf-8");
            int v4 = v6_1.length;
            if(v4 % v3 != 0) {
                v4 += v3 - v4 % v3;
            }
            byte[] v3_1 = new byte[v4];
            System.arraycopy(((Object)v6_1), 0, ((Object)v3_1), 0, v6_1.length);
            v7.init(1, v2, new IvParameterSpec(v1));
            return a(v7.doFinal(v3_1));
        }
        catch(Exception v6) {
            v6.printStackTrace();
        	System.out.print("����ʧ�ܣ�" + v6.toString());
            return null;
        }
    }
    private static String a(byte[] arg6) {
        StringBuilder v0 = new StringBuilder();
        int v2;
        for(v2 = 0; v2 < arg6.length; ++v2) {
            String v3 = Integer.toHexString(arg6[v2] & 0xFF);
            if(v3.length() == 1) {
                v3 = "0" + v3;
            }
            v0.append(v3);
        }
        return v0.toString();
    }
    private static byte[] a(String arg6) {
        byte[] v1;
        int v0 = arg6.length();
        if(v0 % 2 == 1) {
            ++v0;
            v1 = new byte[v0 / 2];
            arg6 = "0" + arg6;
        }
        else {
            v1 = new byte[v0 / 2];
        }
        int v3 = 0;
        int v4 = 0;
        while(v3 < v0) {
            int v5 = v3 + 2;
            v1[v4] = b(arg6.substring(v3, v5));
            ++v4;
            v3 = v5;
        }
        return v1;
    }
    private static byte b(String arg1) {
        return (byte)Integer.parseInt(arg1, 16);
    }
}