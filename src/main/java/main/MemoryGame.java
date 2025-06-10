package main;

import static com.raylib.Raylib.DrawRectangle;
import static com.raylib.Raylib.DrawText;
import static com.raylib.Raylib.GetCharPressed;
import static com.raylib.Raylib.IsKeyPressed;
import static com.raylib.Raylib.KEY_BACKSPACE;
import static com.raylib.Raylib.MeasureText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.raylib.Raylib.Color;

public abstract class MemoryGame {
    protected boolean showStartScreen = true;
    protected boolean shouldReturnToTitle = false;
    protected boolean isGameOver = false;
    protected int score = 0;

    // --- End screen UI state (moved from child classes) ---
    protected String playerName = "";
    protected boolean showLeaderboard = false;
    protected String leaderboardText = "";
    protected Button saveScoreButton;
    protected Button viewLeaderboardButton;
    protected Button tryAgainButton;
    protected Button exitButton;
    protected boolean scoreSaved = false;
    protected String saveMessage = "";

    // --- End screen button colors (change here if needed) ---
    protected static final Color SAVE_SCORE_COLOR = com.raylib.Colors.SKYBLUE;
    protected static final Color LEADERBOARD_COLOR = com.raylib.Colors.LIGHTGRAY;
    protected static final Color TRY_AGAIN_COLOR = com.raylib.Colors.LIME;
    protected static final Color EXIT_COLOR = com.raylib.Colors.RED;
    protected static final Color BUTTON_TEXT_COLOR = com.raylib.Colors.DARKGRAY;

    public MemoryGame(int centerX) {
        // Initialize end screen buttons with fixed colors and positions
        saveScoreButton = new Button(centerX - 200, 350, 150, 50, SAVE_SCORE_COLOR, SAVE_SCORE_COLOR, SAVE_SCORE_COLOR);
        viewLeaderboardButton = new Button(centerX + 20, 350, 200, 50, LEADERBOARD_COLOR, LEADERBOARD_COLOR,
                LEADERBOARD_COLOR);
        tryAgainButton = new Button(centerX - 200, 420, 150, 50, TRY_AGAIN_COLOR, TRY_AGAIN_COLOR, TRY_AGAIN_COLOR);
        exitButton = new Button(centerX + 20, 420, 150, 50, EXIT_COLOR, EXIT_COLOR, EXIT_COLOR);
    }

    public abstract void drawScene();

    public abstract void updateScene();

    public abstract void processInputScene();

    public abstract void reset();

    public final boolean shouldReturnToTitle() {
        return shouldReturnToTitle;
    }

    public final boolean isGameOver() {
        return isGameOver;
    }

    public int getScore() {
        return score;
    }

    protected void drawEndScreen(int centerX, String gameOverText, Color gameOverColor, Color scoreColor,
            Color textColor,
            Color backgroundColor) {
        DrawText(gameOverText, centerX - MeasureText(gameOverText, 48) / 2, 120, 48, gameOverColor);
        DrawText("Your score: " + score, centerX - MeasureText("Your score: " + score, 32) / 2, 180, 32, scoreColor);
        DrawText("Enter your name to save score:", centerX - 200, 250, 28, textColor);
        DrawRectangle(centerX - 100, 290, 200, 40, backgroundColor);
        DrawText(playerName, centerX - 95, 295, 28, textColor);
        saveScoreButton.setText("Save Score", 24, backgroundColor);
        saveScoreButton.draw();
        viewLeaderboardButton.setText("View Leaderboard", 24, backgroundColor);
        viewLeaderboardButton.draw();
        tryAgainButton.setText("Try Again", 24, backgroundColor);
        tryAgainButton.draw();
        exitButton.setText("Exit", 24, backgroundColor);
        exitButton.draw();
        if (!saveMessage.isEmpty()) {
            DrawText(saveMessage, centerX - MeasureText(saveMessage, 24) / 2, 480, 24, scoreColor);
        }
        if (showLeaderboard) {
            DrawRectangle(centerX - 250, 520, 500, 200, backgroundColor);
            DrawText("Leaderboard:", centerX - 240, 530, 24, textColor);
            DrawText(leaderboardText, centerX - 240, 560, 22, textColor);
        }
    }

    protected void updateEndScreenButtons() {
        saveScoreButton.update();
        viewLeaderboardButton.update();
        tryAgainButton.update();
        exitButton.update();
    }

    protected void handleEndScreenButtonActions() {
        if (saveScoreButton.isClicked()) {
            onSaveScore();
        }
        if (viewLeaderboardButton.isClicked()) {
            onViewLeaderboard();
        }
        if (tryAgainButton.isClicked()) {
            onTryAgain();
        }
        if (exitButton.isClicked()) {
            onExit();
        }
    }

    // These can be overridden by subclasses for custom behavior
    protected void onSaveScore() {
        if (playerName.trim().isEmpty()) {
            saveMessage = "Enter your name!";
            return;
        }
        if (scoreSaved) {
            saveMessage = "Score already saved!";
            return;
        }
        String className = this.getClass().getSimpleName();
        String fileName = "src/main/java/main/Leaderboards/" + className + ".txt";
        List<String> lines = new ArrayList<>();
        boolean nameFound = false;
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            // File may not exist yet, that's fine
        }
        List<String> updatedLines = new ArrayList<>();
        for (String l : lines) {
            String[] parts = l.split(":");
            if (parts.length == 2 && parts[0].equals(playerName)) {
                nameFound = true;
                int oldScore;
                try {
                    oldScore = Integer.parseInt(parts[1]);
                } catch (NumberFormatException ex) {
                    oldScore = 0;
                }
                // Only update if new score is higher
                if (score > oldScore) {
                    updatedLines.add(playerName + ":" + score);
                } else {
                    updatedLines.add(l);
                }
            } else {
                updatedLines.add(l);
            }
        }
        if (!nameFound) {
            updatedLines.add(playerName + ":" + score);
        }
        try (FileWriter fw = new FileWriter(fileName, false); BufferedWriter bw = new BufferedWriter(fw)) {
            for (String l : updatedLines) {
                bw.write(l + "\n");
            }
            scoreSaved = true;
            saveMessage = "Score saved!";
        } catch (IOException e) {
            saveMessage = "Error saving score.";
        }
    }

    protected void onViewLeaderboard() {
        String className = this.getClass().getSimpleName();
        String fileName = "src/main/java/main/Leaderboards/" + className + ".txt";
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            leaderboardText = "No scores yet.";
            showLeaderboard = true;
            return;
        }
        // Parse and sort scores
        List<Map.Entry<String, Integer>> scores = new ArrayList<>();
        for (String l : lines) {
            String[] parts = l.split(":");
            if (parts.length == 2) {
                try {
                    scores.add(new AbstractMap.SimpleEntry<>(parts[0], Integer.parseInt(parts[1])));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        scores.sort((a, b) -> b.getValue() - a.getValue());
        StringBuilder sb = new StringBuilder();
        int rank = 1;
        for (Map.Entry<String, Integer> entry : scores) {
            sb.append(rank++).append(". ").append(entry.getKey()).append(" - ").append(entry.getValue()).append("\n");
            if (rank > 10)
                break;
        }
        leaderboardText = sb.length() > 0 ? sb.toString() : "No scores yet.";
        showLeaderboard = true;
    }

    protected void onTryAgain() {
        reset();
        showLeaderboard = false;
        scoreSaved = false;
        saveMessage = "";
        playerName = "";
    }

    protected void onExit() {
        shouldReturnToTitle = true;
    }

    // Call this in processInputScene when showEndScreen is true
    protected void handleNameInput() {
        int key = GetCharPressed();
        while (key > 0) {
            if ((key >= 32) && (key <= 125) && playerName.length() < 16) {
                playerName += (char) key;
            }
            key = GetCharPressed();
        }
        if (IsKeyPressed(KEY_BACKSPACE) && playerName.length() > 0) {
            playerName = playerName.substring(0, playerName.length() - 1);
        }
    }
}
