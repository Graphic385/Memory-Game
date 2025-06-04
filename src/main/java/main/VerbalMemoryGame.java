package main;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class VerbalMemoryGame extends Game {
    private JLabel wordLabel;
    private JButton seenButton, newButton;
    private JPanel buttonPanel;
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
    private JLabel statusLabel;

    public VerbalMemoryGame() {
        super("VerbalMemoryGame");
        titleScreen(gameName,
                "You will be shown word, one at a time. If you've seen the word during the test, click SEEN. If it's a new word, click NEW.",
                null);
    }

    private void updateStatusLabel() {
        statusLabel.setText("Score: " + score + "    Lives: " + lives);
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
        wordLabel.setText(currentWord);
        updateStatusLabel();
    }

    private void handleSeen() {
        if (seenWords.contains(currentWord)) {
            score++;
            nextWord();
        } else {
            lives--;
            updateStatusLabel();
            if (lives <= 0) {
                gameOver();
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
            updateStatusLabel();
            if (lives <= 0) {
                gameOver();
            } else {
                nextWord();
            }
        }
    }

    @Override
    public void onGameOver() {
        wordLabel.setText("");
        seenButton.setEnabled(false);
        newButton.setEnabled(false);
        removeAll();
        revalidate();
        repaint();
    }

    @Override
    public void onRestartGame() {
        // TODO: probably can do this better
        startGame();
        // add(wordLabel, BorderLayout.CENTER);
        // add(buttonPanel, BorderLayout.SOUTH);
        // seenWords.clear();
        // score = 0;
        // lives = 3;
        // seenButton.setEnabled(true);
        // newButton.setEnabled(true);
        // updateStatusLabel();
        // nextWord();
        // revalidate();
        // repaint();
    }

    @Override
    public void startGame() {
        setLayout(new BorderLayout());
        wordLabel = new JLabel("", SwingConstants.CENTER);
        wordLabel.setFont(new Font("Arial", Font.BOLD, 32));
        add(wordLabel, BorderLayout.CENTER);

        buttonPanel = new JPanel();
        seenButton = new JButton("SEEN");
        newButton = new JButton("NEW");
        buttonPanel.add(seenButton);
        buttonPanel.add(newButton);
        add(buttonPanel, BorderLayout.SOUTH);

        seenWords = new HashSet<>();
        random = new Random();
        score = 0;
        lives = 3;

        statusLabel = new JLabel();
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(statusLabel, BorderLayout.NORTH);

        nextWord();
        updateStatusLabel();

        seenButton.addActionListener(_ -> handleSeen());
        newButton.addActionListener(_ -> handleNew());
    }
}