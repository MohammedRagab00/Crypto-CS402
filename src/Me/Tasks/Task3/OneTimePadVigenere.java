package Me.Tasks.Task3;

import java.security.SecureRandom;

public class OneTimePadVigenere {

    public static String processText(String text, String key, boolean encrypt) {
        StringBuilder result = new StringBuilder();
        int keyIndex = 0;

        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isLowerCase(c) ? 'a' : 'A';
                char keyChar = key.charAt(keyIndex % key.length());
                char keyBase = Character.isLowerCase(keyChar) ? 'a' : 'A';

                int shift = (keyChar - keyBase) % 26;
//                shift = key.charAt(keyIndex % key.length()) % 26;
/*
                char processedChar;
                if (encrypt) {
                    processedChar = (char) ((c - base + shift + 26) % 26 + base);
                } else {
                    processedChar = (char) ((c - base - shift + 26) % 26 + base);
                }
*/
                shift = encrypt ? shift : -shift;
                char processedChar = (char) ((c - base + shift + 26) % 26 + base);

                result.append(processedChar);
                keyIndex++;
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    public static String generateRandomKey(String text, int a, int b, int x) {
        StringBuilder key = new StringBuilder();
/*
        final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
                LOWERCASE = "abcdefghijklmnopqrstuvwxyz",
                NUMBERS = "0123456789",
                SPECIAL_CHARACTERS = "!@#$%^&*()_+-=[]{}|;:,.<>?";

        final String ALL_CHARACTERS = UPPERCASE + LOWERCASE + NUMBERS + SPECIAL_CHARACTERS;

        SecureRandom random = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(allCharacters.length());
            key.append(allCharacters.charAt(index));
        }
*/
        int length = 0;
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                ++length;
            }
        }
        for (int i = 0; i < length; i++) {
            key.append((char) (x + 'a'));
            x = (a * x + b) % 26;
        }

        return key.toString();
    }
}