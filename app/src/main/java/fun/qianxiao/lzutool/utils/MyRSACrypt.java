package fun.qianxiao.lzutool.utils;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.StringUtils;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;

public class MyRSACrypt {
    public static RSAPublicKey getPublicKey(String   modulus, String  publicExponent)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        BigInteger bigIntModulus = new BigInteger(modulus,16);
        BigInteger bigIntPrivateExponent = new BigInteger(publicExponent,16);
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(bigIntModulus, bigIntPrivateExponent);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return (RSAPublicKey) publicKey;
    }

    public static String encrypt(RSAPublicKey publicKey, String data) throws Exception {
        if (publicKey == null) {
            throw new Exception("加密公钥为空");
        }
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] output = cipher.doFinal(StringUtils.reverse(data).getBytes());
        return ConvertUtils.bytes2HexString(output,false);
    }
}
