import java.util.Timer;
import java.util.TimerTask;

public class GameLogic {
    private Timer timer;
    private int timeLeft;
    private int time;
    
    public GameLogic(int time) {
	this.time = time;
        timer = new Timer();
    }
    
    public void startTimer(int time) {
        timeLeft = time;
        TimerTask countdownTask = new TimerTask() {
            @Override
            public void run() {
                if (timeLeft > 0) {
                    timeLeft--;
                    System.out.println("Time left: " + timeLeft + " seconds");
                } else {
                    onTimerComplete();
                    timer.cancel(); // Stop the timer after execution
                }  
            }
        };
        System.out.println("Timer started for " + time + " seconds.");
        timer.scheduleAtFixedRate(countdownTask, 0, 1000); // Schedule the task to run every second
    }

    private void onTimerComplete() {
        // Define what happens when the timer completes
        System.out.println("Time's up!");
    }
    
    public int getTimeLeft() {
        return timeLeft; // Method to access the current time left
    }
}
