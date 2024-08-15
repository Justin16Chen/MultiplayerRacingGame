package netcode.Server;

import java.io.DataInputStream;
import java.io.IOException;

public class ReadFromClient extends ServerStream implements Runnable {

    private DataInputStream dataIn;

    public ReadFromClient(GameServer gameServer, int playerID, DataInputStream dataIn) {
        super(gameServer, playerID);
        this.dataIn = dataIn;
        //System.out.println("Read from client #" + playerID + " created");
    }
    @Override
    public void run() {
        while (true) {
            try {
                double x = dataIn.readDouble();
                double y = dataIn.readDouble();
                //System.out.println("ID: " + playerID + " | x: " + x + " | y: " + y);
                pxList.set(playerID, x);
                pyList.set(playerID, y);
            }
            catch (IOException e) {
                System.out.println("Player #" + playerID + " has left the game");
                gameServer.closeEverything();
                break;
            }
        }
    }
}