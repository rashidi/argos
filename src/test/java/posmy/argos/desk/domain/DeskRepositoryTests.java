package posmy.argos.desk.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Index.atIndex;
import static org.assertj.core.groups.Tuple.tuple;
import static posmy.argos.desk.domain.DeskArea.COLLABORATION;
import static posmy.argos.desk.domain.DeskArea.DATA_AND_TECHNOLOGY;
import static posmy.argos.desk.domain.DeskStatus.OCCUPIED;
import static posmy.argos.desk.domain.DeskStatus.VACANT;

/**
 * @author Rashidi Zin
 */
@DataMongoTest
class DeskRepositoryTests {

    @Autowired
    private DeskRepository repository;

    @Test
    void findByStatus() {
        var desk = new Desk();

        desk.setArea(DATA_AND_TECHNOLOGY);
        desk.setLocation(new DeskLocation("D", 12));
        desk.setStatus(VACANT);

        repository.save(desk);

        assertThat(repository.findByStatus(VACANT))
                .hasSize(1)
                .satisfies(persisted -> {
                        assertThat(persisted.getId()).isNotNull();
                        assertThat(persisted.getLocation()).extracting(DeskLocation::getRow, DeskLocation::getColumn).containsOnly("D", 12);
                        }, atIndex(0)
                )
                .extracting(Desk::getArea, Desk::getStatus)
                .containsOnly(tuple(DATA_AND_TECHNOLOGY, VACANT));

        assertThat(repository.findByStatus(OCCUPIED)).isEmpty();
    }

    @Test
    void findByAreaAndStatus() {
        var desk = new Desk();
        var location = new DeskLocation("D", 12);

        desk.setArea(DATA_AND_TECHNOLOGY);
        desk.setLocation(location);
        desk.setStatus(VACANT);

        repository.save(desk);

        assertThat(repository.findByAreaAndStatus(DATA_AND_TECHNOLOGY, VACANT))
                .hasSize(1)
                .extracting(Desk::getArea, Desk::getLocation, Desk::getStatus)
                .containsOnly(tuple(DATA_AND_TECHNOLOGY, location, VACANT));

        assertThat(repository.findByAreaAndStatus(COLLABORATION, VACANT)).isEmpty();
    }

    @Test
    void saveWithSelfAssignedId() {
        var desk = new Desk();

        var id = "36bb818b-23f4-483e-b858-ae8d9ff04e63";
        var location = new DeskLocation("D", 12);

        desk.setId(id);
        desk.setArea(DATA_AND_TECHNOLOGY);
        desk.setLocation(location);
        desk.setStatus(VACANT);

        var persisted = repository.save(desk);

        assertThat(repository.findById(id))
                .isNotEmpty()
                .get()
                .isEqualTo(persisted);
    }

    @AfterEach
    void deleteAll() {
        repository.deleteAll();
    }

}
