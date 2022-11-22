package application;

import application.controller.Controller2;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class Main2 extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();

            fxmlLoader.setLocation(getClass().getClassLoader().getResource("mainUI2.fxml"));
            Pane root = fxmlLoader.load();
            primaryStage.setTitle("Player2");
            primaryStage.setScene(new Scene(root));
            primaryStage.setResizable(false);
            primaryStage.show();

            Controller2 controller2 = fxmlLoader.getController();
            controller2.log.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    System.out.println(2232323);
                    Client client2 = new Client(controller2.inp.getText());
                    System.out.println(controller2.inp.getText());
                    System.out.println(client2.getName());
                    if (client2.getSocket() == null) {
                        try {
                            client2.setSocket(new Socket("localhost", 8912));
                            primaryStage.setTitle(String.valueOf(client2.getSocket().getPort()));
                            client2.setColor(2);
                            client2.setPanel(controller2.game_panel);
                            client2.setBase_square(controller2.base_square);
                            client2.setJdg(controller2.jdg);
                            client2.setWel(controller2.wel);
                            client2.setTurn(controller2.turn);
                            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                                @Override
                                public void handle(WindowEvent windowEvent) {
                                    client2.getPs().println("Quit");
                                    client2.getPs().flush();
                                    client2.stop();
                                }
                            });
                            client2.getWel().setText("Welcome "+controller2.inp.getText());
                            client2.getWel().setStyle("-fx-alignment: Center");
                            client2.getWel().setFont(Font.font("Cambria", 32));
                            client2.setWait(controller2.wait);
                            controller2.inp.setVisible(false);
                            controller2.log.setVisible(false);
                            client2.setDs(new DataInputStream(client2.getSocket().getInputStream()));
                            client2.setPs(new PrintStream(client2.getSocket().getOutputStream()));
                            client2.start();
                            client2.getPs().println("name:"+controller2.inp.getText());
                            client2.getPs().flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
