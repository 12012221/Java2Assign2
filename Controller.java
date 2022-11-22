package application.controller;

import application.Client;
//import application.Listener;
import application.Server;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.awt.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

public class Controller implements Initializable{
    private static final int PLAY_1 = 0;
    private static final int PLAY_2 = 1;

    private static final int EMPTY = -1;
    private static final int BOUND = 90;
    private static final int OFFSET = 15;
    @FXML
    public javafx.scene.control.Button log;
    @FXML
    public javafx.scene.control.TextField inp;

    private Server server = new Server();
    @FXML
    public Pane base_square;
    @FXML
    public Label jdg;
    @FXML
    public Label wel;
    @FXML
    public Label turn;
    @FXML
    public Label oppo;
    @FXML
    public Label wait;

    @FXML
    public Rectangle game_panel;

    @FXML
    public javafx.scene.control.Button bt;

    public Controller(){};

    private static boolean TURN = false;

    private static final int[][] chessBoard = new int[3][3];
    private static final boolean[][] flag = new boolean[3][3];

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
