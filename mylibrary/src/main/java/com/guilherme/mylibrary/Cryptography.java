package com.guilherme.mylibrary;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class Cryptography {
    private static final String KEY = "12345678901234567890123456789012"; // 32 caracteres para 256 bits

    public static String encrypt(String dado) throws Exception {
        SecretKeySpec key = new SecretKeySpec(KEY.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedData = cipher.doFinal(dado.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    public static String decrypt(String dadoCriptografados) throws Exception {
        SecretKeySpec key = new SecretKeySpec(KEY.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedData = Base64.getDecoder().decode(dadoCriptografados);
        byte[] decryptedData = cipher.doFinal(decodedData);
        return new String(decryptedData, "UTF-8");
    }
}