package main;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.Timer;

public class MemoryTileButton extends JButton {
  private int sequenceNumber;
  private boolean tileSelected;

  public MemoryTileButton() {
    setBackground(Color.red);
  }

  public void selected(int milliseconds) {
    Color originalColor = Color.red;
    setBackground(Color.green);

    // Create a Timer that will reset the color after 0.5 seconds (500 milliseconds)
    Timer timer = new Timer(milliseconds, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setBackground(originalColor);
      }
    });

    // Ensure the timer only runs once
    timer.setRepeats(false);
    // Start the timer
    timer.start();
  }

  public void playerSelected() {

    tileSelected = true;

    Color originalColor = Color.red;
    setBackground(Color.green);

    // Create a Timer that will reset the color after (150 milliseconds)
    Timer timer = new Timer(150, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setBackground(originalColor);
        tileSelected = false;
      }
    });

    // Ensure the timer only runs once
    timer.setRepeats(false);
    // Start the timer
    timer.start();
  }

  public int getSequenceNumber() {
    return sequenceNumber;
  }

  public void setSequenceNumber(int sequenceNumber) {
    this.sequenceNumber = sequenceNumber;
  }

  public boolean isTileSelected() {
    return tileSelected;
  }

}
