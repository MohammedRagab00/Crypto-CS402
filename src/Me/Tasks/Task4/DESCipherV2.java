package Me.Tasks.Task4;

public class DESCipherV2 {
    private static final int[] INITIAL_PERMUTATION = {
            58, 50, 42, 34, 26, 18, 10, 2,
            60, 52, 44, 36, 28, 20, 12, 4,
            62, 54, 46, 38, 30, 22, 14, 6,
            64, 56, 48, 40, 32, 24, 16, 8,
            57, 49, 41, 33, 25, 17, 9, 1,
            59, 51, 43, 35, 27, 19, 11, 3,
            61, 53, 45, 37, 29, 21, 13, 5,
            63, 55, 47, 39, 31, 23, 15, 7
    };

    private static final int[] FINAL_PERMUTATION = {
            40, 8, 48, 16, 56, 24, 64, 32,
            39, 7, 47, 15, 55, 23, 63, 31,
            38, 6, 46, 14, 54, 22, 62, 30,
            37, 5, 45, 13, 53, 21, 61, 29,
            36, 4, 44, 12, 52, 20, 60, 28,
            35, 3, 43, 11, 51, 19, 59, 27,
            34, 2, 42, 10, 50, 18, 58, 26,
            33, 1, 41, 9, 49, 17, 57, 25
    };

    private static String hexToBinary(String hex) {
        StringBuilder binary = new StringBuilder();
        for (char ch : hex.toCharArray()) {
            binary.append(String.format("%4s", Integer.toBinaryString(Integer.parseInt(String.valueOf(ch), 16)))
                    .replace(' ', '0'));
        }
        return binary.toString();
    }

    private static String binaryToHex(String binary) {
        StringBuilder hex = new StringBuilder();
        for (int i = 0; i < binary.length(); i += 4) {
            hex.append(Integer.toHexString(Integer.parseInt(binary.substring(i, i + 4), 2)).toUpperCase());
        }
        return hex.toString();
    }

    private static String permute(String input, int[] table) {
        StringBuilder output = new StringBuilder();
        for (int i : table) {
            output.append(input.charAt(i - 1));
        }
        return output.toString();
    }

    // XOR operation
    private static String xor(String a, String b) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < a.length(); i++) {
            result.append(a.charAt(i) == b.charAt(i) ? "0" : "1");
        }
        return result.toString();
    }

    // Encrypt function
    public static String encrypt(String plaintext, String key) {
        String binaryPlaintext = hexToBinary(plaintext);
        binaryPlaintext = permute(binaryPlaintext, INITIAL_PERMUTATION);

        // Split into left and right halves
        String left = binaryPlaintext.substring(0, 32);
        String right = binaryPlaintext.substring(32);

        // Example XOR operation with the key for demonstration
        String keyBinary = hexToBinary(key);
        right = xor(right, keyBinary.substring(0, 32));

        // Swap and apply final permutation
        String combined = right + left;
        return binaryToHex(permute(combined, FINAL_PERMUTATION));
    }

    // Decrypt function (same structure but reversing steps)
    public static String decrypt(String ciphertext, String key) {
        String binaryCiphertext = hexToBinary(ciphertext);
        binaryCiphertext = permute(binaryCiphertext, INITIAL_PERMUTATION);

        String left = binaryCiphertext.substring(0, 32);
        String right = binaryCiphertext.substring(32);

        String keyBinary = hexToBinary(key);
        left = xor(left, keyBinary.substring(0, 32));

        String combined = left + right;
        return binaryToHex(permute(combined, FINAL_PERMUTATION));
    }

    public static void main(String[] args) {
        String plaintext = "123456ABCD132536";
        String key = "AABB09182736CCDD";

        String encryptedText = encrypt(plaintext, key);
        System.out.println("Encrypted Text: " + encryptedText);

        String decryptedText = decrypt(encryptedText, key);
        System.out.println("Decrypted Text: " + decryptedText);
    }
}