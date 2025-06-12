package main;

import static com.raylib.Raylib.*;
import com.raylib.Raylib.Color;
import com.raylib.Raylib.Vector2;
import com.raylib.Raylib.Texture;
import static com.raylib.Raylib.GetScreenWidth;
import static com.raylib.Raylib.GetScreenHeight;
import static com.raylib.Raylib.MeasureText;
import static com.raylib.Raylib.DrawText;
import static com.raylib.Raylib.IsMouseButtonPressed;
import static com.raylib.Raylib.MOUSE_BUTTON_LEFT;

public class AimTrainerGame extends MemoryGame {
    private enum State {
        INTRO, RUNNING, END
    }

    private State state = State.INTRO;
    private int targetsHit = 0;
    private int totalTargets = 30;
    private Vector2 targetPos;
    private long startTime = 0;
    private long endTime = 0;
    private Color background = new Color().r((byte) 62).g((byte) 136).b((byte) 210).a((byte) 255);
    private Color textColor = com.raylib.Colors.BLACK;
    private Texture targetTexture;
    private int targetSize = 110; // Increased size for the target texture

    public AimTrainerGame() {
        super(GetScreenWidth() / 2);
        targetTexture = LoadTexture("resources/target.png");
        reset();
    }

    @Override
    public void reset() {
        state = State.INTRO;
        targetsHit = 0;
        score = 0;
        shouldReturnToTitle = false;
        isGameOver = false;
        startTime = 0;
        endTime = 0;
        targetPos = new Vector2().x(GetScreenWidth() / 2f).y(GetScreenHeight() / 2f);
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
        if (state == State.INTRO) {
            DrawText("Aim Trainer", centerX - MeasureText("Aim Trainer", 48) / 2, 100, 48, textColor);
            DrawText("Hit 30 targets as fast as you can!",
                    centerX - MeasureText("Hit 30 targets as fast as you can!", 28) / 2, 180, 28, textColor);
            DrawText("Click the target below to begin.",
                    centerX - MeasureText("Click the target below to begin.", 24) / 2, 220, 24, textColor);
            DrawTextureEx(targetTexture, new Vector2().x(centerX - targetSize / 2f).y(350 - targetSize / 2f), 0,
                    (float) targetSize / targetTexture.width(), com.raylib.Colors.WHITE);
        } else if (state == State.RUNNING) {
            DrawText("Targets hit: " + targetsHit + "/" + totalTargets, 30, 30, 28, textColor);
            DrawTextureEx(targetTexture,
                    new Vector2().x(targetPos.x() - targetSize / 2f).y(targetPos.y() - targetSize / 2f), 0,
                    (float) targetSize / targetTexture.width(), com.raylib.Colors.WHITE);
        } else if (state == State.END) {
            float totalSeconds = (endTime - startTime) / 1000f;
            float avgTime = totalSeconds / totalTargets;
            score = (int) (avgTime * 1000); // Store as ms for leaderboard sorting
            drawEndScreen(centerX, "Avg Time: " + String.format("%.2f", avgTime) + "s", background);
        }
        EndDrawing();
    }

    @Override
    public void updateScene() {
    }

    @Override
    public void processInputScene() {
        int centerX = GetScreenWidth() / 2;
        if (state == State.INTRO) {
            // Only allow clicking the target to start
            Vector2 mouse = GetMousePosition();
            if (IsMouseButtonPressed(MOUSE_BUTTON_LEFT)) {
                float dx = mouse.x() - centerX;
                float dy = mouse.y() - 350f;
                float hitRadius = targetSize / 2f;
                if (dx * dx + dy * dy <= hitRadius * hitRadius) {
                    state = State.RUNNING;
                    targetsHit = 0;
                    startTime = System.currentTimeMillis();
                    randomizeTarget();
                }
            }
        } else if (state == State.RUNNING) {
            Vector2 mouse = GetMousePosition();
            if (IsMouseButtonPressed(MOUSE_BUTTON_LEFT)) {
                float dx = mouse.x() - targetPos.x();
                float dy = mouse.y() - targetPos.y();
                float hitRadius = targetSize / 2f;
                if (dx * dx + dy * dy <= hitRadius * hitRadius) {
                    targetsHit++;
                    if (targetsHit >= totalTargets) {
                        endTime = System.currentTimeMillis();
                        state = State.END;
                        isGameOver = true;
                    } else {
                        randomizeTarget();
                    }
                }
            }
        } else if (state == State.END) {
            handleNameInput();
            updateEndScreenButtons();
            handleEndScreenButtonActions();
        }
    }

    private void randomizeTarget() {
        float margin = targetSize / 2f + 10;
        float x = margin + (float) Math.random() * (GetScreenWidth() - 2 * margin);
        float y = margin + (float) Math.random() * (GetScreenHeight() - 2 * margin);
        targetPos = new Vector2().x(x).y(y);
    }

    @Override
    protected void drawEndScreen(int centerX, String gameOverText, Color backgroundColor) {
        Color textColor = com.raylib.Colors.BLACK;
        float avgMs = score;
        String avgText = "Average time " + ((int) avgMs) + "ms";
        DrawText(avgText, centerX - MeasureText(avgText, 48) / 2, 120, 48, textColor);
        DrawText("Enter your name to save score:", centerX - 200, 250, 28, textColor);
        DrawRectangleRoundedLines(new Rectangle().x(centerX - 110).y(290).width(280).height(40), 0.8f,
                20, com.raylib.Colors.BLACK);
        DrawText(playerName, centerX - 95, 295, 28, textColor);
        int textWidth = MeasureText(playerName, 28);
        int cursorX = centerX - 95 + textWidth;
        int cursorY = 295;
        int cursorHeight = 28;
        if (((int) (System.currentTimeMillis() / 500) % 2) == 0 && playerName.length() < 16) {
            DrawRectangle(cursorX, cursorY, 2, cursorHeight, textColor);
        }
        saveScoreButton.setText("Save Score", 24, backgroundColor);
        saveScoreButton.draw();
        viewLeaderboardButton.setText("View Leaderboard", 24, backgroundColor);
        viewLeaderboardButton.draw();
        tryAgainButton.setText("Try Again", 24, backgroundColor);
        tryAgainButton.draw();
        exitButton.setText("Exit", 24, backgroundColor);
        exitButton.draw();
        if (!saveMessage.isEmpty()) {
            DrawText(saveMessage, centerX - MeasureText(saveMessage, 24) / 2, 480, 24, textColor);
        }
        if (showLeaderboard) {
            DrawRectangle(centerX - 250, 520, 500, 200, backgroundColor);
            DrawText("Leaderboard:", centerX - 240, 530, 24, textColor);
            DrawText(leaderboardText, centerX - 240, 560, 22, textColor);
        }
    }

    @Override
    protected void onViewLeaderboard() {
        String className = this.getClass().getSimpleName();
        String fileName = "src/main/java/main/Leaderboards/" + className + ".txt";
        java.util.List<String> lines = new java.util.ArrayList<>();
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (java.io.IOException e) {
            leaderboardText = "No scores yet.";
            showLeaderboard = true;
            return;
        }
        java.util.List<java.util.Map.Entry<String, Integer>> scores = new java.util.ArrayList<>();
        for (String l : lines) {
            String[] parts = l.split(":");
            if (parts.length == 2) {
                try {
                    scores.add(new java.util.AbstractMap.SimpleEntry<>(parts[0], Integer.parseInt(parts[1])));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        // Sort ascending (smaller ms is better)
        scores.sort(java.util.Comparator.comparingInt(java.util.Map.Entry::getValue));
        StringBuilder sb = new StringBuilder();
        int rank = 1;
        for (java.util.Map.Entry<String, Integer> entry : scores) {
            sb.append(rank++).append(". ").append(entry.getKey()).append(" - ").append(entry.getValue()).append("ms\n");
            if (rank > 10)
                break;
        }
        leaderboardText = sb.length() > 0 ? sb.toString() : "No scores yet.";
        showLeaderboard = true;
    }
}
