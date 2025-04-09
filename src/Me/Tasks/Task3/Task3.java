package Me.Tasks.Task3;

import Me.Shared.CipherAppTemplate;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Task3 extends CipherAppTemplate {
    private ComboBox<String> cipherChoice;
    private TextField aField;
    private TextField bField;
    private TextField sField;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected String getAppTitle() {
        return "Vigenère & OTP Cipher Tool";
    }

    @Override
    protected void initialize() {
        // Configure the optional button visibility and text
        configureOptionalButton("No Attack Available", true);
    }

    @Override
    protected void setupInputSection(VBox inputBox) {
        // Call parent's implementation first
        // Add cipher-specific controls
        Label cipherLabel = new Label("Choose Cipher:");
        cipherChoice = new ComboBox<>();
        cipherChoice.getItems().addAll("One-Time Pad", "Vigenère Cipher");
        cipherChoice.setValue("Vigenère Cipher");

        HBox paramsBox = new HBox(10);
        Label paramsLabel = new Label("Key Generation Parameters:");
        aField = createTextField("a");
        aField.setPrefWidth(60);
        bField = createTextField("b");
        bField.setPrefWidth(60);
        sField = createTextField("s");
        sField.setPrefWidth(60);

        paramsBox.getChildren().addAll(paramsLabel, aField, bField, sField);
        paramsBox.setVisible(false);

        cipherChoice.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean isOTP = "One-Time Pad".equals(newVal);
            paramsBox.setVisible(isOTP);
            configureOptionalButton(isOTP ? "Create Random Key" : "No Attack Available", true);

            if (!isOTP) {
                aField.clear();
                bField.clear();
                sField.clear();
            }
        });

        inputBox.getChildren().addAll(cipherLabel, cipherChoice, paramsBox);
    }

    @Override
    protected void handleEncrypt() {
        String text = inputTextArea.getText().trim();
        String key = keyField.getText().trim();

        try {
            String selectedCipher = cipherChoice.getValue();
            int textLength = OneTimePadVigenere.length(text);

            if ("One-Time Pad".equals(selectedCipher)) {
                if (key.length() < textLength || key.isEmpty()) {
                    int a = aField.getText().isEmpty() ? 0 : Integer.parseInt(aField.getText());
                    int b = bField.getText().isEmpty() ? 0 : Integer.parseInt(bField.getText());
                    int s = sField.getText().isEmpty() ? 0 : Integer.parseInt(sField.getText());

                    key = OneTimePadVigenere.generateRandomKey(textLength, a, b, s % 26);
                    keyField.setText(key);
                }
            } else {
                if (text.isEmpty() || key.isEmpty()) {
                    showAlert("Error", "Please enter both text and key.");
                    return;
                }
            }

            String encryptedText = OneTimePadVigenere.processText(text, key, true);
            outputTextArea.setText(encryptedText);
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid numeric parameters: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            showAlert("Error", e.getMessage());
        }
    }

    @Override
    protected void handleDecrypt() {
        String text = inputTextArea.getText().trim();
        String key = keyField.getText().trim();

        if (text.isEmpty() || key.isEmpty()) {
            showAlert("Error", "Please enter both text and key.");
            return;
        }

        try {
            String decryptedText = OneTimePadVigenere.processText(text, key, false);
            outputTextArea.setText(decryptedText);
        } catch (IllegalArgumentException e) {
            showAlert("Error", e.getMessage());
        }
    }

    @Override
    protected void handleOptionalAction() {
        String selectedCipher = cipherChoice.getValue();

        if ("One-Time Pad".equals(selectedCipher)) {
            String text = inputTextArea.getText().trim();

            if (text.isEmpty()) {
                showAlert("Error", "Please enter some plaintext to generate a key.");
                return;
            }

            try {
                if (aField.getText().isEmpty() || bField.getText().isEmpty() || sField.getText().isEmpty()) {
                    showAlert("Error", "Please fill all key generation parameters (a, b, s).");
                    return;
                }

                int a = Integer.parseInt(aField.getText());
                int b = Integer.parseInt(bField.getText());
                int s = Integer.parseInt(sField.getText());

                String randomKey = OneTimePadVigenere.generateRandomKey(OneTimePadVigenere.length(text), a, b, s % 26);
                keyField.setText(randomKey);
            } catch (NumberFormatException e) {
                showAlert("Error", "Invalid parameters: All values must be integers.");
            }
        } else {
            showAlert("Information", "No attack available for Vigenère Cipher in this version.");
        }
    }
}