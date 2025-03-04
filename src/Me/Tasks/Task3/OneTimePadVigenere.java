package Me.Tasks.Task3;

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
}