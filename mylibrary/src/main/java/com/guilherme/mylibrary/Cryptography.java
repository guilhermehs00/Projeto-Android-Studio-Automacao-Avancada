package com.guilherme.mylibrary;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class Cryptography {
    // Chave estática para criptografia AES de 256 bits.
    private static final String KEY = "12345678901234567890123456789012"; // 32 caracteres para 256 bits

    // Método para criptografar uma string. Recebe o dado como string e retorna o dado criptografado também como string.
    public static String encrypt(String dado) throws Exception {
        // Cria uma especificação de chave com base na constante KEY para o algoritmo AES.
        SecretKeySpec key = new SecretKeySpec(KEY.getBytes("UTF-8"), "AES");

        // Obtém uma instância do Cipher para o algoritmo AES.
        Cipher cipher = Cipher.getInstance("AES");

        // Inicializa o Cipher para criptografia usando a chave.
        cipher.init(Cipher.ENCRYPT_MODE, key);

        // Criptografa os dados e retorna uma string codificada em Base64 para fácil armazenamento e transmissão.
        byte[] encryptedData = cipher.doFinal(dado.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    // Método para descriptografar uma string criptografada. Recebe o dado criptografado como string e retorna o dado descriptografado também como string.
    public static String decrypt(String dadoCriptografado) throws Exception {
        // Recria a especificação de chave para AES.
        SecretKeySpec key = new SecretKeySpec(KEY.getBytes("UTF-8"), "AES");

        // Obtém uma instância do Cipher para AES.
        Cipher cipher = Cipher.getInstance("AES");

        // Inicializa o Cipher para descriptografia usando a chave.
        cipher.init(Cipher.DECRYPT_MODE, key);

        // Decodifica os dados criptografados de Base64 antes de descriptografá-los.
        byte[] decodedData = Base64.getDecoder().decode(dadoCriptografado);

        // Descriptografa os dados e retorna a string original.
        byte[] decryptedData = cipher.doFinal(decodedData);
        return new String(decryptedData, "UTF-8");
    }
}
