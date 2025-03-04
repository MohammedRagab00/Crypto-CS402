package Me.Tasks.Task2;

public class PlayfairCipher {

    // Encrypts the input text using the Playfair cipher
    public static String encrypt(String text, String key) {
        String processedText = preprocessText(text);
        char[][] keySquare = generateKeySquare(key);
        return transformText(processedText, keySquare, true, text);
    }

    // Decrypts the input text using the Playfair cipher
    public static String decrypt(String text, String key) {
        String processedText = preprocessText(text);
        char[][] keySquare = generateKeySquare(key);
        return transformText(processedText, keySquare, false, text);
    }

    // Preprocesses the text: converts to uppercase, replaces 'J' with 'I', but keeps non-letters
    private static String preprocessText(String text) {
        StringBuilder processed = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                if (c == 'J') c = 'I'; // Replace 'J' with 'I'
                processed.append(Character.toUpperCase(c));
            } else {
                processed.append(c); // Keep non-letter characters unchanged
            }
        }
        return processed.toString();
    }

    // Generates the 5x5 key square
    private static char[][] generateKeySquare(String key) {
        key = key.replaceAll("[^a-zA-Z]", "").toUpperCase();
        key = key.replace("J", "I"); // Replace 'J' with 'I'
        boolean[] used = new boolean[26];
        char[][] keySquare = new char[5][5];
        int row = 0, col = 0;

        for (char c : key.toCharArray()) {
            if (!used[c - 'A']) {
                keySquare[row][col] = c;
                used[c - 'A'] = true;
                col++;
                if (col == 5) {
                    col = 0;
                    row++;
                }
            }
        }

        for (char c = 'A'; c <= 'Z'; c++) {
            if (c != 'J' && !used[c - 'A']) {
                keySquare[row][col] = c;
                col++;
                if (col == 5) {
                    col = 0;
                    row++;
                }
            }
        }
        return keySquare;
    }

    // Transforms the text (encrypts or decrypts) while keeping non-letter characters unchanged
    private static String transformText(String text, char[][] keySquare, boolean encrypt, String originalText) {
        StringBuilder result = new StringBuilder();
        StringBuilder lettersOnly = new StringBuilder();

        // Extract only letters for Playfair processing
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                lettersOnly.append(c);
            }
        }

        String letterText = lettersOnly.toString();
        StringBuilder transformed = new StringBuilder();

        // Process letter pairs
        for (int i = 0; i < letterText.length(); i += 2) {
            char first = letterText.charAt(i);
            char second = (i + 1 < letterText.length()) ? letterText.charAt(i + 1) : 'X'; // Add 'X' if odd length

            if (first == second) {
                second = 'X'; // Handle double letters
            }

            int[] pos1 = findPosition(keySquare, first);
            int[] pos2 = findPosition(keySquare, second);

            if (pos1[0] == pos2[0]) { // Same row
                transformed.append(keySquare[pos1[0]][(pos1[1] + (encrypt ? 1 : 4)) % 5]);
                transformed.append(keySquare[pos2[0]][(pos2[1] + (encrypt ? 1 : 4)) % 5]);
            } else if (pos1[1] == pos2[1]) { // Same column
                transformed.append(keySquare[(pos1[0] + (encrypt ? 1 : 4)) % 5][pos1[1]]);
                transformed.append(keySquare[(pos2[0] + (encrypt ? 1 : 4)) % 5][pos2[1]]);
            } else { // Rectangle swap
                transformed.append(keySquare[pos1[0]][pos2[1]]);
                transformed.append(keySquare[pos2[0]][pos1[1]]);
            }
        }

        // Reinsert non-letters into their original positions
        int letterIndex = 0;
        for (char c : originalText.toCharArray()) {
            if (Character.isLetter(c)) {
                result.append(transformed.charAt(letterIndex++));
            } else {
                result.append(c);
            }
        }
        for (int i = letterIndex; i < transformed.length(); i++) {
            result.append(transformed.charAt(i));
        }
        return result.toString();
    }

    // Finds the position of a character in the key square
    private static int[] findPosition(char[][] keySquare, char c) {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (keySquare[i][j] == c) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{-1, -1};
    }

    public static String letterFrequencyAttack(String ciphertext) {
        // Known frequency of letters in English (in order from most to least frequent)
        String englishFrequency = "ETAOINSHRDLCUMWFGYPBVKJXQZ";

        // Calculate the frequency of letters in the ciphertext
        int[] ciphertextFrequency = new int[26];
        for (char c : ciphertext.toCharArray()) {
            if (Character.isLetter(c)) {
                ciphertextFrequency[c - 'A']++;
            }
        }

        // Sort the ciphertext letters by frequency (most to least frequent)
        StringBuilder sortedCiphertextLetters = new StringBuilder();
        for (int i = 0; i < 26; i++) {
            int maxCount = -1;
            char maxChar = 'A';
            for (char c = 'A'; c <= 'Z'; c++) {
                if (ciphertextFrequency[c - 'A'] > maxCount) {
                    maxCount = ciphertextFrequency[c - 'A'];
                    maxChar = c;
                }
            }
            if (maxCount > 0) {
                sortedCiphertextLetters.append(maxChar);
                ciphertextFrequency[maxChar - 'A'] = -1; // Mark as processed
            }
        }

        // Map the most frequent ciphertext letters to the most frequent English letters
        StringBuilder possiblePlaintext = new StringBuilder();
        for (char c : ciphertext.toCharArray()) {
            if (Character.isLetter(c)) {
                int index = sortedCiphertextLetters.indexOf(String.valueOf(c));
                if (index != -1 && index < englishFrequency.length()) {
                    possiblePlaintext.append(englishFrequency.charAt(index));
                } else {
                    possiblePlaintext.append(c); // Keep the original character if no mapping is found
                }
            } else {
                possiblePlaintext.append(c); // Keep non-letter characters as-is
            }
        }

        return possiblePlaintext.toString();
    }

}