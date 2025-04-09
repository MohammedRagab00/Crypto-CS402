package Me.Tasks.Task2;

import Me.Shared.CipherAppTemplate;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class Task2 extends CipherAppTemplate {

    @Override
    protected void initialize() {
        configureOptionalButton("Frequency Analysis", true);
        keyField.setPromptText("Enter Playfair cipher key (no 'J's)");
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected String getAppTitle() {
        return "Playfair Cipher Tool";
    }

    @Override
    protected void setupInputSection(VBox inputBox) {
        // Add Playfair-specific instructions
        Label instructions = new Label("Note: Key should not contain 'J's. Text will be processed automatically.");

        inputBox.getChildren().addAll(instructions);
    }

    @Override
    protected void handleEncrypt() {
        String text = inputTextArea.getText().trim();
        String key = keyField.getText().trim();

        if (!validateInputs(text, key)) {
            return;
        }

        try {
            String encryptedText = PlayfairCipher.encrypt(text, key);
            outputTextArea.setText("Encrypted: " + encryptedText);
        } catch (IllegalArgumentException e) {
            showAlert("Error", e.getMessage());
        }
    }

    @Override
    protected void handleDecrypt() {
        String text = inputTextArea.getText().trim();
        String key = keyField.getText().trim();

        if (!validateInputs(text, key)) {
            return;
        }

        try {
            String decryptedText = PlayfairCipher.decrypt(text, key);
            outputTextArea.setText("Decrypted: " + decryptedText);
        } catch (IllegalArgumentException e) {
            showAlert("Error", e.getMessage());
        }
    }

    @Override
    protected void handleOptionalAction() {
        String ciphertext = inputTextArea.getText().trim().toUpperCase();

        if (ciphertext.isEmpty()) {
            showAlert("Error", "Please enter ciphertext to analyze");
            return;
        }

        try {
            String possiblePlaintext = PlayfairCipher.letterFrequencyAttack(ciphertext);
            outputTextArea.setText("Possible Plaintext (frequency analysis):\n" + possiblePlaintext);
        } catch (IllegalArgumentException e) {
            showAlert("Error", e.getMessage());
        }
    }

    private boolean validateInputs(String text, String key) {
        if (text.isEmpty() || key.isEmpty()) {
            showAlert("Error", "Please enter both text and key");
            return false;
        }

        if (key.contains("J") || key.contains("j")) {
            showAlert("Error", "Playfair key cannot contain the letter 'J'");
            return false;
        }

        return true;
    }
}