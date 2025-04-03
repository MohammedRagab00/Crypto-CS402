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
    protected Button optionalButton;
    protected boolean showOptionalButton = true;

    @Override
    public void start(Stage primaryStage) {
        try {
            Image appIcon = new Image(AppConstants.APP_ICON_PATH);
            primaryStage.getIcons().add(appIcon);
        } catch (Exception e) {
            System.err.println("Error loading icon: " + e.getMessage());
        }

        ScrollPane rootScrollPane = new ScrollPane();
        rootScrollPane.setFitToWidth(true);
        rootScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        rootScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        VBox mainContainer = new VBox(15);
        mainContainer.setPadding(new Insets(15));
        mainContainer.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(mainContainer, Priority.ALWAYS);

        Label headerTitle = createHeaderTitle();
        TitledPane inputSection = createInputSection();
        inputSection.getStyleClass().add("input-section");
        VBox.setVgrow(inputSection, Priority.ALWAYS);

        TitledPane outputSection = createOutputSection();
        outputSection.getStyleClass().add("output-section");
        VBox.setVgrow(outputSection, Priority.ALWAYS);

        VBox buttonBox = createButtonBox(primaryStage);

        mainContainer.getChildren().addAll(
                headerTitle,
                inputSection,
                outputSection,
                new Separator(),
                buttonBox
        );

        rootScrollPane.setContent(mainContainer);
        Scene scene = new Scene(rootScrollPane, 800, 600);

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

    public static class AppConstants {
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
        keyField.setPromptText("Enter your encryption key");

        inputBox.getChildren().addAll(inputLabel, inputTextArea, keyLabel, keyField);
        setupInputSection(inputBox);

        return new TitledPane("Input", inputBox);
    }

    protected void setupInputSection(VBox inputBox) {
        // For subclasses to add custom input fields
    }

    protected TitledPane createOutputSection() {
        VBox outputBox = new VBox(10);
        outputBox.setPadding(new Insets(10));

        outputTextArea = new TextArea();
        outputTextArea.setPrefHeight(100);
        outputTextArea.setEditable(false);
        outputTextArea.setPromptText("Results will appear here...");

        outputBox.getChildren().addAll(
                new Label("Result:"),
                outputTextArea
        );

        return new TitledPane("Output", outputBox);
    }

    protected VBox createButtonBox(Stage primaryStage) {
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

        HBox secondaryButtons = new HBox(15);
        secondaryButtons.setAlignment(Pos.CENTER);
        secondaryButtons.setPadding(new Insets(0, 0, 10, 0));

        Button loadButton = createButton("Load from File", null);
        Button saveButton = createButton("Save to File", null);

        if (showOptionalButton) {
            optionalButton = createButton("Optional Action", null);
            optionalButton.setOnAction(e -> handleOptionalAction());
            secondaryButtons.getChildren().addAll(loadButton, saveButton, optionalButton);
        } else {
            secondaryButtons.getChildren().addAll(loadButton, saveButton);
        }

        VBox buttonBox = new VBox(10);
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

    protected void configureOptionalButton(String buttonText, boolean visible) {
        this.showOptionalButton = visible;
        if (optionalButton != null) {
            optionalButton.setText(buttonText);
            optionalButton.setVisible(visible);
        }
    }

    protected void handleOptionalAction() {
        // Default implementation does nothing
    }

    protected void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

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
        } catch (IOException ex) {
            showAlert("Error", "Failed to save file: " + ex.getMessage());
        }
    }
}