package posmy.argos.desk.scheduler;

import posmy.argos.desk.domain.Desk;
import posmy.argos.desk.domain.DeskRepository;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;

import static posmy.argos.desk.domain.DeskStatus.OCCUPIED;
import static posmy.argos.desk.domain.DeskStatus.VACANT;
import static posmy.argos.desk.helper.DeskTestHelper.create;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * @author Rashidi Zin
 */
@ExtendWith(MockitoExtension.class)
class UpdateDeskStatusSchedulerTests {

    private final DeskRepository repository = mock(DeskRepository.class);

    private final DeskSchedulerService service = mock(DeskSchedulerService.class);

    private final UpdateDeskStatusScheduler scheduler = new UpdateDeskStatusScheduler(repository, service);

    @Captor
    private ArgumentCaptor<List<Desk>> updatedDeskCaptor;

    @Test
    @DisplayName("Repository.saveAll will not be triggered if inactive occupied desks are not available")
    void updateExpiredOccupancyToVacantWithoutOccupiedDesk() {

        doReturn(Stream.empty()).when(service).findInactiveOccupiedDesks();

        scheduler.updateExpiredOccupancyToVacant();

        verify(service).findInactiveOccupiedDesks();
        verify(repository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("Status will be changed to VACANT before persisting")
    void findInactiveOccupiedDesks() {
        var occupiedDesk = create().status(OCCUPIED);

        doReturn(Stream.of(occupiedDesk)).when(service).findInactiveOccupiedDesks();

        scheduler.updateExpiredOccupancyToVacant();

        verify(repository).saveAll(updatedDeskCaptor.capture());

        List<Desk> updatedDesks = updatedDeskCaptor.getValue();

        assertThat(updatedDesks)
            .hasSize(1)
            .extracting(Desk::status)
            .containsOnly(VACANT);
    }

}