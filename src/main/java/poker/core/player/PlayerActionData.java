package poker.core.player;

public interface PlayerActionData {
    long getGameId();
    PlayerAction getPlayerAction();
    long getUserId();
    long getPlayerId();
    String getNickname();
    int getChips();
    int getPlayerBet();
    long getDateTimeMs();
}
