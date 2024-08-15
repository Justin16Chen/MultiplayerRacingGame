package netcode;

import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class GameServer {
    
    private ServerSocket serverSocket;
    private int numPlayers;
    private int maxPlayers;
    public int FPS = 60;
    private int networkInterval = (int) Math.round(1000 / 60);
    
    public ArrayList<Socket> pSockets;
    public ArrayList<ReadFromClient> pReadRunnables;
    public ArrayList<WriteToClient> pWriteRunnables;

    public ArrayList<Double> pxList;
    public ArrayList<Double> pyList;

    public GameServer() {
        System.out.println("======GAME SERVER======");
        numPlayers = 0;
        maxPlayers = 2;
        pSockets = new ArrayList<Socket>();
        pReadRunnables = new ArrayList<ReadFromClient>();
        pWriteRunnables = new ArrayList<WriteToClient>();
        pxList = new ArrayList<Double>();
        pyList = new ArrayList<Double>();
        pxList.add(0, 100.);
        pyList.add(0, 400.);
        pxList.add(1, 490.);
        pyList.add(1, 400.);

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

                // send network interval (interval that network sends and recieves data at)
                out.writeInt(networkInterval);

                // create reading and writing objects
                ReadFromClient readFromClient = new ReadFromClient(this, numPlayers, in);
                WriteToClient writeToClient = new WriteToClient(this, numPlayers, out, networkInterval);

                // assign proper fields
                pSockets.add(socket);
                pReadRunnables.add(readFromClient);
                pWriteRunnables.add(writeToClient);
            }
            // stat the game
            System.out.println("No longer accepting connections");
            for (int i=0; i<numPlayers; i++) {
                pWriteRunnables.get(i).sendStartMessage();
                new Thread(pReadRunnables.get(i)).start();
                new Thread(pWriteRunnables.get(i)).start();
            }
        } catch (IOException e) {
            System.out.println("Error in acceptConnections in GameServer class");
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
        gs.acceptConnections();
    }
}
