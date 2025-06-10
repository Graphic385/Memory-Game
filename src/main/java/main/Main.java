package main;

import static com.raylib.Raylib.InitWindow;
import static com.raylib.Raylib.SetTargetFPS;
import static com.raylib.Raylib.WindowShouldClose;

public class Main {

    enum Scene {
        TITLE,
        VERBAL_MEMORY_GAME,
        SEQUENCE_GAME
    }

    public static void main(String[] args) {
        InitWindow(1000, 800, "CPT");
        SetTargetFPS(60);

        // Create all screens
        TitleScreen titleScreen = new TitleScreen();
        VerbalMemoryGame2 verbalMemoryGame2 = new VerbalMemoryGame2();
        SequenceGame2 sequenceGame2 = new SequenceGame2();

        Scene currentScene = Scene.TITLE;

        while (!WindowShouldClose()) {
            switch (currentScene) {
                case TITLE:
                    titleScreen.update();
                    titleScreen.processInput();
                    titleScreen.draw();
                    if (titleScreen.isVerbalMemorySelected()) {
                        currentScene = Scene.VERBAL_MEMORY_GAME;
                        verbalMemoryGame2.reset();
                    } else if (titleScreen.isSequenceGameSelected()) {
                        currentScene = Scene.SEQUENCE_GAME;
                        sequenceGame2.reset();
                    }
                    break;
                case VERBAL_MEMORY_GAME:
                    verbalMemoryGame2.updateScene();
                    verbalMemoryGame2.processInputScene();
                    verbalMemoryGame2.drawScene();
                    if (verbalMemoryGame2.shouldReturnToTitle()) {
                        currentScene = Scene.TITLE;
                        titleScreen.reset();
                    }
                    break;
                case SEQUENCE_GAME:
                    sequenceGame2.updateScene();
                    sequenceGame2.processInputScene();
                    sequenceGame2.drawScene();
                    // Only return to title if SequenceGame2 signals it
                    if (sequenceGame2.shouldReturnToTitle()) {
                        currentScene = Scene.TITLE;
                        titleScreen.reset();
                    }
                    break;
            }
        }
    }
}