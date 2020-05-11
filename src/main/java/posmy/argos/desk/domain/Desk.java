package posmy.argos.desk.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

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

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Desk) {
            var desk = (Desk) obj;

            return Objects.equals(id, desk.id());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
