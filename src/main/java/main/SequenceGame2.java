package main;

import static com.raylib.Raylib.*;
import com.raylib.Raylib.Color;
import java.util.ArrayList;
import java.util.Random;

public class SequenceGame2 extends MemoryGame {
    private Color background = new Color().r((byte) 62).g((byte) 136).b((byte) 210).a((byte) 255);
    private Color tileColor = new Color().r((byte) 53).g((byte) 117).b((byte) 194).a((byte) 255);
    private int gridSize = 3;
    private int[][] sequence;
    private int sequenceLength = 1;
    private int playerStep = 0;
    private Random random = new Random();
    private int tileSize = 120;
    private int gridOffsetX, gridOffsetY;
    private boolean playerTurn = false;
    private int highlightIndex = 0;
    private long highlightStartTime = 0;
    private boolean sequenceShowing = false;

    public SequenceGame2() {
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
                for (int y = 0; y < gridSize; y++) {
                    for (int x = 0; x < gridSize; x++) {
                        Color c = tileColor;
                        if (sequenceShowing && highlightIndex < sequenceLength && sequence[highlightIndex][0] == x
                                && sequence[highlightIndex][1] == y) {
                            c = com.raylib.Colors.YELLOW;
                        }
                        DrawRectangle(gridOffsetX + x * tileSize, gridOffsetY + y * tileSize, tileSize - 5,
                                tileSize - 5,
                                c);
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
            if (System.currentTimeMillis() - highlightStartTime > 700) {
                highlightIndex++;
                if (highlightIndex >= sequenceLength) {
                    sequenceShowing = false;
                    playerTurn = true;
                    highlightIndex = 0;
                } else {
                    highlightStartTime = System.currentTimeMillis();
                }
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
                } else if (playerTurn && IsMouseButtonPressed(0)) {
                    int mx = GetMouseX();
                    int my = GetMouseY();
                    int x = (mx - gridOffsetX) / tileSize;
                    int y = (my - gridOffsetY) / tileSize;
                    if (x >= 0 && x < gridSize && y >= 0 && y < gridSize) {
                        if (sequence[playerStep][0] == x && sequence[playerStep][1] == y) {
                            playerStep++;
                            if (playerStep == sequenceLength) {
                                score++;
                                sequenceLength++;
                                if (sequenceLength > gridSize * gridSize) {
                                    sequenceLength = gridSize * gridSize;
                                }
                                nextSequence();
                            }
                        } else {
                            isGameOver = true;
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
        sequence = null;
        shouldReturnToTitle = false;
    }

    private void startGame() {
        gridOffsetX = (GetScreenWidth() - (tileSize * gridSize)) / 2;
        gridOffsetY = (GetScreenHeight() - (tileSize * gridSize)) / 2;
        sequenceLength = 1;
        score = 0;
        nextSequence();
        playerStep = 0;
    }

    private void nextSequence() {
        sequence = new int[gridSize * gridSize][2];
        ArrayList<Integer> used = new ArrayList<>();
        for (int i = 0; i < sequenceLength; i++) {
            int idx;
            do {
                idx = random.nextInt(gridSize * gridSize);
            } while (used.contains(idx));
            used.add(idx);
            sequence[i][0] = idx % gridSize;
            sequence[i][1] = idx / gridSize;
        }
        highlightIndex = 0;
        sequenceShowing = true;
        playerTurn = false;
        highlightStartTime = System.currentTimeMillis();
        playerStep = 0;
    }
}
