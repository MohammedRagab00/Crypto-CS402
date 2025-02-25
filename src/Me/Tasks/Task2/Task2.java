package Me.Tasks.Task2;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.*;
import java.util.Objects;

public class Task2 extends Application {

    private TextArea inputTextArea;
    private TextArea outputTextArea;
    private TextField keyField;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));

        // Main content area
        VBox mainContent = new VBox(15);
        mainContent.setPadding(new Insets(10));

        // Input Section
        TitledPane inputSection = createInputSection();
        inputSection.getStyleClass().add("input-section");

        // Output Section
        TitledPane outputSection = createOutputSection();
        outputSection.getStyleClass().add("output-section");

        // Action Buttons
        VBox buttonBox = createButtonBox(); // Use VBox for button layout

        // Add all components to main content
        mainContent.getChildren().addAll(
                createHeaderTitle(),
                inputSection,
                outputSection,
                new Separator(),
                buttonBox
        );

        root.setCenter(mainContent);

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/Me/Tasks/styles.css")).toExternalForm());

        primaryStage.setTitle("Playfair Cipher Tool");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Label createHeaderTitle() {
        Label headerLabel = new Label("Playfair Cipher Tool");
        headerLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        headerLabel.setPadding(new Insets(0, 0, 10, 0));
        return headerLabel;
    }

    private TitledPane createInputSection() {
        VBox inputBox = new VBox(10);
        inputBox.setPadding(new Insets(10));

        Label inputLabel = new Label("Enter text to encrypt/decrypt:");

        inputTextArea = new TextArea();
        inputTextArea.setPrefHeight(100);
        inputTextArea.setPromptText("Type or paste your text here...");

        Label keyLabel = new Label("Key:");

        keyField = new TextField();
        keyField.setPromptText("Enter a key (letters only)");

        inputBox.getChildren().addAll(inputLabel, inputTextArea, keyLabel, keyField);

        TitledPane inputSection = new TitledPane("Input", inputBox);
        inputSection.setExpanded(true);

        return inputSection;
    }

    private TitledPane createOutputSection() {
        VBox outputBox = new VBox(10);
        outputBox.setPadding(new Insets(10));

        Label outputLabel = new Label("Result:");

        outputTextArea = new TextArea();
        outputTextArea.setPrefHeight(100);
        outputTextArea.setEditable(false);
        outputTextArea.setPromptText("Encrypted/decrypted text will appear here...");

        outputBox.getChildren().addAll(outputLabel, outputTextArea);

        TitledPane outputSection = new TitledPane("Output", outputBox);
        outputSection.setExpanded(true);

        return outputSection;
    }

    private VBox createButtonBox() {
        // First row of buttons (Encrypt, Decrypt, Clear)
        HBox primaryButtons = new HBox(15);
        primaryButtons.setAlignment(Pos.CENTER);
        primaryButtons.setPadding(new Insets(10, 0, 10, 0));

        Button encryptButton = createButton("Encrypt", "encrypt-btn");
        Button decryptButton = createButton("Decrypt", "decrypt-btn");
        Button clearButton = createButton("Clear All", "clear-btn");

        encryptButton.setOnAction(e -> handleEncrypt());
        decryptButton.setOnAction(e -> handleDecrypt());
        clearButton.setOnAction(e -> {
            inputTextArea.clear();
            outputTextArea.clear();
            keyField.clear();
        });

        primaryButtons.getChildren().addAll(encryptButton, decryptButton, clearButton);

        // Second row of buttons (Load from File, Save to File, Attack)
        HBox secondaryButtons = new HBox(15);
        secondaryButtons.setAlignment(Pos.CENTER);
        secondaryButtons.setPadding(new Insets(0, 0, 10, 0));

        Button loadButton = createButton("Load from File", null);
        Button saveButton = createButton("Save to File", null);
        Button attackButton = createButton("Letter Frequency Attack", null);

        loadButton.setOnAction(e -> handleLoadFromFile());
        saveButton.setOnAction(e -> handleWriteToFile());
        attackButton.setOnAction(e -> handleLetterFrequencyAttack());

        secondaryButtons.getChildren().addAll(loadButton, saveButton, attackButton);

        // Combine both rows into a VBox
        VBox buttonBox = new VBox(10); // Spacing between rows
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(primaryButtons, secondaryButtons);

        return buttonBox;
    }

    private Button createButton(String text, String styleClass) {
        Button button = new Button(text);
        button.setPrefWidth(180);
        button.setPrefHeight(35);

        if (styleClass != null) {
            button.getStyleClass().add(styleClass);
        }

        return button;
    }

    private void handleEncrypt() {
        String text = inputTextArea.getText().trim();
        String key = keyField.getText().trim();

        if (text.isEmpty() || key.isEmpty()) {
            showAlert("Error", "Please enter both text and key.");
            return;
        }

        String encryptedText = PlayfairCipher.encrypt(text, key);
        outputTextArea.setText(encryptedText);
    }

    private void handleDecrypt() {
        String text = inputTextArea.getText().trim();
        String key = keyField.getText().trim();

        if (text.isEmpty() || key.isEmpty()) {
            showAlert("Error", "Please enter both text and key.");
            return;
        }

        String decryptedText = PlayfairCipher.decrypt(text, key);
        outputTextArea.setText(decryptedText);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Style the alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/Me/Tasks/styles.css")).toExternalForm());
        alert.showAndWait();
    }

    private void handleWriteToFile() {
        String content = outputTextArea.getText();

        if (content.isEmpty()) {
            showAlert("Error", "No content to save");
            return;
        }

        File file = new File("src\\Me\\Tasks\\IO\\output.txt");
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(content);
            showAlert("Success", "File saved successfully");
        } catch (IOException ex) {
            showAlert("Error", "Failed to save file: " + ex.getMessage());
        }
    }

    private void handleLoadFromFile() {
        File file = new File("src\\Me\\Tasks\\IO\\input.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            inputTextArea.setText(content.toString());
        } catch (IOException ex) {
            showAlert("Error", "Failed to read file: " + ex.getMessage());
        }
    }

    private void handleLetterFrequencyAttack() {
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