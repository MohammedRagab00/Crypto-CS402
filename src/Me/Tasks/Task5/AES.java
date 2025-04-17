package Me.Tasks.Task5;

import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class AES {
    private final int[][] STATE = new int[4][4];

    private int[][] ROUND_KEYS;

    private int numberOfRounds;


    public String encrypt(String plaintext, String key) throws Exception {
        setupForKeySize(key);
        logToFile("Plaintext       : " + plaintext);
        logToFile("Key             : " + key);
        logToFile("Key Size        : " + (key.length() * 8) + " bits, " + numberOfRounds + " rounds.");

        byte[] paddedPlaintext = padPlaintext(plaintext.getBytes(StandardCharsets.UTF_8));
        logToFile("Padded Plaintext: " + bytesToHex(paddedPlaintext));

        keyExpansion(key.getBytes(StandardCharsets.UTF_8));

        StringBuilder result = new StringBuilder();
        int blocks = paddedPlaintext.length / 16;

        for (int blockNum = 0; blockNum < blocks; blockNum++) {

            int start = blockNum * 16;
            byte[] block = Arrays.copyOfRange(paddedPlaintext, start, start + 16);

            logToFile("\n====== PROCESSING BLOCK " + (blockNum + 1) + " OF " + blocks + " ======");
            logToFile("Block Content: " + bytesToHex(block));

            byte[] encryptedBlock = encryptBlock(block);
            result.append(bytesToHex(encryptedBlock));

            logToFile("\nEncrypted Block: " + bytesToHex(encryptedBlock));
        }

        logToFile("\n=== FINAL ENCRYPTION RESULT ===");
        logToFile("Ciphertext: " + result);

        return result.toString();
    }

    public String decrypt(String ciphertext, String key) throws Exception {
        setupForKeySize(key);
        logToFile("Input   : " + ciphertext);
        logToFile("Key     : " + key);
        logToFile("Key Size: " + (key.length() * 8) + " bits, " + numberOfRounds + " rounds.");

        keyExpansion(key.getBytes(StandardCharsets.UTF_8));

        byte[] cipherBytes = hexStringToByteArray(ciphertext);
        int blocks = cipherBytes.length / 16;

        byte[] allDecryptedBytes = new byte[blocks * 16];

        for (int blockNum = 0; blockNum < blocks; blockNum++) {
            int start = blockNum * 16;
            byte[] block = Arrays.copyOfRange(cipherBytes, start, start + 16);

            logToFile("\n====== PROCESSING BLOCK " + (blockNum + 1) + " OF " + blocks + " ======");
            logToFile("Block Content: " + bytesToHex(block));

            byte[] decryptedBlock = decryptBlock(block);
            System.arraycopy(decryptedBlock, 0, allDecryptedBytes, blockNum * 16, 16);

            logToFile("Decrypted Block: " + bytesToHex(decryptedBlock));
        }

        int paddingLength = allDecryptedBytes[allDecryptedBytes.length - 1];
        if (paddingLength > 0 && paddingLength <= 16) {
            allDecryptedBytes = Arrays.copyOf(allDecryptedBytes, allDecryptedBytes.length - paddingLength);
        }

        String decryptedText = new String(allDecryptedBytes, StandardCharsets.UTF_8);
        logToFile("\n=== FINAL DECRYPTION RESULT ===");
        logToFile("\nFinal Decrypted Result: " + decryptedText);
        return decryptedText;
    }

    private void setupForKeySize(String key) {
        int keySize = key.length();

        switch (keySize) {
            case 16:
                numberOfRounds = 10;
                ROUND_KEYS = new int[numberOfRounds + 1][16];
                break;
            case 24:
                numberOfRounds = 12;
                ROUND_KEYS = new int[numberOfRounds + 1][16];
                break;
            case 32:
                numberOfRounds = 14;
                ROUND_KEYS = new int[numberOfRounds + 1][16];
                break;
            default:
                throw new IllegalArgumentException("Key size must be 16 (128 bits), 24 (192 bits), or 32 (256 bits) characters");
        }
    }

    private byte[] encryptBlock(byte[] block) {
        initializeState(block);
        logState("Initial State Matrix:");

        addRoundKey(0);
        logState("After Initial AddRoundKey (Round 0):");

        for (int round = 1; round < numberOfRounds; round++) {
            logToFile("\n------ ROUND " + round + " ------");

            subBytes();
            logState("After SubBytes (Round " + round + "):");

            shiftRows();
            logState("After ShiftRows (Round " + round + "):");

            mixColumns();
            logState("After MixColumns (Round " + round + "):");

            addRoundKey(round);
            logState("After AddRoundKey (Round " + round + "):");
        }

        logToFile("\n------ FINAL ROUND (" + numberOfRounds + ") ------");

        subBytes();
        logState("After SubBytes (Final Round):");

        shiftRows();
        logState("After ShiftRows (Final Round):");

        addRoundKey(numberOfRounds);
        logState("After Final AddRoundKey:");

        return stateToByteArray();
    }


    private byte[] decryptBlock(byte[] block) {
        initializeState(block);
        logState("Initial State Matrix:");

        addRoundKey(numberOfRounds);
        logState("After Initial AddRoundKey:");

        for (int round = numberOfRounds - 1; round >= 1; round--) {
            logToFile("\n------ ROUND " + round + " ------");

            inverseShiftRows();
            logState("After InverseShiftRows (Round " + round + "):");

            inverseSubBytes();
            logState("After InverseSubBytes (Round " + round + "):");

            addRoundKey(round);
            logState("After AddRoundKey (Round " + round + "):");

            inverseMixColumns();
            logState("After InverseMixColumns (Round " + round + "):");
        }

        logToFile("\n------ FINAL ROUND ------");

        inverseShiftRows();
        logState("After InverseShiftRows (Final Round):");

        inverseSubBytes();
        logState("After InverseSubBytes (Final Round):");

        addRoundKey(0);
        logState("After Final AddRoundKey:");

        return stateToByteArray();
    }

    private void subBytes() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int val = STATE[i][j];

                int row = (val >>> 4) & 0x0F;
                int col = val & 0x0F;
                STATE[i][j] = CONSTANTS.S_BOX[row][col];
            }
        }
    }

    private void inverseSubBytes() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int val = STATE[i][j];

                int row = (val >>> 4) & 0x0F;
                int col = val & 0x0F;
                STATE[i][j] = CONSTANTS.INV_S_BOX[row][col];
            }
        }
    }

    private void shiftRows() {
        int temp = STATE[1][0];
        STATE[1][0] = STATE[1][1];
        STATE[1][1] = STATE[1][2];
        STATE[1][2] = STATE[1][3];
        STATE[1][3] = temp;

        swapElements(0, 2, 2);
        swapElements(1, 2, 3);

        temp = STATE[3][3];
        STATE[3][3] = STATE[3][2];
        STATE[3][2] = STATE[3][1];
        STATE[3][1] = STATE[3][0];
        STATE[3][0] = temp;
    }

    private void swapElements(int col1, int row, int col2) {
        int temp = STATE[2][col1];
        STATE[2][col1] = STATE[row][col2];
        STATE[row][col2] = temp;
    }

    private void inverseShiftRows() {
        int temp = STATE[1][3];
        STATE[1][3] = STATE[1][2];
        STATE[1][2] = STATE[1][1];
        STATE[1][1] = STATE[1][0];
        STATE[1][0] = temp;

        swapElements(0, 2, 2);
        swapElements(1, 2, 3);

        temp = STATE[3][0];
        STATE[3][0] = STATE[3][1];
        STATE[3][1] = STATE[3][2];
        STATE[3][2] = STATE[3][3];
        STATE[3][3] = temp;
    }

    private void mixColumns() {
        int[][] temp = new int[4][4];

        for (int c = 0; c < 4; c++) {
            temp[0][c] = multiply(CONSTANTS.MIX_COLUMN[0][0], STATE[0][c]) ^
                    multiply(CONSTANTS.MIX_COLUMN[0][1], STATE[1][c]) ^
                    multiply(CONSTANTS.MIX_COLUMN[0][2], STATE[2][c]) ^
                    multiply(CONSTANTS.MIX_COLUMN[0][3], STATE[3][c]);

            temp[1][c] = multiply(CONSTANTS.MIX_COLUMN[1][0], STATE[0][c]) ^
                    multiply(CONSTANTS.MIX_COLUMN[1][1], STATE[1][c]) ^
                    multiply(CONSTANTS.MIX_COLUMN[1][2], STATE[2][c]) ^
                    multiply(CONSTANTS.MIX_COLUMN[1][3], STATE[3][c]);

            temp[2][c] = multiply(CONSTANTS.MIX_COLUMN[2][0], STATE[0][c]) ^
                    multiply(CONSTANTS.MIX_COLUMN[2][1], STATE[1][c]) ^
                    multiply(CONSTANTS.MIX_COLUMN[2][2], STATE[2][c]) ^
                    multiply(CONSTANTS.MIX_COLUMN[2][3], STATE[3][c]);

            temp[3][c] = multiply(CONSTANTS.MIX_COLUMN[3][0], STATE[0][c]) ^
                    multiply(CONSTANTS.MIX_COLUMN[3][1], STATE[1][c]) ^
                    multiply(CONSTANTS.MIX_COLUMN[3][2], STATE[2][c]) ^
                    multiply(CONSTANTS.MIX_COLUMN[3][3], STATE[3][c]);
        }

        for (int i = 0; i < 4; i++) {
            System.arraycopy(temp[i], 0, STATE[i], 0, 4);
        }
    }

    private void inverseMixColumns() {
        int[][] temp = new int[4][4];

        for (int c = 0; c < 4; c++) {
            temp[0][c] = multiply(CONSTANTS.INV_MIX_COLUMN[0][0], STATE[0][c]) ^
                    multiply(CONSTANTS.INV_MIX_COLUMN[0][1], STATE[1][c]) ^
                    multiply(CONSTANTS.INV_MIX_COLUMN[0][2], STATE[2][c]) ^
                    multiply(CONSTANTS.INV_MIX_COLUMN[0][3], STATE[3][c]);

            temp[1][c] = multiply(CONSTANTS.INV_MIX_COLUMN[1][0], STATE[0][c]) ^
                    multiply(CONSTANTS.INV_MIX_COLUMN[1][1], STATE[1][c]) ^
                    multiply(CONSTANTS.INV_MIX_COLUMN[1][2], STATE[2][c]) ^
                    multiply(CONSTANTS.INV_MIX_COLUMN[1][3], STATE[3][c]);

            temp[2][c] = multiply(CONSTANTS.INV_MIX_COLUMN[2][0], STATE[0][c]) ^
                    multiply(CONSTANTS.INV_MIX_COLUMN[2][1], STATE[1][c]) ^
                    multiply(CONSTANTS.INV_MIX_COLUMN[2][2], STATE[2][c]) ^
                    multiply(CONSTANTS.INV_MIX_COLUMN[2][3], STATE[3][c]);

            temp[3][c] = multiply(CONSTANTS.INV_MIX_COLUMN[3][0], STATE[0][c]) ^
                    multiply(CONSTANTS.INV_MIX_COLUMN[3][1], STATE[1][c]) ^
                    multiply(CONSTANTS.INV_MIX_COLUMN[3][2], STATE[2][c]) ^
                    multiply(CONSTANTS.INV_MIX_COLUMN[3][3], STATE[3][c]);
        }

        for (int i = 0; i < 4; i++) {
            System.arraycopy(temp[i], 0, STATE[i], 0, 4);
        }
    }

    private void addRoundKey(int round) {
        for (int c = 0; c < 4; c++) {
            for (int r = 0; r < 4; r++) {
                STATE[r][c] ^= ROUND_KEYS[round][c * 4 + r];
            }
        }
    }

    private void keyExpansion(byte[] key) {
        int keyWords = key.length / 4;

        final int WORD_PER_ROUND_KEY = 4;

        int totalWords = (numberOfRounds + 1) * WORD_PER_ROUND_KEY;
        int[] w = new int[totalWords];

        for (int i = 0; i < keyWords; i++) {
            int offset = i * 4;
            if (offset + 3 < key.length) {
                w[i] = ((key[offset] & 0xFF) << 24) |
                        ((key[offset + 1] & 0xFF) << 16) |
                        ((key[offset + 2] & 0xFF) << 8) |
                        (key[offset + 3] & 0xFF);
            }
        }

        for (int i = keyWords; i < totalWords; i++) {
            int temp = w[i - 1];

            if (i % keyWords == 0) {
                temp = rotWord(temp);
                temp = subWord(temp);
                temp ^= (CONSTANTS.R_CON[i / keyWords - 1] << 24);
            } else if (keyWords > 6 && i % keyWords == 4) {
                temp = subWord(temp);
            }

            w[i] = w[i - keyWords] ^ temp;
        }

        logToFile("\n========== GENERATED ROUND KEYS ==========");
        for (int round = 0; round <= numberOfRounds; round++) {
            for (int word = 0; word < 4; word++) {
                int wordIndex = round * 4 + word;
                if (wordIndex < totalWords) {
                    int wordValue = w[wordIndex];
                    ROUND_KEYS[round][word * 4] = (wordValue >> 24) & 0xFF;
                    ROUND_KEYS[round][word * 4 + 1] = (wordValue >> 16) & 0xFF;
                    ROUND_KEYS[round][word * 4 + 2] = (wordValue >> 8) & 0xFF;
                    ROUND_KEYS[round][word * 4 + 3] = wordValue & 0xFF;
                }
            }
            logRoundKey(round);
        }
        logToFile("\n======================================");
    }

    private int subWord(int word) {
        int result = 0;
        for (int i = 0; i < 4; i++) {
            int byteVal = (word >>> (24 - i * 8)) & 0xFF;
            int row = (byteVal >>> 4) & 0x0F;
            int col = byteVal & 0x0F;
            int subByte = CONSTANTS.S_BOX[row][col];
            result |= (subByte << (24 - i * 8));
        }
        return result;
    }

    private int rotWord(int word) {
        return ((word << 8) | ((word >>> 24) & 0xFF));
    }


    private int multiply(int a, int b) {
        int result = 0;
        int highBit;

        for (int i = 0; i < 8; i++) {
            if ((b & 1) != 0) {
                result ^= a;
            }

            highBit = a & 0x80;
            a <<= 1;
            if (highBit != 0) {
                a ^= 0x1B;
            }
            b >>= 1;
        }

        return result;
    }

    private void initializeState(byte[] block) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                STATE[j][i] = block[i * 4 + j] & 0xFF;
            }
        }
    }

    private byte[] stateToByteArray() {
        byte[] out = new byte[16];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                out[i * 4 + j] = (byte) STATE[j][i]; // Column-major order
            }
        }
        return out;
    }

    private byte[] padPlaintext(byte[] plaintext) {
        int paddingLength = 16 - (plaintext.length % 16);
        byte[] padded = new byte[plaintext.length + paddingLength];
        System.arraycopy(plaintext, 0, padded, 0, plaintext.length);

        for (int i = plaintext.length; i < padded.length; i++) {
            padded[i] = (byte) paddingLength;
        }

        return padded;
    }

    private void logState(String message) {
        logToFile(message);
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            sb.append(String.format("[%02X, %02X, %02X, %02X]",
                    STATE[i][0], STATE[i][1], STATE[i][2], STATE[i][3]));
            if (i < 3) sb.append("\n");
        }
        logToFile(sb.toString());
    }


    private void logRoundKey(int round) {
        StringBuilder sb = new StringBuilder();
        sb.append("\nRound Key ").append(round).append(":");

        for (int row = 0; row < 4; row++) {
            sb.append("\n[");
            for (int col = 0; col < 4; col++) {
                sb.append(String.format("%02X", ROUND_KEYS[round][col * 4 + row]));
                if (col < 3) sb.append(", ");
            }
            sb.append("]");
        }

        logToFile(sb.toString());
    }

    private void logToFile(String message) {
        try (FileWriter writer = new FileWriter("src\\Me\\Tasks\\IO\\output.txt", true)) {
            writer.write(message + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }


    private byte[] hexStringToByteArray(String s) {
        s = s.replaceAll("\\s", "");
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}