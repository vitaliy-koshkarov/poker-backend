package poker.core.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public enum BuyIn {
//    TODO: add mechanism to change buy-in values in runtime dynamically without the service restart
    _100(100),
    _200(200),
    _500(500),
    _1000(1000);

    private final int value;

    public static boolean isButInExists(int buyInValue) {
        for (BuyIn buyIn : values()) {
            if (buyIn.getValue() == buyInValue) {
                return true;
            }
        }
        return false;
    }
}
