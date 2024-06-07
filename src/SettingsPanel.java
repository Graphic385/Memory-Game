import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.*;

public class SettingsPanel extends JPanel {
    private Board board;
    private JButton startButton;
    private JRadioButton easyMode, mediumMode, hardMode;
    private ButtonGroup modeSelection;

    public SettingsPanel(Board board) {
	this.board = board;
	setLayout(new BorderLayout());

	// Create start button
	startButton = new JButton("Start Game");
	ListenForButton lForButton = new ListenForButton();
	startButton.addActionListener(lForButton);
	add(startButton, BorderLayout.SOUTH);

	//create radio button for size selection
	easyMode = new JRadioButton("Easy Mode (3x3)");
	mediumMode = new JRadioButton("Medium Mode (4x4)");
	hardMode = new JRadioButton("Hard Mode (10x10)");

	// group radio buttons
	modeSelection = new ButtonGroup();
	modeSelection.add(easyMode);
	modeSelection.add(mediumMode);
	modeSelection.add(hardMode);
	
	// adding radio to panel
	JPanel radioPanel = new JPanel(new GridLayout(3, 1));
        radioPanel.add(easyMode);
        radioPanel.add(mediumMode);
        radioPanel.add(hardMode);
        add(radioPanel, BorderLayout.CENTER);
        
	setVisible(true);
    }

    private class ListenForButton implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
	    if(easyMode.isSelected()){    
		board.setBoardSize(3);
	    } else if (mediumMode.isSelected()) {
		board.setBoardSize(4);
	    } else if (hardMode.isSelected()) {
		board.setBoardSize(10);
	    } else {
		JOptionPane.showMessageDialog(SettingsPanel.this, "Please Select a Difficulty Mode", "No Mode Selected", JOptionPane.ERROR_MESSAGE);
		return;
	    }
	    if (e.getSource() == startButton) {
		board.startGame();
	    }
	}

    }
}
