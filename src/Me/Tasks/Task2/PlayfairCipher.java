package Me.Tasks.Task2;

public class PlayfairCipher {

    // Encrypts the input text using the Playfair cipher
    public static String encrypt(String text, String key) {
        String processedText = preprocessText(text);
        char[][] keySquare = generateKeySquare(key);
        return transformText(processedText, keySquare, true);
    }

    // Decrypts the input text using the Playfair cipher
    public static String decrypt(String text, String key) {
        String processedText = preprocessText(text);
        char[][] keySquare = generateKeySquare(key);
        return transformText(processedText, keySquare, false);
    }

    // Preprocesses the text: removes non-letters, converts to uppercase, and handles 'J'
    private static String preprocessText(String text) {
        text = text.replaceAll("[^a-zA-Z]", "").toUpperCase();
        text = text.replace("J", "I"); // Replace 'J' with 'I'
        return text;
    }

    // Generates the 5x5 key square
    private static char[][] generateKeySquare(String key) {
        key = key.replaceAll("[^a-zA-Z]", "").toUpperCase();
        key = key.replace("J", "I"); // Replace 'J' with 'I'
        boolean[] used = new boolean[26];
        char[][] keySquare = new char[5][5];
        int row = 0, col = 0;

        // Fill the key square with the key
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

        // Fill the remaining spaces with the rest of the alphabet
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

    // Transforms the text (encrypts or decrypts) using the key square
    private static String transformText(String text, char[][] keySquare, boolean encrypt) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i += 2) {
            char first = text.charAt(i);
            char second = (i + 1 < text.length()) ? text.charAt(i + 1) : 'X'; // Add 'X' for odd-length pairs
            if (first == second) {
                second = 'X'; // Handle double letters
            }

            int[] pos1 = findPosition(keySquare, first);
            int[] pos2 = findPosition(keySquare, second);

            if (pos1[0] == pos2[0]) { // Same row
                result.append(keySquare[pos1[0]][(pos1[1] + (encrypt ? 1 : 4)) % 5]);
                result.append(keySquare[pos2[0]][(pos2[1] + (encrypt ? 1 : 4)) % 5]);
            } else if (pos1[1] == pos2[1]) { // Same column
                result.append(keySquare[(pos1[0] + (encrypt ? 1 : 4)) % 5][pos1[1]]);
                result.append(keySquare[(pos2[0] + (encrypt ? 1 : 4)) % 5][pos2[1]]);
            } else { // Rectangle
                result.append(keySquare[pos1[0]][pos2[1]]);
                result.append(keySquare[pos2[0]][pos1[1]]);
            }
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