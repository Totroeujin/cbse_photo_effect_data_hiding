package com.example.cbse_photo_effect.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class AESUtils {
    private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";

    public static byte[] encryptAES(String plaintext, String key, String iv) throws Exception {
        if (key == null || key.length() != 32) {
            throw new IllegalArgumentException("Invalid AES key. Must be 32 characters long.");
        }
        if (iv == null || iv.length() != 16) {
            throw new IllegalArgumentException("Invalid AES IV. Must be 16 characters long.");
        }

        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        return cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
    }

    public static String decryptAES(byte[] encryptedData, String key, String iv) throws Exception {
        if (key == null || key.length() != 32) {
            throw new IllegalArgumentException("Invalid AES key. Must be 32 characters long.");
        }
        if (iv == null || iv.length() != 16) {
            throw new IllegalArgumentException("Invalid AES IV. Must be 16 characters long.");
        }

        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] decryptedData = cipher.doFinal(encryptedData);
        return new String(decryptedData, StandardCharsets.UTF_8);
    }
}

