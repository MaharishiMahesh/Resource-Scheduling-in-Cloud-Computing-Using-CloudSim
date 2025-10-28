package mp;

import java.io.*;
import java.util.Arrays;

public class MiniProject {
    private static double[][] commMatrix, execMatrix;
    private final File commFile = new File("CommunicationTimeMatrix.txt");
    private final File execFile = new File("ExecutionTimeMatrix.txt");

    public void GenerateMatrices() {
        commMatrix = new double[Constants.NO_OF_TASKS][Constants.NO_OF_DATA_CENTERS];
        execMatrix = new double[Constants.NO_OF_TASKS][Constants.NO_OF_DATA_CENTERS];
        try {
            if (commFile.exists() && execFile.exists()) {
                readCostMatrix();
            } else {
                initCostMatrix();
            }
        } catch (IOException e) {
            System.err.println("Error initializing or reading matrices: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initCostMatrix() throws IOException {
        System.out.println("Initializing new matrices...");
        try (BufferedWriter commBufferedWriter = new BufferedWriter(new FileWriter(commFile));
             BufferedWriter execBufferedWriter = new BufferedWriter(new FileWriter(execFile))) {

            for (int i = 0; i < Constants.NO_OF_TASKS; i++) {
                for (int j = 0; j < Constants.NO_OF_DATA_CENTERS; j++) {
                    commMatrix[i][j] = Math.random() * 600 + 20; // Random value between 20 and 620
                    execMatrix[i][j] = Math.random() * 500 + 10; // Random value between 10 and 510
                    commBufferedWriter.write(commMatrix[i][j] + " ");
                    execBufferedWriter.write(execMatrix[i][j] + " ");
                }
                commBufferedWriter.newLine();
                execBufferedWriter.newLine();
            }
        }
        System.out.println("Matrices initialized and saved to files.");
    }

    private void readCostMatrix() throws IOException {
        System.out.println("Reading matrices from files...");
        try (BufferedReader commBufferedReader = new BufferedReader(new FileReader(commFile));
             BufferedReader execBufferedReader = new BufferedReader(new FileReader(execFile))) {

            for (int i = 0; i < Constants.NO_OF_TASKS; i++) {
                String[] commValues = commBufferedReader.readLine().trim().split("\\s+");
                String[] execValues = execBufferedReader.readLine().trim().split("\\s+");

                if (commValues.length != Constants.NO_OF_DATA_CENTERS || execValues.length != Constants.NO_OF_DATA_CENTERS) {
                    throw new IOException("File data does not match expected matrix dimensions.");
                }

                for (int j = 0; j < Constants.NO_OF_DATA_CENTERS; j++) {
                    commMatrix[i][j] = Double.parseDouble(commValues[j]);
                    execMatrix[i][j] = Double.parseDouble(execValues[j]);
                }
            }
        }
        System.out.println("Matrices successfully read from files.");
    }

    public static double[][] getCommMatrix() {
        return commMatrix;
    }

    public static double[][] getExecMatrix() {
        return execMatrix;
    }

    // For debugging: Print a matrix
    public static void printMatrix(double[][] matrix) {
        for (double[] row : matrix) {
            System.out.println(Arrays.toString(row));
        }
    }
}
