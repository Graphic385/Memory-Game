package main;

import static com.raylib.Raylib.BeginDrawing;
import static com.raylib.Raylib.ClearBackground;
import static com.raylib.Raylib.DrawRectangle;
import static com.raylib.Raylib.DrawText;
import static com.raylib.Raylib.EndDrawing;
import static com.raylib.Raylib.GetScreenHeight;
import static com.raylib.Raylib.GetScreenWidth;
import static com.raylib.Raylib.IsKeyPressed;
import static com.raylib.Raylib.KEY_ENTER;
import static com.raylib.Raylib.MeasureText;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import com.raylib.Raylib.Color;

public class SequenceGame extends MemoryGame {
    private Color background = new Color().r((byte) 62).g((byte) 136).b((byte) 210).a((byte) 255);
    private Color tileColor = new Color().r((byte) 53).g((byte) 117).b((byte) 194).a((byte) 255);
    private int gridSize = 3;
    private ArrayList<Point> sequence1;
    private int sequenceLength = 1;
    private int playerStep = 0;
    private Random random = new Random();
    private int tileSize = 120;
    private int gridOffsetX, gridOffsetY;
    private boolean playerTurn = false;
    private int highlightIndex = 0;
    private long highlightStartTime = 0;
    private boolean sequenceShowing = false;
    private Button[][] tileButtons;
    private boolean highlightPhase = true; // true = highlight, false = pause

    public SequenceGame() {
        super((int) (GetScreenWidth() / 2));
        startGame();
    }

    @Override
    public void drawScene() {
        BeginDrawing();
        ClearBackground(background);
        int windowWidth = GetScreenWidth();
        int centerX = windowWidth / 2;
        if (isGameOver) {
            drawEndScreen(centerX, "Game Over!", background);
        } else {
            if (showStartScreen) {
                DrawText("Sequence Memory Game", centerX - MeasureText("Sequence Memory Game", 40) / 2, 100, 40,
                        tileColor);
                DrawText("Remember the sequence of highlighted tiles.",
                        centerX - MeasureText("Remember the sequence of highlighted tiles.", 24) / 2, 200, 24,
                        tileColor);
                DrawText("Click the tiles in the same order.",
                        centerX - MeasureText("Click the tiles in the same order.", 24) / 2, 240, 24, tileColor);
                DrawText("Press ENTER to start", centerX - MeasureText("Press ENTER to start", 28) / 2, 350, 28,
                        tileColor);
                if (showLeaderboard) {
                    DrawRectangle(centerX - 250, 460, 500, 200, background);
                    DrawText("Leaderboard:", centerX - 240, 470, 24, tileColor);
                    DrawText(leaderboardText, centerX - 240, 500, 22, tileColor);
                }
            } else {
                // Draw grid using buttons
                for (int y = 0; y < gridSize; y++) {
                    for (int x = 0; x < gridSize; x++) {
                        Button btn = tileButtons[x][y];
                        btn.draw();
                    }
                }
                DrawText("Score: " + score, centerX - MeasureText("Score: " + score, 32) / 2, 50, 32, tileColor);
            }
        }
        EndDrawing();
    }

    @Override
    public void updateScene() {
        if (showStartScreen || isGameOver)
            return;
        if (sequenceShowing) {
            if (highlightIndex < sequenceLength) {
                Point p = sequence1.get(highlightIndex);
                if (highlightPhase) {
                    tileButtons[p.x][p.y].highlight(com.raylib.Colors.WHITE, 300);
                    if (System.currentTimeMillis() - highlightStartTime > 300) {
                        highlightPhase = false;
                        highlightStartTime = System.currentTimeMillis();
                    }
                } else { // pause phase
                    // No highlight
                    if (System.currentTimeMillis() - highlightStartTime > 200) {
                        highlightIndex++;
                        highlightPhase = true;
                        highlightStartTime = System.currentTimeMillis();
                    }
                }
            } else {
                sequenceShowing = false;
                playerTurn = true;
                highlightIndex = 0;
                highlightPhase = true;
            }
        }

    }

    @Override
    public void processInputScene() {
        if (isGameOver) {
            handleNameInput();
            updateEndScreenButtons();
            handleEndScreenButtonActions();
        } else {
            if (showStartScreen) {
                if (IsKeyPressed(KEY_ENTER)) {
                    startGame();
                    showStartScreen = false;
                }
            } else {
                if (sequenceShowing) {
                    return;
                } else if (playerTurn) {
                    for (int y = 0; y < gridSize; y++) {
                        for (int x = 0; x < gridSize; x++) {
                            tileButtons[x][y].update();
                            if (tileButtons[x][y].isClicked()) {
                                if (sequence1.get(playerStep).x == x && sequence1.get(playerStep).y == y) {
                                    playerStep++;
                                    if (playerStep == sequenceLength) {
                                        score++;
                                        sequenceLength++;
                                        nextSequence();
                                    }
                                } else {
                                    isGameOver = true;
                                }
                                return; // Only allow one click per input
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void reset() {
        // Reset all game state for a new round
        isGameOver = false;
        showStartScreen = true;
        score = 0;
        sequenceLength = 1;
        playerStep = 0;
        sequence1 = null;
        showLeaderboard = false;
        shouldReturnToTitle = false;
    }

    private void startGame() {
        gridOffsetX = (GetScreenWidth() - (tileSize * gridSize)) / 2;
        gridOffsetY = (GetScreenHeight() - (tileSize * gridSize)) / 2;
        tileButtons = new Button[gridSize][gridSize];
        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {
                int bx = gridOffsetX + x * tileSize;
                int by = gridOffsetY + y * tileSize;
                tileButtons[x][y] = new Button(bx, by, tileSize - 5, tileSize - 5, tileColor, tileColor,
                        com.raylib.Colors.WHITE);
            }
        }
        sequenceLength = 1;
        score = 0;

        nextSequence();
        playerStep = 0;

    }

    private void nextSequence() {
        if (sequence1 == null || sequenceLength == 1) {
            // First round: initialize sequence array and add first random tile
            sequence1 = new ArrayList<>();
            sequence1.add(new Point(random.nextInt(3), random.nextInt(3)));
        } else {
            // Add one new random tile to the end, keeping previous sequence
            sequence1.add(new Point(random.nextInt(3), random.nextInt(3)));
        }
        highlightIndex = 0;
        sequenceShowing = true;
        playerTurn = false;
        highlightStartTime = System.currentTimeMillis();
        playerStep = 0;
    }
}
