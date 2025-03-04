package Me.Tasks.Task3;

import Me.Shared.CipherAppTemplate;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Task3 extends CipherAppTemplate {

    private ComboBox<String> cipherChoice;
    private Button attackButton;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected String getAppTitle() {
        return "One-Time Pad & Vigenère Cipher Tool";
    }

    @Override
    protected void setupInputSection(VBox inputBox) {
        super.setupInputSection(inputBox);

        Label cipherLabel = new Label("Choose Cipher:");
        cipherChoice = new ComboBox<>();
        cipherChoice.getItems().addAll("One-Time Pad", "Vigenère Cipher");
        cipherChoice.setValue("Vigenère Cipher");

        inputBox.getChildren().addAll(cipherLabel, cipherChoice);
    }

    @Override
    protected VBox createButtonBox(Stage primaryStage) {
        VBox buttonBox = super.createButtonBox(primaryStage);

        attackButton = (Button) ((HBox) buttonBox.getChildren().get(1)).getChildren().get(2);

        attackButton.setText("No Attack Available");

        return buttonBox;
    }

    @Override
    protected void handleEncrypt() {
        String text = inputTextArea.getText().trim();
        String key = keyField.getText().trim();

        if (text.isEmpty() || key.isEmpty()) {
            showAlert("Error", "Please enter both text and key.");
            return;
        }

        String selectedCipher = cipherChoice.getValue();

        try {
            if ("One-Time Pad".equals(selectedCipher)) {
                if (key.length() < text.length()) {
                    showAlert("Error", "For One-Time Pad, the key must be at least as long as the plaintext.");
                    return;
                }
            }

            String encryptedText = OneTimePadVigenere.processText(text, key, true);
            outputTextArea.setText(encryptedText);
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

        String selectedCipher = cipherChoice.getValue();

        try {
            if ("One-Time Pad".equals(selectedCipher)) {
                if (key.length() < text.length()) {
                    showAlert("Error", "For One-Time Pad, the key must be at least as long as the ciphertext.");
                    return;
                }
            }

            String decryptedText = OneTimePadVigenere.processText(text, key, false);
            outputTextArea.setText(decryptedText);
        } catch (IllegalArgumentException e) {
            showAlert("Error", e.getMessage());
        }
    }

    @Override
    protected void handleAttack() {
        System.out.println("No attack for now, stay tuned.");
    }
}