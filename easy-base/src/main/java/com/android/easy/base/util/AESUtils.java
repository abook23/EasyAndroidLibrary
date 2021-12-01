package com.android.easy.base.util;

import android.content.Context;
import android.util.Base64;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESUtils {

    public static AESUtils aesUtils;
    private static final String TAG = "TAG";
    private AESUtils() {
    }
    public synchronized static AESUtils getInstance(Context context) {
        if (aesUtils==null){
            synchronized (AESUtils.class){
                aesUtils = new AESUtils();
            }
        }
        return aesUtils;

    }
    /**
     * AES加密
     * @return
     */
    public String encryptCBC(String data,String key,String iv) {
        try {
            SecretKey secretKey = getSecretKey(key);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey,new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8)));
            byte[] bytes = cipher.doFinal(data.getBytes(Charset.defaultCharset()));
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * AES解密
     * @return
     */
    public String decryptCBC(String data,String key, String iv) {
        try {
            SecretKey secretKey = getSecretKey(key);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey,new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8)));
            byte[] bytes = cipher.doFinal(Base64.decode(data, Base64.DEFAULT));
            return new String(bytes,Charset.defaultCharset());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String encryptECB(String data,String key) {
        try {
            SecretKey secretKey = getSecretKey(key);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] bytes = cipher.doFinal(data.getBytes(Charset.defaultCharset()));
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * AES解密
     * @return
     */
    public String decryptECB(String data,String key) {
        try {
            SecretKey secretKey = getSecretKey(key);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] bytes = cipher.doFinal(Base64.decode(data, Base64.DEFAULT));
            return new String(bytes,Charset.defaultCharset());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static SecretKey getSecretKey(String key) {
        return new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
    }
}
