package Components;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Timer {

    private int seconds = 0;
    private Timeline timeline;
    private Runnable onTick; // callback för GUI:t

    public Timer(Runnable onTick) {
        this.onTick = onTick;
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            seconds++;
            onTick.run();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    public void start() {
        timeline.play();
    }

    public void stop() {
        timeline.pause();
    }

    public void reset() {
        stop();
        seconds = 0;
        onTick.run();
    }

    public int getSeconds() {
        return seconds;
    }

    public String getFormattedTime() {
        int min = seconds / 60;
        int sec = seconds % 60;
        return String.format("%02d:%02d", min, sec);
    }
}
