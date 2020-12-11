package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeScene implements Initializable {
    public ComboBox<String> gameSizeComboBox;
    public Button submit;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gameSizeComboBox.getItems().addAll("8x8", "9x9", "10x10", "11x11", "12x12", "13x13");
        gameSizeComboBox.setValue("9x9");
    }

    public void startGame(ActionEvent actionEvent) throws IOException {

        String data = gameSizeComboBox.getValue().substring(0,gameSizeComboBox.getValue().indexOf('x'));

        System.out.println("Game Size: " + data);
        System.out.println("game is on");


        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("gameScene.fxml"));
        Parent gamePageParent = loader.load();

        Scene gamePageScene = new Scene(gamePageParent);

        GameScene gameScene = loader.getController();
        gameScene.initGame(data);

        //This line gets the Stage information
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();

        stage.setScene(gamePageScene);
        stage.setTitle("Ghost Buster | Game");
        stage.setWidth(600);
        stage.setHeight(600);
        stage.show();
    }
}
