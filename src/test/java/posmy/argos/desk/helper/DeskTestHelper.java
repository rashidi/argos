package posmy.argos.desk.helper;

import posmy.argos.desk.domain.Desk;
import posmy.argos.desk.domain.DeskLocation;

import static posmy.argos.desk.domain.DeskArea.DATA_AND_TECHNOLOGY;

/**
 * @author Rashidi Zin
 */
public final class DeskTestHelper {

    public static Desk create() {
        return new Desk()
                .area(DATA_AND_TECHNOLOGY)
                .location(new DeskLocation().row("D").column(12));
    }

}
