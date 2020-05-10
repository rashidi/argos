package posmy.argos.desk.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author Rashidi Zin
 */
public interface DeskRepository extends MongoRepository<Desk, String> {

    List<Desk> findByStatus(DeskStatus status);

    List<Desk> findByAreaAndStatus(DeskArea area, DeskStatus status);

}
