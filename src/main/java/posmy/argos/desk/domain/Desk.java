package posmy.argos.desk.domain;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Rashidi Zin
 */
@Document
@EqualsAndHashCode
@Accessors(fluent = true)
@Setter(onMethod = @__(@JsonSetter))
@Getter(onMethod = @__(@JsonGetter))
public class Desk {

    @Id
    private String id;

    @EqualsAndHashCode.Exclude
    private DeskArea area;

    @EqualsAndHashCode.Exclude
    private DeskLocation location;

    @EqualsAndHashCode.Exclude
    private DeskStatus status;

}
