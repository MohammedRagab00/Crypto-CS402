package Me.Shared;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.net.URL;
import java.util.Objects;
import java.io.*;

public abstract class CipherAppTemplate extends Application {

    protected TextArea inputTextArea;
    protected TextArea outputTextArea;
    protected TextField keyField;

    @Override
    public void start(Stage primaryStage) {
        try {
            Image appIcon = new Image(AppConstants.APP_ICON_PATH);
            primaryStage.getIcons().add(appIcon);
        } catch (Exception e) {
            System.err.println("Error loading icon: " + e.getMessage());
        }

        // Create a root ScrollPane that will contain everything
        ScrollPane rootScrollPane = new ScrollPane();
        rootScrollPane.setFitToWidth(true);
        rootScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        rootScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // Create the main container
        VBox mainContainer = new VBox(15);
        mainContainer.setPadding(new Insets(15));
        mainContainer.setStyle("-fx-background-color: transparent;");

        // Ensure the VBox expands to fill the ScrollPane
        VBox.setVgrow(mainContainer, Priority.ALWAYS);

        // Create your existing components
        Label headerTitle = createHeaderTitle();

        // Input Section
        TitledPane inputSection = createInputSection();
        inputSection.getStyleClass().add("input-section");

        // Ensure input section can expand
        VBox.setVgrow(inputSection, Priority.ALWAYS);

        // Output Section
        TitledPane outputSection = createOutputSection();
        outputSection.getStyleClass().add("output-section");

        // Ensure output section can expand
        VBox.setVgrow(outputSection, Priority.ALWAYS);

        // Action Buttons
        VBox buttonBox = createButtonBox(primaryStage);

        // Add all components to main container
        mainContainer.getChildren().addAll(
                headerTitle,
                inputSection,
                outputSection,
                new Separator(),
                buttonBox
        );

        // Set the main container as the content of the root ScrollPane
        rootScrollPane.setContent(mainContainer);

        // Create the scene with the root ScrollPane
        Scene scene = new Scene(rootScrollPane, 800, 600);

        // Load CSS using constant
        try {
            URL cssResource = getClass().getResource(AppConstants.APP_CSS_PATH);
            if (cssResource != null) {
                scene.getStylesheets().add(cssResource.toExternalForm());
            } else {
                System.err.println("CSS file not found at: " + AppConstants.APP_CSS_PATH);
            }
        } catch (Exception e) {
            System.err.println("Error loading CSS: " + e.getMessage());
        }

        primaryStage.setTitle(getAppTitle());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public class AppConstants {
        public static final String APP_ICON_PATH = "/Me/Shared/Images/app-icon.png";
        public static final String APP_CSS_PATH = "/Me/Shared/styles.css";
        public static final String DEFAULT_FONT = "System";
    }

    protected abstract String getAppTitle();

    protected Label createHeaderTitle() {
        Label headerLabel = new Label(getAppTitle());
        headerLabel.setFont(Font.font(AppConstants.DEFAULT_FONT, FontWeight.BOLD, 24));
        headerLabel.setPadding(new Insets(0, 0, 10, 0));
        return headerLabel;
    }

    protected TitledPane createInputSection() {
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

        // Allow subclasses to add custom input fields
        setupInputSection(inputBox);

        TitledPane inputSection = new TitledPane("Input", inputBox);
        inputSection.setExpanded(true);

        return inputSection;
    }

    protected void setupInputSection(VBox inputBox) {
        // Subclasses can override this method to add custom input fields
    }

    protected TitledPane createOutputSection() {
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

    protected VBox createButtonBox(Stage primaryStage) {
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
        Button attackButton = createButton("Attack", null);

        loadButton.setOnAction(e -> handleLoadFromFile(primaryStage));
        saveButton.setOnAction(e -> handleWriteToFile(primaryStage));
        attackButton.setOnAction(e -> handleAttack());

        secondaryButtons.getChildren().addAll(loadButton, saveButton, attackButton);

        // Combine both rows into a VBox
        VBox buttonBox = new VBox(10); // Spacing between rows
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(primaryButtons, secondaryButtons);

        return buttonBox;
    }

    protected Button createButton(String text, String styleClass) {
        Button button = new Button(text);
        button.setPrefWidth(180);
        button.setPrefHeight(35);

        if (styleClass != null) {
            button.getStyleClass().add(styleClass);
        }

        return button;
    }

    protected void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Style the alert
        DialogPane dialogPane = alert.getDialogPane();
        try {
            dialogPane.getStylesheets().add(
                    Objects.requireNonNull(
                            getClass().getResource(AppConstants.APP_CSS_PATH)
                    ).toExternalForm()
            );
        } catch (Exception e) {
            System.err.println("Error styling alert: " + e.getMessage());
        }
        alert.showAndWait();
    }

    protected abstract void handleEncrypt();

    protected abstract void handleDecrypt();

    protected abstract void handleAttack();

    protected void handleLoadFromFile(Stage primaryStage) {
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

    protected void handleWriteToFile(Stage primaryStage) {
        String content = outputTextArea.getText();

        if (content.isEmpty()) {
            showAlert("Error", "No content to save");
            return;
        }

        File file = new File("src\\Me\\Tasks\\IO\\output.txt");
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(content);
//            showAlert("Success", "File saved successfully to: " + file.getAbsolutePath());
        } catch (IOException ex) {
            showAlert("Error", "Failed to save file: " + ex.getMessage());
        }
    }
}
