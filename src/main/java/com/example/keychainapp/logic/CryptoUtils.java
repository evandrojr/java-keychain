package com.example.keychainapp.logic;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class CryptoUtils {
    // Chave fixa de 16 bytes (128 bits) - nunca compartilhe em código real!
    private static final String FIXED_KEY = "MinhaChaveFixa123"; // 16 chars

    public static String encrypt(String plainText) throws Exception {
        SecretKeySpec key = new SecretKeySpec(FIXED_KEY.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decrypt(String cipherText) throws Exception {
        SecretKeySpec key = new SecretKeySpec(FIXED_KEY.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decoded = Base64.getDecoder().decode(cipherText);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted, "UTF-8");
    }
}
