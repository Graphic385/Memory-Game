package main;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public abstract class Game extends JPanel {
    protected String gameName;
    protected int score; // Score

    // For game over
    private JPanel gameOverPanel;
    private JLabel gameOverScoreLabel;
    private JTextField nameField;
    private JButton saveScoreButton, tryAgainButton, exitButton, seeLeaderboardButton;

    public Game(String gameName) {
        this.gameName = gameName;
    }

    public void titleScreen(String gameName, String instructions, Image img) {
        instructions += " Click anywhere to start.";
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new java.awt.Color(59, 153, 239)); // blue background

        // Lightning bolt icon (Unicode)
        JLabel lightningLabel = new JLabel("\u26A1");
        lightningLabel.setFont(new Font("Arial", Font.PLAIN, 120));
        lightningLabel.setForeground(java.awt.Color.WHITE);
        lightningLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(Box.createVerticalStrut(40));
        add(lightningLabel);
        add(Box.createVerticalStrut(20));

        JLabel titleLabel = new JLabel(gameName);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 72));
        titleLabel.setForeground(java.awt.Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(titleLabel);
        add(Box.createVerticalStrut(30));

        JLabel instructionsLabel = new JLabel("<html>" + instructions.replace("\n", "<br>") + "</html>");
        instructionsLabel.setFont(new Font("Arial", Font.PLAIN, 28));
        instructionsLabel.setForeground(java.awt.Color.WHITE);
        instructionsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        instructionsLabel.setHorizontalAlignment(JLabel.CENTER);
        add(instructionsLabel);
        add(Box.createVerticalStrut(20));

        // Wait for click anywhere to start
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                removeAll();
                revalidate();
                repaint();
                removeMouseListener(this);
                startGame();
            }
        });
    }

    public abstract void startGame();

    public final void reStartGame() {
        remove(gameOverPanel);
        onRestartGame();
    }

    public abstract void onRestartGame();

    public final void gameOver() {
        System.out.println("lost");
        onGameOver();
        gameOverPanel();
    }

    protected abstract void onGameOver();

    public void gameOverPanel() {

        gameOverPanel = new JPanel();
        gameOverPanel.setLayout(new BoxLayout(gameOverPanel, BoxLayout.Y_AXIS));
        JLabel gameOverLabel = new JLabel("Game Over!");
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 28));
        gameOverLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        gameOverScoreLabel = new JLabel();
        gameOverScoreLabel.setFont(new Font("Arial", Font.PLAIN, 22));
        gameOverScoreLabel.setText("Your score: " + score);
        gameOverScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameField = new JTextField(15);
        nameField.setMaximumSize(new Dimension(200, 30));
        nameField.setText("");
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveScoreButton = new JButton("Save Score");
        saveScoreButton.setEnabled(true);
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
        add(gameOverPanel); // Ensure the panel is added to the UI
        revalidate();
        repaint();

        saveScoreButton.addActionListener(_ -> {
            String name = nameField.getText().trim();
            if (!name.isEmpty()) {
                try {
                    File file = new File("src/Leaderboards/" + gameName + ".txt");
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
        seeLeaderboardButton.addActionListener(_ -> {
            try (BufferedReader reader = new BufferedReader(
                    new FileReader("src/Leaderboards/" + gameName + ".txt"))) {
                HashMap<String, Integer> leaderBoard = new HashMap<>();
                String line;
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
        tryAgainButton.addActionListener(_ -> reStartGame());
        exitButton.addActionListener(_ -> System.exit(0)); // TODO: should go to settings panel
    }
}