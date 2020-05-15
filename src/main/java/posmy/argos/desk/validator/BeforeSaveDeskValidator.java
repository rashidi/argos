package posmy.argos.desk.validator;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import posmy.argos.desk.domain.Desk;
import posmy.argos.desk.domain.DeskRepository;
import posmy.argos.desk.domain.DeskStatus;

import static posmy.argos.desk.domain.DeskStatus.OCCUPIED;

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

        if (DeskStatus.VACANT == status)
            return;

        var location = desk.location();

        Example<Desk> occupiedByLocation = Example.of(new Desk().location(location).status(OCCUPIED));

        boolean isOccupied = repository.exists(occupiedByLocation);

        if (isOccupied) {
            errors.rejectValue("location", "location.occupied", "desk.location is not available");
        }

    }
}
