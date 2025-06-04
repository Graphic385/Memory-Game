import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class VerbalMemoryGame extends JPanel {
    private JLabel wordLabel;
    private JButton seenButton, newButton;
    private JPanel buttonPanel;
    private Set<String> seenWords;
    private String currentWord;
    private int score;
    private int lives;
    private Random random;
    private String[] wordBank = {
            "apple", "banana", "car", "dog", "elephant", "flower", "guitar", "house", "island", "jungle",
            "kite", "lemon", "mountain", "notebook", "orange", "piano", "queen", "river", "sun", "tree",
            "umbrella", "violin", "window", "xylophone", "yacht", "zebra", "cloud", "desk", "engine", "forest",
            "garden", "hat", "ice", "jacket", "key", "lamp", "mirror", "nest", "ocean", "pencil",
            "quilt", "road", "star", "train", "unicorn", "vase", "whale", "x-ray", "yogurt", "zipper"
    };
    private JPanel gameOverPanel;
    private JTextField nameField;
    private JButton saveScoreButton, tryAgainButton, exitButton, seeLeaderboardButton;
    private JLabel gameOverScoreLabel;
    private JLabel statusLabel;

    public VerbalMemoryGame() {
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
        setupGameOverPanel();
        updateStatusLabel();

        seenButton.addActionListener(e -> handleSeen());
        newButton.addActionListener(e -> handleNew());
    }

    private void setupGameOverPanel() {
        gameOverPanel = new JPanel();
        gameOverPanel.setLayout(new BoxLayout(gameOverPanel, BoxLayout.Y_AXIS));
        JLabel gameOverLabel = new JLabel("Game Over!");
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 28));
        gameOverLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        gameOverScoreLabel = new JLabel();
        gameOverScoreLabel.setFont(new Font("Arial", Font.PLAIN, 22));
        gameOverScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameField = new JTextField(15);
        nameField.setMaximumSize(new Dimension(200, 30));
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveScoreButton = new JButton("Save Score");
        tryAgainButton = new JButton("Try Again");
        exitButton = new JButton("Exit");
        seeLeaderboardButton = new JButton("See Leaderboard");
        saveScoreButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        tryAgainButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        seeLeaderboardButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        gameOverPanel.add(Box.createVerticalStrut(20));
        gameOverPanel.add(gameOverLabel);
        gameOverPanel.add(Box.createVerticalStrut(10));
        gameOverPanel.add(gameOverScoreLabel);
        gameOverPanel.add(Box.createVerticalStrut(10));
        gameOverPanel.add(new JLabel("Enter your name to save score:"));
        gameOverPanel.add(nameField);
        gameOverPanel.add(Box.createVerticalStrut(10));
        gameOverPanel.add(saveScoreButton);
        gameOverPanel.add(Box.createVerticalStrut(10));
        gameOverPanel.add(seeLeaderboardButton);
        gameOverPanel.add(Box.createVerticalStrut(10));
        gameOverPanel.add(tryAgainButton);
        gameOverPanel.add(Box.createVerticalStrut(10));
        gameOverPanel.add(exitButton);
        gameOverPanel.add(Box.createVerticalStrut(20));

        saveScoreButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (!name.isEmpty()) {
                try {
                    File file = new File("src/Leaderboards/VerbalMemoryGame.txt");
                    ArrayList<String> lines = new ArrayList<>();
                    boolean found = false;
                    int bestScore = score;
                    // Read all lines and check for existing name
                    if (file.exists()) {
                        BufferedReader reader = new BufferedReader(new FileReader(file));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.startsWith(name + ":")) {
                                found = true;
                                String[] parts = line.split(":");
                                if (parts.length == 2) {
                                    int oldScore = Integer.parseInt(parts[1].trim());
                                    if (score > oldScore) {
                                        lines.add(name + ": " + score);
                                    } else {
                                        lines.add(line); // keep old score if it's better
                                        bestScore = oldScore;
                                    }
                                } else {
                                    lines.add(line);
                                }
                            } else {
                                lines.add(line);
                            }
                        }
                        reader.close();
                    }
                    if (!found) {
                        lines.add(name + ": " + score);
                    }
                    BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
                    for (String l : lines) {
                        writer.write(l);
                        writer.newLine();
                    }
                    writer.close();
                    gameOverScoreLabel.setText("Score saved for " + name + ": " + bestScore);
                } catch (IOException | NumberFormatException e1) {
                    gameOverScoreLabel.setText("Score failed to save.");
                    e1.printStackTrace();
                }
                saveScoreButton.setEnabled(false);
            }
        });
        seeLeaderboardButton.addActionListener(e -> {
            try {
                BufferedReader reader = new BufferedReader(new FileReader("src/Leaderboards/VerbalMemoryGame.txt"));
                HashMap<String, Integer> leaderBoard = new HashMap<>();
                String line;
                String name;
                while ((line = reader.readLine()) != null) {
                    String[] info = line.split(": ");
                    leaderBoard.put(info[0], Integer.parseInt(info[1]));
                }
                ArrayList<Entry<String, Integer>> entries = new ArrayList<>(leaderBoard.entrySet());
                entries.sort((a, b) -> b.getValue().compareTo(a.getValue())); // descending order
                for (Entry<String, Integer> e2 : entries) {
                    System.out.println(e2.getKey() + ": " + e2.getValue());
                }

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });
        tryAgainButton.addActionListener(e -> resetGame());
        exitButton.addActionListener(e -> System.exit(0));
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

    private void gameOver() {
        wordLabel.setText("");
        seenButton.setEnabled(false);
        newButton.setEnabled(false);
        removeAll();
        gameOverScoreLabel.setText("Your score: " + score);
        nameField.setText("");
        saveScoreButton.setEnabled(true);
        add(gameOverPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void resetGame() {
        remove(gameOverPanel);
        add(wordLabel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        seenWords.clear();
        score = 0;
        lives = 3;
        seenButton.setEnabled(true);
        newButton.setEnabled(true);
        updateStatusLabel();
        nextWord();
        revalidate();
        repaint();
    }
}
