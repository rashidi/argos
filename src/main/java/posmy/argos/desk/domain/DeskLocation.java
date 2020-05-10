package posmy.argos.desk.domain;

import lombok.Data;
import lombok.NonNull;

/**
 * @author Rashidi Zin
 */
@Data
public class DeskLocation {

    @NonNull
    private String row;

    @NonNull
    private Integer column;

}
