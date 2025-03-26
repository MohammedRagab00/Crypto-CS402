package Me.Tasks.Task3;

import Me.Shared.CipherAppTemplate;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Task3 extends CipherAppTemplate {

    private ComboBox<String> cipherChoice;
    private Button attackButton;
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
    protected void setupInputSection(VBox inputBox) {
        super.setupInputSection(inputBox);

        Label cipherLabel = new Label("Choose Cipher:");
        cipherChoice = new ComboBox<>();
        cipherChoice.getItems().addAll("One-Time Pad", "Vigenère Cipher");
        cipherChoice.setValue("Vigenère Cipher");

        HBox paramsBox = new HBox(10);
        Label paramsLabel = new Label("Key Generation Parameters:");
        aField = new TextField();
        aField.setPromptText("a");
        aField.setPrefWidth(60);
        bField = new TextField();
        bField.setPromptText("b");
        bField.setPrefWidth(60);
        sField = new TextField();
        sField.setPromptText("s");
        sField.setPrefWidth(60);
        paramsBox.getChildren().addAll(paramsLabel, aField, bField, sField);
        paramsBox.setVisible(false);

        cipherChoice.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean isOTP = "One-Time Pad".equals(newVal);
            paramsBox.setVisible(isOTP);
            if (!isOTP) {
                // Clear fields when switching away from OTP
                aField.clear();
                bField.clear();
                sField.clear();
            }
        });

        inputBox.getChildren().addAll(cipherLabel, cipherChoice, paramsBox);
    }

    @Override
    protected VBox createButtonBox(Stage primaryStage) {
        VBox buttonBox = super.createButtonBox(primaryStage);

        attackButton = (Button) ((HBox) buttonBox.getChildren().get(1)).getChildren().get(2);

        attackButton.setText("No Attack Available");

        cipherChoice.valueProperty().addListener((observable, oldValue, newValue) -> {
            if ("One-Time Pad".equals(newValue)) {
                attackButton.setText("Create Random Key");
            } else {
                attackButton.setText("No Attack Available");
            }
        });

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
        String selectedCipher = cipherChoice.getValue();

        if ("One-Time Pad".equals(selectedCipher)) {
            String text = inputTextArea.getText().trim();

            if (text.isEmpty()) {
//                showAlert("Error", "Please enter some plaintext to generate a key.");
                return;
            }
            try {
/*
            String[] arr = keyField.getText().split(",");
            if (arr.length > 2)
                try {
                    int a = Integer.parseInt(arr[0]), b = Integer.parseInt(arr[1]), s = Integer.parseInt(arr[2]);
                    System.out.println(a + ", " + b + ", " + s);
                    String randomKey = OneTimePadVigenere.generateRandomKey(text, a, b, s);
                    keyField.setText(randomKey);
*/
                if (aField.getText().isEmpty() || bField.getText().isEmpty() || sField.getText().isEmpty()) {
                    return;
                }
                int a = aField.getText().isEmpty() ? 0 : Integer.parseInt(aField.getText()),
                        b = bField.getText().isEmpty() ? 0 : Integer.parseInt(bField.getText()),
                        s = sField.getText().isEmpty() ? 0 : Integer.parseInt(sField.getText());

                String randomKey = OneTimePadVigenere.generateRandomKey(text, a, b, s % 26);
                keyField.setText(randomKey);
            } catch (NumberFormatException e) {
                showAlert("Error", "Invalid parameters: " + e.getMessage());
            }
        } else {
            System.out.println("No attack for now, stay tuned.");
        }
    }
}