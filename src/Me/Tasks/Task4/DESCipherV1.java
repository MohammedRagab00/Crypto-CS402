package Me.Tasks.Task4;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class DESCipherV1 {
    public static String processText(String text, String key, boolean encrypt) throws Exception {
        validateKey(key);

        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        SecretKey secretKey = generateKey(key);

        cipher.init(encrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, secretKey);

        byte[] textBytes = encrypt ?
                text.getBytes(StandardCharsets.UTF_8) :
                Base64.getDecoder().decode(text);

        byte[] processed = cipher.doFinal(textBytes);

        return encrypt ?
                Base64.getEncoder().encodeToString(processed) :
                new String(processed, StandardCharsets.UTF_8);
    }

    public static String generateKeySchedule(String key) {
        validateKey(key);

        StringBuilder sb = new StringBuilder();
        sb.append("Original Key: ").append(key).append("\n")
                .append("Hex: ").append(bytesToHex(key.getBytes())).append("\n\n");

        for (int i = 0; i < 16; i++) {
            String roundKey = transformKeyForRound(key, i + 1);
            sb.append(String.format("Round %2d: %s (Hex: %s)%n",
                    i + 1,
                    roundKey,
                    bytesToHex(roundKey.getBytes())));
        }
        return sb.toString();
    }

    private static SecretKey generateKey(String key) throws InvalidKeyException,
            NoSuchAlgorithmException, InvalidKeySpecException {
        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        return keyFactory.generateSecret(desKeySpec);
    }

    private static String transformKeyForRound(String key, int round) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] transformed = new byte[keyBytes.length];
        for (int i = 0; i < keyBytes.length; i++) {
            transformed[i] = (byte) ((keyBytes[i] + round) % 256);
        }
        return new String(transformed, StandardCharsets.UTF_8);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }

    private static void validateKey(String key) {
        if (key == null || key.length() != 8) {
            throw new IllegalArgumentException("DES key must be exactly 8 characters (56-bit effective)");
        }
    }
}