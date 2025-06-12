package main;

import static com.raylib.Raylib.BeginDrawing;
import static com.raylib.Raylib.ClearBackground;
import static com.raylib.Raylib.EndDrawing;
import static com.raylib.Raylib.LoadTexture;

import com.raylib.Raylib.Color;

public class TitleScreen {
    Color backgroundColour;
    Color backgroundColourBright;
    Color backgroundColourBrighter;
    Color buttonOutline;
    Color buttonHoverColour;
    Color buttonClickColour;
    Button[] buttons;
    Button sequencyMemory, verbalMemory, reactionTimeTest, numberMemoryGame, aimTrainerGame, typingTestGame;
    private boolean verbalMemorySelected = false;
    private boolean sequenceGameSelected = false;
    private boolean reactionTimeSelected = false;
    private boolean numberMemorySelected = false;
    private boolean aimTrainerSelected = false;
    private boolean typingTestSelected = false;
    private float scrollOffset = 0f; // Horizontal scroll offset
    private final int buttonY = 450; // All buttons at same y-level
    private final int buttonSpacing = 40; // Space between buttons
    private final int buttonWidth = 200; // Button width

    public TitleScreen() {
        backgroundColour = new Color().r((byte) 62).g((byte) 136).b((byte) 210).a((byte) 255);
        buttonOutline = new Color().r((byte) 53).g((byte) 117).b((byte) 194).a((byte) 255);
        // Place buttons at same y-level, x will be set dynamically
        sequencyMemory = new Button(0, buttonY, buttonWidth, 200, backgroundColour);
        verbalMemory = new Button(0, buttonY, buttonWidth, 200, backgroundColour);
        reactionTimeTest = new Button(0, buttonY, buttonWidth, 200, backgroundColour);
        numberMemoryGame = new Button(0, buttonY, buttonWidth, 200, backgroundColour);
        aimTrainerGame = new Button(0, buttonY, buttonWidth, 200, backgroundColour);
        typingTestGame = new Button(0, buttonY, buttonWidth, 200, backgroundColour); // New button for Typing Test Game
        verbalMemory.addOutline(10, buttonOutline);
        sequencyMemory.addOutline(10, buttonOutline);
        reactionTimeTest.addOutline(10, buttonOutline);
        numberMemoryGame.addOutline(10, buttonOutline);
        aimTrainerGame.addOutline(10, buttonOutline);
        typingTestGame.addOutline(10, buttonOutline); // Outline for new button
        sequencyMemory.addImageIcon(LoadTexture("resources/sequenceMemory.png"), 0.75f);
        verbalMemory.addImageIcon(LoadTexture("resources/verbalMemory.png"), 0.75f);
        numberMemoryGame.addImageIcon(LoadTexture("resources/numberMemory.png"), 0.75f);
        reactionTimeTest.addImageIcon(LoadTexture("resources/reactionTime.png"), 0.75f);
        aimTrainerGame.addImageIcon(LoadTexture("resources/aimTrainer.png"), 0.75f);
        typingTestGame.addImageIcon(LoadTexture("resources/verbalMemory.png"), 0.75f); // Placeholder icon
        // Add to buttons array
        buttons = new Button[] { sequencyMemory, verbalMemory, reactionTimeTest, numberMemoryGame, aimTrainerGame,
                typingTestGame };
    }

    public void draw() {
        BeginDrawing();
        ClearBackground(backgroundColour);
        // Draw big title text: 'Human' and 'Benchmark' on separate lines, black, much
        // bigger
        String title1 = "Human";
        String title2 = "Benchmark";
        int fontSize1 = 160;
        int fontSize2 = 160;
        int screenWidth = com.raylib.Raylib.GetScreenWidth();
        int textWidth1 = com.raylib.Raylib.MeasureText(title1, fontSize1);
        int textWidth2 = com.raylib.Raylib.MeasureText(title2, fontSize2);
        int x1 = (screenWidth - textWidth1) / 2;
        int x2 = (screenWidth - textWidth2) / 2;
        int y1 = 60;
        int y2 = y1 + fontSize1 + 10; // 10px gap between lines
        Color black = new Color().r((byte) 0).g((byte) 0).b((byte) 0).a((byte) 255);
        com.raylib.Raylib.DrawText(title1, x1, y1, fontSize1, black);
        com.raylib.Raylib.DrawText(title2, x2, y2, fontSize2, black);
        for (Button b : buttons) {
            b.draw();
        }
        EndDrawing();
    }

    public void update() {
        // Handle horizontal mouse wheel scroll (Raylib: GetMouseWheelMoveV().x)
        float wheelMove = com.raylib.Raylib.GetMouseWheelMoveV().x();
        if (wheelMove != 0) {
            scrollOffset -= wheelMove * 40; // Adjust scroll speed as needed
        }
        int x = (int) -scrollOffset + buttonSpacing;
        for (Button b : buttons) {
            b.setPosition(x, buttonY);
            b.update();
            x += buttonWidth + buttonSpacing;
        }
        // Only set flags, do not transition scenes here
        verbalMemorySelected = false;
        sequenceGameSelected = false;
        reactionTimeSelected = false;
        numberMemorySelected = false;
        aimTrainerSelected = false;
        typingTestSelected = false; // Reset typing test selection
        if (sequencyMemory.isClicked()) {
            sequenceGameSelected = true;
        }
        if (verbalMemory.isClicked()) {
            verbalMemorySelected = true;
        }
        if (reactionTimeTest.isClicked()) {
            reactionTimeSelected = true;
        }
        if (numberMemoryGame.isClicked()) {
            numberMemorySelected = true;
        }
        if (aimTrainerGame.isClicked()) {
            aimTrainerSelected = true;
        }
        if (typingTestGame.isClicked()) { // Check if typing test button is clicked
            typingTestSelected = true;
        }
    }

    public void processInput() {
        // No-op: input is handled in update() for stateless scene management
    }

    public boolean isVerbalMemorySelected() {
        return verbalMemorySelected;
    }

    public boolean isSequenceGameSelected() {
        return sequenceGameSelected;
    }

    public boolean isReactionTimeSelected() {
        return reactionTimeSelected;
    }

    public boolean isNumberMemorySelected() {
        return numberMemorySelected;
    }

    public boolean isAimTrainerSelected() {
        return aimTrainerSelected;
    }

    public boolean isTypingTestSelected() { // New method to check if Typing Test Game is selected
        return typingTestSelected;
    }

    public void reset() {
        verbalMemorySelected = false;
        sequenceGameSelected = false;
        reactionTimeSelected = false;
        numberMemorySelected = false;
        aimTrainerSelected = false;
        typingTestSelected = false; // Reset typing test selection
        for (Button b : buttons) {
            b.update(); // clear click state
        }
    }
}
