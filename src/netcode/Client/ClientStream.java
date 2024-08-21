package netcode.client;

import java.util.ArrayList;

import gameplay.PlayerSprite;

public class ClientStream {

    protected PlayerFrame playerFrame;
    protected ConnectFrame connectFrame;
    protected ArrayList<PlayerSprite> players;

    public ClientStream(ConnectFrame connectFrame) {
        this.connectFrame = connectFrame;
    }
    public void setPlayerFrame(PlayerFrame playerFrame) {
        this.playerFrame = playerFrame;
    }
    public void setPlayers(ArrayList<PlayerSprite> players) {
        this.players = players;
    }
}
