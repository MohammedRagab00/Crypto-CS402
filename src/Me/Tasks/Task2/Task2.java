package Me.Tasks.Task2;

import Me.Shared.CipherAppTemplate;

public class Task2 extends CipherAppTemplate {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected String getAppTitle() {
        return "Playfair Cipher Tool";
    }

    @Override
    protected void handleEncrypt() {
        String text = inputTextArea.getText().trim();
        String key = keyField.getText().trim();

        if (text.isEmpty() || key.isEmpty()) {
            showAlert("Error", "Please enter both text and key.");
            return;
        }

        // Call Playfair encryption logic
        String encryptedText = PlayfairCipher.encrypt(text, key);
        outputTextArea.setText(encryptedText);
    }

    @Override
    protected void handleDecrypt() {
        String text = inputTextArea.getText().trim();
        String key = keyField.getText().trim();

        if (text.isEmpty() || key.isEmpty()) {
            showAlert("Error", "Please enter both text and key.");
            return;
        }

        // Call Playfair decryption logic
        String decryptedText = PlayfairCipher.decrypt(text, key);
        outputTextArea.setText(decryptedText);
    }

    @Override
    protected void handleOptionalAction() {
        String ciphertext = inputTextArea.getText().trim().toUpperCase();

        if (ciphertext.isEmpty()) {
            showAlert("Error", "Please enter ciphertext to analyze.");
            return;
        }

        // Call the letter frequency attack method from PlayfairCipher
        String possiblePlaintext = PlayfairCipher.letterFrequencyAttack(ciphertext);

        // Display the result
        outputTextArea.setText("Possible Plaintext (based on letter frequency):\n" + possiblePlaintext);
    }
}