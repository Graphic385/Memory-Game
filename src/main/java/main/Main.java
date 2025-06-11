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
        VerbalMemoryGame verbalMemoryGame = new VerbalMemoryGame();
        SequenceGame sequenceGame = new SequenceGame();

        Scene currentScene = Scene.TITLE;

        while (!WindowShouldClose()) {
            switch (currentScene) {
                case TITLE:
                    titleScreen.update();
                    titleScreen.processInput();
                    titleScreen.draw();
                    if (titleScreen.isVerbalMemorySelected()) {
                        currentScene = Scene.VERBAL_MEMORY_GAME;
                        verbalMemoryGame.reset();
                    } else if (titleScreen.isSequenceGameSelected()) {
                        currentScene = Scene.SEQUENCE_GAME;
                        sequenceGame.reset();
                    }
                    break;
                case VERBAL_MEMORY_GAME:
                    verbalMemoryGame.updateScene();
                    verbalMemoryGame.processInputScene();
                    verbalMemoryGame.drawScene();
                    if (verbalMemoryGame.shouldReturnToTitle()) {
                        currentScene = Scene.TITLE;
                        titleScreen.reset();
                    }
                    break;
                case SEQUENCE_GAME:
                    sequenceGame.updateScene();
                    sequenceGame.processInputScene();
                    sequenceGame.drawScene();
                    // Only return to title if SequenceGame2 signals it
                    if (sequenceGame.shouldReturnToTitle()) {
                        currentScene = Scene.TITLE;
                        titleScreen.reset();
                    }
                    break;
            }
        }
    }
}