package posmy.argos.desk.domain;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Rashidi Zin
 */
@Data
@Accessors(fluent = true)
public class DeskLocation {

    String row;

    Integer column;

}
