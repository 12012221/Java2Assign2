package application;

import application.controller.Controller;
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

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();

            fxmlLoader.setLocation(getClass().getClassLoader().getResource("mainUI.fxml"));
            Pane root = fxmlLoader.load();
            primaryStage.setTitle("Player1");
            primaryStage.setScene(new Scene(root));
            primaryStage.setResizable(false);
            primaryStage.show();

            Controller controller = fxmlLoader.getController();
            controller.log.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    System.out.println(2232323);
                    Client client1 = new Client(controller.inp.getText());
                    System.out.println(controller.inp.getText());
                    System.out.println(client1.getName());
                    if (client1.getSocket() == null) {
                        try {
                            client1.setSocket(new Socket("localhost", 8912));
                            primaryStage.setTitle(String.valueOf(client1.getSocket().getPort()));
                            client1.setColor(1);
                            client1.setPanel(controller.game_panel);
                            client1.setBase_square(controller.base_square);
                            client1.setJdg(controller.jdg);
                            client1.setWel(controller.wel);
                            client1.setTurn(controller.turn);
                            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                                @Override
                                public void handle(WindowEvent windowEvent) {
                                    client1.getPs().println("Quit");
                                    client1.getPs().flush();
                                    client1.stop();
                                }
                            });
                            client1.getWel().setText("Welcome " + controller.inp.getText());
                            client1.getWel().setStyle("-fx-alignment: Center");
                            client1.getWel().setFont(Font.font("Cambria", 32));
                            client1.setWait(controller.wait);
                            controller.inp.setVisible(false);
                            controller.log.setVisible(false);
                            client1.setDs(new DataInputStream(client1.getSocket().getInputStream()));
                            client1.setPs(new PrintStream(client1.getSocket().getOutputStream()));
                            client1.start();
                            client1.getPs().println("name:"+controller.inp.getText());
                            client1.getPs().flush();
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
