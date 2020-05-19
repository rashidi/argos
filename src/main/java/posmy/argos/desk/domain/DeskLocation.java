package posmy.argos.desk.domain;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Objects;

/**
 * @author Rashidi Zin
 */
@Accessors(fluent = true)
@Setter(onMethod = @__(@JsonSetter))
@Getter(onMethod = @__(@JsonGetter))
public class DeskLocation {

    String row;

    Integer column;

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof DeskLocation)) {
            return false;
        }

        var other = (DeskLocation) obj;

        return Objects.equals(row, other.row) && Objects.equals(column, other.column);
    }

}
