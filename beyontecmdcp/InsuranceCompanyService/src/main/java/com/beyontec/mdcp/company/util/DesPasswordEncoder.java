package com.beyontec.mdcp.company.util;

import lombok.SneakyThrows;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StreamUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class DesPasswordEncoder implements PasswordEncoder {

    private static final String KEYS_PATH = "/keys/";

    private SecretKey secretKey;
    private SecretKey passwordKey;

    public DesPasswordEncoder() {
        secretKey = this.getSecretKey("key.out");
        passwordKey = this.getSecretKey("pwkey.out");
    }

    @Override
    @SneakyThrows
    public String encode(CharSequence rawPassword) {
        return this.encryptPassword(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return this.decryptPassword(encodedPassword).equals(rawPassword.toString());
    }

    private Cipher createCipher(int cipherMode) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("DESede");
            cipher.init(cipherMode, this.secretKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipher;
    }

    private SecretKey getSecretKey(String fileName) {
        SecretKey key = null;
        try (InputStream in = this.getClass().getResourceAsStream(KEYS_PATH + fileName)) {
            byte[] bytes = StreamUtils.copyToByteArray(in);
            DESedeKeySpec keyspec = new DESedeKeySpec(bytes);
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede");
            key = keyfactory.generateSecret(keyspec);
        } catch (Exception ie) {
            ie.printStackTrace();
        }
        return key;
    }

    @SneakyThrows
	public String encryptPassword(String password) {
        password = password + Base64.getEncoder().encodeToString(passwordKey.getEncoded());
        byte[] encodedPassword = password.getBytes(StandardCharsets.UTF_16);
        Cipher cipher = this.createCipher(Cipher.ENCRYPT_MODE);
        byte[] encryptedPassword = cipher.doFinal(encodedPassword);
        return Base64.getEncoder().encodeToString(encryptedPassword);
    }

    @SneakyThrows
    public String decryptPassword(String password) {
        byte[] decodedPassword = Base64.getMimeDecoder().decode(password.getBytes());
        Cipher cipher = this.createCipher(Cipher.DECRYPT_MODE);
        byte[] utf16 = cipher.doFinal(decodedPassword);
        String decryptedPassword = new String(utf16, StandardCharsets.UTF_16);
        return decryptedPassword.split("/")[0];
    }

    
//    
//public static void main(String args[]) {
//		
//		String password = "Test@123";
//		
//		DesPasswordEncoder DesPasswordEncoder = new DesPasswordEncoder();
//		System.out.println("Encrypted Password "+DesPasswordEncoder.encryptPassword(password));
//		
//		
//		String encryptedPassword = "lw/YKqXQ4zWGSD6lGabfVqWxlXZpdM1v5E9ba9yimG+cFOgyZMpsGI2AGPn8lNn3lBYziTfpqSPnB+EkWjU/lVbeXYnqL2JIy0ZB116jc1Ui4O0vXVAZqQ==";
//		System.out.println("decryptPassword Password "+DesPasswordEncoder.decryptPassword(encryptedPassword));
//	}
}
