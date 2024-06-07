import javax.swing.*;

public class MemoryTileButton extends JButton {
    private boolean isFlipped;
    private int faceValue;

    public MemoryTileButton(int faceValue) {
	this.faceValue = faceValue;
	this.isFlipped = false;
	setText("");
    }

    public void flip() {
	if (isFlipped) {
	    setText("");
	} else {
	    setText(String.valueOf(faceValue));
	}
	isFlipped = !isFlipped;
    }
    
    public int getFaceValue() {
	return faceValue;
    }
    
    public boolean isFlipped() {
	return isFlipped;
    }
    
}
