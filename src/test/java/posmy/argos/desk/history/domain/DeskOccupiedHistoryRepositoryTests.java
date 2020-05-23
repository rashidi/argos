package posmy.argos.desk.history.domain;

import posmy.argos.desk.domain.DeskLocation;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import static posmy.argos.desk.helper.DeskTestHelper.create;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.DAYS;
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
    void findFirstByLocationOrderByEndDesc() {
        var today = now();
        var todayEnd = today.plus(9, HOURS);

        var yesterday = today.minus(1, DAYS);
        var yesterdayEnd = yesterday.plus(9, HOURS);

        var occupant = "rashidi";
        var location = new DeskLocation().row("D").column(12);

        repository.saveAll(List.of(
                new DeskOccupiedHistory().occupant(occupant).location(location).since(yesterday).end(yesterdayEnd),
                new DeskOccupiedHistory().occupant(occupant).location(location).since(today).end(todayEnd)));

        assertThat(repository.findFirstByLocationOrderByEndDesc(location)).isNotEmpty().get().satisfies(persisted -> {
            assertThat(persisted.id()).isNotNull();
            assertThat(persisted.occupant()).isEqualTo(occupant);
            assertThat(persisted.location()).isEqualTo(location);
            assertThat(persisted.since()).isAfter(yesterday).isBeforeOrEqualTo(today);
            assertThat(persisted.end()).isAfter(yesterdayEnd).isBeforeOrEqualTo(todayEnd);
        });
    }

    @Test
    void existsByLocationAndEndAfter() {
        var location = create().location();

        repository.save(
            new DeskOccupiedHistory().location(location).since(now().minus(9, HOURS)).end(now().minus(1, HOURS))
        );

        assertThat(repository.existsByLocationAndEndAfter(location, now().minus(2, HOURS))).isTrue();

        assertThat(repository.existsByLocationAndEndAfter(location, now())).isFalse();
    }

    @AfterEach
    void deleteAll() {
        repository.deleteAll();
    }

}
