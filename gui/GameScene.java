package gui;

import javafx.geometry.Insets;
import test.Game;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;


public class GameScene {
    public Game game;
    public int gameSize;
    public Pane selectedPane;

    public Button back;
    public GridPane board;
    public Label ghostLabel;
    public Label gameSizeLabel;
    public Button advanceTimeButton;
    public Button bustButton;

    public void initGame(String size) {
        gameSize = Integer.parseInt(size);
        gameSizeLabel.setText(": " + size + "x" + size);

        game = new Game(gameSize);
        initBoard();
    }

    public void initBoard() {
        int n = gameSize;
        ghostLabel.setText("");
        ghostLabel.setFont(Font.font(15));
        ghostLabel.setStyle("-fx-font-weight: bold");

        bustButton.setDisable(true);

        for (int i = 0; i < n; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPercentWidth(100.0 / n);
            colConst.setHalignment(HPos.valueOf("CENTER"));
            board.getColumnConstraints().add(colConst);
        }
        for (int i = 0; i < n; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPercentHeight(100.0 / n);
            rowConst.setValignment(VPos.valueOf("CENTER"));
            board.getRowConstraints().add(rowConst);
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                board.getChildren().add(createPane(i, j));
            }
        }

        double defaultValue = 100.0/(n*n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                board.getChildren().add(createLabel(i, j, defaultValue));
            }
        }

        updateGameBoard();

        /*double radius = board.getColumnConstraints().get(0).getPercentWidth()*1.5;
        for (int i = 0; i < n-2; i++) {
            board.getChildren().add(createCircle(0, i+1, radius, "BLACK"));
            board.getChildren().add(createCircle(n-1, i+1, radius,"BLACK"));
        }
        for (int i = 0; i < n-2; i++) {
            board.getChildren().add(createCircle( i+1, 0, radius,"WHITE"));
            board.getChildren().add(createCircle( i+1, n-1, radius, "WHITE"));
        }*/
    }

    public Label createLabel(int row, int col, double defaultValue) {
        Label label = new Label();

        label.setId(row + "_" + col);
        label.setFont(Font.font(15));
        label.setStyle("-fx-font-weight: bold");

        label.getProperties().put("gridpane-row", row);
        label.getProperties().put("gridpane-column", col);
        label.setDisable(true);

        return label;
    }

    public Pane createPane(int row, int col) {
        Pane pane = new Pane();
        //pane.setStyle("-fx-background-color: #3C64A3; -fx-border-color: #000000");
        pane.setStyle("-fx-border-color: #000000");
        pane.getProperties().put("gridpane-row", row);
        pane.getProperties().put("gridpane-column", col);
        pane.setOnMouseClicked(this::clickOnPane);

        return pane;
    }

    public void resetAllPane() {
        for (Node node: board.getChildren()) {
            if (node instanceof Pane) {
                //node.setStyle("-fx-background-color: #3C64A3; -fx-border-color: #000000");
                node.setStyle("-fx-border-color: #000000");
            }
        }
    }

    public Label searchLabelByID(String labelID) {
        for (Node node: board.getChildren()) {
            if (node instanceof Label) {
                if (node.getId().equals(labelID)) return (Label) node;
            }
        }
        return null;
    }

    public void updateGameBoard() {
        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.CEILING);

        double[][] gameBoard = game.getBoard();
        for (int i = 0; i < gameSize; i++) {
            for (int j = 0; j < gameSize; j++) {
                searchLabelByID(i + "_" + j).setText(df.format(gameBoard[i][j]*100));
            }
        }
    }

    public void advanceTime() {
        resetAllPane();
        ghostLabel.setText("");
        bustButton.setDisable(true);

        game.advanceTime();
        updateGameBoard();
    }

    public void sense() {
        ghostLabel.setText("");
        int row = (int) selectedPane.getProperties().get("gridpane-row");
        int col = (int) selectedPane.getProperties().get("gridpane-column");

        String style;
        char color = game.sense(row, col);
        updateGameBoard();

        if (color == 'r') style = "-fx-background-color: #FF0000; -fx-border-color: #000000";
        else if (color == 'y') style = "-fx-background-color: #fcba03; -fx-border-color: #000000";
        else style = "-fx-background-color: #00ff00; -fx-border-color: #000000";

        int index = row*board.getColumnCount()+col;
        board.getChildren().get(index).setStyle(style);
    }

    public void bust() {
        int row = (int) selectedPane.getProperties().get("gridpane-row");
        int col = (int) selectedPane.getProperties().get("gridpane-column");

        boolean result = game.checkGhost(row, col);
        if (result) {
            System.out.println("found ghost");
            ghostLabel.setText("Ghost Found");
            ghostLabel.setTextFill(Paint.valueOf("green"));

            game.resetBoard();
            board.setDisable(true);
            advanceTimeButton.setDisable(true);
            bustButton.setDisable(true);
        }
        else {
            ghostLabel.setText("Ghost Not Found");
            ghostLabel.setTextFill(Paint.valueOf("red"));
            game.sense(row, col);
        }

        bustButton.setDisable(true);
        resetAllPane();
        updateGameBoard();
    }

    public void showResult(String result) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);

        window.setTitle("Ghost Check");

        Label label = new Label();
        label.setText(result);

        Button closeButton = new Button("OK");
        closeButton.setOnAction(e -> window.close());

        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(label, closeButton);
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox, 250, 100);
        window.setScene(scene);
        window.showAndWait();
    }

    public void clickOnPane(MouseEvent mouseEvent) {
        selectedPane = (Pane) mouseEvent.getSource();
        bustButton.setDisable(false);
        sense();
    }

    public void backToHomeScene(ActionEvent actionEvent) throws IOException {
        Parent homePageParent = FXMLLoader.load(getClass().getResource("homeScene.fxml"));
        Scene homePageScene = new Scene(homePageParent);

        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();

        stage.setScene(homePageScene);
        stage.setTitle("Ghost Buster | Home");
        stage.setWidth(500);
        stage.setHeight(150);
        stage.show();
    }
}