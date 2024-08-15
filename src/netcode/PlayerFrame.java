package netcode;
import javax.swing.*;

import gameplay.*;
import input.*;

import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.awt.*;

public class PlayerFrame extends JFrame {

    // properties of the JFrame
    private int width, height;
    private Container contentPane;

    // gameplay elements
    private PlayerSprite player;
    private PlayerSprite enemy;
    private DrawingComponent dc;
    private Timer gameLoopTimer;

    // netcode elements
    private Socket socket;
    int playerID;
    private ReadFromServer readFromServerRunnable;
    private WriteToServer writeToServerRunnable;
    private int networkInterval = (int) Math.round(1000 / 60);


    // input
    private KeyInput keyInput;
    private MouseInput mouseInput;

    public PlayerFrame(int width, int height) {
        this.width = width;
        this.height = height;
    }

    // connects client to server
    private void connectToServer() {
        try {
            // create socket to connect to server
            socket = new Socket("localhost", 1234);

            // create input and output streams
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            // read playerID
            playerID = in.readInt();
            System.out.println("You are player #" + playerID);

            // instantiate reading and writing runnables
            readFromServerRunnable = new ReadFromServer(in);
            writeToServerRunnable = new WriteToServer(out, networkInterval);
            readFromServerRunnable.waitForStartMessage();

        } catch (IOException e) {
            closeEverything();
            //System.out.println("Error in connectToServer in PlayerFrame class");
            //e.printStackTrace();
        }
    }

    // sets up the window
    public void setupGUI() {
        contentPane = this.getContentPane();
        this.setTitle("Player #" + playerID);
        contentPane.setPreferredSize(new Dimension(width, height));

        createSprites();
        dc = new DrawingComponent();
        contentPane.add(dc);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
    }
    // creates the sprites for the game
    private void createSprites() {
        if (playerID == 1) {
            player = new PlayerSprite(100, 400, 50, 50, Color.BLUE);
            enemy  = new PlayerSprite(490, 400, 50, 50, Color.RED );
        } else {
            player = new PlayerSprite(490, 400, 50, 50, Color.RED );
            enemy  = new PlayerSprite(100, 400, 50, 50, Color.BLUE);
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
        int hdir = keyInput.keyDownInt("D") - keyInput.keyDownInt("A");
        int vdir = keyInput.keyDownInt("S") - keyInput.keyDownInt("W");

        player.moveX(hdir * player.getSpeed());
        player.moveY(vdir * player.getSpeed());
    }

    // starts the threads with the reading and writing to server runnables
    public void startInputOutputStreams() {
        new Thread(readFromServerRunnable).start();
        new Thread(writeToServerRunnable).start();
    }
    // draw function
    private class DrawingComponent extends JComponent {
         
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            player.drawSprite(g2);
            enemy.drawSprite(g2);
        }
    }

    private class ReadFromServer implements Runnable {
        
        private DataInputStream dataIn;

        public ReadFromServer(DataInputStream dataIn) {
            this.dataIn = dataIn;
            System.out.println("read from server runnable created");
        }

        @Override
        public void run() {
            try {
                while (true) {
                    enemy.setX(dataIn.readDouble());
                    enemy.setY(dataIn.readDouble());
                }
            } catch(IOException e) {
                closeEverything();
                //System.out.println("error when reading data from the server");
                //e.printStackTrace();
            }
        }

        public void waitForStartMessage() {
            try {
                String startMessage = dataIn.readUTF();
                System.out.println(startMessage);
            } catch(IOException e) {
                closeEverything();
                //System.out.println("error when waiting for the starting message");
                //e.printStackTrace();
            }
        }
    }

    private class WriteToServer implements Runnable {
        
        private int interval;
        private DataOutputStream dataOut;

        public WriteToServer(DataOutputStream dataOut, int interval) {
            this.dataOut = dataOut;
            this.interval = interval;
            System.out.println("write to server runnable created");
        }

        @Override
        public void run() {
            try {
                while(true) {
                    // send the x and y
                    dataOut.writeDouble(player.getX());
                    dataOut.writeDouble(player.getY());
                    dataOut.flush();
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException e) {
                        closeEverything();
                        //System.out.println("Interrupted while player #" + playerID + " thread was sleeping");
                    }
                }
            } catch (IOException e) {
                closeEverything();
                //System.out.println("error when writing to server on player " + playerID);
                //e.printStackTrace();
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
    }
    public static void main(String[] args) {
        PlayerFrame pf = new PlayerFrame(640, 480);
        pf.connectToServer();
        pf.setupGUI();
        pf.setupInputListeners();
        pf.startGameLoop();
        pf.startInputOutputStreams();
    }
}
