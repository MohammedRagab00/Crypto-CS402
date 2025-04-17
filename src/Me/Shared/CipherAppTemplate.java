package Me.Shared;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
//import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.WindowEvent;

import java.io.*;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class CipherAppTemplate extends Application {
    protected TextArea inputTextArea;
    protected TextArea outputTextArea;
    protected TextField keyField;
    protected Button optionalButton;
    protected boolean showOptionalButton = true;
    protected Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        setupPrimaryStage(primaryStage);

        ScrollPane rootScrollPane = createRootScrollPane();
        VBox mainContainer = createMainContainer();

        setupMainContainer(mainContainer);
        rootScrollPane.setContent(mainContainer);

        Scene scene = createScene(rootScrollPane);
        setupSceneStyles(scene);

        primaryStage.setScene(scene);
        initialize();
        setupWindowCloseHandler(primaryStage);
        primaryStage.show();
    }

    // Improved setup methods
    private void setupPrimaryStage(Stage primaryStage) {
        try {
            Image appIcon = new Image(AppConstants.APP_ICON_PATH);
            primaryStage.getIcons().add(appIcon);
        } catch (Exception e) {
            handleError("Error loading icon", e);
        }
        primaryStage.setTitle(getAppTitle());
    }

    private ScrollPane createRootScrollPane() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        return scrollPane;
    }

    private VBox createMainContainer() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(15));
        container.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(container, Priority.ALWAYS);
        return container;
    }

    private void setupMainContainer(VBox container) {
        Label headerTitle = createHeaderTitle();
        TitledPane inputSection = createInputSection();
        TitledPane outputSection = createOutputSection();
        VBox buttonBox = createButtonBox(primaryStage);

        container.getChildren().addAll(
                headerTitle,
                inputSection,
                outputSection,
                new Separator(),
                buttonBox
        );
    }

    private Scene createScene(ScrollPane root) {
        return new Scene(root, 800, 600);
    }

    private void setupSceneStyles(Scene scene) {
        try {
            URL cssResource = getClass().getResource(AppConstants.APP_CSS_PATH);
            if (cssResource != null) {
                scene.getStylesheets().add(cssResource.toExternalForm());
            } else {
                showAlert("Warning", "CSS file not found at: " + AppConstants.APP_CSS_PATH);
            }
        } catch (Exception e) {
            handleError("Error loading CSS", e);
        }
    }

    private void setupWindowCloseHandler(Stage stage) {
        stage.setOnCloseRequest(this::handleWindowClose);
    }

    protected void handleWindowClose(WindowEvent event) {
        // Can be overridden by subclasses for cleanup
    }

    // CONSTANTS class with additional configuration options
    public static class AppConstants {
        public static final String APP_ICON_PATH = "/Me/Shared/Images/app-icon.png";
        public static final String APP_CSS_PATH = "/Me/Shared/styles.css";
        public static final String DEFAULT_FONT = "System";
        public static final String DEFAULT_INPUT_PROMPT = "Type or paste your text here...";
        public static final String DEFAULT_KEY_PROMPT = "Enter your encryption key";
        public static final String DEFAULT_OUTPUT_PROMPT = "Results will appear here...";
        public static final int DEFAULT_TEXTAREA_HEIGHT = 100;
        public static final int DEFAULT_BUTTON_WIDTH = 180;
        public static final int DEFAULT_BUTTON_HEIGHT = 35;
    }

    // UI Component Factories
    protected Label createHeaderTitle() {
        Label headerLabel = new Label(getAppTitle());
        headerLabel.setFont(Font.font(AppConstants.DEFAULT_FONT, FontWeight.BOLD, 24));
        headerLabel.setPadding(new Insets(0, 0, 10, 0));
        return headerLabel;
    }

    protected TextArea createTextArea(String promptText, int prefHeight) {
        TextArea textArea = new TextArea();
        textArea.setPromptText(promptText);
        textArea.setPrefHeight(prefHeight);
        return textArea;
    }

    protected TextField createTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        return textField;
    }

    protected TitledPane createInputSection() {
        VBox inputBox = new VBox(10);
        inputBox.setPadding(new Insets(10));

        inputTextArea = createTextArea(AppConstants.DEFAULT_INPUT_PROMPT, AppConstants.DEFAULT_TEXTAREA_HEIGHT);
        keyField = createTextField(AppConstants.DEFAULT_KEY_PROMPT);

        inputBox.getChildren().addAll(
                new Label("Enter text to encrypt/decrypt:"),
                inputTextArea,
                new Label("Key:"),
                keyField
        );

        setupInputSection(inputBox);
        return createTitledPane("Input", inputBox);
    }

    protected TitledPane createOutputSection() {
        VBox outputBox = new VBox(10);
        outputBox.setPadding(new Insets(10));

        outputTextArea = createTextArea(AppConstants.DEFAULT_OUTPUT_PROMPT, AppConstants.DEFAULT_TEXTAREA_HEIGHT);
        outputTextArea.setEditable(false);

        outputBox.getChildren().addAll(
                new Label("Result:"),
                outputTextArea
        );

        return createTitledPane("Output", outputBox);
    }

    protected TitledPane createTitledPane(String title, VBox content) {
        TitledPane pane = new TitledPane(title, content);
        VBox.setVgrow(pane, Priority.ALWAYS);
        pane.setCollapsible(false);
        return pane;
    }

    protected VBox createButtonBox(Stage primaryStage) {
        HBox primaryButtons = createButtonRow(
                createButton("Encrypt", "encrypt-btn", e -> handleEncrypt()),
                createButton("Decrypt", "decrypt-btn", e -> handleDecrypt()),
                createButton("Clear All", "clear-btn", e -> clearAllFields())
        );

        HBox secondaryButtons = createButtonRow(
                createButton("Load from File", null, e -> handleLoadFromFile()),
                createButton("Save to File", null, e -> handleWriteToFile())
        );

        if (showOptionalButton) {
            optionalButton = createButton("Optional Action", null, e -> handleOptionalAction());
            secondaryButtons.getChildren().add(optionalButton);
        }

        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(primaryButtons, secondaryButtons);

        return buttonBox;
    }

    protected HBox createButtonRow(Button... buttons) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER);
        row.setPadding(new Insets(10, 0, 10, 0));
        row.getChildren().addAll(buttons);
        return row;
    }

    protected Button createButton(String text, String styleClass, Consumer<Void> action) {
        Button button = new Button(text);
        button.setPrefWidth(AppConstants.DEFAULT_BUTTON_WIDTH);
        button.setPrefHeight(AppConstants.DEFAULT_BUTTON_HEIGHT);

        if (styleClass != null) {
            button.getStyleClass().add(styleClass);
        }

        button.setOnAction(e -> action.accept(null));
        return button;
    }

    protected void clearAllFields() {
        inputTextArea.clear();
        outputTextArea.clear();
        keyField.clear();
    }

    protected void configureOptionalButton(String buttonText, boolean visible) {
        this.showOptionalButton = visible;
        if (optionalButton != null) {
            optionalButton.setText(buttonText);
            optionalButton.setVisible(visible);
        }
    }

    protected void handleLoadFromFile() {
/*
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Text File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );

        File file = fileChooser.showOpenDialog(primaryStage);
*/
        File file = new File("src\\Me\\Tasks\\IO\\input.txt");
        if (file != null) {
            loadFileContent(file);
        }
    }

    protected void loadFileContent(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            inputTextArea.setText(content.toString());
        } catch (IOException ex) {
            handleError("Failed to read file", ex);
        }
    }

    protected void handleWriteToFile() {
        String content = outputTextArea.getText();
        if (content.isEmpty()) {
            showAlert("Error", "No content to save");
            return;
        }
/*
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Output File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );
        File file = fileChooser.showSaveDialog(primaryStage);
*/
        File file = new File("src\\Me\\Tasks\\IO\\output.txt");
        saveFileContent(file, content);
    }

    protected void saveFileContent(File file, String content) {
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(content);
            showAlert("Success", "File saved successfully");
        } catch (IOException ex) {
            handleError("Failed to save file", ex);
        }
    }

    protected void handleError(String message, Exception ex) {
        System.err.println(message + ": " + ex.getMessage());
        showAlert("Error", message + ": " + ex.getMessage());
    }

    protected void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        try {
            alert.getDialogPane().getStylesheets().add(
                    Objects.requireNonNull(
                            getClass().getResource(AppConstants.APP_CSS_PATH)
                    ).toExternalForm()
            );
        } catch (Exception e) {
            System.err.println("Error styling alert: " + e.getMessage());
        }
        alert.showAndWait();
    }

    protected boolean confirmAction(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        try {
            alert.getDialogPane().getStylesheets().add(
                    Objects.requireNonNull(
                            getClass().getResource(AppConstants.APP_CSS_PATH)
                    ).toExternalForm()
            );
        } catch (Exception e) {
            System.err.println("Error styling alert: " + e.getMessage());
        }

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    // Abstract methods to be implemented by subclasses
    protected abstract String getAppTitle();

    protected abstract void handleEncrypt();

    protected abstract void handleDecrypt();

    protected abstract void initialize();

    protected abstract void setupInputSection(VBox inputBox);

    protected void handleOptionalAction() {
        // Default implementation does nothing
    }
}