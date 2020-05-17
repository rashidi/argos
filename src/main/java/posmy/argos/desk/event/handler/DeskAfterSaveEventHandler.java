package posmy.argos.desk.event.handler;

import lombok.AllArgsConstructor;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import posmy.argos.desk.context.DeskProperties;
import posmy.argos.desk.domain.Desk;
import posmy.argos.desk.domain.DeskStatus;
import posmy.argos.desk.history.domain.DeskOccupiedHistory;
import posmy.argos.desk.history.domain.DeskOccupiedHistoryRepository;

import java.util.function.Consumer;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.HOURS;
import static posmy.argos.security.SecurityHelper.getLoggedInUsername;

/**
 * @author Rashidi Zin
 */
@RepositoryEventHandler
@AllArgsConstructor
public class DeskAfterSaveEventHandler {

    private final DeskOccupiedHistoryRepository historyRepository;

    private final DeskProperties properties;

    @HandleAfterSave
    void createHistoryInformation(Desk source) {

        if (DeskStatus.VACANT == source.status()) {
            return;
        }

        var location = source.location();
        var occupant = getLoggedInUsername();
        var since = now();
        var end = since.plus(properties.getPeriod().getMaxHour(), HOURS);

        var history = new DeskOccupiedHistory().location(location).occupant(occupant).since(since).end(end);

        historyRepository.save(history);
    }

    @HandleAfterSave
    void updateHistoryEndTime(Desk source) {

        if (DeskStatus.OCCUPIED == source.status()) {
            return;
        }

        var location = source.location();

        Consumer<DeskOccupiedHistory> updateEndTime = p -> {
            p.end(now());
            historyRepository.save(p);
        };

        historyRepository
                .findFirstByLocationOrderByEndDesc(location)
                .ifPresent(updateEndTime);
    }

}
