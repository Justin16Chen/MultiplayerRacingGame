package netcode.client;

import java.io.DataOutputStream;
import java.io.IOException;

import gameplay.PlayerSprite;

public class WriteToServer extends ClientStream implements Runnable {
        
    private int interval;
    private DataOutputStream dataOut;

    public WriteToServer(ConnectFrame connectFrame, DataOutputStream dataOut, int interval) {
        super(connectFrame);
        this.dataOut = dataOut;
        this.interval = interval;
        //System.out.println("write to server runnable created");
    }

    @Override
    public void run() {
        try {
            while(true) {
                PlayerSprite player = players.get(playerFrame.playerID);
                
                // send the x and y
                dataOut.writeDouble(player.getX());
                dataOut.writeDouble(player.getY());
                dataOut.flush();
                //System.out.println("ID: " + playerFrame.playerID + " | x: " + player.getX() + " | y: " + player.getY());
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    connectFrame.closeEverything();
                    //System.out.println("Interrupted while player #" + playerID + " thread was sleeping");
                }
            }
        } catch (IOException e) {
            connectFrame.closeEverything();
            //System.out.println("error when writing to server on player " + playerID);
            //e.printStackTrace();
        }
    }
}