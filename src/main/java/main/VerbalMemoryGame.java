package main;

import static com.raylib.Raylib.BeginDrawing;
import static com.raylib.Raylib.ClearBackground;
import static com.raylib.Raylib.DrawText;
import static com.raylib.Raylib.EndDrawing;
import static com.raylib.Raylib.GetScreenHeight;
import static com.raylib.Raylib.GetScreenWidth;
import static com.raylib.Raylib.IsKeyPressed;
import static com.raylib.Raylib.KEY_ENTER;
import static com.raylib.Raylib.MeasureText;
import static com.raylib.Helpers.newColor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.raylib.Raylib.Color;

public class VerbalMemoryGame extends MemoryGame {
    private Color background = newColor(62, 136, 210, 255);
    private Color darkGray = newColor(80, 80, 80, 255);
    private Color maroon = newColor(128, 0, 0, 255);
    private Color black = newColor(0, 0, 0, 255);
    private Color lightGray = newColor(200, 200, 200, 255);
    private Color darkGreen = newColor(0, 100, 0, 255);
    private Set<String> seenWords;
    private String currentWord;
    private int lives;
    private Random random;
    private ArrayList<String> wordList;
    private boolean showStartScreen = true;
    private Button seenButton;
    private Button newButton;

    public VerbalMemoryGame() {
        super((int) (GetScreenWidth() / 2));
        random = new Random();
        seenButton = new Button(350, 500, 200, 80, maroon, maroon, maroon);
        seenButton.setText("SEEN", 36, lightGray);
        newButton = new Button(650, 500, 200, 80, darkGreen, darkGreen, darkGreen);
        newButton.setText("NEW", 36, lightGray);
        loadWordsFromFile();
    }

    private void loadWordsFromFile() {
        wordList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(ResourceLoader.loadResource("resources/10000-english-no-swears.txt")))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    wordList.add(line.trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Fallback: add a default word if file fails to load
            wordList.add("default");
        }
    }

    private void startGame() {
        seenWords = new HashSet<>();
        score = 0;
        lives = 3;
        nextWord();
    }

    private void nextWord() {
        // 70% chance to show a new word, 30% to show a seen word (if any)
        if (!seenWords.isEmpty() && random.nextInt(10) < 3) {
            int idx = random.nextInt(seenWords.size());
            currentWord = seenWords.toArray(new String[0])[idx];
        } else {
            String newWord;
            do {
                newWord = wordList.get(random.nextInt(wordList.size()));
            } while (seenWords.contains(newWord));
            currentWord = newWord;
        }
    }

    private void handleSeen() {
        if (seenWords.contains(currentWord)) {
            score++;
            nextWord();
        } else {
            lives--;
            if (lives <= 0) {
                isGameOver = true;
            } else {
                nextWord();
            }
        }
    }

    private void handleNew() {
        if (!seenWords.contains(currentWord)) {
            seenWords.add(currentWord);
            score++;
            nextWord();
        } else {
            lives--;
            if (lives <= 0) {
                isGameOver = true;
            } else {
                nextWord();
            }
        }
    }

    @Override
    public void reset() {
        // Reset all game state for a new round
        isGameOver = false;
        shouldReturnToTitle = false;
        showStartScreen = true;
        score = 0;
        lives = 3;
        seenWords = new HashSet<>();
        currentWord = null;
        System.out.println("hello");
    }

    @Override
    public void drawScene() {
        BeginDrawing();
        ClearBackground(background);
        int windowWidth = GetScreenWidth();
        int windowHeight = GetScreenHeight();
        int centerX = windowWidth / 2;
        if (showStartScreen) {
            DrawText("Verbal Memory Game", centerX - MeasureText("Verbal Memory Game", 40) / 2, 100, 40, darkGray);
            DrawText("You will be shown words, one at a time.",
                    centerX - MeasureText("You will be shown words, one at a time.", 24) / 2, 200, 24, darkGray);
            DrawText("If you've seen the word during the test, press SEEN.",
                    centerX - MeasureText("If you've seen the word during the test, press SEEN.", 24) / 2, 240, 24,
                    darkGray);
            DrawText("If it's a new word, press NEW.",
                    centerX - MeasureText("If it's a new word, press NEW.", 24) / 2,
                    280, 24, darkGray);
            DrawText("Press ENTER to start", centerX - MeasureText("Press ENTER to start", 28) / 2, 350, 28,
                    maroon);
        } else if (isGameOver) {
            drawEndScreen(centerX, "Game Over!", background);
        } else {
            DrawText(currentWord, centerX - MeasureText(currentWord, 48) / 2, 200, 48, black);
            DrawText("Score: " + score + "    Lives: " + lives,
                    centerX - MeasureText("Score: " + score + "    Lives: " + lives, 32) / 2, 100, 32, darkGray);
            // Center buttons
            int buttonY = windowHeight - 200;
            int buttonSpacing = 100;
            int buttonWidth = 200;
            int seenX = centerX - buttonWidth - buttonSpacing / 2;
            int newX = centerX + buttonSpacing / 2;
            seenButton.setPosition(seenX, buttonY);
            newButton.setPosition(newX, buttonY);
            seenButton.draw();
            newButton.draw();
        }

        EndDrawing();

    }

    @Override
    public void updateScene() {
        if (showStartScreen || isGameOver)
            return;
        seenButton.update();
        newButton.update();
    }

    @Override
    public void processInputScene() {
        if (isGameOver) {
            handleNameInput();
            updateEndScreenButtons();
            handleEndScreenButtonActions();
        } else if (showStartScreen) {
            if (IsKeyPressed(KEY_ENTER)) {
                startGame();
                showStartScreen = false;
            }
        } else {
            if (seenButton.isClicked()) {
                handleSeen();
            } else if (newButton.isClicked()) {
                handleNew();
            }
        }
    }

}
