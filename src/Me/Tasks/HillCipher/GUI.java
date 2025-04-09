package Me.Tasks.HillCipher;

import Me.Shared.CipherAppTemplate;
import javafx.scene.layout.*;

public class GUI extends CipherAppTemplate {

    @Override
    protected void initialize() {
        configureOptionalButton("Validate Key", true);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected String getAppTitle() {
        return "Hill Cipher (n×n)";
    }

    @Override
    protected void setupInputSection(VBox inputBox) {
        keyField.setPromptText("Enter space/comma separated numbers for key matrix");
    }

    @Override
    protected void handleEncrypt() {
        processCipherOperation(true);
    }

    @Override
    protected void handleDecrypt() {
        processCipherOperation(false);
    }

    private void processCipherOperation(boolean encrypt) {
        try {
            String text = inputTextArea.getText().trim();
            String key = keyField.getText().trim();

            if (!validateInputs(text, key)) return;

            int[][] keyMatrix = Algo.parseKeyMatrix(key);
            String result = new Algo(keyMatrix).processText(text, encrypt);

            outputTextArea.setText(result);
        } catch (IllegalArgumentException e) {
            showAlert("Error", "Invalid operation: " + e.getMessage());
        }
    }

    @Override
    protected void handleOptionalAction() {
        try {
            String key = keyField.getText().trim();
            if (key.isEmpty()) {
                showAlert("Error", "Please enter a key to validate");
                return;
            }

            int[][] keyMatrix = Algo.parseKeyMatrix(key);
            new Algo(keyMatrix); // Validates the key
            showAlert("Success", "Key matrix is valid and invertible");
        } catch (IllegalArgumentException e) {
            showAlert("Error", "Invalid key: " + e.getMessage());
        }
    }

    private boolean validateInputs(String text, String key) {
        if (text.isEmpty() || key.isEmpty()) {
            showAlert("Error", "Please enter both text and key");
            return false;
        }
        return true;
    }
}