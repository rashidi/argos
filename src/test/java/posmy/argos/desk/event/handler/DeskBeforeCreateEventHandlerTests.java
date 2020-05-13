package posmy.argos.desk.event.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import posmy.argos.desk.domain.Desk;
import posmy.argos.desk.domain.DeskLocation;

import static org.assertj.core.api.Assertions.assertThat;
import static posmy.argos.desk.domain.DeskArea.DATA_AND_TECHNOLOGY;
import static posmy.argos.desk.domain.DeskStatus.OCCUPIED;
import static posmy.argos.desk.domain.DeskStatus.VACANT;

/**
 * @author Rashidi Zin
 */
class DeskBeforeCreateEventHandlerTests {

    private final DeskBeforeCreateEventHandler handler = new DeskBeforeCreateEventHandler();

    @Test
    @DisplayName("Initial status will be VACANT")
    void setInitialStatus() {
        var location = new DeskLocation().row("D").column(12);
        var desk = new Desk().location(location).area(DATA_AND_TECHNOLOGY).status(OCCUPIED);

        handler.setInitialStatus(desk);

        assertThat(desk.status()).isEqualTo(VACANT);
    }

}
