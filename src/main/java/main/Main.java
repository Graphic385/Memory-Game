package main;

import static com.raylib.Raylib.InitWindow;
import static com.raylib.Raylib.SetTargetFPS;
import static com.raylib.Raylib.WindowShouldClose;

public class Main {

    enum Scene {
        TITLE,
        VERBAL_MEMORY_GAME,
        SEQUENCE_GAME,
        REACTION_TIME_TEST,
        NUMBER_MEMORY_GAME, // Add new scene
        AIM_TRAINER_GAME, // Add new scene
        TYPING_TEST_GAME // Add new scene
    }

    public static void main(String[] args) {
        InitWindow(1000, 800, "CPT");
        SetTargetFPS(60);

        // Create all screens
        TitleScreen titleScreen = new TitleScreen();
        VerbalMemoryGame verbalMemoryGame = new VerbalMemoryGame();
        SequenceGame sequenceGame = new SequenceGame();
        ReactionTimeTest reactionTimeTest = new ReactionTimeTest();
        NumberMemoryGame numberMemoryGame = new NumberMemoryGame(); // Add new game
        AimTrainerGame aimTrainerGame = new AimTrainerGame(); // Add new game
        TypingTestGame typingTestGame = new TypingTestGame(); // Add new game

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
                    } else if (titleScreen.isReactionTimeSelected()) {
                        currentScene = Scene.REACTION_TIME_TEST;
                        reactionTimeTest.reset();
                    } else if (titleScreen.isNumberMemorySelected()) {
                        currentScene = Scene.NUMBER_MEMORY_GAME;
                        numberMemoryGame.reset();
                    } else if (titleScreen.isAimTrainerSelected()) {
                        currentScene = Scene.AIM_TRAINER_GAME;
                        aimTrainerGame.reset();
                    } else if (titleScreen.isTypingTestSelected()) {
                        currentScene = Scene.TYPING_TEST_GAME;
                        typingTestGame.reset();
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
                    if (sequenceGame.shouldReturnToTitle()) {
                        currentScene = Scene.TITLE;
                        titleScreen.reset();
                    }
                    break;
                case REACTION_TIME_TEST:
                    reactionTimeTest.updateScene();
                    reactionTimeTest.processInputScene();
                    reactionTimeTest.drawScene();
                    if (reactionTimeTest.shouldReturnToTitle()) {
                        currentScene = Scene.TITLE;
                        titleScreen.reset();
                    }
                    break;
                case NUMBER_MEMORY_GAME:
                    numberMemoryGame.updateScene();
                    numberMemoryGame.processInputScene();
                    numberMemoryGame.drawScene();
                    if (numberMemoryGame.shouldReturnToTitle()) {
                        currentScene = Scene.TITLE;
                        titleScreen.reset();
                    }
                    break;
                case AIM_TRAINER_GAME:
                    aimTrainerGame.updateScene();
                    aimTrainerGame.processInputScene();
                    aimTrainerGame.drawScene();
                    if (aimTrainerGame.shouldReturnToTitle()) {
                        currentScene = Scene.TITLE;
                        titleScreen.reset();
                    }
                    break;
                case TYPING_TEST_GAME:
                    typingTestGame.updateScene();
                    typingTestGame.processInputScene();
                    typingTestGame.drawScene();
                    if (typingTestGame.shouldReturnToTitle()) {
                        currentScene = Scene.TITLE;
                        titleScreen.reset();
                    }
                    break;
            }
        }
    }
}