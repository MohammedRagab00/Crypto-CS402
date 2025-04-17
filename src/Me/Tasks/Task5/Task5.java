package Me.Tasks.Task5;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.FileWriter;

import javafx.scene.paint.Color;

public class Task5 extends Application {

    private TextArea messageTextArea;
    private TextArea resultTextArea;
    private TextField encryptionKeyField;
    private ToggleGroup cryptographyMode;
    private Label feedbackLabel;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("AES Encryption/Decryption Tool");

        // Define dark theme colors
        String darkBackground = "#282828";
        String elementBackground = "#333333";
        String textColor = "#e8e8e8";
        String accentColor = "#5294e2";

        // Message input section
        Label messageLabel = new Label("Message:");
        messageLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + textColor);

        messageTextArea = new TextArea();
        messageTextArea.setWrapText(true);
        messageTextArea.setStyle("-fx-control-inner-background: " + elementBackground +
                "; -fx-text-fill: " + textColor);

        // Key input section
        Label encryptionKeyLabel = new Label("Key:");
        encryptionKeyLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + textColor);

        encryptionKeyField = new TextField();
        encryptionKeyField.setPromptText("16 or 24 or 32 characters");
        encryptionKeyField.setStyle("-fx-background-color: " + elementBackground +
                "; -fx-text-fill: " + textColor);

        // Operation selection section
        Label modeLabel = new Label("Operation Mode:");
        modeLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + textColor);

        cryptographyMode = new ToggleGroup();

        RadioButton encryptModeButton = new RadioButton("Encrypt");
        encryptModeButton.setToggleGroup(cryptographyMode);
        encryptModeButton.setSelected(true);
        encryptModeButton.setStyle("-fx-text-fill: " + textColor);

        RadioButton decryptModeButton = new RadioButton("Decrypt");
        decryptModeButton.setToggleGroup(cryptographyMode);
        decryptModeButton.setStyle("-fx-text-fill: " + textColor);

        // Create horizontal box for radio buttons
        HBox modeSelectionBox = new HBox(20);
        modeSelectionBox.setAlignment(Pos.CENTER_LEFT);
        modeSelectionBox.getChildren().addAll(encryptModeButton, decryptModeButton);

        // Process button
        Button executeButton = new Button("Process");
        executeButton.setOnAction(e -> performCryptographyOperation());
        executeButton.setPrefWidth(150);
        executeButton.setStyle("-fx-background-color: " + accentColor +
                "; -fx-text-fill: white; -fx-font-weight: bold");

        // Result section
        Label resultLabel = new Label("Result:");
        resultLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + textColor);

        resultTextArea = new TextArea();
        resultTextArea.setEditable(false);
        resultTextArea.setWrapText(true);
        resultTextArea.setStyle("-fx-control-inner-background: " + elementBackground +
                "; -fx-text-fill: " + textColor);

        // Feedback label
        feedbackLabel = new Label();
        feedbackLabel.setWrapText(true);
        feedbackLabel.setStyle("-fx-text-fill: " + textColor);

        // Main layout
        VBox mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: " + darkBackground);

        // Add all components to layout
        mainLayout.getChildren().addAll(
                messageLabel, messageTextArea,
                encryptionKeyLabel, encryptionKeyField,
                modeLabel, modeSelectionBox,
                executeButton,
                resultLabel, resultTextArea,
                feedbackLabel
        );

        Scene scene = new Scene(mainLayout, 600, 550);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void performCryptographyOperation() {
        try {
            new FileWriter("src\\Me\\Tasks\\IO\\output.txt", false).close();

            String inputText = messageTextArea.getText();
            String key = encryptionKeyField.getText();

            if (inputText.isEmpty() || key.isEmpty()) {
                feedbackLabel.setText("Error: Input text and key must not be empty");
                feedbackLabel.setTextFill(Color.RED);
                return;
            }

            int keyLength = key.length();
            if (keyLength != 16 && keyLength != 24 && keyLength != 32) {
                feedbackLabel.setText("Key length must be 16, 24, or 32 characters (128, 192, or 256 bits)");
                feedbackLabel.setTextFill(Color.RED);
                return;
            }

            RadioButton selectedRadioButton = (RadioButton) cryptographyMode.getSelectedToggle();
            String operation = selectedRadioButton.getText();

            AES aesProcessor = new AES();
            String result;
            if (operation.equals("Encrypt")) {
                result = aesProcessor.encrypt(inputText, key);
            } else {
                inputText = inputText.replaceAll("\\s", "");
                result = aesProcessor.decrypt(inputText, key);
            }

            resultTextArea.setText(result);
            feedbackLabel.setText("Operation completed successfully");
            feedbackLabel.setTextFill(Color.GREEN);

        } catch (Exception e) {
            feedbackLabel.setText("Error: " + e.getMessage());
            feedbackLabel.setTextFill(Color.RED);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}