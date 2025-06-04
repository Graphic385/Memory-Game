package main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class GamePanel extends JPanel {
	private int boardX, boardY;
	protected MemoryTileButton[][] buttons;

	public GamePanel(int boardSize) {
		this.boardX = boardSize;
		this.boardY = boardSize;

		initializeButtons();
	}

	public GamePanel(int boardX, int boardY) {
		this.boardX = boardX;
		this.boardY = boardY;
		initializeButtons();
	}

	private void initializeButtons() {
		setLayout(new GridLayout(boardY, boardX));
		buttons = new MemoryTileButton[boardY][boardX];
		boardInitialization();
	}

	protected void boardInitialization() {
		for (int i = 0; i < boardY; i++) {
			for (int j = 0; j < boardX; j++) {

				buttons[i][j] = new MemoryTileButton();
				buttons[i][j].setText("");
				add(buttons[i][j]);

				// Add an ActionListener to handle the button flip
				buttons[i][j].addActionListener(new ListenForButton());
			}
		}
	}

	protected void buttonPressed(JButton button) {
	}

	protected class ListenForButton implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			buttonPressed((JButton) e.getSource());
		}
	}

	public int getBoardX() {
		return boardX;
	}

	public int getBoardY() {
		return boardY;
	}

	public MemoryTileButton[][] getButtons() {
		return buttons;
	}
}