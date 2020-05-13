package posmy.argos.desk.event.handler;

import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import posmy.argos.desk.domain.Desk;

import static posmy.argos.desk.domain.DeskStatus.VACANT;

/**
 * @author Rashidi Zin
 */
@RepositoryEventHandler
public class DeskBeforeCreateEventHandler {

    @HandleBeforeCreate
    void setInitialStatus(Desk source) {
        source.status(VACANT);
    }

}
