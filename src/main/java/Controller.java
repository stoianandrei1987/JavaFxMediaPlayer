import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    public Button previousBtn;
    @FXML
    public Button rewindBtn;
    @FXML
    public Button pauseBtn;
    @FXML
    public Button stopBtn;
    @FXML
    public Button playBtn;
    @FXML
    public Button fastForward;
    @FXML
    public Slider slider;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }


}
