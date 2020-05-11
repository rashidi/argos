package posmy.argos.desk.event.handler;

import com.microsoft.azure.spring.autoconfigure.aad.UserPrincipal;
import lombok.AllArgsConstructor;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.security.core.context.SecurityContextHolder;
import posmy.argos.desk.domain.Desk;
import posmy.argos.desk.domain.DeskStatus;
import posmy.argos.desk.history.domain.DeskOccupiedHistory;
import posmy.argos.desk.history.domain.DeskOccupiedHistoryRepository;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.HOURS;

/**
 * @author Rashidi Zin
 */
@RepositoryEventHandler
@AllArgsConstructor
public class DeskAfterSaveEventHandler {

    private final DeskOccupiedHistoryRepository historyRepository;

    @HandleAfterSave
    void createHistoryInformation(Desk source) {

        if (DeskStatus.VACANT == source.status()) {
            return;
        }

        var location = source.location();
        var occupant = getLoggedInUsername();
        var since = now();
        var end = since.plus(9, HOURS);

        var history = new DeskOccupiedHistory().location(location).occupant(occupant).since(since).end(end);

        historyRepository.save(history);
    }

    private String getLoggedInUsername() {
        var principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return principal.getName();
    }

}
