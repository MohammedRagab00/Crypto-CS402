package Me.Tasks.Task4;

import Me.Shared.CipherAppTemplate;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Task4 extends CipherAppTemplate {
    private TextArea keysTextArea;

    public Task4() {
        configureOptionalButton("Show Key Schedule", true);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected String getAppTitle() {
        return "DES Encryption Tool";
    }

    @Override
    protected void setupInputSection(VBox inputBox) {
        super.setupInputSection(inputBox);
        keyField.setPromptText("Enter 8-character key (56-bit effective)");

        HBox paramsBox = new HBox(10);
        Label paramsLabel = new Label("Rounds to Show:");
        TextField roundsField = new TextField("16");
        roundsField.setPrefWidth(60);
        paramsBox.getChildren().addAll(paramsLabel, roundsField);
        inputBox.getChildren().add(paramsBox);
    }

    @Override
    protected TitledPane createOutputSection() {
        VBox outputBox = new VBox(10);
        outputBox.setPadding(new Insets(10));

        outputTextArea = createOutputArea("Result:", 100);
        keysTextArea = createOutputArea("Key Schedule:", 150);

        outputBox.getChildren().addAll(
                new Label("Result:"), outputTextArea,
                new Label("Key Schedule:"), keysTextArea
        );

        return new TitledPane("Output", outputBox);
    }

    private TextArea createOutputArea(String prompt, int height) {
        TextArea area = new TextArea();
        area.setPromptText(prompt);
        area.setPrefHeight(height);
        area.setEditable(false);
        return area;
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
        String text = inputTextArea.getText().trim();
        String key = keyField.getText().trim();

        try {
            if (validateInputs(text, key)) {
                String result = DESCipher.processText(text, key, encrypt);
                String keys = DESCipher.generateKeySchedule(key);

                outputTextArea.setText(encrypt ?
                        "Encrypted: " + result :
                        "Decrypted: " + result);
                keysTextArea.setText(keys);
            }
        } catch (Exception e) {
            showAlert("Error", (encrypt ? "Encryption" : "Decryption") +
                    " failed: " + e.getMessage());
        }
    }

    @Override
    protected void handleOptionalAction() {
        String key = keyField.getText().trim();
        try {
            if (validateKey(key)) {
                keysTextArea.setText(DESCipher.generateKeySchedule(key));
            }
        } catch (IllegalArgumentException e) {
            showAlert("Error", e.getMessage());
        }
    }

    private boolean validateInputs(String text, String key) {
        if (text.isEmpty() || key.isEmpty()) {
            showAlert("Error", "Please enter both text and key");
            return false;
        }
        return validateKey(key);
    }

    private boolean validateKey(String key) {
        if (key.length() != 8) {
            showAlert("Error", "DES key must be exactly 8 characters (56-bit effective)");
            return false;
        }
        return true;
    }
}