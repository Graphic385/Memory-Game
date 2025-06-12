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
    private int testDuration = 60; // seconds
    private int visibleWords = 12;
    private Color background = new Color().r((byte) 62).g((byte) 136).b((byte) 210).a((byte) 255);
    private Color black = new Color().r((byte) 0).g((byte) 0).b((byte) 0).a((byte) 255);
    private Color green = new Color().r((byte) 0).g((byte) 180).b((byte) 0).a((byte) 255);
    private Color red = new Color().r((byte) 200).g((byte) 0).b((byte) 0).a((byte) 255);
    private Random random = new Random();
    private int[] durations = { 15, 30, 60 };
    private int selectedDurationIndex = 0; // default to 60s

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
            DrawText("Select time:", centerX - 200, 270, 28, black);
            for (int i = 0; i < durations.length; i++) {
                String label = durations[i] + "s";
                int x = centerX - 60 + i * 70;
                int y = 270;
                Color color = (i == selectedDurationIndex) ? green : black;
                DrawRectangle(x - 10, y - 5, 60, 40,
                        (i == selectedDurationIndex)
                                ? new Color().r((byte) 220).g((byte) 255).b((byte) 220).a((byte) 255)
                                : new Color().r((byte) 230).g((byte) 230).b((byte) 230).a((byte) 255));
                DrawText(label, x, y, 32, color);
            }
            DrawText("Press LEFT/RIGHT to select, ENTER to start",
                    centerX - MeasureText("Press LEFT/RIGHT to select, ENTER to start", 22) / 2, 330, 22, black);
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
        for (int i = 0; i < visibleWords && wordIndex + i < testWords.size(); i++) {
            String word = testWords.get(wordIndex + i);
            int x = xStart + (i % wordsPerLine) * 120;
            int yOffset = y + (i / wordsPerLine) * 50;
            if (i == 0) {
                // Draw user input over the current word
                drawWordWithInput(word, userInput, x, yOffset, fontSize);
            } else {
                DrawText(word, x, yOffset, fontSize, black);
            }
        }
    }

    private void drawWordWithInput(String target, String input, int x, int y, int fontSize) {
        int offset = 0;
        int len = Math.max(target.length(), input.length());
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
            offset += MeasureText(s, fontSize);
        }
        // Draw cursor
        DrawText("_", x + offset, y, fontSize, black);
    }

    @Override
    public void updateScene() {
        // No-op, handled in drawScene for timer
    }

    @Override
    public void processInputScene() {
        if (state == State.TITLE) {
            if (IsKeyPressed(KEY_LEFT)) {
                selectedDurationIndex = (selectedDurationIndex + durations.length - 1) % durations.length;
            } else if (IsKeyPressed(KEY_RIGHT)) {
                selectedDurationIndex = (selectedDurationIndex + 1) % durations.length;
            } else if (IsKeyPressed(KEY_ENTER)) {
                testDuration = durations[selectedDurationIndex];
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
                    if (userInput.equals(testWords.get(wordIndex))) {
                        wordIndex++;
                        userInput = "";
                        if (testWords.size() - wordIndex < visibleWords) {
                            for (int i = 0; i < 10; i++) {
                                testWords.add(wordList.get(random.nextInt(wordList.size())));
                            }
                        }
                    }
                } else if (key == KEY_BACKSPACE || key == 8) {
                    if (userInput.length() > 0) {
                        userInput = userInput.substring(0, userInput.length() - 1);
                    }
                } else if (key >= 32 && key <= 125) {
                    userInput += (char) key;
                }
                key = GetCharPressed();
            }
        } else if (state == State.END) {
            handleNameInput();
            updateEndScreenButtons();
            handleEndScreenButtonActions();
        }
    }
}
