import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Duration;


import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private MediaPlayer player;
    private Media media;
    private String initialDir = "C:\\Users\\Andrei\\IdeaProjects\\JavaFxMediaPlayer\\src";
    private String[] extensions = {"*.mp4", "*.mpeg", "*.mpg", "*.avi", "*.divx", "*.wmv", "*.mkv"};
    private boolean isPlaying = false;
    private static SimpleBooleanProperty rewindIsPressed = new SimpleBooleanProperty(false);
    private static SimpleBooleanProperty fastForwardIsPressed = new SimpleBooleanProperty(false);
    private static boolean sliderPressed = false;
    private Long fastForwardRewindSpeed = (long) 400;

    @FXML
    public Button rewindBtn;
    @FXML
    public Button pauseBtn;
    @FXML
    public Button playStopBtn;
    @FXML
    public Button fastForward;
    @FXML
    public Slider slider;
    @FXML
    public MediaView mediaView;
    @FXML
    public GridPane gridPane;
    @FXML
    public Label loadFileTextLabel;
    @FXML
    public Slider volumeSlider;


    private void setSliderPosition() {

        player.currentTimeProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Duration duration = player.getMedia().getDuration();
                        slider.setDisable(duration.isUnknown());
                        if (!slider.isDisabled() && duration.greaterThan(Duration.ZERO) && !sliderPressed) {
                            slider.setValue((player.getCurrentTime().toMillis()) / ((duration).toMillis()) * 100);
                        }
                    }
                });
            }
        });
    }

    private void setHandlersAndListeners() {

        slider.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                sliderPressed = true;
                player.seek(media.getDuration().multiply(slider.getValue() / 100));
                sliderPressed = false;
            }
        });

        slider.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                sliderPressed = true;
            }
        });

        slider.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                sliderPressed = false;
                setSliderPosition();
            }
        });

        fastForward.setOnMousePressed(event -> {
            fastForwardIsPressed.set(true);
        });

        fastForward.setOnMouseReleased((event) -> {
            fastForwardIsPressed.set(false);
            player.play();
        });

        rewindBtn.setOnMousePressed((event -> {
            rewindIsPressed.set(true);
        }));

        rewindBtn.setOnMouseReleased((event -> {
            rewindIsPressed.set(false);
            player.play();
        }));

        rewindIsPressed.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                Double[] currentTime = new Double[1];
                currentTime[0] = player.getCurrentTime().toMillis();

                if (newValue == true) {
                    player.pause();
                    Thread t = new Thread(() -> {
                        while (rewindIsPressed.get() == true) {
                            if (currentTime[0] - fastForwardRewindSpeed >= 0) {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                player.seek(Duration.millis(currentTime[0]));
                                currentTime[0] -= fastForwardRewindSpeed;

                            }
                        }
                    });
                    t.start();
                }
            }
        });

        fastForwardIsPressed.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                Double duration = media.getDuration().toMillis();
                Double[] currentTime = new Double[1];
                currentTime[0] = player.getCurrentTime().toMillis();

                if (newValue == true) {
                    player.pause();
                    Thread t = new Thread(() -> {

                        while (fastForwardIsPressed.get() == true)
                            if (currentTime[0] + fastForwardRewindSpeed <= duration) {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                player.seek(Duration.millis(currentTime[0]));
                                currentTime[0] += fastForwardRewindSpeed;
                            }
                    });
                    t.start();
                }
            }
        });


        slider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {

                if (media != null) {
                    Duration duration = media.getDuration();
                    if (sliderPressed) {
                        player.seek(duration.multiply(slider.getValue() / 100.0));
                    }

                }
            }
        });
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gridPane.setOpacity(0);
        slider.setDisable(true);
        volumeSlider.setValue(75);


    }


    public void fadeIn(MouseEvent mouseEvent) {
        FadeTransition fi = new FadeTransition();
        fi.setNode(gridPane);
        fi.setFromValue(0);
        fi.setToValue(1);
        fi.setDuration(Duration.seconds(0.4));
        fi.play();
    }

    public void fadeOut(MouseEvent mouseEvent) {
        FadeTransition fo = new FadeTransition();
        fo.setNode(gridPane);
        fo.setFromValue(1);
        fo.setToValue(0);
        fo.setDuration(Duration.seconds(0.4));
        fo.play();
    }

    public void loadMedia() {
        FileChooser fc = new FileChooser();
        FileChooser.ExtensionFilter ef = new FileChooser.
                ExtensionFilter("video file", extensions);
        fc.getExtensionFilters().add(ef);
        fc.setTitle("Open File...");
        File inDir = new File(initialDir);
        if (inDir.exists()) fc.setInitialDirectory(inDir);
        File f = fc.showOpenDialog(Main.getPrimaryStageCopy());
        if (f != null) {

            if (player != null && !player.statusProperty().get().equals(MediaPlayer.Status.DISPOSED))
                player.dispose();
            media = new Media(f.toURI().toString());
            player = new MediaPlayer(media);
            player.volumeProperty().bind(volumeSlider.valueProperty().divide(100));
            mediaView.setMediaPlayer(player);
            slider.setDisable(false);
            setHandlersAndListeners();
            setSliderPosition();
            loadFileTextLabel.setVisible(false);
            player.play();
            isPlaying = true;
        }
    }

    public void playStop(MouseEvent mouseEvent) {
        if (isPlaying == true) {
            player.stop();
            isPlaying = false;
        } else {
            setHandlersAndListeners();
            setSliderPosition();
            player.play();
            isPlaying = true;
        }
    }

    public void pause(MouseEvent mouseEvent) {
        player.pause();
        isPlaying = false;
    }
}
