package posmy.argos.desk.junit.arguments;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import posmy.argos.desk.domain.Desk;
import posmy.argos.desk.domain.DeskLocation;

import java.util.stream.Stream;

import static posmy.argos.desk.domain.DeskArea.DATA_AND_TECHNOLOGY;

/**
 * @author Rashidi Zin
 */
public class InvalidLocationsArgumentProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
                Arguments.of(new Desk().area(DATA_AND_TECHNOLOGY)), // location is null
                Arguments.of(new Desk().area(DATA_AND_TECHNOLOGY).location(new DeskLocation().column(12))), // location.row is null
                Arguments.of(new Desk().area(DATA_AND_TECHNOLOGY).location(new DeskLocation().row("D"))), // location.column is null
                Arguments.of(new Desk().area(DATA_AND_TECHNOLOGY).location(new DeskLocation().row("").column(12))) // location.row is empty
        );
    }
}
