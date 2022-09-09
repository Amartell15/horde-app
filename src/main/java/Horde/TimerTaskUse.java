package Horde;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;

import java.io.File;
import java.util.TimerTask;

public class TimerTaskUse extends TimerTask {
    Text minutesTimer, secondsTimer;
    int seconds = 0, minutes = 0;
    int duration;
    Controller controller;
    static String musicFile = "ring.mp3"; //notification sound
    static Media sound = new Media(new File(musicFile).toURI().toString());
    static MediaPlayer mediaPlayer = new MediaPlayer(sound);

    TimerTaskUse(Text minutesTimer, Text secondsTimer, TextField duration, Controller controller) {
        this.minutesTimer = minutesTimer;
        this.secondsTimer = secondsTimer;
        this.duration = Integer.parseInt(duration.getText());
        minutes = this.duration;
        this.controller = controller;
    }

    TimerTaskUse(TimerTaskUse timerTask) {
        this.minutesTimer = timerTask.minutesTimer;
        this.secondsTimer = timerTask.secondsTimer;
        this.minutes = timerTask.minutes;
        this.seconds = timerTask.seconds;
        this.controller = timerTask.controller;
    }


    @Override
    public void run() {
        seconds--;
        if (seconds < 0) { // change minutes or stop timer
            if (minutes > 0) { // decrement minutes, set seconds to 59
                minutes--;
                seconds = 59;

                Platform.runLater(() -> {

                    setMinutesText(minutes);
                });
            }
        }
        if (seconds == 0 && minutes == 0) { //out of time
            controller.startTimer(null);
            Platform.runLater(() -> {
                User user = controller.users.remove(0);
                user.isActive = false;
                controller.users.add(user);
                controller.getStage().setAlwaysOnTop(true);

            });

            controller.timerTask = null;
            if (controller.users.stream().filter(user -> user.isActive).count() == 0) {
                controller.users.forEach(user -> user.isActive = true);
                //TimerTask timerTask = new TimerTaskUse();
            }
            mediaPlayer.play();
            cancel();
        }
        Platform.runLater(() -> {

            setSecondsText(seconds);
        });

    }

    public void setMinutesText(int minutes) {
        if (minutes > 9) {
            minutesTimer.setText(String.valueOf(minutes));
        } else {
            minutesTimer.setText("0" + minutes);
        }
    }

    public void setSecondsText(int seconds) {
        if (seconds > 9) {
            secondsTimer.setText(String.valueOf(seconds));
        } else {
            secondsTimer.setText("0" + seconds);
        }
    }
}
