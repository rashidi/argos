package posmy.argos.desk.event.handler;

import com.microsoft.azure.spring.autoconfigure.aad.UserPrincipal;
import com.nimbusds.jwt.JWTClaimsSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import posmy.argos.desk.domain.Desk;
import posmy.argos.desk.domain.DeskLocation;
import posmy.argos.desk.history.domain.DeskOccupiedHistory;
import posmy.argos.desk.history.domain.DeskOccupiedHistoryRepository;

import java.util.List;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.HOURS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static posmy.argos.desk.domain.DeskArea.DATA_AND_TECHNOLOGY;
import static posmy.argos.desk.domain.DeskStatus.OCCUPIED;
import static posmy.argos.desk.domain.DeskStatus.VACANT;

/**
 * @author Rashidi Zin
 */
@ExtendWith(MockitoExtension.class)
class DeskAfterSaveEventHandlerTests {

    private final DeskOccupiedHistoryRepository historyRepository = mock(DeskOccupiedHistoryRepository.class);

    private final DeskAfterSaveEventHandler handler = new DeskAfterSaveEventHandler(historyRepository);

    @BeforeAll
    static void registerUserContext() {
        JWTClaimsSet claims = new JWTClaimsSet.Builder().claim("name", "rashidi").build();
        var authentication = new PreAuthenticatedAuthenticationToken(new UserPrincipal(null, claims), "", List.of());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @DisplayName("VACANT desk will not be processed")
    void createHistoryInformationWithVacantDesk() {
        var desk = new Desk().status(VACANT);

        handler.createHistoryInformation(desk);

        verify(historyRepository, never()).save(any(DeskOccupiedHistory.class));
    }

    @Test
    @DisplayName("OCCUPIED table will be stored in history with occupant, since, and, end time")
    void createHistoryInformation() {
        var location = new DeskLocation().row("D").column(12);
        var desk = new Desk().status(OCCUPIED).location(location).area(DATA_AND_TECHNOLOGY);

        handler.createHistoryInformation(desk);

        ArgumentCaptor<DeskOccupiedHistory> historyCaptor = ArgumentCaptor.forClass(DeskOccupiedHistory.class);

        verify(historyRepository).save(historyCaptor.capture());

        var persisted = historyCaptor.getValue();
        var since = persisted.since();

        assertThat(persisted)
                .satisfies(p -> {
                    assertThat(p.location()).extracting(DeskLocation::row, DeskLocation::column).containsOnly(location.row(), location.column());
                    assertThat(p.occupant()).isEqualTo("rashidi");
                    assertThat(since).isBeforeOrEqualTo(now());
                    assertThat(p.end()).isEqualTo(since.plus(9, HOURS));
                });
    }

}
