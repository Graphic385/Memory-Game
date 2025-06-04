package main;

import java.util.ArrayList;
import java.util.Random;
import javax.swing.JButton;

public class ChimpGame extends GamePanel {
	private ArrayList<MemoryTileButton> sequencedButtons;
	static private int boardX = 7;
	static private int boardY = 5;
	private int currentNumber;
	private int playerIndex;
	private boolean firstClick;

	public ChimpGame() {
		super(boardX, boardY);
		this.sequencedButtons = new ArrayList<MemoryTileButton>();

		startGame();
	}

	protected void boardInitialization() {
		for (int i = 0; i < boardY; i++) {
			for (int j = 0; j < boardX; j++) {

				buttons[i][j] = new MemoryTileButton();
				buttons[i][j].setText("");
				buttons[i][j].setVisible(false);
				add(buttons[i][j]);

				// Add an ActionListener to handle the button flip
				buttons[i][j].addActionListener(new ListenForButton());
			}
		}
	}

	private void startGame() {
		currentNumber = 0;
		playerIndex = 0;
		firstClick = false;
		generateFirstNumbers();
	}

	private void nextRound() {
		playerIndex = 0;
		firstClick = false;
		generateNumber();
		displayNumbers();
	}

	private void generateFirstNumbers() {
		for (int i = 0; i <= 5; i++) {
			System.out.println(i);
			generateNumber();
			displayNumbers();
		}
	}

	private void displayNumbers() {
		for (int i = 0; i < sequencedButtons.size(); i++) {
			sequencedButtons.get(i).setVisible(true);
			sequencedButtons.get(i).setText(Integer.toString(i + 1));
		}
	}

	private void hideButtons() {
		for (int i = 0; i < sequencedButtons.size(); i++) {
			sequencedButtons.get(i).setText("");
		}
	}

	private void generateNumber() {
		currentNumber++;
		Random rand = new Random();
		int randX = rand.nextInt(boardY);
		int randY = rand.nextInt(boardX);
		System.out.println(checkValidTile(randX, randY));
		if (checkValidTile(randX, randY)) {
			sequencedButtons.add(buttons[randX][randY]);
		}
	}

	private boolean checkValidTile(int x, int y) {
		for (int i = 0; i < sequencedButtons.size(); i++) {
			if (buttons[x][y] == sequencedButtons.get(i)) {
				return false;
			}
		}
		return true;
	}

	protected void buttonPressed(JButton button) {
		MemoryTileButton tileButton = (MemoryTileButton) button;
		tileButton.setVisible(false);

		if (firstClick == false) {
			hideButtons();
			firstClick = true;
		}

		if (playerIndex >= sequencedButtons.size()) {
			gameOver();
			return;
		}

		System.out.println(sequencedButtons.size());
		System.out.println(playerIndex);
		if (tileButton == sequencedButtons.get(playerIndex)) {
			System.out.println("correct");
			if (playerIndex + 1 == currentNumber) {
				nextRound();
				System.out.println("next round");
			} else {
				playerIndex++;
			}
		} else {
			gameOver();
		}
	}

	private void gameOver() {
		System.out.println("Game Over");
	}
}