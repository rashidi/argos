package posmy.argos.desk.history.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import posmy.argos.desk.domain.DeskLocation;

import java.time.Instant;

/**
 * @author Rashidi Zin
 */
@Document
@Accessors(fluent = true)
@Getter @Setter
public class DeskOccupiedHistory {

    @Id
    private String id;

    private DeskLocation location;

    private String occupant;

    private Instant since;

    private Instant end;

}
