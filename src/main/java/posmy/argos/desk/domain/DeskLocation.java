package posmy.argos.desk.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author Rashidi Zin
 */
@Getter
@Setter
@Accessors(fluent = true)
@EqualsAndHashCode
public class DeskLocation {

    String row;

    Integer column;

}
