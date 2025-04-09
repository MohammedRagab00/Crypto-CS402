package Me.Tasks.Task4;

import Me.Shared.CipherAppTemplate;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Task4 extends CipherAppTemplate {
    private TextArea keysTextArea;
    private TextField roundsField;

    @Override
    protected void initialize() {
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
        // Add DES-specific controls
        HBox paramsBox = new HBox(10);
        Label paramsLabel = new Label("Rounds to Show:");
        roundsField = createTextField("16");
        roundsField.setPrefWidth(60);
        paramsBox.getChildren().addAll(paramsLabel, roundsField);
        inputBox.getChildren().add(paramsBox);
    }

    @Override
    protected TitledPane createOutputSection() {
        VBox outputBox = new VBox(10);
        outputBox.setPadding(new Insets(10));

        outputTextArea = createTextArea("Result will appear here...", 100);
        outputTextArea.setEditable(false);

        keysTextArea = createTextArea("Key schedule will appear here...", 150);
        keysTextArea.setEditable(false);

        outputBox.getChildren().addAll(
                new Label("Result:"),
                outputTextArea,
                new Label("Key Schedule:"),
                keysTextArea
        );

        TitledPane pane = new TitledPane("Output", outputBox);
        pane.setCollapsible(false);
        return pane;
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
                String result = DESCipherV1.processText(text, key, encrypt);
                String keys = DESCipherV1.generateKeySchedule(key);

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
                keysTextArea.setText(DESCipherV1.generateKeySchedule(key));
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