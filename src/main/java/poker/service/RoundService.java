package poker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.model.Round;
import poker.repository.RoundRepository;
import poker.util.Util;

import java.util.HashSet;

@Service
@Log4j2
@RequiredArgsConstructor
public class RoundService {
    private final RoundRepository roundRepo;

    public long createRound(long gameId) {
        Round round = Round.builder()
            .gameId(gameId)
            .lastAggressorPlayerId(Util.LONG_ZERO)
            .lastMaxBet(Util.INT_ZERO)
            .playersToAct(new HashSet<>())
            .build();

        Round newRound = roundRepo.save(round);
        log.info("Created round {}", newRound);

        return newRound.getId();
    }
}
