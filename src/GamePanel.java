import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.*;

public class GamePanel extends JPanel {
    private int boardSize;
    private MemoryTileButton[][] buttons;

    public GamePanel(int boardSize) {
	this.boardSize = boardSize;
	initializeButtons();
    }

    private void initializeButtons() {
	setLayout(new GridLayout(boardSize, boardSize));
	buttons = new MemoryTileButton[boardSize][boardSize];

	int totalButtons = boardSize * boardSize;
	ArrayList<Integer> numbers = new ArrayList<>(totalButtons);
	for (int i = 1; i <= totalButtons; i++) {
	    numbers.add(i);
	}
	Collections.shuffle(numbers);
	int index = 0;

	for (int i = 0; i < boardSize; i++) {
	    for (int j = 0; j < boardSize; j++) {

		int faceValue = numbers.get(index++);

		buttons[i][j] = new MemoryTileButton(faceValue);
		add(buttons[i][j]);

		// Add an ActionListener to handle the button flip
		buttons[i][j].addActionListener(new ListenForButton());
	    }
	}
    }

    private class ListenForButton implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
	    MemoryTileButton button = (MemoryTileButton) e.getSource();
	    button.flip();
	}
    }
}
