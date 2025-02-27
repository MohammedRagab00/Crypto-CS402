package Me.Tasks.Task1;

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

public class V2 extends Application {

    private TextArea inputTextArea;
    private TextArea outputTextArea;
    private TextArea bruteForceResultsArea;
    private TextField shiftValueField;
    private TextField monoKeyField;

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

        // Brute Force Results Section
        TitledPane bruteForceSection = createBruteForceSection();
        bruteForceSection.getStyleClass().add("brute-force-section");

        // Action Buttons
        HBox primaryButtons = createPrimaryButtons(primaryStage);
        HBox secondaryButtons = createSecondaryButtons(primaryStage);

        // Add all components to main content
        mainContent.getChildren().addAll(
                createHeaderTitle(),
                inputSection,
                outputSection,
                bruteForceSection,
                new Separator(),
                primaryButtons,
                secondaryButtons
        );

        root.setCenter(mainContent);

        Scene scene = new Scene(root, 800, 700);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/Me/Tasks/styles.css")).toExternalForm());

        primaryStage.setTitle("Caesar Salad");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Label createHeaderTitle() {
        Label headerLabel = new Label("Caesar Cipher Tool");
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

        HBox shiftBox = new HBox(10);
        shiftBox.setAlignment(Pos.CENTER_LEFT);

        Label shiftValueLabel = new Label("Shift Value (0-25):");

        shiftValueField = new TextField();
        shiftValueField.setPrefWidth(80);
        shiftValueField.setPromptText("0-25");

        Label monoKeyLabel = new Label("Monoalphabetic Key:");

        monoKeyField = new TextField();
        monoKeyField.setPromptText("26 unique letters (a-z)");
        monoKeyField.setText("abcdefghijklmnopqrstuvwxyz"); // Pre-populate with the alphabet


        shiftBox.getChildren().addAll(shiftValueLabel, shiftValueField, monoKeyLabel, monoKeyField);

        inputBox.getChildren().addAll(inputLabel, inputTextArea, shiftBox);

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

    private TitledPane createBruteForceSection() {
        VBox bruteForceBox = new VBox(10);
        bruteForceBox.setPadding(new Insets(10));

        Label bruteForceLabel = new Label("Brute Force Results:");

        bruteForceResultsArea = new TextArea();
        bruteForceResultsArea.setPrefHeight(150);
        bruteForceResultsArea.setEditable(false);
        bruteForceResultsArea.setPromptText("All possible shift combinations will appear here...");

        bruteForceBox.getChildren().addAll(bruteForceLabel, bruteForceResultsArea);

        TitledPane bruteForceSection = new TitledPane("Brute Force Analysis", bruteForceBox);
        bruteForceSection.setExpanded(false);

        return bruteForceSection;
    }

    private HBox createPrimaryButtons(Stage primaryStage) {
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 10, 0));

        Button encryptButton = createButton("Encrypt", "encrypt-btn");
        Button decryptButton = createButton("Decrypt", "decrypt-btn");
        Button clearButton = createButton("Clear All", "clear-btn");

        encryptButton.setOnAction(e -> handleEncode());
        decryptButton.setOnAction(e -> handleDecode());
        clearButton.setOnAction(e -> {
            inputTextArea.clear();
            outputTextArea.clear();
            bruteForceResultsArea.clear();
            shiftValueField.clear();
            monoKeyField.clear();
        });

        buttonBox.getChildren().addAll(encryptButton, decryptButton, clearButton);

        return buttonBox;
    }

    private HBox createSecondaryButtons(Stage primaryStage) {
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(0, 0, 10, 0));

        Button bruteForceButton = createButton("Brute Force Attack", null);
        Button monoAlphabeticButton = createButton("MonoAlphabetic Cipher", null);
        Button knownPlaintextButton = createButton("Known Plaintext Attack", null);
        Button fileOperationsButton = createButton("File Operations", null);

        bruteForceButton.setOnAction(e -> handleBruteForce());
        monoAlphabeticButton.setOnAction(e -> handleMonoAlphabetic());
        knownPlaintextButton.setOnAction(e -> handleKnownPlaintextAttack());

        // Create a popover menu for file operations
        fileOperationsButton.setOnAction(e -> {
            ContextMenu menu = new ContextMenu();
            MenuItem loadItem = new MenuItem("Load from File");
            MenuItem saveItem = new MenuItem("Save to File");

            loadItem.setOnAction(event -> handleLoadFromFile(primaryStage));
            saveItem.setOnAction(event -> handleWriteToFile(primaryStage));

            menu.getItems().addAll(loadItem, saveItem);
            menu.show(fileOperationsButton, javafx.geometry.Side.BOTTOM, 0, 0);
        });

        buttonBox.getChildren().addAll(bruteForceButton, monoAlphabeticButton, knownPlaintextButton, fileOperationsButton);

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

    // Event Handler Methods
    private void handleEncode() {
        String text = inputTextArea.getText();
        if (text.isEmpty()) {
            showAlert("Error", "Please enter input text");
            return;
        }

        int shift = validateShift(shiftValueField.getText());
        if (shift == -1) return;

        outputTextArea.setText(applyCaesarCipher(shift, text));
    }

    private void handleDecode() {
        String text = inputTextArea.getText();
        if (text.isEmpty()) {
            showAlert("Error", "Please enter input text");
            return;
        }

        int shift = validateShift(shiftValueField.getText());
        if (shift == -1) return;

        outputTextArea.setText(applyCaesarCipher(-shift, text));
    }

    private void handleBruteForce() {
        String encodedText = inputTextArea.getText();
        if (encodedText.isEmpty()) {
            showAlert("Error", "Please enter text to brute force");
            return;
        }

        StringBuilder result = new StringBuilder();
        for (int shift = 1; shift < 26; shift++) {
            result.append("Shift ").append(shift).append(": ")
                    .append(applyCaesarCipher(-shift, encodedText)).append("\n\n");
        }
        bruteForceResultsArea.setText(result.toString());
    }

    private void handleMonoAlphabetic() {
        String key = monoKeyField.getText().toLowerCase();
        String inputText = inputTextArea.getText();

        if (inputText.isEmpty()) {
            showAlert("Error", "Please enter input text");
            return;
        }

        if (key.length() != 26) {
            showAlert("Error", "Key must be exactly 26 characters long");
            return;
        }

        // Check for duplicate characters in key
        boolean[] used = new boolean[26];
        for (char c : key.toCharArray()) {
            if (!Character.isLetter(c)) {
                showAlert("Error", "Key must contain only letters");
                return;
            }

            int index = c - 'a';
            if (used[index]) {
                showAlert("Error", "Key must not contain duplicate letters");
                return;
            }
            used[index] = true;
        }

        StringBuilder result = new StringBuilder();
        for (char c : inputText.toCharArray()) {
            if (Character.isLetter(c)) {
                if (Character.isLowerCase(c)) {
                    result.append(key.charAt(c - 'a'));
                } else {
                    result.append(Character.toUpperCase(key.charAt(Character.toLowerCase(c) - 'a')));
                }
            } else {
                result.append(c);
            }
        }
        outputTextArea.setText(result.toString());
    }

    private void handleKnownPlaintextAttack() {
        String encodedText = inputTextArea.getText();
        String knownPlaintext = outputTextArea.getText();

        if (encodedText.isEmpty() || knownPlaintext.isEmpty()) {
            showAlert("Error", "Both input text and output text are required");
            return;
        }

        // Find the first letter position in both texts
        int index = -1;
        for (int i = 0; i < Math.min(encodedText.length(), knownPlaintext.length()); i++) {
            if (Character.isLetter(encodedText.charAt(i)) && Character.isLetter(knownPlaintext.charAt(i))) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            showAlert("Error", "Could not find matching letter positions");
            return;
        }

        // Calculate shift
        char encChar = Character.toLowerCase(encodedText.charAt(index));
        char plainChar = Character.toLowerCase(knownPlaintext.charAt(index));
        int shift = (-encChar + plainChar + 26) % 26;

        // Update UI with results
        shiftValueField.setText(String.valueOf(shift));
        outputTextArea.setText("Deduced Shift: " + shift + "\nDecrypted Text: " +
                applyCaesarCipher(-shift, encodedText));
    }

    private void handleWriteToFile(Stage primaryStage) {
        String content = bruteForceResultsArea.getText().isEmpty() ?
                outputTextArea.getText() : bruteForceResultsArea.getText();

        if (content.isEmpty()) {
            showAlert("Error", "No content to save");
            return;
        }

/*
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Results");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );

        File file = fileChooser.showSaveDialog(primaryStage);
*/
        File file = new File("src\\Me\\Tasks\\IO\\output.txt");
        if (file != null) {
            try (FileWriter fw = new FileWriter(file)) {
                fw.write(content);
                showAlert("Success", "File saved successfully to " + file.getAbsolutePath());
            } catch (IOException ex) {
                showAlert("Error", "Failed to save file: " + ex.getMessage());
            }
        }
    }

    private void handleLoadFromFile(Stage primaryStage) {
/*
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );

        File file = fileChooser.showOpenDialog(primaryStage);
*/

        File file = new File("src\\Me\\Tasks\\IO\\input.txt");

        if (file != null) {
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
    }

    // Utility Methods
    private int validateShift(String shiftText) {
        try {
            int shift = Integer.parseInt(shiftText);
            if (shift < 0 || shift > 25) {
                showAlert("Error", "Shift value must be between 0 and 25");
                return -1;
            }
            return shift;
        } catch (NumberFormatException ex) {
            showAlert("Error", "Please enter a valid number for shift");
            return -1;
        }
    }

    private String applyCaesarCipher(int shift, String text) {
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isLowerCase(c) ? 'a' : 'A';
                result.append((char) ((c + shift - base + 26) % 26 + base));
            } else {
                result.append(c);
            }
        }
        return result.toString();
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
}