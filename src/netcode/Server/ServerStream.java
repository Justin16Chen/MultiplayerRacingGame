package netcode.server;

import java.util.ArrayList;

// parent class for ReadFromClient and WriteToClient
// stores some shared properties
public class ServerStream {
    protected Server server;
    protected int playerID;
    protected ArrayList<Double> pxList;
    protected ArrayList<Double> pyList;

    public ServerStream(Server server, int playerID) {
        this.server = server;
        this.playerID = playerID;
        this.pxList = server.pxList;
        this.pyList = server.pyList;
    }
}
