package application;

import java.io.*;
import java.net.*;
import java.util.Objects;
import java.util.StringTokenizer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.application.Platform;
import javafx.scene.text.Font;

public class Client extends Thread {
    private Socket socket = null;
    private Rectangle panel;
    private static final int BOUND = 90;
    private static final int OFFSET = 15;
    private javafx.scene.control.Button quit;
    private Pane base_square;
    private Label jdg;
    private Label wel;
    private Label turn;
    private Label oppo;
    private Label wait;
    private int current;
    private static final int[][] chessBoard = new int[3][3];
    private static final boolean[][] flag = new boolean[3][3];
    private String name;

    private String opponent;

    private int color;

    private DataInputStream inputStream;

    private PrintStream printStream;

    public Client(String name) {
        this.name = name;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    public Socket getSocket() {
        return this.socket;
    }
    public void setColor(int color) {
        this.color = color;
    }
    public int getColor() {
        return this.color;
    }

    public Rectangle getPanel() {
        return panel;
    }

    public void setPanel(Rectangle panel) {
        this.panel = panel;
    }


    public DataInputStream getDs() {
        return inputStream;
    }

    public void setDs(DataInputStream printStream) {
        this.inputStream = printStream;
    }

    public PrintStream getPs() {
        return printStream;
    }

    public void setPs(PrintStream printStream) {
        this.printStream = printStream;
    }

    public void setBase_square(Pane base_square) {
        this.base_square = base_square;
    }

    @Override
    public void run() {
        String line = null;
        while (true) {
            try {
                line = inputStream.readLine();
                System.out.println(line);
                StringTokenizer msgs = new StringTokenizer(line, ":");
                String dir = msgs.nextToken();
                String value;
                if (dir.equalsIgnoreCase("MATCH")) {
                    System.out.println(line);
                    String oppo = msgs.nextToken();
                    opponent = oppo;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            wait.setText("Game Started with " + opponent);
                            wait.setFont(Font.font("Cambria", 20));
                            wait.setStyle("-fx-alignment: Center");
                        }
                    });
                    String clr = msgs.nextToken();
                    color = Integer.parseInt(clr);
                } else if (dir.equalsIgnoreCase("Await")){
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            wait.setText("Please wait");
                            wait.setFont(Font.font("Cambria", 25));
                            wait.setStyle("-fx-alignment: Center");
                        }
                    });
                } else if (dir.equalsIgnoreCase("MSG")){
                    value = msgs.nextToken();

                    current = color;
                    fill(value);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            turn.setText("Your turn");
                            turn.setFont(Font.font("Cambria", 15));
                        }
                    });
                    play();
                } else if (dir.equalsIgnoreCase("move")) {
                    value = msgs.nextToken();
                    current = color;
                    play();
                } else if (dir.equalsIgnoreCase("you")) {
                    value = msgs.nextToken();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            wel.setText("Your port: "+value);
                        }
                    });
                } else if (dir.equalsIgnoreCase("Disconnected")) {
                    value = msgs.nextToken();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            wait.setText("Disconnected");
                            wait.setFont(Font.font("Cambria", 32));
                            panel.setOnMouseClicked(null);
                        }
                    });
                } else if (dir.equalsIgnoreCase("judge")) {
                    value = msgs.nextToken();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (!Objects.equals(value, "Draw")) {
                                wait.setText("You " + value);
                            } else wait.setText(value);
                            wait.setFont(Font.font("Cambria", 32));
                            turn.setVisible(false);
                            panel.setOnMouseClicked(null);
                        }
                    });
                } else if (dir.equalsIgnoreCase("Quit")) {
                    value = msgs.nextToken();
                    String ou;
                    if (Integer.parseInt(value) != color) {
                        ou = "Opponent quit.";
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                wait.setText(ou);
                                wait.setFont(Font.font("Cambria", 32));
                                panel.setOnMouseClicked(null);
                            }
                        });
                    } else {
                        ou = "You quit.";
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                wait.setText(ou);
                                wait.setFont(Font.font("Cambria", 32));
                                panel.setOnMouseClicked(null);
                            }
                        });
                        try {
                            inputStream.close();
                            printStream.close();
                            socket.close();
                            this.interrupt();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        wait.setText("Disconnected!");
                        panel.setOnMouseClicked(null);
                    }
                });
                break;
            }
        }
    }


    private void fill(String value){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                StringTokenizer s = new StringTokenizer(value, " ");
                int i = Integer.parseInt(s.nextToken());
                int j = Integer.parseInt(s.nextToken());
                refreshBoard(i, j, color == 1 ? 2 : 1);
                System.out.println(color);
            }
        });

    }
    private void play() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                panel.setOnMouseClicked(event -> {
                    int x = (int) (event.getX() / BOUND);
                    int y = (int) (event.getY() / BOUND);
                    if (refreshBoard(x, y, color)) {
                        printStream.println("MSG:" + opponent + ":" + x + " " + y);
                        printStream.flush();
                        current = -color;
                    }
                    turn.setText("Opponent Turn");
                    turn.setFont(Font.font("Cambria", 15));
                });
            }
        });
    }
    private boolean refreshBoard (int x, int y, int color) {
        if (chessBoard[x][y] == 0 && current == this.color) {
            chessBoard[x][y] = color;
            drawChess();
            return true;
        }
        return false;
    }

    private void drawChess () {
        for (int i = 0; i < chessBoard.length; i++) {
            for (int j = 0; j < chessBoard[0].length; j++) {
                if (flag[i][j]) {
                    // This square has been drawing, ignore.
                    continue;
                }
                switch (chessBoard[i][j]) {
                    case 2:
                        drawCircle(i, j);
                        break;
                    case 1:
                        drawLine(i, j);
                        break;
                    case 0:
                        // do nothing
                        break;
                    default:
                        System.err.println("Invalid value!");
                }
            }
        }
    }

    private void drawCircle(int i, int j) {
        Circle circle = new Circle();
        base_square.getChildren().add(circle);
        circle.setCenterX(i * BOUND + BOUND / 2.0 + OFFSET);
        circle.setCenterY(j * BOUND + BOUND / 2.0 + OFFSET);
        circle.setRadius(BOUND / 2.0 - OFFSET / 2.0);
        circle.setStroke(Color.RED);
        circle.setFill(Color.TRANSPARENT);
        flag[i][j] = true;

    }

    private void drawLine (int i, int j) {
        Line line_a = new Line();
        Line line_b = new Line();
        base_square.getChildren().add(line_a);
        base_square.getChildren().add(line_b);
        line_a.setStartX(i * BOUND + OFFSET * 1.5);
        line_a.setStartY(j * BOUND + OFFSET * 1.5);
        line_a.setEndX((i + 1) * BOUND + OFFSET * 0.5);
        line_a.setEndY((j + 1) * BOUND + OFFSET * 0.5);
        line_a.setStroke(Color.BLUE);

        line_b.setStartX((i + 1) * BOUND + OFFSET * 0.5);
        line_b.setStartY(j * BOUND + OFFSET * 1.5);
        line_b.setEndX(i * BOUND + OFFSET * 1.5);
        line_b.setEndY((j + 1) * BOUND + OFFSET * 0.5);
        line_b.setStroke(Color.BLUE);
        flag[i][j] = true;
    }

    public Label getJdg() {
        return jdg;
    }

    public void setJdg(Label jdg) {
        this.jdg = jdg;
    }

    public Label getWel() {
        return wel;
    }

    public void setWel(Label wel) {
        this.wel = wel;
    }

    public Label getWait() {
        return wait;
    }

    public void setWait(Label wait) {
        this.wait = wait;
    }

    public Button getQuit() {
        return quit;
    }

    public void setQuit(Button quit) {
        this.quit = quit;
    }

    public Label getTurn() {
        return turn;
    }

    public void setTurn(Label turn) {
        this.turn = turn;
    }
}
