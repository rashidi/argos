package posmy.argos.desk.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Rashidi Zin
 */
@Data
@Document
public class Desk {

    @Id
    private String id;

    private DeskArea area;

    private DeskLocation location;

    private DeskStatus status;

}
