package posmy.argos.desk.scheduler;

import posmy.argos.desk.domain.Desk;
import posmy.argos.desk.domain.DeskLocation;
import posmy.argos.desk.domain.DeskRepository;
import posmy.argos.desk.history.domain.DeskOccupiedHistoryRepository;

import java.time.Instant;
import java.util.List;

import org.assertj.core.groups.Tuple;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;

import static posmy.argos.desk.domain.DeskStatus.OCCUPIED;
import static posmy.argos.desk.helper.DeskTestHelper.create;

import static java.time.Instant.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * @author Rashidi Zin
 */
@ExtendWith(MockitoExtension.class)
public class DeskSchedulerServiceTests {

    private final DeskRepository repository = mock(DeskRepository.class);

    private final DeskOccupiedHistoryRepository historyRepository = mock(DeskOccupiedHistoryRepository.class);

    private final DeskSchedulerService service = new DeskSchedulerService(repository, historyRepository);

    @Captor
    private ArgumentCaptor<Example<Desk>> deskCriteriaCaptor;

    @Test
    @DisplayName("Avoid finding Desk History if no OCCUPIED Desk available")
    void findInactiveOccupiedDesksWithNoOccupiedDesk() {

        doReturn(List.of()).when(repository).findAll(any(Example.class));

        var desks = service.findInactiveOccupiedDesks();

        verify(repository).findAll(deskCriteriaCaptor.capture());
        verify(historyRepository, never()).existsByLocationAndEndAfter(any(DeskLocation.class), any(Instant.class));

        var deskStatusProbe = deskCriteriaCaptor.getValue().getProbe();

        assertThat(deskStatusProbe.status()).isEqualTo(OCCUPIED);

        assertThat(desks).isEmpty();
    }

    @Test
    @DisplayName("Return empty result if Desk is still being OCCUPIED")
    void findInactiveOccupiedDesksWithActiveOccupiedDesk() {

        var activelyOccupied = create().status(OCCUPIED);

        doReturn(Lists.list(activelyOccupied)).when(repository).findAll(any(Example.class));

        doReturn(true).when(historyRepository).existsByLocationAndEndAfter(any(DeskLocation.class), any(Instant.class));

        var desks = service.findInactiveOccupiedDesks();

        verify(repository).findAll(deskCriteriaCaptor.capture());

        ArgumentCaptor<DeskLocation> locationCaptor = ArgumentCaptor.forClass(DeskLocation.class);
        ArgumentCaptor<Instant> endCaptor = ArgumentCaptor.forClass(Instant.class);

        verify(historyRepository).existsByLocationAndEndAfter(locationCaptor.capture(), endCaptor.capture());

        var location = locationCaptor.getValue();
        var end = endCaptor.getValue();

        assertThat(location).isEqualTo(activelyOccupied.location());
        assertThat(end).isBeforeOrEqualTo(now());
        assertThat(desks).isEmpty();
    }

    @Test
    void findInactiveOccupiedDesks() {
        var activelyOccupied = create().status(OCCUPIED);

        doReturn(Lists.list(activelyOccupied)).when(repository).findAll(any(Example.class));

        doReturn(false).when(historyRepository).existsByLocationAndEndAfter(any(DeskLocation.class), any(Instant.class));

        var desks = service.findInactiveOccupiedDesks();

        verify(repository).findAll(any(Example.class));
        verify(historyRepository).existsByLocationAndEndAfter(eq(activelyOccupied.location()), any(Instant.class));

        assertThat(desks)
            .hasSize(1)
            .extracting("location.row", "location.column")
            .contains(Tuple.tuple(activelyOccupied.location().row(), activelyOccupied.location().column()));
    }
}