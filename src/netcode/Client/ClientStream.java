package netcode.Client;

import java.util.ArrayList;

import gameplay.PlayerSprite;

public class ClientStream {

    protected PlayerFrame playerFrame;
    protected ArrayList<PlayerSprite> players;

    public ClientStream(PlayerFrame playerFrame) {
        this.playerFrame = playerFrame;
    }
    public void setPlayers(ArrayList<PlayerSprite> players) {
        this.players = players;
    }
}
