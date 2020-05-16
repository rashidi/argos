package posmy.argos.desk.validator;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import posmy.argos.desk.domain.Desk;
import posmy.argos.desk.domain.DeskLocation;
import posmy.argos.desk.domain.DeskRepository;

import static posmy.argos.desk.domain.DeskStatus.OCCUPIED;
import static posmy.argos.desk.domain.DeskStatus.VACANT;
import static posmy.argos.security.SecurityHelper.getLoggedInUsername;

/**
 * @author Rashidi Zin
 */
@AllArgsConstructor
public class BeforeSaveDeskValidator implements Validator {

    private final DeskRepository repository;

    @Override
    public boolean supports(Class<?> clazz) {
        return Desk.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        var desk = (Desk) target;
        var status = desk.status();

        if (VACANT == status)
            return;

        boolean isActiveOccupant = isActiveOccupant();

        if (isActiveOccupant) {
            errors.rejectValue("occupant", "occupant.active", "desk.occupant is active");
        }

        boolean isOccupied = isOccupied(desk.location());

        if (isOccupied) {
            errors.rejectValue("location", "location.occupied", "desk.location is not available");
        }

    }

    private boolean isActiveOccupant() {
        var username = getLoggedInUsername();

        Example<Desk> byOccupant = Example.of(new Desk().status(OCCUPIED).occupant(username));

        return repository.exists(byOccupant);
    }

    private boolean isOccupied(DeskLocation location) {
        Example<Desk> occupiedByLocation = Example.of(new Desk().location(location).status(OCCUPIED));

        return repository.exists(occupiedByLocation);
    }
}
