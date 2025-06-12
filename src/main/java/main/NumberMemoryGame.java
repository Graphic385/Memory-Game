package main;

import static com.raylib.Raylib.*;
import static com.raylib.Raylib.IsMouseButtonPressed;
import static com.raylib.Raylib.MOUSE_BUTTON_LEFT;
import static com.raylib.Raylib.GetScreenWidth;
import static com.raylib.Raylib.GetScreenHeight;
import static com.raylib.Raylib.MeasureText;
import static com.raylib.Raylib.DrawText;
import static com.raylib.Raylib.DrawRectangle;
import static com.raylib.Raylib.DrawRectangleLines;
import static com.raylib.Raylib.GetCharPressed;
import static com.raylib.Raylib.IsKeyPressed;
import static com.raylib.Raylib.KEY_BACKSPACE;

import com.raylib.Raylib.Color;

import java.util.Random;

public class NumberMemoryGame extends MemoryGame {
    private int level = 1;
    private String currentNumber = "";
    private String userInput = "";
    private boolean showNumber = true;
    private boolean showResult = false;
    private boolean showStartScreen = true;
    private boolean isGameOver = false;
    private long numberShownTime = 0;
    private long memorizeDuration = 0;
    private float progress = 1.0f;
    private String lastInput = "";
    private String lastNumber = "";
    private boolean lastResultCorrect = false;
    private Random random = new Random();
    private Button nextButton;
    private Color background = new Color().r((byte) 62).g((byte) 136).b((byte) 210).a((byte) 255);

    public NumberMemoryGame() {
        super((int) (GetScreenWidth() / 2));
        int centerX = GetScreenWidth() / 2;
        int btnW = 200, btnH = 60;
        int btnX = centerX - btnW / 2, btnY = 350;
        nextButton = new Button(btnX, btnY, btnW, btnH, com.raylib.Colors.LIGHTGRAY);
        nextButton.setText("Next", 32, com.raylib.Colors.BLACK);
    }

    @Override
    public void drawScene() {
        BeginDrawing();
        ClearBackground(background);
        int windowWidth = GetScreenWidth();
        int windowHeight = GetScreenHeight();
        int centerX = windowWidth / 2;
        int centerY = windowHeight / 2;
        Color textColor = com.raylib.Colors.BLACK;
        if (showStartScreen) {
            DrawText("Number Memory Game", centerX - MeasureText("Number Memory Game", 40) / 2, 100, 40, textColor);
            DrawText("Memorize the number, then type it in.",
                    centerX - MeasureText("Memorize the number, then type it in.", 24) / 2, 200, 24, textColor);
            DrawText("Click to start", centerX - MeasureText("Click to start", 28) / 2, 350, 28, textColor);
        } else if (isGameOver) {
            score = level - 1;
            drawEndScreen(centerX, "Game Over!", background);
        } else if (showResult) {
            DrawText("Level " + (level - (lastResultCorrect ? 1 : 0)),
                    centerX - MeasureText("Level " + (level - (lastResultCorrect ? 1 : 0)), 40) / 2, 120, 40,
                    textColor);
            DrawText("You entered: " + lastInput, centerX - MeasureText("You entered: " + lastInput, 32) / 2, 200, 32,
                    textColor);
            DrawText("Correct number: " + lastNumber, centerX - MeasureText("Correct number: " + lastNumber, 32) / 2,
                    250, 32, textColor);
            nextButton.draw();
        } else if (showNumber) {
            DrawText(currentNumber, centerX - MeasureText(currentNumber, 64) / 2, centerY - 32, 64, textColor);
            // Draw progress bar
            int barW = 400, barH = 20;
            int barX = centerX - barW / 2, barY = centerY + 60;
            DrawRectangle(barX, barY, barW, barH, com.raylib.Colors.LIGHTGRAY);
            DrawRectangle(barX, barY, (int) (barW * progress), barH, com.raylib.Colors.GREEN);
            DrawRectangleLines(barX, barY, barW, barH, com.raylib.Colors.DARKGRAY);
        } else {
            DrawText("Enter the number:", centerX - MeasureText("Enter the number:", 32) / 2, centerY - 60, 32,
                    textColor);
            // Draw input box
            int boxW = 300, boxH = 60;
            int boxX = centerX - boxW / 2, boxY = centerY;
            DrawRectangle(boxX, boxY, boxW, boxH, com.raylib.Colors.LIGHTGRAY);
            DrawRectangleLines(boxX, boxY, boxW, boxH, com.raylib.Colors.DARKGRAY);
            DrawText(userInput, boxX + 20, boxY + 15, 36, textColor);
        }
        EndDrawing();
    }

    @Override
    public void updateScene() {
        if (showStartScreen || isGameOver || showResult)
            return;
        if (showNumber) {
            long now = System.currentTimeMillis();
            float total = memorizeDuration;
            float elapsed = now - numberShownTime;
            progress = Math.max(0, 1.0f - (elapsed / total));
            if (elapsed >= memorizeDuration) {
                showNumber = false;
                userInput = "";
            }
        }
    }

    @Override
    public void processInputScene() {
        if (showStartScreen) {
            if (IsMouseButtonPressed(MOUSE_BUTTON_LEFT)) {
                level = 1;
                isGameOver = false;
                showStartScreen = false;
                showResult = false;
                nextLevel();
            }
        } else if (isGameOver) {
            handleNameInput();
            updateEndScreenButtons();
            handleEndScreenButtonActions();
        } else if (showResult) {
            nextButton.update();
            if (nextButton.isClicked()) {
                showResult = false;
                if (lastResultCorrect) {
                    nextLevel();
                } else {
                    isGameOver = true;
                }
            }
        } else if (showNumber) {
            // No input while number is shown
        } else {
            // Input for user to type number
            int key = GetCharPressed();
            while (key > 0) {
                if ((key >= 48 && key <= 57) && userInput.length() < 20) {
                    userInput += (char) key;
                }
                key = GetCharPressed();
            }
            if (IsKeyPressed(KEY_BACKSPACE) && userInput.length() > 0) {
                userInput = userInput.substring(0, userInput.length() - 1);
            }
            if (IsKeyPressed(KEY_ENTER) || IsMouseButtonPressed(MOUSE_BUTTON_LEFT)) {
                if (!userInput.isEmpty()) {
                    lastInput = userInput;
                    lastNumber = currentNumber;
                    if (userInput.equals(currentNumber)) {
                        level++;
                        lastResultCorrect = true;
                        showResult = true;
                    } else {
                        lastResultCorrect = false;
                        showResult = true;
                    }
                }
            }
        }
    }

    @Override
    public void reset() {
        level = 1;
        isGameOver = false;
        showStartScreen = true;
        showResult = false;
        showNumber = true;
        userInput = "";
        lastInput = "";
        lastNumber = "";
        shouldReturnToTitle = false;
    }

    private void nextLevel() {
        int digits = level;
        if (digits > 20)
            digits = 20;
        int min = (int) Math.pow(10, digits - 1);
        int max = (int) Math.pow(10, digits) - 1;
        if (digits == 1)
            min = 0;
        currentNumber = String.valueOf(random.nextInt(max - min + 1) + min);
        memorizeDuration = (long) (digits * 1200);
        numberShownTime = System.currentTimeMillis();
        showNumber = true;
        userInput = "";
    }
}
