package netcode.client;

import javax.swing.*;

import gameplay.*;
import input.*;
import utils.ParentFrame;

import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.awt.*;

/*public class PlayerFrame extends JFrame {

    // properties of the JFrame
    private int width, height;
    private Container contentPane;

    // gameplay elements
    public ArrayList<PlayerSprite> players;
    public ArrayList<Double[]> spawnPositions;
    private DrawingComponent dc;
    private Timer gameLoopTimer;

    // netcode elements
    private Socket socket;
    int playerID;
    int numPlayers;
    private ReadFromServer readFromServerRunnable;
    private WriteToServer writeToServerRunnable;
    private int networkInterval;


    // input
    private KeyInput keyInput;
    private MouseInput mouseInput;

    public PlayerFrame(int width, int height) {
        this.width = width;
        this.height = height;
    }

    // connects client to server
    private void connectToServer() {
        System.out.println("connecting to server");
        try {
            // create socket to connect to server
            socket = new Socket("localhost", 1234);

            // create input and output streams
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            // read playerID
            playerID = in.readInt();
            System.out.println("You are player #" + playerID);

            // read network interval
            networkInterval = in.readInt();

            // instantiate reading and writing runnables
            readFromServerRunnable = new ReadFromServer(this, in);
            writeToServerRunnable = new WriteToServer(this, out, networkInterval);

            System.out.println("created reading and writing to server runnables");
        } catch (IOException e) {
            closeEverything();
            System.out.println("failed to connect to server");
            //e.printStackTrace();
        }
    }

    public void getPlayerInfo() {
        
        readFromServerRunnable.recieveStartingData();
        
        players = new ArrayList<PlayerSprite>();
        spawnPositions = new ArrayList<Double[]>();

        for (int i=0; i<numPlayers; i++) {
            Double[] position = {100., 100.};
            spawnPositions.add(position);
        }
    }

    // sets up the window
    public void setupGUI() {
        contentPane = getContentPane();
        setTitle("Player #" + playerID);
        contentPane.setPreferredSize(new Dimension(width, height));

        createSprites();
        dc = new DrawingComponent();
        contentPane.add(dc);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }
    // creates the sprites for the game
    private void createSprites() {
        System.out.println("number of players: " + numPlayers);
        for (int i=0; i<numPlayers; i++) {
            int currentID = i;
            Double[] position = spawnPositions.get(i);
            Color color = currentID == playerID ? Color.BLUE : Color.RED;
            players.add(new PlayerSprite(position[0], position[1], 50, 50, color));
        }
    }
    
    // adds input listeners to the window
    public void setupInputListeners() {
        keyInput = new KeyInput();
        mouseInput = new MouseInput();
        this.addKeyListener(keyInput);
        this.addMouseListener(mouseInput);
        this.setFocusable(true);
    }
    
    // starts and manages the game loop
    public void startGameLoop() {
        // in milliseconds (ms) (60 FPS = 1000/60)
        int interval = (int) (1000/60);
        double secondsInterval = interval / 1000;

        // function that gets called once every [interval] ms
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                keyInput.update();
                mouseInput.update();
                updateGame(secondsInterval);
                dc.repaint();
            }
        };

        // timer class calls the actionPerformed function in ActionListener class once every [interval] ms
        gameLoopTimer = new Timer(interval, al);
        gameLoopTimer.start();
    }
    // game loop
    private void updateGame(double interval) {
        PlayerSprite player = players.get(playerID);

        int hdir = keyInput.keyDownInt("D") - keyInput.keyDownInt("A");
        int vdir = keyInput.keyDownInt("S") - keyInput.keyDownInt("W");

        player.moveX(hdir * player.getSpeed());
        player.moveY(vdir * player.getSpeed());
    }

    // starts the threads with the reading and writing to server runnables
    public void startInputOutputStreams() {
        // allow the player info to be sent to the server
        readFromServerRunnable.setPlayers(players);
        writeToServerRunnable.setPlayers(players);

        // start the runnables
        new Thread(readFromServerRunnable).start();
        new Thread(writeToServerRunnable).start();
    }
    
    // draw function
    private class DrawingComponent extends JComponent {
         
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            
            for (PlayerSprite player : players) {
                player.drawSprite(g2);
                g.drawString(String.valueOf(playerID), (int) player.getX() + 5, (int) player.getY() - 50);
                g.drawString(String.valueOf(player.getX() + ", " + player.getY()), (int) player.getX() + 5, (int) player.getY() - 20);
            }
        }
    }
    
    public void closeEverything() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        dispose();
    }
    public static void main(String[] args) {
        PlayerFrame pf = new PlayerFrame(640, 480);
        pf.connectToServer();
        pf.getPlayerInfo();
        pf.setupGUI();
        pf.setupInputListeners();
        pf.startGameLoop();
        pf.startInputOutputStreams();
    }
}
*/

public class PlayerFrame extends ParentFrame {

    protected ReadFromServer readFromServerRunnable;
    protected WriteToServer writeToServerRunnable;
    public int networkInterval;
    public int playerID;
    public int numPlayers;
    public ArrayList<PlayerSprite> players;

    public PlayerFrame(ReadFromServer readFromServerRunnable, WriteToServer writeToServerRunnable, int networkInterval, int playerID, int width, int height) {
        super("Player #" + playerID, width, height);
        this.readFromServerRunnable = readFromServerRunnable;
        this.writeToServerRunnable = writeToServerRunnable;
        this.networkInterval = networkInterval;
        this.playerID = playerID;
    }

    @Override
    public void setupGUI() {
        setupKeyboardListener();
        setupMouseListener();
    }

    public void getPlayerInfo() {
        
        readFromServerRunnable.recieveStartingData();
        
        players = new ArrayList<PlayerSprite>();

        for (int i=0; i<numPlayers; i++) {
            Double[] position = {100., 100.};
            Color color = i == playerID ? Color.BLUE : Color.RED;   // you are blue - enemies are red
            players.add(new PlayerSprite(position[0], position[1], 50, 50, color));
        }
    }

    // starts the threads with the reading and writing to server runnables
    public void startInputOutputStreams() {

        // allow the player info to be sent to the server
        readFromServerRunnable.setPlayers(players);
        writeToServerRunnable.setPlayers(players);

        // start the runnables
        new Thread(readFromServerRunnable).start();
        new Thread(writeToServerRunnable).start();
    }

    @Override
    public void update(int dt) {
        PlayerSprite player = players.get(playerID);

        int hdir = keyInput.keyDownInt("D") - keyInput.keyDownInt("A");
        int vdir = keyInput.keyDownInt("S") - keyInput.keyDownInt("W");

        //System.out.println("hdir: " + hdir + " | vdir: " + vdir);

        player.moveX(hdir * player.getSpeed());
        player.moveY(vdir * player.getSpeed());

        //System.out.println("x: " + player.getX() + " | y: " + player.getY());
    }

    @Override
    public void draw(Graphics2D g2) {
        for (PlayerSprite player : players) {
            player.drawSprite(g2);
            g2.drawString(String.valueOf(playerID), (int) player.getX() + 5, (int) player.getY() - 50);
            g2.drawString(String.valueOf(player.getX() + ", " + player.getY()), (int) player.getX() + 5, (int) player.getY() - 20);
        }
    }

}