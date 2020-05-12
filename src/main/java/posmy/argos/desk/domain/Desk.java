package posmy.argos.desk.domain;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Rashidi Zin
 */
@Document
@Data
@Accessors(fluent = true)
@Setter(onMethod = @__(@JsonSetter))
@Getter(onMethod = @__(@JsonGetter))
public class Desk {

    @Id
    private String id;

    private DeskArea area;

    private DeskLocation location;

    private DeskStatus status;

}
