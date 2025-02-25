package Me.Tasks.Task1;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class V1 extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        Label wordLabel = new Label("Word:");
        TextField wordField = new TextField();
        TextField monoField = new TextField();
        Label shiftLabel = new Label("Shift Value:");
        TextField shiftField = new TextField();
        TextArea outputArea = new TextArea();
        TextArea bruteArea = new TextArea();
        outputArea.setEditable(false);

        HBox buttonBox = new HBox(10);
        Button encodeButton = new Button("Encode");
        Button decodeButton = new Button("Decode");
        Button bruteForceButton = new Button("Brute Force Attack");
        Button monoButton = new Button("mono Alphabetic");
        Button WriteOnFile = new Button("Write");
        Button plaintextAttackButton = new Button("Plaintext Attack");
        buttonBox.getChildren().addAll(encodeButton, decodeButton, bruteForceButton, WriteOnFile, plaintextAttackButton, monoButton);

        root.getChildren().addAll(wordLabel, wordField, shiftLabel, shiftField, monoField, outputArea, bruteArea, buttonBox);

        encodeButton.setOnAction(e -> {
            int shift;
            try {
                shift = Integer.parseInt(shiftField.getText());
            } catch (NumberFormatException ex) {
                outputArea.setText("Invalid shift value");
                return;
            }
            String text = wordField.getText();
            if (text.isEmpty()) {
                outputArea.setText("Enter word");
            } else {
                outputArea.setText(caesarCipher(shift, text));
            }
        });

        decodeButton.setOnAction(e -> {
            int shift;
            try {
                shift = Integer.parseInt(shiftField.getText());
            } catch (NumberFormatException ex) {
                outputArea.setText("Invalid shift value");
                return;
            }
            String encodedText = wordField.getText();
            if (encodedText.isEmpty()) {
                outputArea.setText("No encoded text found");
            } else {
                outputArea.setText(caesarCipher(-(shift % 26), encodedText));
            }
        });

        bruteForceButton.setOnAction(e -> {
            String encodedText = wordField.getText();
            if (encodedText.isEmpty()) {
                bruteArea.setText("Enter word");
            } else {
                StringBuilder result = new StringBuilder("Brute Force Results:\n");
                for (int shift = 1; shift < 26; shift++) {
                    result.append("Shift ").append(shift).append(": ").append(caesarCipher(-shift, encodedText)).append("\n");
                }
                bruteArea.setText(result.toString());
            }
        });

        monoButton.setOnAction(e -> {
            char[] key = monoField.getText().toCharArray();
            if (key.length == 26) {
                StringBuilder result = new StringBuilder("Mono Results:\n");

                for (char c : wordField.getText().toCharArray()) {
                    result.append(key[c - 'a']);
                }
                outputArea.setText(result.toString());
            } else {
                outputArea.setText("you should enter 26 letters!");
            }
        });

        plaintextAttackButton.setOnAction(e -> {
            try {
                String encodedText = wordField.getText(), decodedText = outputArea.getText();
                if (encodedText.isEmpty()) {
                    outputArea.setText("Enter encoded text");
                } else {
                    int num = decodedText.charAt(0) - encodedText.charAt(0), ans;
                    ans = num >= 0 ? num % 26 : num + 26;
                    outputArea.setText("Plaintext Attack Result: " + ans);
                    shiftField.setText(String.valueOf(ans));
                }
            } catch (Exception ignored) {

            }
        });

        WriteOnFile.setOnAction(e -> {
            try (FileWriter fw = new FileWriter("src\\Me\\Tasks\\Task1\\myBrute.txt")) {
                fw.write(bruteArea.getText());

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        });

        Scene scene = new Scene(root, 600, 500);
        primaryStage.setTitle("Caesar Salad Tool");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private String caesarCipher(int shift, String text) {
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

    // A simple implementation of MonoAlphabetic Cipher encryption
    private String monoAlphabeticCipher(String text, TextField m) {
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        String shuffledAlphabet = m.getText();
        Map<Character, Character> cipherMap = new HashMap<>();
        for (int i = 0; i < alphabet.length(); i++) {
            cipherMap.put(alphabet.charAt(i), shuffledAlphabet.charAt(i));
            cipherMap.put(Character.toUpperCase(alphabet.charAt(i)), Character.toUpperCase(shuffledAlphabet.charAt(i)));
        }

        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (cipherMap.containsKey(c)) {
                result.append(cipherMap.get(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}
