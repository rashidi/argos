package posmy.argos.desk.event.handler;

import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import posmy.argos.desk.domain.Desk;

import static posmy.argos.security.SecurityHelper.getLoggedInUsername;

/**
 * @author Rashidi Zin
 */
@RepositoryEventHandler
public class DeskBeforeSaveEventHandler {

    @HandleBeforeSave
    void assignOccupant(Desk desk) {
        desk.occupant(getLoggedInUsername());
    }

}
