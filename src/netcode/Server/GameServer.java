package netcode.Server;

import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class GameServer {
    
    protected ServerSocket serverSocket;
    protected int currentPlayerCount;
    protected int requiredPlayers;
    protected int maxPlayers;
    public int FPS = 60;
    protected int networkInterval = (int) Math.round(1000 / 60);
    int minPort = 1024;
    int maxPort = 49151;

    private ServerFrame serverFrame;
    
    protected ArrayList<Socket> pSockets;
    protected ArrayList<ReadFromClient> pReadRunnables;
    protected ArrayList<WriteToClient> pWriteRunnables;

    protected ArrayList<Double> pxList;
    protected ArrayList<Double> pyList;

    public GameServer() {
        System.out.println("======GAME SERVER======");
        currentPlayerCount = 0;
        requiredPlayers = 2;
        maxPlayers = 4;
        pSockets = new ArrayList<Socket>();
        pReadRunnables = new ArrayList<ReadFromClient>();
        pWriteRunnables = new ArrayList<WriteToClient>();
        pxList = new ArrayList<Double>();
        pyList = new ArrayList<Double>();
        for (int i=0; i<maxPlayers; i++) {
            pxList.add(100.);
            pyList.add(100.);
        }
    }

    // sets up the server GUI 
    public void setupGUI() {
        serverFrame = new ServerFrame(this, 640, 480);
    }

    // creates the server
    public boolean createServer(int port) {
        if (port < minPort || port > maxPort) {
            return false;
        }
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Error connecting to port " + port);
            return false;
        }
        return true;
    }

    // allows the server to accept clients
    public void acceptConnections() {
        try {
            System.out.println("Waiting for connections...");

            // wait for [currentPlayerCount] players
            while (currentPlayerCount < maxPlayers) {

                // accept client
                Socket socket = serverSocket.accept();

                // create input and output streams for socket connection
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                // update the number of players
                currentPlayerCount++;
                System.out.println("Player #" + currentPlayerCount + " has connected");
                System.out.println(currentPlayerCount + "/" + maxPlayers + " has joined");

                // send player id
                int playerID = currentPlayerCount - 1;
                out.writeInt(playerID);

                // send network interval (interval that network sends and recieves data at)
                out.writeInt(networkInterval);

                // create reading and writing objects
                ReadFromClient readFromClient = new ReadFromClient(this, playerID, in);
                WriteToClient writeToClient = new WriteToClient(this, playerID, out, networkInterval);

                // assign proper fields
                pSockets.add(socket);
                pReadRunnables.add(readFromClient);
                pWriteRunnables.add(writeToClient);
            }
            // stat the game
            System.out.println("No longer accepting connections");

            
        } catch (IOException e) {
            System.out.println("Error in acceptConnections in GameServer class");
        }
    }

    // starts the game for all of the clients
    public void start() {
        for (int i=0; i<currentPlayerCount; i++) {
            pWriteRunnables.get(i).sendStartingData();
            new Thread(pReadRunnables.get(i)).start();
            new Thread(pWriteRunnables.get(i)).start();
        }
    }


    // closes all of the connections
    public void closeEverything() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            for (Socket pSocket : pSockets) {
                if (pSocket != null) {
                    pSocket.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        GameServer gs = new GameServer();
        gs.setupGUI();
    }
}
