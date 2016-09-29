package tech.kilocode.pomodoro.controllers;

import tech.kilocode.pomodoro.model.Attempt;
import tech.kilocode.pomodoro.model.AttemptKind;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.util.Duration;

import java.io.Console;

public class Home {
    @FXML
    private VBox container;

    @FXML
    private Label title;

    @FXML
    private TextArea message;

    @FXML
    private HBox action_buttons;

    @FXML
    private Label time;
    private Attempt mCurrentAttempt;
    private StringProperty mTimerText;
    private Timeline mTimeline;
    private AudioClip mApplause;

    public Home() {
        mTimerText = new SimpleStringProperty();
        setTimerText(25*60);
        mApplause = new AudioClip(getClass().getResource("/sounds/applause.mp3").toExternalForm());
    }

    public String getTimerText() {
        return mTimerText.get();
    }

    public StringProperty timerTextProperty() {
        return mTimerText;
    }

    public void setTimerText(String timerText) {
        mTimerText.set(timerText);
    }

    public void setTimerText(int remainingSeconds) {
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;
        setTimerText(String.format("%02d:%02d", minutes, seconds));

    }

    private void prepareAttempt(AttemptKind kind) {
        reset();
        mCurrentAttempt = new Attempt(kind, "");
        addAttemptStyle(kind);
        title.setText(kind.getDisplayName());
        setTimerText(mCurrentAttempt.getRemainingSeconds());
        mTimeline = new Timeline();
        mTimeline.setCycleCount(kind.getTotalSeconds());
        mTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), e -> {
            mCurrentAttempt.tick();
            setTimerText(mCurrentAttempt.getRemainingSeconds());
        }));
        mTimeline.setOnFinished(e -> {
            saveCurrentEvent();
            mApplause.play();
            prepareAttempt(mCurrentAttempt.getKind() == AttemptKind.FOCUS ?
                            AttemptKind.BREAK : AttemptKind.FOCUS);
        });
    }

    private void reset() {
        clearAttemptStyles();
        if (mTimeline != null && mTimeline.getStatus() == Animation.Status.RUNNING){
            mTimeline.stop();
        }
    }

    private void saveCurrentEvent() {
        mCurrentAttempt.setMessage(message.getText());
        mCurrentAttempt.save();
    }

    public void playTimer() {
        container.getStyleClass().add("playing");
        mTimeline.play();
    }

    public void pauseTimer() {
        container.getStyleClass().remove("playing");
        mTimeline.pause();
    }
    private void addAttemptStyle(AttemptKind kind) {
        container.getStyleClass().add(kind.toString().toLowerCase());
    }

    private void clearAttemptStyles() {
        container.getStyleClass().remove("playing");
        for (AttemptKind kind : AttemptKind.values()) {
            container.getStyleClass().remove(kind.toString().toLowerCase());
        }

    }

    //hides the initial view and loads the setting view.
    public void showSetting(ActionEvent actionEvent){
        action_buttons.getStyleClass().add("hide");
        message.getStyleClass().add("hide");
        time.getStyleClass().add("hide");
        System.out.printf("");
        Console console = System.console();
    }


    public void handleRestart(ActionEvent actionEvent) {
        prepareAttempt(AttemptKind.FOCUS);
        playTimer();

    }

    public void handlePlay(ActionEvent actionEvent) {
        if (mCurrentAttempt == null) {
            handleRestart(actionEvent);
        } else {
            playTimer();
        }

    }

    public void handlePause(ActionEvent actionEvent) {
        pauseTimer();
    }
}
