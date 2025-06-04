package main;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class SequenceGame extends GamePanel {
	private ArrayList<MemoryTileButton> sequencedButtons;
	private int roundNumber;
	private int playerIndex = 0;
	private JLabel roundLabel;

	public SequenceGame() {
		super(3);
		this.sequencedButtons = new ArrayList<MemoryTileButton>();

		// Initialize the round label
		roundLabel = new JLabel("Round: 1");
		roundLabel.setHorizontalAlignment(SwingConstants.CENTER);
		roundLabel.setFont(new Font("Serif", Font.BOLD, 20));

		// Add the round label to the top of the panel
		setLayout(new BorderLayout());
		add(roundLabel, BorderLayout.NORTH);

		// Add the buttons panel to the center
		JPanel buttonsPanel = new JPanel(new GridLayout(3, 3));
		MemoryTileButton[][] buttons = super.getButtons();
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				buttonsPanel.add(buttons[i][j]);
			}
		}
		add(buttonsPanel, BorderLayout.CENTER);
		startGame();
	}

	public void startGame() {
		roundNumber = 1;
		updateRoundLabel();
		generateNextSequence();
		displaySequence();
	}

	protected void buttonPressed(JButton button) {
		MemoryTileButton tileButton = (MemoryTileButton) button;
		tileButton.playerSelected();
		if (playerIndex > sequencedButtons.size()) {
			gameOver();
			return;
		}

		if (tileButton == sequencedButtons.get(playerIndex)) {
			System.out.println("correct");
			if (playerIndex + 1 == roundNumber) {
				nextRound();
				System.out.println("next round");
			} else {
				playerIndex++;
			}
		} else {
			gameOver();
		}
	}

	public void gameOver() {
		System.out.println("You lost");
	}

	public void nextRound() {
		playerIndex = 0;
		roundNumber++;
		updateRoundLabel();
		generateNextSequence();
		this.revalidate();
		this.repaint();

		// Use a Swing Timer instead of Thread.sleep
		Timer delayTimer = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				displaySequence();
			}
		});
		delayTimer.setRepeats(false);
		delayTimer.start();
	}

	private void generateNextSequence() {
		Random rand = new Random();
		int num1 = rand.nextInt(3);
		int num2 = rand.nextInt(3);

		sequencedButtons.add(super.getButtons()[num1][num2]);
	}

	private void displaySequence() {
		Timer timer = new Timer(1000, null); // Create a Timer with a 1000ms delay
		final int[] currentIndex = { 0 }; // Use an array to hold the index, so it can be modified inside the
											// ActionListener

		timer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (currentIndex[0] < sequencedButtons.size()) {
					MemoryTileButton currentButton = sequencedButtons.get(currentIndex[0]);
					currentButton.selected(500);
					currentIndex[0]++;
				} else {
					timer.stop(); // Stop the timer when all buttons in the sequence have been displayed
				}
			}
		});

		timer.setInitialDelay(0); // Start the timer immediately
		timer.start();
	}

	private void updateRoundLabel() {
		roundLabel.setText("Round: " + roundNumber);
		System.out.println(roundNumber);
	}

	public void resetGame() {
		startGame();
	}
}
