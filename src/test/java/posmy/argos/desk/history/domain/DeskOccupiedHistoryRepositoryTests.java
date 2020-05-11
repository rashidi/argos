package posmy.argos.desk.history.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import posmy.argos.desk.domain.DeskLocation;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.HOURS;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Rashidi Zin
 */
@DataMongoTest
class DeskOccupiedHistoryRepositoryTests {

    @Autowired
    private DeskOccupiedHistoryRepository repository;

    @Test
    void findByLocationAndEndAfter() {
        var location = new DeskLocation().row("D").column(12);
        var since = now();
        var end = since.plus(9, HOURS);

        var history = new DeskOccupiedHistory()
                .location(location)
                .occupant("rashidi")
                .since(since)
                .end(end);

        repository.save(history);

        assertThat(repository.findByLocationAndEndAfter(location, now()))
                .isNotEmpty()
                .get()
                .satisfies(persisted -> {
                    assertThat(persisted.id()).isNotNull();
                    assertThat(persisted.occupant()).isEqualTo(history.occupant());
                    assertThat(persisted.location()).extracting(DeskLocation::row, DeskLocation::column).containsOnly("D", 12);
                    assertThat(persisted.since()).isBeforeOrEqualTo(since);
                    assertThat(persisted.end()).isBeforeOrEqualTo(end);
                });
    }

    @AfterEach
    void deleteAll() {
        repository.deleteAll();
    }

}
