package application;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

public class Server {
    private ServerSocket server;
    private int[][] board = new int[3][3];
    private int port;
    private Socket socket = null;
    private Handle handle = new Handle();
    private StartGame startGame = new StartGame();
    private ArrayList<Player> players = new ArrayList<>();

    public void init() {
        try {
            this.port = 8912;
            this.server = new ServerSocket(this.port);
            this.handle.start();
            this.startGame.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    class Handle extends Thread{
        String name1;
        String name2;
        public void run() {
            while (true){
                if (players.size() < 2){
                    try {
                        socket = server.accept();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String nn = "port "+String.valueOf(socket.getPort());
                    Player player = new Player(socket, nn);
                    synchronized (new Handle()) {
                        players.add(player);
                        if (players.size() == 1)
                            player.send("Await:Please wait");
                    }
                    player.start();
                }
                else {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    class StartGame extends Thread{
        public void run() {
            while (true){
                Player x = null;
                Player o = null;

                synchronized (new StartGame()) {
                    for (Player player:players){
                        if (!player.con){
                            x = player;
                            break;
                        }
                    }
                    for (Player player:players){
                        if (player != x && !player.con){
                            o = player;
                            break;
                        }
                    }
                }
                if (x != null && o != null){
                    System.out.println("!212121");
                    x.con = true;
                    x.color = 1;
                    o.con = true;
                    o.color = 2;
                    x.send("you:" + x.name);
                    o.send("you:" + o.name);
                    x.send("MATCH:" + o.name + ":1");
                    o.send("MATCH:" + x.name + ":2");
                    x.send("move:1");
                }
            }
        }
    }
    class Player extends Thread{
        Socket s;
        String name;
        InetAddress ip;
        int color;
        PrintStream printStream;
        DataInputStream inputStream;

        Boolean con = false;

        public void send(String msg){
            printStream.println(msg);
            printStream.flush();
        }

        public void run() {
            while (true) {
                String line = null;
                try {
                    line = inputStream.readLine();
                    if (line != null) {
                        StringTokenizer msgs = new StringTokenizer(line, ":");
                        String dir = msgs.nextToken();
                        String valueName;
                        String value;
                        if (dir.equalsIgnoreCase("MSG")) {
                            valueName = msgs.nextToken();
                            value = msgs.nextToken();
                            boolean b = false;
                            for (Player player : players) {
                                if (valueName.equals(player.name)) {   //找到匹配的player
                                    player.send("MSG:" + value);
                                } else {
                                    StringTokenizer s = new StringTokenizer(value, " ");
                                    int r = Integer.parseInt(s.nextToken());
                                    int c = Integer.parseInt(s.nextToken());
                                    board[r][c] = player.color;
                                }
                            }
                            for (int i = 0; i < 3; i++) {
                                if (board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                                    b = true;
                                    for (Player player : players) {
                                        if (board[i][0] == player.color)
                                            player.send("judge:win");
                                        else if (board[i][0] != 0) player.send("judge:lose");
                                    }
                                } else if (board[0][i] == board[1][i] && board[1][i] == board[2][i]) {
                                    b = true;
                                    for (Player player : players) {
                                        if (board[0][i] == player.color)
                                            player.send("judge:win");
                                        else if (board[0][i] != 0) player.send("judge:lose");
                                    }
                                }
                            }
                            if (board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
                                b = true;
                                for (Player player : players) {
                                    if (board[0][0] == player.color)
                                        player.send("judge:win");
                                    else if (board[0][0] != 0) player.send("judge:lose");
                                }
                            }
                            if (board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
                                b = true;
                                for (Player player : players) {
                                    if (board[2][0] == player.color)
                                        player.send("judge:win");
                                    else if (board[2][0] != 0) player.send("judge:lose");
                                }
                            }
                            for (int i = 0; i < 3; i++) {
                                for (int j = 0; j < 3; j++) {
                                    if (board[i][j] == 0) {
                                        b = true;
                                        break;
                                    }
                                }
                            }
                            if (!b) {
                                for (Player player : players) {
                                    player.send("judge:Draw");
                                }
                            }
                        } else if (dir.equalsIgnoreCase("QUIT")) {
                            quit(this);
                            return;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    quit(this);
                    return;
                }
            }
        }

        public Player(Socket s, String name) {
            this.s = s;
            try {
                printStream = new PrintStream(s.getOutputStream());
                inputStream = new DataInputStream(s.getInputStream());
                this.name = name;
                this.ip = s.getLocalAddress();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public synchronized void quit(Player player){
        for (Player cplayer : players) {
            cplayer.send("QUIT:" + player.color);
        }
        players.remove(player);
    }

}
