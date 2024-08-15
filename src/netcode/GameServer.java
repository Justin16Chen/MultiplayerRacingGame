package netcode;

import java.net.*;
import java.io.*;

public class GameServer {
    
    private ServerSocket serverSocket;
    private int numPlayers;
    private int maxPlayers;
    private int networkInterval = (int) Math.round(1000 / 60);
    
    private Socket p1Socket;
    private Socket p2Socket;
    private ReadFromClient p1ReadRunnable, p2ReadRunnable;
    private WriteToClient p1WriteRunnable, p2WriteRunnable;

    private double p1x, p1y, p2x, p2y;

    public GameServer() {
        System.out.println("======GAME SERVER======");
        numPlayers = 0;
        maxPlayers = 2;
        p1x = 100;
        p1y = 400;
        p2x = 490;
        p2y = 400;

        try {
            serverSocket = new ServerSocket(1234);
        } catch (IOException e) {
            System.out.println("Error in GameServer constructor");
            e.printStackTrace();
            closeEverything();
        }
    }
    
    public void acceptConnections() {
        try {
            System.out.println("Waiting for connections...");

            // wait for [numPlayers] players
            while (numPlayers < maxPlayers) {

                // accept client
                Socket socket = serverSocket.accept();

                // create input and output streams for socket connection
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                // update the number of players
                numPlayers++;
                System.out.println("Player #" + numPlayers + " has connected");
                System.out.println(numPlayers + "/" + maxPlayers + " has joined");

                // send player id
                out.writeInt(numPlayers);

                // create reading and writing objects
                ReadFromClient readFromClient = new ReadFromClient(numPlayers, in);
                WriteToClient writeToClient = new WriteToClient(numPlayers, out, networkInterval);

                // assign proper fields
                if (numPlayers == 1) {
                    p1Socket = socket;
                    p1ReadRunnable = readFromClient;
                    p1WriteRunnable = writeToClient;
                } else {
                    p2Socket = socket;
                    p2ReadRunnable = readFromClient;
                    p2WriteRunnable = writeToClient;
                }
            }
            System.out.println("No longer accepting connections");
            p1WriteRunnable.sendStartMessage();
            p2WriteRunnable.sendStartMessage();
            new Thread(p1ReadRunnable).start();
            new Thread(p2ReadRunnable).start();
            new Thread(p1WriteRunnable).start();
            new Thread(p2WriteRunnable).start();
        } catch (IOException e) {
            System.out.println("Error in acceptConnections in GameServer class");
        }
    }

    private class ReadFromClient implements Runnable {

        private int playerID;
        private DataInputStream dataIn;

        public ReadFromClient(int playerID, DataInputStream dataIn) {
            this.playerID = playerID;
            this.dataIn = dataIn;
            System.out.println("Read from client #" + playerID + " created");
        }
        @Override
        public void run() {
            while (true) {
                try {
                    if (playerID == 1) {
                        p1x = dataIn.readDouble();
                        p1y = dataIn.readDouble();
                    } else {
                        p2x = dataIn.readDouble();
                        p2y = dataIn.readDouble();
                    }
                } catch (IOException e) {
                    System.out.println("Player #" + playerID + " has left the game");
                    closeEverything();
                    break;
                }
            }
            
        }
    }
    private class WriteToClient implements Runnable {

        private int interval;
        private int playerID;
        private DataOutputStream dataOut;

        public WriteToClient(int playerID, DataOutputStream dataOut, int interval) {
            this.playerID = playerID;
            this.dataOut = dataOut;
            System.out.println("Write to client #" + playerID + " created");
        }
        @Override
        public void run() {
            while (true) {
                try {
                    if (playerID == 1) {
                        dataOut.writeDouble(p2x);
                        dataOut.writeDouble(p2y);
                        dataOut.flush();
                    } else {
                        dataOut.writeDouble(p1x);
                        dataOut.writeDouble(p1y);
                        dataOut.flush();
                    }
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException e) {
                        //System.out.println("error when thread is sleeping when writing from the server to the client");
                        //e.printStackTrace();
                        closeEverything();
                        break;
                    }
                } catch (IOException e) {
                    closeEverything();
                    break;
                    //System.out.println("error when writing to client from server");
                    //e.printStackTrace();
                }
            }
            
        }
    
        public void sendStartMessage() {
            try {
                dataOut.writeUTF("Both players have joined. The game will start soon!");
            } catch (IOException e) {
                //System.out.println("error when sending start message from the server");
                //e.printStackTrace();
                closeEverything();
            }
        }
    }

    private void closeEverything() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            if (p1Socket != null) {
                p1Socket.close();
            }
            if (p2Socket != null) {
                p2Socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        GameServer gs = new GameServer();
        gs.acceptConnections();
    }
}
