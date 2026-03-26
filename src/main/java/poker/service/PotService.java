package poker.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.model.Pot;
import poker.repository.PotRepository;

@Service
@Log4j2
public class PotService {
    private final PotRepository potRepo;

    public PotService(PotRepository potRepo) {
        this.potRepo = potRepo;
    }

    public Pot createPot() {
        var pot = Pot.builder()
            .total(0)
            .build();
        var newPot = potRepo.save(pot);
        log.info("Created pot {}", newPot);
        return newPot;
    }

    public Pot getPotById(Long potId) {
        return potRepo.getPotById(potId);
    }
}
