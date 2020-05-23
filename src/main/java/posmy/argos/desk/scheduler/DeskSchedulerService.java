package posmy.argos.desk.scheduler;

import posmy.argos.desk.domain.Desk;
import posmy.argos.desk.domain.DeskRepository;
import posmy.argos.desk.history.domain.DeskOccupiedHistoryRepository;

import java.util.ArrayList;
import java.util.stream.Stream;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;

import static posmy.argos.desk.domain.DeskStatus.OCCUPIED;

import static java.time.Instant.now;

/**
 * @author Rashidi Zin
 */
@Service
@AllArgsConstructor
public class DeskSchedulerService {

    private final DeskRepository repository;

    private final DeskOccupiedHistoryRepository historyRepository;

    /**
     * Find list of Desks that are marked as OCCUPIED but no longer being occupied.
     */
    @Transactional(readOnly = true)
    public Stream<Desk> findInactiveOccupiedDesks() {
        var withStatusOccupied = Example.of(new Desk().status(OCCUPIED));
        
        var occupiedDesks = repository.findAll(withStatusOccupied);
        var inactiveOccupiedDesks = new ArrayList<Desk>();

        for (Desk desk : occupiedDesks) {
            var isInactive = !historyRepository.existsByLocationAndEndAfter(desk.location(), now());

            if (isInactive) {
                inactiveOccupiedDesks.add(desk);
            }
        }

        return inactiveOccupiedDesks.stream();
    }

}