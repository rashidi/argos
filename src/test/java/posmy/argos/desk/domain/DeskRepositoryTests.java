package posmy.argos.desk.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import static org.assertj.core.api.Assertions.assertThat;
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

        assertThat(repository.findByStatus(VACANT)).hasSize(1);

        assertThat(repository.findByStatus(OCCUPIED)).isEmpty();
    }

    @Test
    void findByAreaAndStatus() {
        var desk = new Desk();

        desk.setArea(DATA_AND_TECHNOLOGY);
        desk.setLocation(new DeskLocation("D", 12));
        desk.setStatus(VACANT);

        repository.save(desk);

        assertThat(repository.findByAreaAndStatus(DATA_AND_TECHNOLOGY, VACANT)).hasSize(1);

        assertThat(repository.findByAreaAndStatus(COLLABORATION, VACANT)).isEmpty();
    }

}
