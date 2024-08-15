package netcode;

import java.io.DataInputStream;
import java.io.IOException;

public class ReadFromServer extends ClientStream implements Runnable {
    
    private DataInputStream dataIn;

    public ReadFromServer(PlayerFrame playerFrame, DataInputStream dataIn) {
        super(playerFrame);
        this.dataIn = dataIn;
    }

    @Override
    public void run() {
        try {
            while (true) {
                int playerID = dataIn.readInt();
                double x = dataIn.readDouble();
                double y = dataIn.readDouble();
                if (playerFrame.playerID == playerID) {
                }
                else if (enemy.playerID == playerID) {
                    enemy.setX(x);
                    enemy.setY(y);
                }
                //System.out.println("p" + playerID + ": x: " + x + " | y: " + y);
            }
        } catch(IOException e) {
            playerFrame.closeEverything();
            //System.out.println("error when reading data from the server");
            //e.printStackTrace();
        }
    }

    public void waitForStartMessage() {
        try {
            String startMessage = dataIn.readUTF();
            System.out.println(startMessage);
        } catch(IOException e) {
            playerFrame.closeEverything();
            //System.out.println("error when waiting for the starting message");
            //e.printStackTrace();
        }
    }
}
