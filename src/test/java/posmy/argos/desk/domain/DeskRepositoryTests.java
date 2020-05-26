package posmy.argos.desk.domain;

import posmy.argos.containers.mongodb.MongoDBTestContainerSetup;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Example;

import static posmy.argos.desk.domain.DeskArea.COLLABORATION;
import static posmy.argos.desk.domain.DeskArea.DATA_AND_TECHNOLOGY;
import static posmy.argos.desk.domain.DeskStatus.OCCUPIED;
import static posmy.argos.desk.domain.DeskStatus.VACANT;
import static posmy.argos.desk.helper.DeskTestHelper.create;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Index.atIndex;
import static org.assertj.core.groups.Tuple.tuple;

/**
 * @author Rashidi Zin
 */
@DataMongoTest
class DeskRepositoryTests extends MongoDBTestContainerSetup {
    
    @Autowired
    private DeskRepository repository;
        
    @Test
    void findByStatus() {
        var desk = new Desk().area(DATA_AND_TECHNOLOGY).location(new DeskLocation().row("D").column(12)).status(VACANT);

        repository.save(desk);

        assertThat(repository.findByStatus(VACANT))
                .hasSize(1)
                .satisfies(persisted -> {
                        assertThat(persisted.id()).isNotNull();
                        assertThat(persisted.location()).extracting(DeskLocation::row, DeskLocation::column).containsOnly("D", 12);
                        }, atIndex(0)
                )
                .extracting(Desk::area, Desk::status)
                .containsOnly(tuple(DATA_AND_TECHNOLOGY, VACANT));

        assertThat(repository.findByStatus(OCCUPIED)).isEmpty();
    }

    @Test
    void findByAreaAndStatus() {
        var location = new DeskLocation().row("D").column(12);
        var desk = new Desk().location(location).area(DATA_AND_TECHNOLOGY).status(VACANT);

        repository.save(desk);

        assertThat(repository.findByAreaAndStatus(DATA_AND_TECHNOLOGY, VACANT))
                .hasSize(1)
                .extracting(Desk::area, Desk::location, Desk::status)
                .containsOnly(tuple(DATA_AND_TECHNOLOGY, location, VACANT));

        assertThat(repository.findByAreaAndStatus(COLLABORATION, VACANT)).isEmpty();
    }

    @Test
    void saveWithSelfAssignedId() {
        var id = "36bb818b-23f4-483e-b858-ae8d9ff04e63";
        var location = new DeskLocation().row("D").column(12);

        var desk = new Desk().id(id).area(DATA_AND_TECHNOLOGY).location(location).status(VACANT);

        var persisted = repository.save(desk);

        assertThat(repository.findById(id))
                .isNotEmpty()
                .get()
                .isEqualTo(persisted);
    }

    @Test
    void existsByLocationAndStatus() {
        var persisted = repository.save(create().status(OCCUPIED));

        assertThat(
                repository.exists(Example.of(new Desk().location(persisted.location()).status(OCCUPIED)))
        )
                .isTrue();

        assertThat(
                repository.exists(Example.of(new Desk().location(persisted.location()).status(VACANT)))
        )
                .isFalse();

        assertThat(
                repository.exists(Example.of(new Desk().location(new DeskLocation().row("UB").column(40)).status(OCCUPIED)))
        )
                .isFalse();

        assertThat(
                repository.exists(Example.of(new Desk().location(new DeskLocation().row("UB").column(40)).status(VACANT)))
        )
                .isFalse();
    }

    @Test
    void activeByOccupant() {
        var desk = repository.save(create().status(OCCUPIED).occupant("rashidi"));

        assertThat(
                repository.exists(Example.of(new Desk().status(OCCUPIED).occupant(desk.occupant())))
        )
                .isTrue();

        assertThat(
                repository.exists(Example.of(new Desk().status(OCCUPIED).occupant("liam")))
        )
                .isFalse();

        assertThat(
                repository.exists(Example.of(new Desk().status(VACANT).occupant(desk.occupant())))
        )
                .isFalse();

        assertThat(
                repository.exists(Example.of(new Desk().status(VACANT).occupant("liam")))
        )
                .isFalse();
    }

    @AfterEach
    void deleteAll() {
        repository.deleteAll();
    }

}
