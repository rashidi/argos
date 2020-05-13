package posmy.argos.desk.validator;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import posmy.argos.desk.domain.Desk;
import posmy.argos.desk.domain.DeskRepository;

/**
 * @author Rashidi Zin
 */
@AllArgsConstructor
public class BeforeCreateDeskValidator implements Validator {

    private final DeskRepository repository;

    @Override
    public boolean supports(Class<?> clazz) {
        return Desk.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        var source = (Desk) target;

        var findByLocation = Example.of(new Desk().location(source.location()));

        boolean isExist = repository.exists(findByLocation);

        if (isExist) {
            errors.rejectValue("location", "location.exist", "desk.location already exist");
        }

    }

}
