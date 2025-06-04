import javax.swing.*;

public class Board extends JFrame {
	private SettingsPanel settingsPanel;
	private SequenceGame sequenceGame;
	private ChimpGame chimpGame;
	private VerbalMemoryGame verbalMemoryGame;
	private int boardSize;
	public GameDifficulty gameDifficulty;
	public GameType gameType;

	public Board() {
		setTitle("Memory Game");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(600, 600);

		settingsPanel = new SettingsPanel(this);
		add(settingsPanel);
		setVisible(true);
	}

	public void startGame() {
		remove(settingsPanel);
		if (gameType == GameType.sequenceGame) {
			sequenceGame = new SequenceGame();
			add(sequenceGame);
		} else if (gameType == GameType.chimpGame) {
			chimpGame = new ChimpGame();
			add(chimpGame);
		} else if (gameType == GameType.verbalMemoryGame) {
			verbalMemoryGame = new VerbalMemoryGame();
			add(verbalMemoryGame);
		}

		revalidate();
		repaint();
	}

	public int getBoardSize() {
		return boardSize;
	}

	public void setBoardSize(int boardSize) {
		this.boardSize = boardSize;
	}
}
