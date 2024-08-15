package netcode;

import java.util.ArrayList;

// parent class for ReadFromClient and WriteToClient
// stores some shared properties
public class ServerStream {
    protected GameServer gameServer;
    protected int playerID;
    protected ArrayList<Double> pxList;
    protected ArrayList<Double> pyList;

    public ServerStream(GameServer gameServer, int playerID) {
        this.gameServer = gameServer;
        this.playerID = playerID;
        this.pxList = gameServer.pxList;
        this.pyList = gameServer.pyList;
    }
}
