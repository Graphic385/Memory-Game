import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class SettingsPanel extends JPanel {
	private Board board;
	private JButton startButton;
	private JRadioButton easyMode, mediumMode, hardMode;
	private ButtonGroup modeSelection;
	private JPanel cards;
	private String gameNames[] = { "Sequence Game", "Matching Game", "Verbal Memory Game" };
	private JComboBox<String> gameSelecter;

	public SettingsPanel(Board board) {
		this.board = board;
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;

		// Create start button
		startButton = new JButton("Start Game");
		ListenForButton lForButton = new ListenForButton();
		startButton.addActionListener(lForButton);
		add(startButton, gbc);

		gbc.gridy++;
		gameSelecter = new JComboBox<String>(gameNames);
		gameSelecter.setSelectedIndex(0);
		board.gameType = GameType.sequenceGame;
		gameSelecter.setEditable(false);
		ListenForComboBox lForComboBox = new ListenForComboBox();
		gameSelecter.addItemListener(lForComboBox);
		add(gameSelecter, gbc);

		// create radio button for size selection
		easyMode = new JRadioButton("Easy Mode (3x3)");
		mediumMode = new JRadioButton("Medium Mode (4x4)");
		hardMode = new JRadioButton("Hard Mode (10x10)");

		// group radio buttons
		modeSelection = new ButtonGroup();
		modeSelection.add(easyMode);
		modeSelection.add(mediumMode);
		modeSelection.add(hardMode);
		easyMode.setSelected(true);

		// adding radio to panel
		JPanel radioPanel = new JPanel(new GridLayout(3, 1));
		radioPanel.add(easyMode);
		radioPanel.add(mediumMode);
		radioPanel.add(hardMode);

		// create the cards
		JPanel card1 = new JPanel();
		card1.add(radioPanel);

		JPanel card2 = new JPanel();
		card2.add(new JTextField("TextField", 20));

		JPanel card3 = new JPanel();
		card3.add(new JLabel("No settings for Verbal Memory Game"));

		// create the panel that contains the cards
		cards = new JPanel(new CardLayout());
		cards.add(card1, gameNames[0]);
		cards.add(card2, gameNames[1]);
		cards.add(card3, gameNames[2]);

		gbc.gridy++;
		add(cards, gbc);
		setVisible(true);
	}

	private class ListenForComboBox implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent e) {
			CardLayout cl = (CardLayout) (cards.getLayout());
			cl.show(cards, (String) e.getItem());
		}
	}

	private class ListenForButton implements ActionListener {
		@Override

		public void actionPerformed(ActionEvent e) {
			if ((String) gameSelecter.getSelectedItem() == gameNames[0]) {
				board.gameType = GameType.sequenceGame;
				if (easyMode.isSelected()) {
					board.gameDifficulty = GameDifficulty.easy;
					board.setBoardSize(3);
				} else if (mediumMode.isSelected()) {
					board.gameDifficulty = GameDifficulty.medium;
					board.setBoardSize(4);
				} else if (hardMode.isSelected()) {
					board.gameDifficulty = GameDifficulty.hard;
					board.setBoardSize(10);
				} else if (board.gameType == GameType.sequenceGame) {
					JOptionPane.showMessageDialog(SettingsPanel.this, "Please Select a Difficulty Mode",
							"No Mode Selected",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
			} else if ((String) gameSelecter.getSelectedItem() == gameNames[1]) {
				board.gameType = GameType.chimpGame;
			} else if ((String) gameSelecter.getSelectedItem() == gameNames[2]) {
				board.gameType = GameType.verbalMemoryGame;
			}

			if (e.getSource() == startButton) {
				board.startGame();
			}
		}
	}
}
