package netcode;

import java.io.DataOutputStream;
import java.io.IOException;

public class WriteToClient extends ServerStream implements Runnable {

    private int interval;
    private DataOutputStream dataOut;

    public WriteToClient(GameServer gameServer, int playerID, DataOutputStream dataOut, int interval) {
        super(gameServer, playerID);
        this.dataOut = dataOut;
        this.interval = interval;
        //System.out.println("Write to client #" + playerID + " created");
    }
    @Override
    public void run() {
        while (true) {
            try {
                for (int i=0; i<pxList.size(); i++) {
                    int currentID = i + 1;
                    // only send the position data for other players
                    if (playerID == currentID) {
                        continue;
                    }
                    // write the player index, and then their x and y position
                    dataOut.writeInt(currentID);
                    dataOut.writeDouble(pxList.get(i));
                    dataOut.writeDouble(pyList.get(i));
                    dataOut.flush();
                    //System.out.println("Writing to p" + playerID + " | p" + currentID + " position: " + pxList.get(i) + " | " + pyList.get(i));
                }
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    //System.out.println("error when thread is sleeping when writing from the server to the client");
                    //e.printStackTrace();
                    gameServer.closeEverything();
                    break;
                }
            } catch (IOException e) {
                gameServer.closeEverything();
                //System.out.println("error when writing to client from server");
                //e.printStackTrace();
                break;
            }
        } 
    }
    public void sendStartMessage() {
        try {
            dataOut.writeUTF("All of the players have joined. The game will start soon!");
        } catch (IOException e) {
            //System.out.println("error when sending start message from the server");
            //e.printStackTrace();
            gameServer.closeEverything();
        }
    }
}