package posmy.argos.desk.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Rashidi Zin
 */
@Document
@Getter
@Setter
@Accessors(fluent = true)
public class Desk {

    @Id
    private String id;

    private DeskArea area;

    private DeskLocation location;

    private DeskStatus status;

}
