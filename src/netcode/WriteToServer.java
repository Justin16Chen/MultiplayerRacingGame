package netcode;

import java.io.DataOutputStream;
import java.io.IOException;

public class WriteToServer extends ClientStream implements Runnable {
        
    private int interval;
    private DataOutputStream dataOut;

    public WriteToServer(PlayerFrame playerFrame, DataOutputStream dataOut, int interval) {
        super(playerFrame);
        this.dataOut = dataOut;
        this.interval = interval;
    }

    @Override
    public void run() {
        try {
            while(true) {
                // send the x and y
                dataOut.writeDouble(player.getX());
                dataOut.writeDouble(player.getY());
                dataOut.flush();
                //System.out.println("ID: " + playerFrame.playerID + " | x: " + player.getX() + " | y: " + player.getY());
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    playerFrame.closeEverything();
                    //System.out.println("Interrupted while player #" + playerID + " thread was sleeping");
                }
            }
        } catch (IOException e) {
            playerFrame.closeEverything();
            //System.out.println("error when writing to server on player " + playerID);
            //e.printStackTrace();
        }
    }
}