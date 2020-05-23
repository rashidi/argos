package posmy.argos.desk.history.domain;

import posmy.argos.desk.domain.DeskLocation;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * @author Rashidi Zin
 */
@RepositoryRestResource(exported = false)
public interface DeskOccupiedHistoryRepository extends MongoRepository<DeskOccupiedHistory, String> {

    Optional<DeskOccupiedHistory> findFirstByLocationOrderByEndDesc(DeskLocation location);

    boolean existsByLocationAndEndAfter(DeskLocation location, Instant end);
    
}
