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
    Button sequencyMemory, verbalMemory;
    private boolean verbalMemorySelected = false;
    private boolean sequenceGameSelected = false;
    private float scrollOffset = 0f; // Horizontal scroll offset
    private final int buttonY = 400; // All buttons at same y-level
    private final int buttonSpacing = 40; // Space between buttons
    private final int buttonWidth = 200; // Button width

    public TitleScreen() {
        backgroundColour = new Color().r((byte) 62).g((byte) 136).b((byte) 210).a((byte) 255);
        buttonOutline = new Color().r((byte) 53).g((byte) 117).b((byte) 194).a((byte) 255);
        // Place buttons at same y-level, x will be set dynamically
        sequencyMemory = new Button(0, buttonY, buttonWidth, 200, backgroundColour);
        verbalMemory = new Button(0, buttonY, buttonWidth, 200, backgroundColour);
        verbalMemory.addOutline(10, buttonOutline);
        sequencyMemory.addOutline(10, buttonOutline);
        sequencyMemory.addImageIcon(LoadTexture("resources/sequenceMemory.png"), 0.75f);
        verbalMemory.addImageIcon(LoadTexture("resources/verbalMemory.png"), 0.75f);
        buttons = new Button[] { sequencyMemory, verbalMemory };
    }

    public void draw() {
        BeginDrawing();
        ClearBackground(backgroundColour);
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
        if (sequencyMemory.isClicked()) {
            sequenceGameSelected = true;
        }
        if (verbalMemory.isClicked()) {
            verbalMemorySelected = true;
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

    public void reset() {
        verbalMemorySelected = false;
        sequenceGameSelected = false;
        for (Button b : buttons) {
            b.update(); // clear click state
        }
    }
}
