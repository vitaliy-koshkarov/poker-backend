package poker.game;

import lombok.extern.log4j.Log4j2;

import java.util.UUID;

@Log4j2
public class GameEventStub implements GameEvent {
    private final UUID uuid;

    public GameEventStub(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public UUID getEventUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return "GameEventStub{" + uuid + "}";
    }
}
