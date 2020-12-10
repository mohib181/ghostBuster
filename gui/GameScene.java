package gui;

import test.Main;
import test.Position;

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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


public class GameScene {
    public int gameSize;
    //public Game game;

    public String gameMode;
    public Circle selectedCircle;
    public Pane selectedPane;

    public Button back;
    public GridPane board;
    public Label turnLabel;
    public Label moveLabel;
    public Label gameSizeLabel;
    public Label gameModeLabel;
    public Label player1NameLabel;
    public Label player2NameLabel;
    public Label player1ColorLabel;
    public Label player2ColorLabel;

    public void initGame(String string) {
        initData(string);               //game data Initialization
        initBoard();                    //game board Initialization
        //game = new Game(gameSize);      //game initialization
    }

    public void initData(String string) {
        String[] data = string.split("&&&&");

        gameSize = Integer.parseInt(data[0]);
        gameSizeLabel.setText(": " + data[0] + "x" + data[0]);
    }

    public void initBoard() {
        int n = gameSize;
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

        double defaultValue = 1.0/n;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                board.getChildren().add(createLabel(i, j, defaultValue));
            }
        }

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
        label.setText(Double.toString(defaultValue));
        label.setDisable(true);
        label.getProperties().put("gridpane-row", row);
        label.getProperties().put("gridpane-column", col);

        return label;
    }

    public Circle createCircle(int row, int col, double radius, String color) {
        Circle circle = new Circle(radius);

        circle.setId(color + "_" + row + "_" + col);
        circle.setFill(Paint.valueOf(color));
        circle.getProperties().put("gridpane-row", row);
        circle.getProperties().put("gridpane-column", col);
        circle.setOnMouseClicked(this::showMoves);

        return circle;
    }

    public Pane createPane(int row, int col) {
        Pane pane = new Pane();
        pane.setStyle("-fx-background-color: #3C64A3; -fx-border-color: #000000");
        pane.getProperties().put("gridpane-row", row);
        pane.getProperties().put("gridpane-column", col);
        pane.setOnMouseClicked(this::clickOnPane);

        return pane;
    }

    public void resetAllPane() {
        for (Node node: board.getChildren()) {
            if (node instanceof Pane) {
                node.setStyle("-fx-background-color: #3C64A3; -fx-border-color: #000000");
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

    public Circle searchCircleByID(String circleID) {
        for (Node node: board.getChildren()) {
            if (node instanceof Circle) {
                if (node.getId().contains(circleID)) return (Circle) node;
            }
        }
        return null;
    }


    public void makeMove(Circle circle, Pane pane) {
        int row = (int) circle.getProperties().get("gridpane-row");
        int col = (int) circle.getProperties().get("gridpane-column");
        int moveAheadRow = (int) pane.getProperties().get("gridpane-row");
        int moveAheadCol = (int) pane.getProperties().get("gridpane-column");

        Circle moveAheadCircle = searchCircleByID(moveAheadRow + "_" + moveAheadCol);
        if (moveAheadCircle != null) board.getChildren().remove(moveAheadCircle);                   //removing the circle in target pane if there is any(Capture Move)

        board.getChildren().remove(circle);

        //updating circle position
        circle.getProperties().clear();
        circle.getProperties().put("gridpane-row", moveAheadRow);
        circle.getProperties().put("gridpane-column", moveAheadCol);

        //setting up circleID with the new Position
        String circleID = circle.getId().replace("_" + row + "_" + col, "_" + moveAheadRow + "_" + moveAheadCol);
        circle.setId(circleID);

        board.getChildren().add(circle);

        //currentPlayer.makeMove(game.getBoard(), row, col, moveAheadRow, moveAheadCol);
        //System.out.println(currentPlayer.getPlayerName() + ": (" + row + "," + col + ")--->(" + moveAheadRow + "," + moveAheadCol + ")" );
        //game.printBoard();

        //checkWinner();
    }


    public void showWinner(String winnerName) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);

        window.setTitle("Winner");

        Label winnerLabel = new Label();
        winnerLabel.setText(winnerName + " has won");

        Button closeButton = new Button("OK");
        closeButton.setOnAction(e -> window.close());

        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(winnerLabel, closeButton);
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox, 250, 100);
        window.setScene(scene);
        window.showAndWait();
    }

    public void clickOnPane(MouseEvent mouseEvent) {
        Pane pane = (Pane) mouseEvent.getSource();
        selectedPane = (Pane) mouseEvent.getSource();
        //activate two buttons sense and bust
        if (pane.getStyle().equals("-fx-background-color: #00d974; -fx-border-color: #000000")) {
            makeMove(selectedCircle, pane);
        }
        resetAllPane();
    }

    public void showMoves(MouseEvent mouseEvent) {
        /*resetAllPane();
        selectedCircle = (Circle) mouseEvent.getSource();

        int row = (int) selectedCircle.getProperties().get("gridpane-row");
        int col = (int) selectedCircle.getProperties().get("gridpane-column");

        int index = row*board.getColumnCount()+col;
        board.getChildren().get(index).setStyle("-fx-background-color: #fcba03; -fx-border-color: #000000");

        ArrayList<Position> moves = currentPlayer.generateMoves(game.getBoard(), row, col);
        for (Position pos: moves) {
            index = pos.getX()*board.getColumnCount()+pos.getY();
            board.getChildren().get(index).setStyle("-fx-background-color: #00d974; -fx-border-color: #000000");
        }*/
        //System.out.println("Showed All Possible Moves");
    }

    public void backToHomeScene(ActionEvent actionEvent) throws IOException {
        Parent homePageParent = FXMLLoader.load(getClass().getResource("homeScene.fxml"));
        Scene homePageScene = new Scene(homePageParent);

        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();

        stage.setScene(homePageScene);
        stage.setTitle("Ghost Buster | Home");
        stage.setWidth(600);
        stage.setHeight(300);
        stage.show();
    }
}