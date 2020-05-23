package posmy.argos.desk.scheduler;

import posmy.argos.desk.domain.DeskRepository;

import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;

import lombok.AllArgsConstructor;

import static posmy.argos.desk.domain.DeskStatus.VACANT;

/**
 * @author Rashidi Zin
 */
@AllArgsConstructor
public class UpdateDeskStatusScheduler {

    private final DeskRepository repository;

    private final DeskSchedulerService service;

    @Scheduled(fixedDelayString = "${argos.desk.scheduler-delay}")
    void updateExpiredOccupancyToVacant() {
        var inactiveOccupiedDesks = service.findInactiveOccupiedDesks();

        var updatedDesks = inactiveOccupiedDesks.map(desk -> desk.status(VACANT)).collect(Collectors.toList());

        if (!updatedDesks.isEmpty()) {
            repository.saveAll(updatedDesks);
        }
        
    }

}