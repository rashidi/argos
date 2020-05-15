package posmy.argos.desk.domain;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author Rashidi Zin
 */
@EqualsAndHashCode
@Accessors(fluent = true)
@Setter(onMethod = @__(@JsonSetter))
@Getter(onMethod = @__(@JsonGetter))
public class DeskLocation {

    String row;

    Integer column;

}
