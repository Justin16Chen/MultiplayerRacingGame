package netcode.server;

import java.io.DataOutputStream;
import java.io.IOException;

public class WriteToClient extends ServerStream implements Runnable {

    private int interval;
    public DataOutputStream dataOut;

    public WriteToClient(Server server, int playerID, DataOutputStream dataOut, int interval) {
        super(server, playerID);
        this.dataOut = dataOut;
        this.interval = interval;
        //System.out.println("Write to client #" + playerID + " created");
    }
    @Override
    public void run() {
        while (true) {
            try {
                for (int i=0; i<pxList.size(); i++) {
                    // only send the position data for other players
                    if (playerID == i) {
                        continue;
                    }
                    // write the player index, and then their x and y position
                    //System.out.println("Writing to p" + playerID + " | p" + i + " position: " + pxList.get(i) + " | " + pyList.get(i));
                    dataOut.writeInt(i);
                    dataOut.writeDouble(pxList.get(i));
                    dataOut.writeDouble(pyList.get(i));
                    dataOut.flush();
                }
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    //System.out.println("error when thread is sleeping when writing from the server to the client");
                    //e.printStackTrace();
                    server.closeEverything();
                    break;
                }
            } catch (IOException e) {
                server.closeEverything();
                //System.out.println("error when writing to client from server");
                //e.printStackTrace();
                break;
            }
        } 
    }
    
    public void sendStartingData() {
        try {
            dataOut.writeUTF("All of the players have joined. The game will start soon!");
            dataOut.writeInt(server.currentPlayerCount);
        } catch (IOException e) {
            //System.out.println("error when sending start message from the server");
            //e.printStackTrace();
            server.closeEverything();
        }
    }
}