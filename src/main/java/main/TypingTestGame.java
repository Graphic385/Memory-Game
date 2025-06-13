package main;

import static com.raylib.Raylib.*;
import static com.raylib.Raylib.GetScreenWidth;
import static com.raylib.Raylib.GetScreenHeight;
import static com.raylib.Raylib.MeasureText;
import static com.raylib.Raylib.DrawText;
import static com.raylib.Raylib.DrawRectangle;
import static com.raylib.Raylib.GetCharPressed;
import static com.raylib.Raylib.IsKeyPressed;
import static com.raylib.Raylib.KEY_ENTER;
import static com.raylib.Raylib.KEY_BACKSPACE;

import com.raylib.Raylib.Color;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TypingTestGame extends MemoryGame {
    private enum State {
        TITLE, WAITING, RUNNING, END
    }

    private State state = State.TITLE;
    private List<String> wordList = new ArrayList<>();
    private List<String> testWords = new ArrayList<>();
    private int wordIndex = 0;
    private String userInput = "";
    private long startTime = 0;
    private long elapsedTime = 0;
    private int testDuration = 15; // seconds
    private int visibleWords = 12;
    private Color background = new Color().r((byte) 62).g((byte) 136).b((byte) 210).a((byte) 255);
    private Color black = new Color().r((byte) 0).g((byte) 0).b((byte) 0).a((byte) 255);
    private Color green = new Color().r((byte) 0).g((byte) 180).b((byte) 0).a((byte) 255);
    private Color red = new Color().r((byte) 200).g((byte) 0).b((byte) 0).a((byte) 255);
    private Random random = new Random();
    private int correctCharCount = 0;
    private int incorrectCharCount = 0;

    public TypingTestGame() {
        super(GetScreenWidth() / 2);
        loadWordsFromFile();
        reset();
    }

    private void loadWordsFromFile() {
        wordList.clear();
        try (BufferedReader br = new BufferedReader(new FileReader("resources/10000-english-no-swears.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    wordList.add(line.trim());
                }
            }
        } catch (IOException e) {
            wordList.add("default");
        }
    }

    private void generateTestWords() {
        testWords.clear();
        for (int i = 0; i < 100; i++) {
            testWords.add(wordList.get(random.nextInt(wordList.size())));
        }
        wordIndex = 0;
        userInput = "";
    }

    @Override
    public void reset() {
        state = State.TITLE;
        shouldReturnToTitle = false;
        isGameOver = false;
        score = 0;
        userInput = "";
        elapsedTime = 0;
        startTime = 0;
        generateTestWords();
        playerName = "";
        saveMessage = "";
        scoreSaved = false;
        showLeaderboard = false;
        correctCharCount = 0;
        incorrectCharCount = 0;
    }

    @Override
    public void drawScene() {
        BeginDrawing();
        ClearBackground(background);
        int centerX = GetScreenWidth() / 2;
        int centerY = GetScreenHeight() / 2;
        if (state == State.TITLE) {
            DrawText("Typing Test", centerX - MeasureText("Typing Test", 48) / 2, 120, 48, black);
            DrawText("Type as fast as you can!", centerX - MeasureText("Type as fast as you can!", 28) / 2, 200, 28,
                    black);
            DrawText("Press ENTER to start", centerX - MeasureText("Press ENTER to start", 22) / 2, 270, 22, black);
        } else if (state == State.WAITING) {
            DrawText("Start typing to begin!", centerX - MeasureText("Start typing to begin!", 32) / 2, 200, 32, black);
            drawWords(centerX, centerY);
        } else if (state == State.RUNNING) {
            long now = System.currentTimeMillis();
            elapsedTime = (now - startTime) / 1000;
            int timeLeft = Math.max(0, testDuration - (int) elapsedTime);
            DrawText("Time: " + timeLeft, 40, 40, 32, black);
            drawWords(centerX, centerY);
            if (timeLeft <= 0) {
                state = State.END;
                isGameOver = true;
                score = wordIndex;
            }
        } else if (state == State.END) {
            drawEndScreen(centerX, "Time's up!", background);
        }
        EndDrawing();
    }

    private void drawWords(int centerX, int centerY) {
        int y = centerY - 80;
        int fontSize = 36;
        int wordsPerLine = 6;
        int xStart = centerX - 350;
        int line = 0;
        int x = xStart;
        for (int i = 0; i < visibleWords && wordIndex + i < testWords.size(); i++) {
            String word = testWords.get(wordIndex + i);
            int wordWidth = MeasureText(word, fontSize) + 30; // Add padding between words
            if (i % wordsPerLine == 0 && i != 0) {
                line++;
                x = xStart;
            }
            int yOffset = y + line * 50;
            if (i == 0) {
                drawWordWithInput(word, userInput, x, yOffset, fontSize);
            } else {
                DrawText(word, x, yOffset, fontSize, black);
            }
            x += wordWidth;
        }
    }

    private void drawWordWithInput(String target, String input, int x, int y, int fontSize) {
        int offset = 0;
        int len = Math.max(target.length(), input.length());
        int charSpacing = 4; // Add extra space between characters
        for (int i = 0; i < len; i++) {
            char c = (i < target.length()) ? target.charAt(i) : ' ';
            Color color = black;
            if (i < input.length()) {
                if (i < target.length() && input.charAt(i) == c) {
                    color = green;
                } else {
                    color = red;
                }
            }
            String s = (i < target.length()) ? String.valueOf(c) : " ";
            DrawText(s, x + offset, y, fontSize, color);
            offset += MeasureText(s, fontSize) + charSpacing;
        }
    }

    @Override
    public void updateScene() {
        // No-op, handled in drawScene for timer
    }

    @Override
    public void processInputScene() {
        if (state == State.TITLE) {
            if (IsKeyPressed(KEY_ENTER)) {
                state = State.WAITING;
                userInput = "";
                wordIndex = 0;
                generateTestWords();
            }
        } else if (state == State.WAITING) {
            int key = GetCharPressed();
            if (key > 0) {
                state = State.RUNNING;
                startTime = System.currentTimeMillis();
                userInput += (char) key;
            }
        } else if (state == State.RUNNING) {
            int key = GetCharPressed();
            while (key > 0) {
                if (key == ' ') {
                    String target = testWords.get(wordIndex);
                    int minLen = Math.min(userInput.length(), target.length());
                    for (int i = 0; i < minLen; i++) {
                        if (userInput.charAt(i) == target.charAt(i)) {
                            correctCharCount++;
                        } else {
                            incorrectCharCount++;
                        }
                    }
                    // Count extra chars as incorrect
                    if (userInput.length() > target.length()) {
                        incorrectCharCount += userInput.length() - target.length();
                    } else if (target.length() > userInput.length()) {
                        incorrectCharCount += target.length() - userInput.length();
                    }
                    wordIndex++;
                    userInput = "";
                    if (testWords.size() - wordIndex < visibleWords) {
                        for (int i = 0; i < 10; i++) {
                            testWords.add(wordList.get(random.nextInt(wordList.size())));
                        }
                    }
                } else if (key >= 32 && key <= 125) {
                    userInput += (char) key;
                }
                key = GetCharPressed();
            }
            // Handle backspace using IsKeyPressed for reliability
            if (IsKeyPressed(KEY_BACKSPACE) && userInput.length() > 0) {
                userInput = userInput.substring(0, userInput.length() - 1);
            }
        } else if (state == State.END) {
            handleNameInput();
            updateEndScreenButtons();
            handleEndScreenButtonActions();
        }
    }

    @Override
    protected void drawEndScreen(int centerX, String message, Color bg) {
        Color textColor = black;
        int totalTime = testDuration; // seconds
        float minutes = totalTime / 60.0f;
        int wpm = (int) (correctCharCount / 5.0f / minutes);
        int totalTyped = correctCharCount + incorrectCharCount;
        int accuracy = totalTyped > 0 ? (int) Math.round(100.0 * correctCharCount / totalTyped) : 100;
        int rawWpm = (int) (totalTyped / 5.0f / minutes);
        DrawText("Game Over", centerX - MeasureText("Game Over", 48) / 2, 120, 48, textColor);
        String wpmText = "Your WPM is " + wpm + " with " + accuracy + "% accuracy | Raw WPM is " + rawWpm;
        DrawText(wpmText, centerX - MeasureText(wpmText, 32) / 2, 180, 32, textColor);
        DrawText("Enter your name to save score:", centerX - 200, 250, 28, textColor);
        DrawRectangleRoundedLines(new com.raylib.Raylib.Rectangle().x(centerX - 110).y(290).width(280).height(40), 0.8f,
                20, textColor);
        DrawText(playerName, centerX - 95, 295, 28, textColor);
        int textWidth = MeasureText(playerName, 28);
        int cursorX = centerX - 95 + textWidth;
        int cursorY = 295;
        int cursorHeight = 28;
        if (((int) (System.currentTimeMillis() / 500) % 2) == 0 && playerName.length() < 16) {
            DrawRectangle(cursorX, cursorY, 2, cursorHeight, textColor);
        }
        saveScoreButton.setText("Save Score", 24, bg);
        saveScoreButton.draw();
        viewLeaderboardButton.setText("View Leaderboard", 24, bg);
        viewLeaderboardButton.draw();
        tryAgainButton.setText("Try Again", 24, bg);
        tryAgainButton.draw();
        exitButton.setText("Exit", 24, bg);
        exitButton.draw();
        if (!saveMessage.isEmpty()) {
            DrawText(saveMessage, centerX - MeasureText(saveMessage, 24) / 2, 480, 24, textColor);
        }
        if (showLeaderboard) {
            DrawRectangle(centerX - 250, 520, 500, 200, bg);
            DrawText("Leaderboard:", centerX - 240, 530, 24, textColor);
            DrawText(leaderboardText, centerX - 240, 560, 22, textColor);
        }
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
        int totalTime = testDuration; // seconds
        float minutes = totalTime / 60.0f;
        int wpm = (int) (correctCharCount / 5.0f / minutes);
        String className = this.getClass().getSimpleName();
        String fileName = "src/main/java/main/Leaderboards/" + className + ".txt";
        java.io.File file = new java.io.File(fileName);
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (java.io.IOException e) {
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
                    String[] parts = line.split(":");
                    if (parts.length == 2 && parts[0].equals(playerName)) {
                        int oldScore = Integer.parseInt(parts[1]);
                        found = true;
                        if (wpm > oldScore) {
                            lines.add(playerName + ":" + wpm);
                            updated = true;
                        } else {
                            lines.add(line);
                        }
                    } else {
                        lines.add(line);
                    }
                }
            }
            if (!found) {
                lines.add(playerName + ":" + wpm);
                updated = true;
            }
            try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter(fileName, false))) {
                for (String l : lines) {
                    bw.write(l + "\n");
                }
            }
            scoreSaved = true;
            if (found && updated) {
                saveMessage = "Score updated!";
            } else if (!found) {
                saveMessage = "Score saved!";
            } else {
                saveMessage = "Existing score is higher or equal.";
            }
        } catch (java.io.IOException | NumberFormatException e) {
            saveMessage = "Error saving score.";
        }
    }

    @Override
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
        List<java.util.Map.Entry<String, Integer>> scores = new ArrayList<>();
        for (String l : lines) {
            String[] parts = l.split(":");
            if (parts.length == 2) {
                try {
                    scores.add(new java.util.AbstractMap.SimpleEntry<>(parts[0], Integer.parseInt(parts[1])));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        scores.sort((a, b) -> b.getValue() - a.getValue());
        StringBuilder sb = new StringBuilder();
        int rank = 1;
        for (java.util.Map.Entry<String, Integer> entry : scores) {
            sb.append(rank++)
                    .append(". ")
                    .append(entry.getKey())
                    .append(" - ")
                    .append(entry.getValue())
                    .append(" WPM\n");
            if (rank > 10)
                break;
        }
        leaderboardText = sb.length() > 0 ? sb.toString() : "No scores yet.";
        showLeaderboard = true;
    }
}
