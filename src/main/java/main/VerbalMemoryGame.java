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

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.raylib.Raylib.Color;

public class VerbalMemoryGame extends MemoryGame {
    private Color background = new Color().r((byte) 62).g((byte) 136).b((byte) 210).a((byte) 255);
    private Color darkGray = new Color().r((byte) 80).g((byte) 80).b((byte) 80).a((byte) 255);
    private Color maroon = new Color().r((byte) 128).g((byte) 0).b((byte) 0).a((byte) 255);
    private Color black = new Color().r((byte) 0).g((byte) 0).b((byte) 0).a((byte) 255);
    private Color lightGray = new Color().r((byte) 200).g((byte) 200).b((byte) 200).a((byte) 255);
    private Color darkGreen = new Color().r((byte) 0).g((byte) 100).b((byte) 0).a((byte) 255);
    private Set<String> seenWords;
    private String currentWord;
    private int lives;
    private Random random;
    private String[] wordBank = {
            "apple", "banana", "car", "dog", "elephant", "flower", "guitar", "house", "island", "jungle",
            "kite", "lemon", "mountain", "notebook", "orange", "piano", "queen", "river", "sun", "tree",
            "umbrella", "violin", "window", "xylophone", "yacht", "zebra", "cloud", "desk", "engine", "forest",
            "garden", "hat", "ice", "jacket", "key", "lamp", "mirror", "nest", "ocean", "pencil",
            "quilt", "road", "star", "train", "unicorn", "vase", "whale", "x-ray", "yogurt", "zipper"
    };
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
                newWord = wordBank[random.nextInt(wordBank.length)];
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
