package posmy.argos.desk.domain;

import lombok.Data;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import static lombok.AccessLevel.PRIVATE;

/**
 * @author Rashidi Zin
 */
@Data
@Document
public class Desk {

    @Id
    @Setter(PRIVATE)
    private String id;

    private DeskArea area;

    private DeskLocation location;

    private DeskStatus status;

}
