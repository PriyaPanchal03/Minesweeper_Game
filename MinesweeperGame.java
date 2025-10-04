import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.scene.control.*;
import javafx.application.Platform;

import java.io.*;
import java.util.*;

public class MinesweeperGame {

    int numRows, numCols, mineCount;
    int tilesClicked = 0;
    int secondsPassed = 0;
    boolean gameOver = false;

    MineTile[][] board;
    ArrayList<MineTile> mineList = new ArrayList<>();
    Thread timerThread;

    BorderPane root;
    Text titleText, timerText;

    private final String FILE_NAME = "best_time.txt";

    public MinesweeperGame(int numRows, int numCols, int mineCount,
                           BorderPane root, Text titleText, Text timerText) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.mineCount = mineCount;
        this.root = root;
        this.titleText = titleText;
        this.timerText = timerText;
        this.board = new MineTile[numRows][numCols];
    }

    public void setupUI() {
        VBox topBox = new VBox(5, titleText, timerText);
        topBox.setAlignment(javafx.geometry.Pos.CENTER);
        root.setTop(topBox);

        GridPane grid = new GridPane();
        grid.setHgap(2);
        grid.setVgap(2);
        grid.setAlignment(javafx.geometry.Pos.CENTER);

        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                MineTile tile = new MineTile(r, c, this);
                board[r][c] = tile;
                grid.add(tile, c, r);
            }
        }
        root.setCenter(grid);

        setMines();
    }

    private void setMines() {
        Random random = new Random();
        while (mineList.size() < mineCount) {
            int r = random.nextInt(numRows);
            int c = random.nextInt(numCols);
            if (!mineList.contains(board[r][c])) mineList.add(board[r][c]);
        }
    }

    // ---------------- Game Logic ----------------
    public void revealMines() {
        for (MineTile t : mineList) t.setText("💣");
        gameOver = true;
        stopTimer();
        titleText.setText("💥 Game Over! 💥");
    }

    public void checkMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) return;

        MineTile tile = board[r][c];
        if (tile.isDisabled()) return;

        tile.setDisable(true);
        tilesClicked++;

        int mines = countMinesAround(r, c);
        if (mines > 0) {
            tile.setText(Integer.toString(mines));
        } else {
            for (int i = r - 1; i <= r + 1; i++)
                for (int j = c - 1; j <= c + 1; j++)
                    if (i >= 0 && i < numRows && j >= 0 && j < numCols)
                        checkMine(i, j);
        }

        if (tilesClicked == numRows * numCols - mineCount) {
            gameOver = true;
            stopTimer();
            titleText.setText("🎉 You Won in " + secondsPassed + "s!");
            saveBestTime();
        }
    }

    private int countMinesAround(int r, int c) {
        int count = 0;
        for (int i = r - 1; i <= r + 1; i++)
            for (int j = c - 1; j <= c + 1; j++)
                if (i >= 0 && i < numRows && j >= 0 && j < numCols)
                    if (mineList.contains(board[i][j])) count++;
        return count;
    }

    // ---------------- Timer ----------------
    public void startTimer() {
        timerThread = new Thread(() -> {
            try {
                while (!gameOver) {
                    Thread.sleep(1000);
                    secondsPassed++;
                    Platform.runLater(() -> timerText.setText("⏱ Time: " + secondsPassed + "s"));
                }
            } catch (InterruptedException e) {
                showError("Timer interrupted!");
            }
        });
        timerThread.setDaemon(true);
        timerThread.start();
    }

    public void stopTimer() {
        gameOver = true;
        if (timerThread != null && timerThread.isAlive()) timerThread.interrupt();
    }

    // ---------------- File I/O ----------------
    private void saveBestTime() {
        try {
            int bestTime = readBestTime();
            if (bestTime == -1 || secondsPassed < bestTime) {
                try (FileWriter fw = new FileWriter(FILE_NAME)) {
                    fw.write(Integer.toString(secondsPassed));
                }
                titleText.setText("🏆 New Best Time: " + secondsPassed + "s!");
            } else {
                titleText.setText("✅ Completed in " + secondsPassed + "s (Best: " + bestTime + "s)");
            }
        } catch (IOException e) {
            showError("Error saving score: " + e.getMessage());
        }
    }

    private int readBestTime() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            return Integer.parseInt(br.readLine());
        } catch (FileNotFoundException e) {
            return -1;
        } catch (Exception e) {
            showError("Error reading best time: " + e.getMessage());
            return -1;
        }
    }

    public void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Something went wrong!");
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
