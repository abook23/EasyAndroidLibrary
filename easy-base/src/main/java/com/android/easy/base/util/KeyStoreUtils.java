package com.android.easy.base.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 *将常量加密存储在本地,这样更加安全
 */
public class KeyStoreUtils {
    public String iv = "123456789abcdef";//偏移量
    public static final String DEFAULT_SECRETKEY_NAME = "default_secretkey_name";
    public static final String STORE_FILE_NAME = "crypto";
    private KeyStore keyStore;

    private KeyStoreUtils(KeyStore keyStore) {
        this.keyStore = keyStore;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public synchronized static KeyStoreUtils getInstance(Context context) {
        KeyStore keyStore;
        File file = new File(context.getFilesDir(), STORE_FILE_NAME);
        try {
            keyStore = getKeyStore(file);
            initKey(keyStore, file);
            KeyStoreUtils crypto = new KeyStoreUtils(keyStore);
            return crypto;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private static void initKey(KeyStore keyStore, File file) throws Exception {
        if (!keyStore.containsAlias(DEFAULT_SECRETKEY_NAME)) { // 秘钥不存在，则生成秘钥
            KeyGenerator keyGenerator = generateKeyGenerator();
            SecretKey secretKey = keyGenerator.generateKey();
            storeKey(keyStore, file, secretKey);
        }
    }

    private static void storeKey(KeyStore keyStore, File file, SecretKey secretKey) throws Exception {
        if (Build.VERSION.SDK_INT >= 23) {
            keyStore.setKeyEntry(DEFAULT_SECRETKEY_NAME, secretKey, null, null);
        } else {
            keyStore.setKeyEntry(DEFAULT_SECRETKEY_NAME, secretKey, null, null);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                keyStore.store(fos, null);
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    fos.close();
                }
            }
        }
    }

    private static KeyStore getKeyStore(File file) throws Exception {
        if (Build.VERSION.SDK_INT >= 23) {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            return keyStore;
        } else {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

            if (!file.exists()) {
                boolean isSuccess = file.createNewFile();
                if (!isSuccess) {
                    throw new SecurityException("创建内部存储文件失败");
                }
                keyStore.load(null, null);
            } else if (file.length() <= 0) {
                keyStore.load(null, null);
            } else {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(file);
                    keyStore.load(fis, null);
                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (fis != null) {
                        fis.close();
                    }
                }
            }
            return keyStore;
        }
    }

    @SuppressLint("DeletedProvider")
    private static KeyGenerator generateKeyGenerator() throws Exception {
        KeyGenerator keyGenerator;
        if (Build.VERSION.SDK_INT >= 23) {
            keyGenerator = KeyGenerator.getInstance("AES", "AndroidKeyStore");
            keyGenerator.init(new KeyGenParameterSpec.Builder(DEFAULT_SECRETKEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(false)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
        } else {
            keyGenerator = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG", "Crypto");
            secureRandom.setSeed(generateSeed());
            keyGenerator.init(128, secureRandom);
        }

        return keyGenerator;
    }

    private static byte[] generateSeed() {
        try {
            ByteArrayOutputStream seedBuffer = new ByteArrayOutputStream();
            DataOutputStream seedBufferOut =
                    new DataOutputStream(seedBuffer);
            seedBufferOut.writeLong(System.currentTimeMillis());
            seedBufferOut.writeLong(System.nanoTime());
            seedBufferOut.writeInt(android.os.Process.myPid());
            seedBufferOut.writeInt(android.os.Process.myUid());
            seedBufferOut.write(Build.BOARD.getBytes());
            return seedBuffer.toByteArray();
        } catch (IOException e) {
            throw new SecurityException("Failed to generate seed", e);
        }
    }

    /**
     * AES加密
     *
     * @param content
     * @return
     */
    public String aesEncrypt(String alias, String content) {
        try {
            SecretKey secretKey = getSecretKey(keyStore);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8)));
            byte[] bytes = cipher.doFinal();
//            byte[] iv = cipher.getIV();
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * AES解密
     *
     * @return
     */
    public String aesDecrypt(String alias, String data) {
        try {
            SecretKey secretKey = getSecretKey(keyStore);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8)));
            byte[] bytes = cipher.doFinal(Base64.decode(alias, Base64.DEFAULT));
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static SecretKey getSecretKey(KeyStore keyStore) {
        try {
            return (SecretKey) keyStore.getKey(DEFAULT_SECRETKEY_NAME, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

