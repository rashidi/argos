package posmy.argos.desk.history.domain;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import posmy.argos.desk.domain.DeskLocation;

import java.util.Optional;

/**
 * @author Rashidi Zin
 */
@RepositoryRestResource(exported = false)
public interface DeskOccupiedHistoryRepository extends MongoRepository<DeskOccupiedHistory, String> {

    Optional<DeskOccupiedHistory> findFirstByLocationOrderByEndDesc(DeskLocation location);

}
