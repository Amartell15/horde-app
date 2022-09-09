package Horde;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.util.converter.IntegerStringConverter;

import java.util.*;
import java.util.regex.Pattern;

public class Controller {
    @FXML
    public TextField breakTime;
    @FXML
    public TextField duration;
    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Button addName;
    @FXML
    private Text minutesTimer;
    @FXML
    private Text currentMobber;
    @FXML
    private Text secondsTimer;
    @FXML
    private ListView<User> nextQueue;

    TimerTask timerTask, pauseTimerTask;
    private Stage stage;
    boolean pause, isBreak;
    int period = 1000;

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    // Create the time intervals
    ObservableList<User> users = FXCollections.observableArrayList(Database.getUsers());

    public void initStuff() {
        FXCollections.sort(users, Comparator.comparingInt(User::getOrderIndex));
        nextQueue.setItems(users);
        nextQueue.setCellFactory(new UserListCellFactory());
        currentMobber.setTextAlignment(TextAlignment.CENTER);
        if (!users.isEmpty()) {
            currentMobber.setText(users.get(0).name);
        } else {
            currentMobber.setText("Mobber");
        }
        final TextFormatter<Integer> durationFormatter = new TextFormatter<>(
                new IntegerStringConverter(), 15, c -> Pattern.matches("\\d*", c.getText()) ? c : null);
        duration.setTextFormatter(durationFormatter);
        final TextFormatter<Integer> breakFormatter = new TextFormatter<>(
                new IntegerStringConverter(), 5, c -> Pattern.matches("\\d*", c.getText()) ? c : null);
        breakTime.setTextFormatter(breakFormatter);

    }

    @FXML
    void addName(ActionEvent event) {
        //add a name to the database from the user
        TextInputDialog addName = new TextInputDialog();

        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Add User");

        // Set button types
        ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField name = new TextField();
        name.setPromptText("John Doe");
        TextField email = new TextField();
        email.setPromptText("John.doe@selu.com");

        gridPane.add(new Label("Name:"), 0, 0);
        gridPane.add(name, 1, 0);
        gridPane.add(new Label("Email:"), 0, 1);
        gridPane.add(email, 1, 1);

        dialog.getDialogPane().setContent(gridPane);

        // Request focus on the username field by default
        Platform.runLater(() -> name.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(name.getText(), email.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(pair -> {
            String newName = pair.getKey();
            User user = new User(pair.getValue(), users.size() + 1, true, newName);
            Database.newUser(newName, user);
            users.add(user);
            if (users.size() == 1) {
                currentMobber.setText(users.get(0).name);
            }
            System.out.println("Name =" + pair.getKey() + ", Email =" + pair.getValue());
        });
    }

    @FXML
    void pauseTimer(ActionEvent event) {
        if (timerTask != null && !pause) {
            pause = true;
            pauseTimerTask = new TimerTaskUse((TimerTaskUse) timerTask);
            timerTask.cancel();
        }

    }

    @FXML
    public void startTimer(ActionEvent event) {
        // the button is pressed --> the timer starts
        if (pause) {
            Timer timer = new Timer();
            timer.schedule(pauseTimerTask, 0, period);
            pause = false;
            timerTask = pauseTimerTask;
        } else if (timerTask == null) {
            getStage().setAlwaysOnTop(false);
            if (users.stream().noneMatch(user -> user.isActive)) {
                int time = Integer.parseInt(breakTime.getText());
                if (time > 9) {
                    minutesTimer.setText(breakTime.getText());
                } else {
                    minutesTimer.setText("0" + breakTime.getText());
                }
                Timer timer = new Timer();
                timerTask = new TimerTaskUse(minutesTimer, secondsTimer, breakTime, this);
                timer.schedule(timerTask, 0, period);
                users.stream().forEach(user -> user.isActive = true);
                secondsTimer.setText("00");
            } else {
                currentMobber.setText(users.get(0).name);
                secondsTimer.setText("00");
                timerTask = new TimerTaskUse(minutesTimer, secondsTimer, duration, this);
                Timer timer = new Timer();
                timer.schedule(timerTask, 0, period);
                int time = Integer.parseInt(duration.getText());
                if (time > 9) {
                    minutesTimer.setText(duration.getText());
                } else {
                    minutesTimer.setText("0" + duration.getText());
                }
            }
        }
    }

    @FXML
    void stopTimer(ActionEvent event) {
        if (pause) {
            timerTask = null;
            pause = false;
        } else if (timerTask != null) {
            FXCollections.sort(users, Comparator.comparingInt(User::getOrderIndex));
            timerTask.cancel();
            timerTask = null;
        }
        minutesTimer.setText("00");
        secondsTimer.setText("00");
    }

    @FXML
    void submitTime(ActionEvent event) {
        if (isBreak) {
            setMinutesText(breakTime.getText());
        } else {
            setMinutesText(duration.getText());
        }
        //set break Time
    }

    public void moveUser(DragEvent dragEvent) { //drag to move around users

    }

    public void handleKeyPress(KeyEvent keyEvent) {
        if (keyEvent.getSource() == nextQueue) {
            if (keyEvent.getCode() == KeyCode.DELETE && keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
                User user = nextQueue.getFocusModel().getFocusedItem();
                deleteUser(user);
                if (user.equals(currentMobber.getText())) {
                    currentMobber.setText("Mobber");
                }
            }
        }
        if (keyEvent.getCode() == KeyCode.ENTER && keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {

            if (keyEvent.getSource() == duration) {
                setMinutesText(duration.getText());
            } else if (keyEvent.getSource() == breakTime) {
                setMinutesText(duration.getText());
            }
        }
    }

    private void deleteUser(User user) {
        users.remove(user);
        Database.deleteUser(user);
    }

    private void setMinutesText(String minutes) {
        if (minutes.length() >= 2) {
            minutesTimer.setText(minutes);
        } else {
            minutesTimer.setText("0" + minutes);
        }
    }

    public void skipButton(ActionEvent actionEvent) {
        User user = users.remove(0);
        user.isActive = false;
        users.add(user);
        if (timerTask != null) {
            timerTask.cancel();
            minutesTimer.setText("00");
            secondsTimer.setText("00");
            timerTask = null;
        }
        if (users.stream().noneMatch(user1 -> user1.isActive)) {
            currentMobber.setText("Break");
            setMinutesText(breakTime.getText());
        } else {
            currentMobber.setText(users.get(0).name);
        }

    }

    public void randomButton(ActionEvent actionEvent) {
        Collections.shuffle(users, new Random());
        currentMobber.setText(users.get(0).name);
        timerTask.cancel();
        timerTask = null;
    }
} // end controller class