package pl.fhframework.dp.commons.fh.helper;

import org.springframework.web.util.UriUtils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

public class AESCypher {
    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public static SecretKey getKeyFromPassword(String password, String salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec)
                .getEncoded(), "AES");
        return secret;
    }

    private static String encodeURLValue(String value) {
        return UriUtils.encode(value, StandardCharsets.UTF_8.toString());
    }

    private static String decodeURLValue(String value) {
        return UriUtils.decode(value, StandardCharsets.UTF_8.toString());
    }

    public static String encrypt(String password, String text) throws InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException,
            BadPaddingException, InvalidKeyException {
        return AESCypher.encrypt(password, text, true);
    }

    public static String encrypt(String password, String text, boolean asUrlEncoded) throws NoSuchAlgorithmException, InvalidKeySpecException,
            NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException {
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        SecretKey key = AESCypher.getKeyFromPassword(password, now);
        IvParameterSpec iv = generateIv();

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] cipherText = cipher.doFinal(text.getBytes());
        String data = Base64.getEncoder().encodeToString(cipherText);
        String encodedData = Base64.getEncoder().encodeToString(iv.getIV()) + "~" + data;
        if (asUrlEncoded) {
            return AESCypher.encodeURLValue(encodedData);
        }
        return encodedData;
    }

    public static String decrypt(String password, String encryptedText) throws InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException,
            InvalidKeySpecException, InvalidKeyException {
        return AESCypher.decrypt(password, encryptedText, true);
    }

    public static String decrypt(String password, String encryptedText, boolean asUrlEncoded) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        String encryptedRaw = encryptedText;
        if (asUrlEncoded) {
            encryptedRaw = AESCypher.decodeURLValue(encryptedRaw);
        }
        String[] dataAndIV = encryptedRaw.split("~");
        String ivBase64 = dataAndIV[0];
        String cipherText = encryptedText.replace(ivBase64 + "~", "");
        byte[] ivByte = Base64.getDecoder().decode(ivBase64);
        IvParameterSpec iv = new IvParameterSpec(ivByte);

        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        SecretKey key = AESCypher.getKeyFromPassword(password, now);

        cipher.init(Cipher.DECRYPT_MODE, key, iv);

        byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(plainText);
    }
}
