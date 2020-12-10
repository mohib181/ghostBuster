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
    public ComboBox<String> gameModeComboBox;
    public TextField player1;
    public TextField player2;
    public Button submit;
    public Label secondLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gameSizeComboBox.getItems().addAll("6x6", "8x8");
        gameModeComboBox.getItems().addAll("Human vs Human", "Human vs Computer");

        gameSizeComboBox.setValue("6x6");
        gameModeComboBox.setValue("Human vs Human");
    }

    public void gameModeChanged(ActionEvent actionEvent) {
        System.out.println(gameModeComboBox.getValue());
        if (gameModeComboBox.getValue().equals("Human vs Computer")) {
            player2.clear();
            player2.setDisable(true);
            secondLabel.setDisable(true);
        }
        else {
            player2.setDisable(false);
            secondLabel.setDisable(false);
        }
    }

    public void startGame(ActionEvent actionEvent) throws IOException {
        if (player1.getText().equals("")) player1.setText("Player1");
        if (player2.getText().equals("")) {
            if (gameModeComboBox.getValue().equals("Human vs Human")) player2.setText("Player2");
            else player2.setText("Computer");
        }
        String data = gameSizeComboBox.getValue().charAt(0) + "&&&&" + gameModeComboBox.getValue() + "&&&&" + player1.getText() + "&&&&" + player2.getText();

        System.out.println("Player 1: " + player1.getText());
        System.out.println("Player 2: " + player2.getText());
        System.out.println("Game Mode: " + gameModeComboBox.getValue());
        System.out.println("Game Size: " + gameSizeComboBox.getValue());
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
