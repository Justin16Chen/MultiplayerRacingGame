package netcode;

import gameplay.PlayerSprite;

public class ClientStream {

    protected PlayerFrame playerFrame;
    protected PlayerSprite player, enemy;

    public ClientStream(PlayerFrame playerFrame) {
        this.playerFrame = playerFrame;
    }
    public void setPlayers(PlayerSprite player, PlayerSprite enemy) {
        this.player = player;
        this.enemy = enemy;
    }
}
