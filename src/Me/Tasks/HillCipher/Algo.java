package Me.Tasks.HillCipher;

import java.util.Arrays;

public class Algo {
    private final int[][] keyMatrix;
    private final int matrixSize;

    public Algo(int[][] keyMatrix) {
        validateKeyMatrix(keyMatrix);
        this.keyMatrix = keyMatrix;
        this.matrixSize = keyMatrix.length;
    }

    public String encrypt(String plaintext) {
        return processText(plaintext, true);
    }

    public String decrypt(String ciphertext) {
        return processText(ciphertext, false);
    }

    public String processText(String text, boolean encrypt) {
        String processedText = prepareText(text);
        int[] numbers = textToNumbers(processedText);
        int[] resultNumbers = new int[numbers.length];

        int[][] workingMatrix = encrypt ? keyMatrix : getInverseMatrix();

        for (int i = 0; i < numbers.length; i += matrixSize) {
            int[] block = processBlock(numbers, i, workingMatrix);
            System.arraycopy(block, 0, resultNumbers, i, Math.min(matrixSize, numbers.length - i));
        }

        return numbersToText(resultNumbers, text);
    }

    private int[] processBlock(int[] numbers, int startIndex, int[][] matrix) {
        int[] block = Arrays.copyOfRange(numbers, startIndex, Math.min(startIndex + matrixSize, numbers.length));

        if (block.length < matrixSize) {
            block = padBlock(block);
        }

        return multiplyWithMatrix(block, matrix);
    }

    private int[] padBlock(int[] block) {
        int[] paddedBlock = new int[matrixSize];
        System.arraycopy(block, 0, paddedBlock, 0, block.length);
        Arrays.fill(paddedBlock, block.length, matrixSize, 'X' - 'A');
        return paddedBlock;
    }

    private String prepareText(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                sb.append(c);
            }
        }
        int padding = (matrixSize - (sb.length() % matrixSize)) % matrixSize;
        sb.append("X".repeat(padding));
        return sb.toString();
    }

    private int[] textToNumbers(String text) {
        return text.toUpperCase().chars()
                .map(c -> c - 'A')
                .toArray();
    }

    private String numbersToText(int[] numbers, String originalText) {
        StringBuilder result = new StringBuilder();
        int letterIndex = 0;

        for (int i = 0; i < originalText.length(); i++) {
            char originalChar = originalText.charAt(i);
            if (Character.isLetter(originalChar)) {
                if (letterIndex >= numbers.length) break;
                char newChar = (char) ((Character.isUpperCase(originalChar) ? 'A' : 'a') + numbers[letterIndex]);
                result.append(newChar);
                letterIndex++;
            } else {
                result.append(originalChar);
            }
        }

        while (letterIndex < numbers.length) {
            result.append((char) ('A' + numbers[letterIndex++]));
        }

        return result.toString();
    }

    private int[] multiplyWithMatrix(int[] vector, int[][] matrix) {
        int[] result = new int[matrixSize];
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                result[i] += matrix[i][j] * vector[j];
            }
            result[i] = Math.floorMod(result[i], 26);
        }
        return result;
    }

    private int[][] getInverseMatrix() {
        int det = Math.floorMod(calculateDeterminant(keyMatrix), 26);
        int detInverse = findModularInverse(det, 26);
        int[][] adjugate = calculateAdjugate(keyMatrix);

        int[][] inverse = new int[matrixSize][matrixSize];
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                inverse[i][j] = Math.floorMod(adjugate[i][j] * detInverse, 26);
            }
        }
        return inverse;
    }

    private int calculateDeterminant(int[][] matrix) {
        if (matrix.length == 1) return matrix[0][0];
        if (matrix.length == 2) {
            return Math.floorMod(matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0], 26);
        }

        int det = 0;
        for (int i = 0; i < matrix.length; i++) {
            int[][] minor = createMinor(matrix, 0, i);
            int sign = (i % 2 == 0) ? 1 : -1;
            det = Math.floorMod(det + sign * matrix[0][i] * calculateDeterminant(minor), 26);
        }
        return det;
    }

    private int[][] calculateAdjugate(int[][] matrix) {
        int[][] adjugate = new int[matrixSize][matrixSize];
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                int[][] minor = createMinor(matrix, i, j);
                int cofactor = Math.floorMod(((i + j) % 2 == 0 ? 1 : -1) * calculateDeterminant(minor), 26);
                adjugate[j][i] = cofactor;
            }
        }
        return adjugate;
    }

    private int[][] createMinor(int[][] matrix, int row, int col) {
        int[][] minor = new int[matrix.length-1][matrix.length-1];
        int minorRow = 0;
        for (int i = 0; i < matrix.length; i++) {
            if (i == row) continue;
            int minorCol = 0;
            for (int j = 0; j < matrix.length; j++) {
                if (j == col) continue;
                minor[minorRow][minorCol++] = matrix[i][j];
            }
            minorRow++;
        }
        return minor;
    }

    private int findModularInverse(int a, int m) {
        a = Math.floorMod(a, m);
        for (int x = 1; x < m; x++) {
            if (Math.floorMod(a * x, m) == 1) return x;
        }
        throw new ArithmeticException("Modular inverse doesn't exist");
    }

    private void validateKeyMatrix(int[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix.length != matrix[0].length) {
            throw new IllegalArgumentException("Key matrix must be square (n×n)");
        }
        try {
            findModularInverse(Math.floorMod(calculateDeterminant(matrix), 26), 26);
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("Key matrix determinant must be coprime with 26");
        }
    }

    public static int[][] parseKeyMatrix(String keyString) {
        String[] parts = keyString.trim().split("[ ,]+");
        int size = (int) Math.sqrt(parts.length);
        if (size * size != parts.length) {
            throw new IllegalArgumentException("Key must form a square matrix (n×n values)");
        }

        int[][] matrix = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = Math.floorMod(Integer.parseInt(parts[i * size + j]), 26);
            }
        }
        return matrix;
    }
}