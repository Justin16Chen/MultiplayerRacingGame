package netcode.client;

import java.io.DataInputStream;
import java.io.IOException;

public class ReadFromServer extends ClientStream implements Runnable {
    
    public DataInputStream dataIn;

    public ReadFromServer(ConnectFrame connectFrame, DataInputStream dataIn) {
        super(connectFrame);
        this.dataIn = dataIn;
        //System.out.println("read from server runnable created");
    }

    @Override
    public void run() {
        try {
            while (true) {
                int playerID = dataIn.readInt();
                double x = dataIn.readDouble();
                double y = dataIn.readDouble();
                //System.out.println("player #" + playerFrame.playerID + " is recieving (" + x + ", " + y + ") from player #" + playerID);
                if (playerFrame.playerID != playerID) {
                    playerFrame.players.get(playerID).setX(x);
                    playerFrame.players.get(playerID).setY(y);
                }
                //System.out.println("p" + playerID + ": x: " + x + " | y: " + y);
            }
        } catch(IOException e) {
            connectFrame.closeEverything();
            //System.out.println("error when reading data from the server");
            //e.printStackTrace();
        }
    }

    public void recieveStartingData() {
        try {
            // get the starting message from the server
            String startMessage = dataIn.readUTF();
            System.out.println("SERVER: " + startMessage);

            // get final number of players
            playerFrame.numPlayers = dataIn.readInt();

        } catch (IOException e) {
            connectFrame.closeEverything();
            //System.out.println("Error in getting player info");
            //e.printStackTrace();
        }
    }
}
