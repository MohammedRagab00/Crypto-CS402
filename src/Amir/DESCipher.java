//package Amir;
//
//import javafx.application.Application;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.layout.*;
//import javafx.stage.Stage;
//
//import java.io.FileWriter;
//import java.io.IOException;
////import java.math.BigInteger;
//
//public class DESCipher extends Application {
//
//    private TextArea inputTextArea;
//    private TextArea outputTextArea;
//    private TextField keyField;
//    private ToggleGroup operationGroup;
//
//    private static final int[] PC1 = {
//            57, 49, 41, 33, 25, 17, 9,
//            1, 58, 50, 42, 34, 26, 18,
//            10, 2, 59, 51, 43, 35, 27,
//            19, 11, 3, 60, 52, 44, 36,
//            63, 55, 47, 39, 31, 23, 15,
//            7, 62, 54, 46, 38, 30, 22,
//            14, 6, 61, 53, 45, 37, 29,
//            21, 13, 5, 28, 20, 12, 4
//    };
//
//    private static final int[] PC2 = {
//            14, 17, 11, 24, 1, 5,
//            3, 28, 15, 6, 21, 10,
//            23, 19, 12, 4, 26, 8,
//            16, 7, 27, 20, 13, 2,
//            41, 52, 31, 37, 47, 55,
//            30, 40, 51, 45, 33, 48,
//            44, 49, 39, 56, 34, 53,
//            46, 42, 50, 36, 29, 32
//    };
//
//    private static final int[] SHIFTS = {
//            1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1
//    };
//
//    public static final int[][][] S_BOX = {
//            {
//                    {14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7},
//                    {0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8},
//                    {4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0},
//                    {15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13}
//            },
//            {
//                    {15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10},
//                    {3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5},
//                    {0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15},
//                    {13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9}
//            },
//            {
//                    {10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8},
//                    {13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1},
//                    {13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7},
//                    {1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12}
//            },
//            {
//                    {7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15},
//                    {13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9},
//                    {10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4},
//                    {3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14}
//            },
//            {
//                    {2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9},
//                    {14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6},
//                    {4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14},
//                    {11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3}
//            },
//            {
//                    {12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11},
//                    {10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8},
//                    {9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6},
//                    {4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13}
//            },
//            {
//                    {4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1},
//                    {13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6},
//                    {1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2},
//                    {6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12}
//            },
//            {
//                    {13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7},
//                    {1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2},
//                    {7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8},
//                    {2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11}
//            }
//    };
//
//    private static final int[] IP = {
//            58, 50, 42, 34, 26, 18, 10, 2,
//            60, 52, 44, 36, 28, 20, 12, 4,
//            62, 54, 46, 38, 30, 22, 14, 6,
//            64, 56, 48, 40, 32, 24, 16, 8,
//            57, 49, 41, 33, 25, 17, 9, 1,
//            59, 51, 43, 35, 27, 19, 11, 3,
//            61, 53, 45, 37, 29, 21, 13, 5,
//            63, 55, 47, 39, 31, 23, 15, 7
//    };
//
//    private static final int[] FP = {
//            40, 8, 48, 16, 56, 24, 64, 32,
//            39, 7, 47, 15, 55, 23, 63, 31,
//            38, 6, 46, 14, 54, 22, 62, 30,
//            37, 5, 45, 13, 53, 21, 61, 29,
//            36, 4, 44, 12, 52, 20, 60, 28,
//            35, 3, 43, 11, 51, 19, 59, 27,
//            34, 2, 42, 10, 50, 18, 58, 26,
//            33, 1, 41, 9, 49, 17, 57, 25
//    };
//
//    private static final int[] E = {
//            32, 1, 2, 3, 4, 5, 4, 5, 6, 7, 8, 9,
//            8, 9, 10, 11, 12, 13, 12, 13, 14, 15, 16, 17,
//            16, 17, 18, 19, 20, 21, 20, 21, 22, 23, 24, 25,
//            24, 25, 26, 27, 28, 29, 28, 29, 30, 31, 32, 1
//    };
//
//    private static final int[] P = {
//            16, 7, 20, 21, 29, 12, 28, 17,
//            1, 15, 23, 26, 5, 18, 31, 10,
//            2, 8, 24, 14, 32, 27, 3, 9,
//            19, 13, 30, 6, 22, 11, 4, 25
//    };
//
//    @Override
//    public void start(Stage primaryStage) {
//        primaryStage.setTitle("DES Cipher Application");
//
//        Label inputLabel = new Label("Input Text:");
//        inputTextArea = new TextArea();
//        inputTextArea.setWrapText(false);
//        inputTextArea.setPrefHeight(100);
//
//        Label keyLabel = new Label("DES Key (in hex):");
//        keyField = new TextField();
//        keyField.setPromptText("");
//
//        Label operationLabel = new Label("Operation:");
//        operationGroup = new ToggleGroup();
//        RadioButton encryptRadio = new RadioButton("Encrypt");
//        encryptRadio.setToggleGroup(operationGroup);
//        encryptRadio.setSelected(true);
//        RadioButton decryptRadio = new RadioButton("Decrypt");
//        decryptRadio.setToggleGroup(operationGroup);
//
//        HBox radioBox = new HBox(10, encryptRadio, decryptRadio);
//        radioBox.setAlignment(Pos.CENTER_LEFT);
//
//        Button processButton = new Button("Process");
//        processButton.setOnAction(e -> processText());
//
//        Button generateKeysButton = new Button("Generate Keys");
//        generateKeysButton.setOnAction(e -> generateAndSaveKeys());
//
//        Label outputLabel = new Label("Result:");
//        outputTextArea = new TextArea();
//        outputTextArea.setWrapText(false);
//        outputTextArea.setEditable(false);
//        outputTextArea.setPrefHeight(100);
//
//        GridPane grid = new GridPane();
//        grid.setAlignment(Pos.CENTER);
//        grid.setHgap(10);
//        grid.setVgap(10);
//        grid.setPadding(new Insets(25, 25, 25, 25));
//
//        grid.add(inputLabel, 0, 0);
//        grid.add(inputTextArea, 0, 1, 2, 1);
//        grid.add(keyLabel, 0, 2);
//        grid.add(keyField, 1, 2);
//        grid.add(operationLabel, 0, 3);
//        grid.add(radioBox, 1, 3);
//        grid.add(processButton, 0, 4);
//        grid.add(generateKeysButton, 1, 4);
//        grid.add(outputLabel, 0, 5);
//        grid.add(outputTextArea, 0, 6, 2, 1);
//
//        ColumnConstraints col1 = new ColumnConstraints();
//        col1.setPrefWidth(100);
//        ColumnConstraints col2 = new ColumnConstraints();
//        col2.setPrefWidth(200);
//        grid.getColumnConstraints().addAll(col1, col2);
//
//        Scene scene = new Scene(grid, 500, 400);
//        primaryStage.setScene(scene);
//        primaryStage.show();
//    }
//
//    private void processText() {
//        String input = inputTextArea.getText().trim();
//        String key = keyField.getText().trim();
//        boolean isEncrypt = ((RadioButton) operationGroup.getSelectedToggle()).getText().equals("Encrypt");
//
//        if (key.length() != 16 || !key.matches("[0-9A-Fa-f]+")) {
//            showAlert("Key must be exactly 16 hexadecimal characters");
//            return;
//        }
//
//        try {
//            String result;
//            String[] keys = generateKeys(key);
//
//            if (isEncrypt) {
//                if (input.length() != 8) {
//                    input = padding(input);
//                }
//                result = encrypt(input, keys);
//                outputTextArea.setText("Ciphertext (hex):\n" + result);
//            } else {
//                if (input.length() != 16) {
//                    showAlert("Ciphertext must be exactly 16 hexadecimal characters");
//                    return;
//                }
//                result = decrypt(input, keys);
//                outputTextArea.setText("Decrypted text:\n" + result);
//            }
//        } catch (Exception e) {
//            showAlert("Error: " + e.getMessage());
//        }
//    }
//
//    private void generateAndSaveKeys() {
//        String key = keyField.getText().trim();
//
//        if (key.length() != 16 || !key.matches("[0-9A-Fa-f]+")) {
//            showAlert("Key must be exactly 16 hexadecimal characters");
//            return;
//        }
//
//        String[] keys = generateKeys(key);
//        saveKeysToFile(keys);
////        showAlert("16 round keys generated and saved");
//    }
//
//    private String padding(String mes) {
//        int paddingLen = Math.max(8 - mes.length(), 0);
//        return mes + String.valueOf((char) 'X').repeat(paddingLen);
//    }
//
//    private String[] generateKeys(String hexKey) {
//        String binaryKey = hexToBinary(hexKey);
//        String permutedKey = permutation(binaryKey, PC1, 56);
//
//        String left = permutedKey.substring(0, 28);
//        String right = permutedKey.substring(28, 56);
//
//        String[] keys = new String[16];
//
//        for (int i = 0; i < 16; i++) {
//            left = LShift(left, SHIFTS[i]);
//            right = LShift(right, SHIFTS[i]);
//
//            String combineKey = left + right;
//            keys[i] = permutation(combineKey, PC2, 48);
//        }
//        return keys;
//    }
//
//    private String encrypt(String text, String[] keys) {
//        String binaryText = hexToBinary(stringToHex(text));
//        String permutedText = permutation(binaryText, IP, 64);
//        String left = permutedText.substring(0, 32);
//        String right = permutedText.substring(32, 64);
//
//        for (int i = 0; i < 16; i++) {
//            String function = feistel(right, keys[i]);
//            String newRight = xor(left, function);
//            left = right;
//            right = newRight;
//        }
//        String combined = right + left;
//        return binaryToHex(permutation(combined, FP, 64));
//    }
//
//    private String decrypt(String ciphertext, String[] keys) {
//        String binaryText = hexToBinary(ciphertext);
//        String permutedText = permutation(binaryText, IP, 64);
//        String left = permutedText.substring(0, 32);
//        String right = permutedText.substring(32, 64);
//
//        for (int i = 15; i >= 0; i--) {
//            String function = feistel(right, keys[i]);
//            String newRight = xor(left, function);
//            left = right;
//            right = newRight;
//        }
//        String combined = right + left;
//        String binaryResult = permutation(combined, FP, 64);
//        String hexResult = binaryToHex(binaryResult);
//        return hexToString(hexResult);
//    }
//
//    private String feistel(String half, String key) {
//        String expanded = permutation(half, E, 48);
//        String myXOR = xor(expanded, key);
//        String substituted = sBoxSubstitution(myXOR);
//        return permutation(substituted, P, 32);
//    }
//
//    private String sBoxSubstitution(String input) {
//        StringBuilder output = new StringBuilder();
//        for (int i = 0; i < 8; i++) {
//            int row = Integer.parseInt("" + input.charAt(i * 6) + input.charAt(i * 6 + 5), 2);
//            int col = Integer.parseInt(input.substring(i * 6 + 1, i * 6 + 5), 2);
//            output.append(String.format("%4s", Integer.toBinaryString(S_BOX[i][row][col])).replace(' ', '0'));
//        }
//        return output.toString();
//    }
//
//    private String xor(String a, String b) {
//        StringBuilder result = new StringBuilder();
//        for (int i = 0; i < a.length(); i++) {
//            result.append(a.charAt(i) == b.charAt(i) ? '0' : '1');
//        }
//        return result.toString();
//    }
//
//    private String stringToHex(String input) {
//        StringBuilder hex = new StringBuilder();
//        for (char c : input.toCharArray()) {
//            hex.append(String.format("%02X", (int) c));
//        }
//        return hex.toString();
//    }
//
//    private String hexToString(String hex) {
//        StringBuilder text = new StringBuilder();
//        for (int i = 0; i < hex.length(); i += 2) {
//            String str = hex.substring(i, i + 2);
//            text.append((char) Integer.parseInt(str, 16));
//        }
//        return text.toString();
//    }
//
//    private String hexToBinary(String hex) {
//        StringBuilder binary = new StringBuilder();
//        for (char c : hex.toCharArray()) {
//            binary.append(String.format("%4s", Integer.toBinaryString(Integer.parseInt(String.valueOf(c), 16)))
//                    .replace(' ', '0'));
//        }
///*
//        String s = new BigInteger(hex, 16).toString(2);
//        System.out.println(s+'\n'+binary);
//*/
//        return binary.toString();
//    }
//
//    private String binaryToHex(String binary) {
//        StringBuilder hex = new StringBuilder();
//        for (int i = 0; i < binary.length(); i += 4) {
//            hex.append(Integer.toHexString(Integer.parseInt(binary.substring(i, i + 4), 2)));
//        }
//        return hex.toString().toUpperCase();
//    }
//
//    private String permutation(String input, int[] table, int n) {
//        StringBuilder output = new StringBuilder();
//        for (int i = 0; i < n; i++) {
//            output.append(input.charAt(table[i] - 1));
//        }
//        return output.toString();
//    }
//
//    private String LShift(String input, int shifts) {
//        return input.substring(shifts) + input.substring(0, shifts);
//    }
//
//    private void saveKeysToFile(String[] keys) {
//        try (FileWriter writer = new FileWriter("src\\Me\\Tasks\\IO\\output.txt")) {
//            for (int i = 0; i < keys.length; i++) {
//                String binaryKey = keys[i];
//                String hexKey = binaryToHex(binaryKey);
//                writer.write(String.format("Round %2d Key:\n", i + 1));
//                writer.write(String.format("Binary: %s\n", binaryKey));
//                writer.write(String.format("Hex:    %s\n\n", hexKey));
//            }
//            writer.write("DES Key Generation Completed");
//        } catch (IOException e) {
//            showAlert("Error saving keys: " + e.getMessage());
//        }
//    }
//
//    private void showAlert(String message) {
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle("Information");
//        alert.setHeaderText(null);
//        alert.setContentText(message);
//        alert.showAndWait();
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}
