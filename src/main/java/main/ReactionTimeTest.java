package main;

import static com.raylib.Raylib.*;
import static com.raylib.Raylib.IsMouseButtonPressed;
import static com.raylib.Raylib.MOUSE_BUTTON_LEFT;

import com.raylib.Raylib.Color;

public class ReactionTimeTest extends MemoryGame {
    private Color background = new Color().r((byte) 62).g((byte) 136).b((byte) 210).a((byte) 255);
    private Color boxRed = com.raylib.Colors.RED;
    private Color boxGreen = com.raylib.Colors.LIME;
    private boolean showStartScreen = true;
    private boolean isGameOver = false;
    private boolean waitingForGreen = false;
    private boolean boxIsGreen = false;
    private long greenTime = 0;
    private long reactionTime = 0;
    private long waitStart = 0;
    private long waitDuration = 0;
    private boolean clickedEarly = false;
    private int trialCount = 0;
    private static final int TOTAL_TRIALS = 5;
    private long[] trialTimes = new long[TOTAL_TRIALS];
    private boolean showTrialResult = false;
    private String trialMessage = "";
    private boolean showTooSoon = false;

    public ReactionTimeTest() {
        super((int) (GetScreenWidth() / 2));
    }

    @Override
    public void drawScene() {
        BeginDrawing();
        Color bg = background;
        if (!showStartScreen && !isGameOver) {
            if (waitingForGreen) {
                bg = boxRed;
            } else if (boxIsGreen) {
                bg = boxGreen;
            } else if (showTrialResult || showTooSoon) {
                bg = background;
            }
        }
        ClearBackground(bg);
        int windowWidth = GetScreenWidth();
        int windowHeight = GetScreenHeight();
        if (showStartScreen) {
            DrawText("Reaction Time Test", windowWidth / 2 - MeasureText("Reaction Time Test", 40) / 2, 100, 40,
                    com.raylib.Colors.BLACK);
            DrawText("When the background turns green, click as quickly as you can.",
                    windowWidth / 2
                            - MeasureText("When the background turns green, click as quickly as you can.", 24) / 2,
                    200, 24, com.raylib.Colors.BLACK);
            DrawText("Click to start", windowWidth / 2 - MeasureText("Click to start", 28) / 2, 350, 28,
                    com.raylib.Colors.BLACK);
        } else if (isGameOver) {
            // Custom end screen: patch score line to always show ms
            int centerX = windowWidth / 2;
            Color textColor = com.raylib.Colors.BLACK;
            String gameOverText = "All trials complete!";
            DrawText(gameOverText, centerX - MeasureText(gameOverText, 48) / 2, 120, 48, textColor);
            String scoreText = "Your score: " + score + " ms";
            DrawText(scoreText, centerX - MeasureText(scoreText, 32) / 2, 180, 32, textColor);
            DrawText("Enter your name to save score:", centerX - 200, 250, 28, textColor);
            com.raylib.Raylib.Rectangle nameRect = new com.raylib.Raylib.Rectangle();
            nameRect.x(centerX - 110);
            nameRect.y(290);
            nameRect.width(280);
            nameRect.height(40);
            DrawRectangleRoundedLines(nameRect, 0.8f, 20, com.raylib.Colors.BLACK);
            DrawText(playerName, centerX - 95, 295, 28, textColor);
            int textWidth = MeasureText(playerName, 28);
            int cursorX = centerX - 95 + textWidth;
            int cursorY = 295;
            int cursorHeight = 28;
            if (((int) (System.currentTimeMillis() / 500) % 2) == 0 && playerName.length() < 16) {
                DrawRectangle(cursorX, cursorY, 2, cursorHeight, textColor);
            }
            saveScoreButton.setText("Save Score", 24, background);
            saveScoreButton.draw();
            viewLeaderboardButton.setText("View Leaderboard", 24, background);
            viewLeaderboardButton.draw();
            tryAgainButton.setText("Try Again", 24, background);
            tryAgainButton.draw();
            exitButton.setText("Exit", 24, background);
            exitButton.draw();
            if (!saveMessage.isEmpty()) {
                DrawText(saveMessage, centerX - MeasureText(saveMessage, 24) / 2, 480, 24, textColor);
            }
            if (showLeaderboard) {
                DrawRectangle(centerX - 250, 520, 500, 200, background);
                DrawText("Leaderboard:", centerX - 240, 530, 24, textColor);
                DrawText(leaderboardText, centerX - 240, 560, 22, textColor);
            }
        } else if (showTrialResult) {
            Color textColor = com.raylib.Colors.BLACK;
            DrawText(trialMessage, windowWidth / 2 - MeasureText(trialMessage, 32) / 2, windowHeight / 2 - 16, 32,
                    textColor);
            DrawText("Click to continue", windowWidth / 2 - MeasureText("Click to continue", 24) / 2,
                    windowHeight / 2 + 40, 24, textColor);
        } else if (showTooSoon) {
            Color textColor = com.raylib.Colors.BLACK;
            DrawText("Too soon! Try again", windowWidth / 2 - MeasureText("Too soon! Try again", 32) / 2,
                    windowHeight / 2 - 16, 32, textColor);
            DrawText("Click to continue", windowWidth / 2 - MeasureText("Click to continue", 24) / 2,
                    windowHeight / 2 + 40, 24, textColor);
        }
        EndDrawing();
    }

    @Override
    public void updateScene() {
        if (showStartScreen || isGameOver || showTrialResult || showTooSoon)
            return;
        if (waitingForGreen) {
            if (System.currentTimeMillis() - waitStart >= waitDuration) {
                waitingForGreen = false;
                boxIsGreen = true;
                greenTime = System.currentTimeMillis();
            }
        }
    }

    @Override
    public void processInputScene() {
        if (showStartScreen) {
            if (IsMouseButtonPressed(MOUSE_BUTTON_LEFT)) {
                trialCount = 0;
                for (int i = 0; i < TOTAL_TRIALS; i++)
                    trialTimes[i] = 0;
                showStartScreen = false;
                isGameOver = false;
                showTooSoon = false;
                startTest();
            }
        } else if (isGameOver) {
            handleNameInput();
            updateEndScreenButtons();
            handleEndScreenButtonActions();
        } else if (showTrialResult) {
            if (IsMouseButtonPressed(MOUSE_BUTTON_LEFT)) {
                showTrialResult = false;
                if (trialCount < TOTAL_TRIALS) {
                    startTest();
                } else {
                    // Calculate average and set as score for end screen
                    long sum = 0;
                    for (int i = 0; i < TOTAL_TRIALS; i++)
                        sum += trialTimes[i];
                    score = (int) (sum / TOTAL_TRIALS);
                    isGameOver = true;
                }
            }
        } else if (showTooSoon) {
            if (IsMouseButtonPressed(MOUSE_BUTTON_LEFT)) {
                showTooSoon = false;
                clickedEarly = false;
                startTest();
            }
        } else {
            if (waitingForGreen && !clickedEarly && IsMouseButtonPressed(MOUSE_BUTTON_LEFT)) {
                showTooSoon = true;
                waitingForGreen = false;
                boxIsGreen = false;
            } else if (boxIsGreen && IsMouseButtonPressed(MOUSE_BUTTON_LEFT)) {
                reactionTime = System.currentTimeMillis() - greenTime;
                trialTimes[trialCount] = reactionTime;
                trialCount++;
                trialMessage = "Trial " + trialCount + ": " + reactionTime + " ms";
                showTrialResult = true;
                boxIsGreen = false;
                clickedEarly = false;
            }
        }
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
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
        String fileName = "leaderboards/" + className + ".txt";
        java.io.File file = new java.io.File(fileName);
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (Exception e) {
            saveMessage = "Error creating leaderboard file.";
            return;
        }
        java.util.List<String> lines = new java.util.ArrayList<>();
        boolean found = false;
        boolean updated = false;
        try {
            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(fileName))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty())
                        continue;
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        String name = parts[0].trim();
                        int s = 0;
                        try {
                            s = Integer.parseInt(parts[1].replaceAll("[^0-9]", ""));
                        } catch (NumberFormatException e) {
                            continue;
                        }
                        if (name.equalsIgnoreCase(playerName)) {
                            found = true;
                            if (score < s) {
                                lines.add(playerName + ": " + score + " ms");
                                updated = true;
                            } else {
                                lines.add(line);
                            }
                        } else {
                            lines.add(line);
                        }
                    }
                }
            }
            if (!found) {
                lines.add(playerName + ": " + score + " ms");
            } else if (!updated) {
                // Already added best score
            }
            // Sort ascending (lower is better)
            java.util.Collections.sort(lines, (a, b) -> {
                int sa = Integer.parseInt(a.replaceAll("[^0-9]", ""));
                int sb = Integer.parseInt(b.replaceAll("[^0-9]", ""));
                return Integer.compare(sa, sb);
            });
            try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter(fileName))) {
                for (String l : lines) {
                    pw.println(l);
                }
            }
            saveMessage = "Score saved!";
            scoreSaved = true;
        } catch (Exception e) {
            saveMessage = "Error saving score.";
        }
    }

    @Override
    protected void onViewLeaderboard() {
        String className = this.getClass().getSimpleName();
        String fileName = "leaderboards/" + className + ".txt";
        java.io.File file = new java.io.File(fileName);
        java.util.List<String> lines = new java.util.ArrayList<>();
        try {
            if (file.exists()) {
                try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(fileName))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line.trim().isEmpty())
                            continue;
                        lines.add(line);
                    }
                }
                // Sort ascending (lower is better)
                java.util.Collections.sort(lines, (a, b) -> {
                    int sa = Integer.parseInt(a.replaceAll("[^0-9]", ""));
                    int sb = Integer.parseInt(b.replaceAll("[^0-9]", ""));
                    return Integer.compare(sa, sb);
                });
                StringBuilder sb = new StringBuilder();
                int rank = 1;
                for (String l : lines) {
                    sb.append(rank).append(". ").append(l).append("\n");
                    rank++;
                }
                leaderboardText = sb.toString();
            } else {
                leaderboardText = "No scores yet.";
            }
            showLeaderboard = true;
        } catch (Exception e) {
            leaderboardText = "Error reading leaderboard.";
            showLeaderboard = true;
        }
    }

    @Override
    public void reset() {
        showStartScreen = true;
        isGameOver = false;
        waitingForGreen = false;
        boxIsGreen = false;
        greenTime = 0;
        reactionTime = 0;
        waitStart = 0;
        waitDuration = 0;
        clickedEarly = false;
        trialCount = 0;
        for (int i = 0; i < TOTAL_TRIALS; i++)
            trialTimes[i] = 0;
        showTrialResult = false;
        trialMessage = "";
        showTooSoon = false;
        playerName = "";
        saveMessage = "";
        scoreSaved = false;
        showLeaderboard = false;
        leaderboardText = "";
        shouldReturnToTitle = false;
    }

    private void startTest() {
        waitingForGreen = true;
        boxIsGreen = false;
        showTrialResult = false;
        showTooSoon = false;
        waitStart = System.currentTimeMillis();
        waitDuration = 1000 + (long) (Math.random() * 2000); // 1-3 seconds
    }
}
