import java.awt.*;
import javax.swing.*;

public class Board extends JFrame {
    private GamePanel gamePanel;
    private SettingsPanel settingsPanel;
    private GameLogic gameLogic;
    private int boardSize;

    public Board() {
	setTitle("Memory Game");
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setSize(600,600);

	settingsPanel = new SettingsPanel(this);
	add(settingsPanel);
	setVisible(true);

    }

    protected void startGame() {
	remove(settingsPanel);
	gamePanel = new GamePanel(boardSize);
	add(gamePanel);
	//gameLogic = new GameLogic(2);
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
